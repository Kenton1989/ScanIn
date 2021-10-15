from typing import List
from DatabaseAccessor import DatabaseAccessor
from datetime import datetime, timedelta
from PIL import Image
import hashlib

from face_recognizer import FaceRecognizer


def get_1st_or_None(row):
    if row == None or len(row) < 1:
        return None
    else:
        return row[0]


class Manager:
    def __init__(self, dbAccessor: DatabaseAccessor) -> None:
        self._dbAccessor = dbAccessor


class ManagerError(Exception):
    pass


class AccountManager(Manager):

    def __init__(self, dbAccessor: DatabaseAccessor, faceRecognizer: FaceRecognizer) -> Manager:
        super().__init__(dbAccessor)
        self._recognizer = faceRecognizer

    # TODO: format of image
    def registerAccount(self, PID, name, plainPwd, imageList: List[Image.Image]):
        existing_user = self._dbAccessor.getUserInfo(PID)
        if existing_user != None:
            raise ManagerError('user id already exists')

        existing_face = self._recognizer.recognize_face(imageList)
        if existing_face != None:
            raise ManagerError('the face already exists')

        hashedPwd = self._hashPassword(plainPwd)
        self._dbAccessor.registration(PID, name, hashedPwd)
        self._recognizer.register_face(PID, imageList)

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

    def genAuthentication(self, PID):
        hashedPwd = self._dbAccessor.getHashedPwd(PID)
        return {
            'username': PID,
            'hashed_password': hashedPwd,
        }

    def verifyAuthentication(self, auth):
        PID = auth['username']
        hashedPassword = auth['hashed_password']
        trueHash = self._dbAccessor.getHashedPwd(PID)
        return hashedPassword == trueHash

    def getPidFromAuth(self, auth):
        return auth['username']

    def getAllUser(self, querySender):
        if not self._dbAccessor.isAdmin(querySender):
            return []

        res = self._dbAccessor.getAllPerson()
        return _to_person_object(res)

    def getUserInfo(self, PID):
        res = self._dbAccessor.getUserInfo(PID)
        if res == None:
            return None
        else:
            return {
                'pid': res[0],
                'name': res[1],
            }

    def _hashPassword(self, plainPwd: str):
        return hashlib.sha256(plainPwd.encode('utf8')).hexdigest()


class CheckInManager(Manager):

    def __init__(self, dbAccessor, faceRecognizer: FaceRecognizer) -> Manager:
        super().__init__(dbAccessor)
        self._recognizer = faceRecognizer
    # return pid

    def recognition(self, imageList):
        recognizedPID = self._recognizer.recognize_face(imageList)
        return recognizedPID

    def getCurrentAction(self, PID, SID):
        records = self._dbAccessor.getAttendance(PID, SID)
        length = len(records)
        if (length % 2 == 0):
            return True
        else:
            return False

    def checkIn(self, PID, SID):
        """Check in and return the newly created history. If error happens, return None"""
        if not self._dbAccessor.isAuthorizedPerson(PID, SID):
            return None
        if not self._dbAccessor.takeAttendance(PID, SID):
            return None
        res = _to_history_object(
            self._dbAccessor.getAttendance(PID, SID, limit=1))
        return get_1st_or_None(res)


class SessionManager(Manager):
    def __init__(self, dbAccessor):
        super().__init__(dbAccessor)

    def getCurrentSessions(self, participantId):
        res = self._dbAccessor.getCurrentSessions(participantId)
        return _to_session_brief(res)

    def addSession(self, times, period: timedelta, attendeeList,
                   sessionName, creator, venue, sTime: datetime, eTime: datetime):

        if (not self._dbAccessor.isAdmin()):
            return False

        sessionList = []
        lastSession = None

        sTime += period * times
        eTime += period * times
        for i in range(0, times):
            sTime -= period
            eTime -= period
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

    # TODO: max number of rows not restricted
    def getHistory(self, PID=None, SID=None, sDatetime=None, eDatetime=None, limit=50):
        return _to_history_object(self._dbAccessor.getAttendance(PID, SID, sDatetime, eDatetime, limit))

    def getLastHistory(self, PID, SID):
        res_list = self.getHistory(PID=PID, SID=SID, limit=1)
        return get_1st_or_None(res_list)

    def preloadPID(self, PID):
        if (self._dbAccessor.isAdmin(PID)):
            res = self._dbAccessor.getAllPerson()
        else:
            res = self._dbAccessor.getSomePerson(PID)
        return _to_person_object(res)

    def preloadSID(self, PID):
        if (self._dbAccessor.isAdmin(PID)):
            res = self._dbAccessor.getAllSID()
        else:
            res = self._dbAccessor.getSomeSID(PID)
        return _to_session_brief(res)


def _to_session_brief(list_of_session_tuple: list):
    res = []
    for sid, name, beg, end in list_of_session_tuple:
        res.append({
            'sid': sid,
            'name': name,
            'beg_time': beg,
            'end_time': end
        })
    return res


def _to_person_object(list_of_persion_tuple: list):
    res = []
    for pid, name in list_of_persion_tuple:
        res.append({
            'pid': pid,
            'name': name,
        })
    return res


def _to_history_object(list_of_history_tuple: list):
    res = []
    for aid, pid, p_name, sid, s_name, time, is_check_in in list_of_history_tuple:
        res.append({
            'id': aid,
            'pid': pid,
            'attendee_name': p_name,
            'sid': sid,
            'session_name': s_name,
            'time': time.isoformat(),
            'is_in': bool(is_check_in),
        })
    return res
