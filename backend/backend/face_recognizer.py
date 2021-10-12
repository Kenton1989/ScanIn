import face_recognition
import numpy
from DatabaseAccessor import DatabaseAccessor
from typing import List
from PIL import Image

def _checkLen(vects, labels):
    if len(vects) != len(labels):
        print("WARNING: length of vector does not match length of labels")
        print("recognizer returned without modification, or null recognizer created")
        return False
    return True


class FaceRecognizer:
    def __init__(self,  db_accessor: DatabaseAccessor):
        # TODO: use accessor to get the initialization data
        init_vectors: List[numpy.ndarray] = []
        init_labels: List[str] = []
        return

        if not _checkLen(init_vectors, init_labels):
            self.vectors = None
            self.labels = None
            self.label_dict = None
            return
        if len(init_vectors[0]) != 128 and not isinstance(init_vectors[0], int):
            init_vectors = [face_recognition.face_encodings(
                idx) for idx in range(len(init_vectors))]
        self.vectors = init_vectors
        self.labels = init_labels
        tempDict = {}
        for idx in range(len(init_vectors)):
            tempDict[init_labels[idx]] = init_vectors[idx]
        self.label_dict = tempDict

    def recognize_face(self, raw_images: Image.Image):
        assert len(raw_images) > 1
        image = numpy.array(raw_images[0])
        unknown_encoding = face_recognition.face_encodings(image)
        results = face_recognition.compare_faces(
            self.vectors, unknown_encoding, tolerance=Hyperparams.TOLERANCE)
        result_labels = []
        for idx in range(len(results)):
            if results[idx]:
                result_labels.append(self.labels[idx])
        # TODO: only return one optimal PID or None
        return result_labels

    def register_face(self, pid: int, raw_images: List[Image.Image]):
        assert len(raw_images) > 1
        image = numpy.array(raw_images[0])
        result = face_recognition.face_encodings(image)
        self._send_db_register(pid, result)

    def _send_db_register(self, pid: int, encoding: numpy.ndarray):
        pass

    def get_vectors(self):
        return self.vectors

    def get_labels(self):
        return self.labels

    def get_label_dict(self):
        return self.label_dict

    def reset(self, new_vects: List[numpy.ndarray], new_labels: List[str]):
        if _checkLen(new_vects, new_labels):
            for idx in range(len(new_vects)):
                if len(new_vects[idx]) != 128:
                    print("WARNING: length of vector[", idx, "] is not 128.")
                    print(
                        "you must make sure that every element in vector has the dimension of 128 for recognition.")
                    print("recognizer returned without modification")
                    return False
            if len(new_vects[0]) != 128:
                new_vects = [face_recognition.face_encodings(
                    idx) for idx in range(len(new_vects))]
            self.vectors = new_vects
            self.labels = new_labels
            tempDict = {}
            for idx in range(len(new_vects)):
                tempDict[new_labels[idx]] = new_vects[idx]
            self.label_dict = tempDict
            print("recognizer successfully updated.")
            return True
        return False

    def reset_from_db(self, new_vects: List[numpy.ndarray], new_labels: List[str]):
        pass

    def _get_db_vectors(self):
        pass


class Hyperparams:
    TOLERANCE = 0.6
