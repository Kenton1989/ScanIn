import face_recognition
import numpy
import math
from DatabaseAccessor import DatabaseAccessor
from typing import List, Dict
from PIL import Image


class FaceRecognizer:
    def __init__(self, db_accessor: DatabaseAccessor):
        self.vectors: numpy.array = None
        self.labels: List[str] = None
        self.db_accessor: DatabaseAccessor() = db_accessor
        self.setup()

    def setup(self):
        init_labels, init_vectors = self.db_accessor.getFacialVectors()
        assert len(init_labels) == len(init_vectors)
        assert all([len(v) == 128 for v in init_vectors])

        self.vectors = numpy.array(init_vectors)
        self.labels = init_labels

    def recognize_face(self, raw_images: List[Image.Image]):
        assert len(raw_images) > 0

        if len(self.vectors) <= 0:
            return None

        image = numpy.array(raw_images[0])
        unknown_encoding_list = face_recognition.face_encodings(image)
        if len(unknown_encoding_list) <= 0:
            return None
        unknown_encoding = unknown_encoding_list[0]

        dist_list = face_recognition.face_distance(
            self.vectors, unknown_encoding)
        min_idx = numpy.argmin(dist_list)

        min_distance = dist_list[min_idx]
        min_label = self.labels[min_idx]
        min_encoding = self.vectors[min_idx]

        if min_distance >= Hyperparams.TOLERANCE:
            return None

        return min_label

    def register_face(self, pid: str, raw_images: List[Image.Image]):
        assert len(raw_images) > 0
        image = numpy.array(raw_images[0])

        result = face_recognition.face_encodings(image)[0]
        self.vectors = numpy.concatenate((self.vectors, [result]), axis=0)
        self.labels.append(pid)

        self._send_db_register(pid, result)

    def _send_db_register(self, pid: str, encoding: numpy.ndarray):
        self.db_accessor.addFacialVector(pid, tuple(encoding))

    def get_vectors(self):
        return self.vectors

    def get_labels(self):
        return self.labels


class Hyperparams:
    TOLERANCE = 0.6
