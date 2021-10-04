

from re import T


class AccountManager:

    def __init__(self, dbAccessor, faceRcognizer) -> None:
        self._dbAccessor = dbAccessor
        self._recognizer = faceRcognizer

    def registerAccount(self, PID, name, hashed_pwd, image):
        exist = self._dbAccessor.getUserInfo(PID)
        if (exist):
            return False
        
        facial_vector = self._recognizer.register_face(image)
        self._dbAccessor.registration(PID, name, hashed_pwd, facial_vector)
        return True 

    def disableAccount(self, PID):
        exist = self._dbAccessor.getUserInfo(PID)
        if (not exist):
            return False

        self._dbAccessor.disableAccount(PID)
        return True

class CheckInManager:

    def __init__(self, dbAccessor) -> None:
        self._dbAccessor = dbAccessor

    def getCurrentAction(self, PID, SID):
        records = self._dbAccessor.getAttendance(PID, SID)
        length = len(records)
        if (length % 2 == 0):
            return True
        else:
            return False

    def checkIn(self, PID, SID, checkIn):
        return self._dbAccessor.takeAttendance(PID, SID, checkIn)

