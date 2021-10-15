import face_recognition
import numpy
import math
from DatabaseAccessor import DatabaseAccessor
from typing import List, Dict
from PIL import Image


class FaceRecognizer:
    def __init__(self, db_accessor: DatabaseAccessor):
        self.vectors: List[numpy.array] = None
        self.labels: List[str] = None
        self.db_accessor: DatabaseAccessor() = db_accessor
        self.setup()

    def setup(self):
        init_labels, init_vectors = self.db_accessor.getFacialVectors()
        assert len(init_labels) == len(init_vectors)
        assert all([len(v) == 128 for v in init_vectors])

        self.vectors = [numpy.array(v) for v in init_vectors]
        self.labels = init_labels

    def recognize_face(self, raw_images: List[Image.Image]):
        assert len(raw_images) > 0
        image = numpy.array(raw_images[0])
        unknown_encoding = face_recognition.face_encodings(image)

        cur_min_distance = math.inf
        cur_min_label = None
        cur_min_encoding = None

        for label, vector in zip(self.labels, self.vectors):
            if face_recognition.face_distance(vector, unknown_encoding) < cur_min_distance:
                cur_min_label = label
                cur_min_distance = unknown_encoding

        if cur_min_label == None:
            return None

        results = face_recognition.compare_faces(
            [cur_min_encoding], unknown_encoding, tolerance=Hyperparams.TOLERANCE)

        if results[0] == False:
            return None

        return cur_min_label

    def register_face(self, pid: str, raw_images: List[Image.Image]):
        assert len(raw_images) > 0
        image = numpy.array(raw_images[0])

        result = face_recognition.face_encodings(image)
        self.vectors.append(result)
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
