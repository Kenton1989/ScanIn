from DatabaseAccessor import DatabaseAccessor
from datetime import datetime, timedelta

from backend.backend.face_recognizer import FaceRecognizer


class Manager:
    def __init__(self, dbAccessor: DatabaseAccessor) -> None:
        self._dbAccessor = dbAccessor


class AccountManager(Manager):

    def __init__(self, dbAccessor, faceRecognizer:FaceRecognizer) -> Manager:
        super().__init__(dbAccessor)
        self._recognizer = faceRecognizer

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

    def login(self, PID, plainPassword):
        res = self._dbAccessor.getAuthInfo(PID, plainPassword)
        if res == None:
            return None
        else:
            return {
                "username": res[0],
                "hashed_password": res[1],
            }

    def authentication(self, PID, hashedPassword):
        trueHash = self._dbAccessor.getHashedPwd(PID)
        return hashedPassword == trueHash


class CheckInManager(Manager):

    def __init__(self, dbAccessor, faceRecognizer:FaceRecognizer) -> Manager:
        super().__init__(dbAccessor)
        self._recognizer = faceRecognizer
    # return pid
    def recognition(self, image):
        recognizedPID = self._recognizer.recognize_face(image)
        return recognizedPID

    def getCurrentAction(self, PID, SID):
        records = self._dbAccessor.getAttendance(PID, SID)
        length = len(records)
        if (length % 2 == 0):
            return True
        else:
            return False

    def checkIn(self, PID, SID, checkIn):
        return self._dbAccessor.takeAttendance(PID, SID, checkIn)

class SessionManager(Manager):
    def __init__(self, dbAccessor):
        super().__init__(dbAccessor)

    #TODO: done?
    def addSession(self, times, period:timedelta, attendeeList,
                sessionName, creator, venue, sTime:datetime, eTime:datetime):
        
        if (not self._dbAccessor.isAdmin()):
            return False

        sessionList = []
        lastSession = None

        sTime.__add__(period * times)
        eTime.__add__(period * times)
        for i in range(0, times):
            sTime.__add__(-period)
            eTime.__add__(-period)
            lastSession = self._dbAccessor.addSession(
                sessionName, creator, venue, sTime, eTime, lastSession)
            sessionList.append(lastSession)

        for s in sessionList:
            for p in attendeeList:
                self._dbAccessor.addAuthorizedPerson(p, s)
        return True

    def deleteSession(self, SID, linked):
        next_session = self._dbAccessor.getNextSession(SID)
        while (linked and next_session):
            self._dbAccessor.deleteSession(SID)
            SID = next_session
            next_session = self._dbAccessor.getNextSession(SID)
            
        self._dbAccessor.deleteSession(SID)

    def addAttendee(self, PID, SID, linked):
        next_session = self._dbAccessor.getNextSession(SID)
        
        while (linked and next_session):
            self._dbAccessor.addAuthorizedPerson(PID, SID)
            SID = next_session
            next_session = self._dbAccessor.getNextSession(SID)
            
        self._dbAccessor.addAuthorizedPerson(PID, SID)

    def deleteAttendee(self, PID, SID, linked):
        next_session = self._dbAccessor.getNextSession(SID)
        
        while (linked and next_session):
            self._dbAccessor.removeAuthorizedPerson(PID, SID)
            SID = next_session
            next_session = self._dbAccessor.getNextSession(SID)
            
        self._dbAccessor.removeAuthorizedPerson(PID, SID)

    def updateSessionTime(self, SID, sTime, eTime, linked):
        if (sTime < eTime):
            return False
        next_session = self._dbAccessor.getNextSession(SID)
        
        while (linked and next_session):
            self._dbAccessor.updateSessionTime(SID, sTime, eTime)
            SID = next_session
            next_session = self._dbAccessor.getNextSession(SID)
            
        self._dbAccessor.updateSessionTime(SID, sTime, eTime)


class HistoryManager(Manager):
    def __init__(self, dbAccessor):
        super().__init__(dbAccessor)

    #TODO
    def getHistory(self, PID, SID, sDate, eDate):
        return self._dbAccessor.getAttendance(PID, SID, sDate, eDate)
        
    def preloadPID(self, PID):
        if (self._dbAccessor.isAdmin(PID)):
            return self._dbAccessor.getAllPerson()
        else: 
            return (PID)
            
    def preloadSID(self, PID):
        if (self._dbAccessor.isAdmin(PID)):
            return self._dbAccessor.getAllSID()
        else: 
            return self._dbAccessor.getSomeSID(PID)
            