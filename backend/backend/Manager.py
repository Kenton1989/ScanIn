from DatabaseAccessor import DatabaseAccessor
from datetime import datetime, timedelta


class Manager:
    def __init__(self, dbAccessor:DatabaseAccessor) -> None:
        self._dbAccessor = dbAccessor

class AccountManager(Manager):

    def __init__(self, dbAccessor, faceRcognizer) -> Manager:
        super.__init__(self, dbAccessor)
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

    def authentication(self, PID, inputHashedPwd):
        hashedPwd = self._dbAccessor.getHashedPwd(PID, inputHashedPwd)
        return hashedPwd == inputHashedPwd

class CheckInManager(Manager):

    # return pid
    def recognition(self, image):
        pass

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

    #TODO: done?
    def addSession(self, times, period:timedelta, attendeeList,
                sessionName, creator, venue, sTime:datetime, eTime:datetime):
        
        sessionList = []
        lastSession = None

        sTime.__add__(period * times)
        eTime.__add__(period * times)
        for i in range(0, times):
            sTime.__add__(-period)
            eTime.__add__(-period)
            lastSession = self._dbAccessor.addSession(sessionName, creator, venue, sTime, eTime, lastSession)
            sessionList.append(lastSession)

        for s in sessionList:
            for p in attendeeList:
                self._dbAccessor.addAuthorizedPerson(p, s)

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

    #TODO
    def getHistory(self, PID, SID, sDate, eDate):
        return self._dbAccessor.getAttendance(PID, SID, sDate, eDate)
        
    def preloading(self):
        pass