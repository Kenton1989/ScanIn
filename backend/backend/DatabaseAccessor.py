import mysql.connector
from datetime import datetime, date

DEFAULT_MYSQL_PARAM = {
    'host': 'localhost',
    'port': 3306,
    'user': 'cz3002',
    'password': 'cz3002@MySql',
    'database': 'cz3002',
}


def get_1st_or_None(row):
    if row == None:
        return None
    else:
        return row[0]


class DatabaseAccessor:
    def __init__(self, mySqlParam=DEFAULT_MYSQL_PARAM):
        self._connection = mysql.connector.connect(**mySqlParam)

    def close(self):
        self._connection.close()

    # account part
    def registration(self, PID, name, hashedPwd):
        cursor = self._connection.cursor()

        sql = "INSERT INTO person_info(PID, name, hashed_password, activated) VALUES (%(PID)s, %(name)s, %(passwd)s, 1);"
        param = {'PID': PID, 'name': name, 'passwd': hashedPwd}
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def getUserInfo(self, PID):
        cursor = self._connection.cursor()

        sql = "SELECT * FROM person_info WHERE PID = %(PID)s"
        param = {'PID': PID}
        cursor.execute(sql, param)
        result = cursor.fetchone()
        return result

    def disableAccount(self, PID):
        cursor = self._connection.cursor()
        sql = "UPDATE person_info SET activated = False WHERE PID = %(PID)s"
        param = {'PID': PID}
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def getHashedPwd(self, PID):
        cursor = self._connection.cursor()
        sql = "SELECT hashed_password FROM person_info WHERE PID = %(PID)s"
        param = {'PID': PID}
        cursor.execute(sql, param)
        res = cursor.fetchone()
        return get_1st_or_None(res)

    def getAuthInfo(self, PID, plainPassword):
        cursor = self._connection.cursor()
        sql = "SELECT PID, hashed_password FROM person_info WHERE PID = %(PID)s AND hashed_password = SHA2(%(password)s, 256)"
        param = {'PID': PID, 'password': plainPassword}
        cursor.execute(sql, param)
        res = cursor.fetchone()
        return res

    def getFacialVectors(self):
        cursor = self._connection.cursor()  # TODO: format needs change?
        sql = "SELECT * FROM facial_vector;"
        cursor.execute(sql)
        result = tuple(cursor)
        pid = [row[0] for row in result]
        feature = [row[1:] for row in result]
        return pid, feature

    def addFacialVector(self, PID, facialVector):
        cursor = self._connection.cursor()  # TODO: format needs change?
        sql = "INSERT INTO facial_vector VALUES (%s" + ", %s"*128 + ");"
        param = (PID,) + tuple(facialVector)
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def isAdmin(self, PID):
        cursor = self._connection.cursor()
        sql = "SELECT is_admin FROM person_info WHERE PID = %s;"
        param = (PID)
        result = cursor.execute(sql, param).fetchone()

        if result != None:
            return result[0]
        else:
            return None
    # account part end

    # check in part
    def takeAttendance(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "INSERT INTO attendance(PID, SID, checkIn) SELECT %(PID)s, %(SID)s, (COUNT(*)+1)%2 FROM attendance a WHERE a.PID = %(PID)s AND a.SID = %(SID)s;"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        self._connection.commit()

        return cursor.rowcount > 0
    # check in part end

    # session part
    def isAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "SELECT COUNT(*) FROM authorized_attendee WHERE PID = %(PID)s AND SID = %(SID)s"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        res = cursor.fetchone()
        return res > 0

    def addAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "INSERT INTO authorized_attendee(PID, SID) VALUES  (%(PID)s, %(SID)s)"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def removeAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "DELETE FROM authorized_attendee(PID, SID) WHERE PID = %(PID)s AND SID = %(SID)s"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def addSession(self, sessionName, creator, venue, startTime, endTime, nextSession):
        cursor = self._connection.cursor()
        sql = "INSERT INTO session_info(creator, session_name, venue, start_time, end_time, next_session) VALUES(%s, %s, %s, %s, %s, %s)"
        param = (creator, sessionName, venue, startTime, endTime, nextSession)
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.lastrowid

    def deleteSession(self, SID):
        cursor = self._connection.cursor()
        sql = "DELETE FROM session_info WHERE SID = %(SID)s"
        param = {'SID': SID}
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def getNextSession(self, SID):
        cursor = self._connection.cursor()
        sql = "SELECT next_session FROM session_info WHERE SID = %(SID)s"
        param = {'SID': SID}
        cursor.execute(sql, param)
        next_session = tuple(cursor)
        return next_session

    def updateSessionTime(self, SID, newStartTime, newEndTime):
        cursor = self._connection.cursor()
        sql = "UPDATE session_info  SET start_time = %(sTime)s AND end_time = %(eTime)s WHERE SID = %(SID)s"
        param = {'sTime': newStartTime, 'eTime': newEndTime, 'SID': SID}
        cursor.execute(sql, param)
        self._connection.commit()
        return cursor.rowcount > 0

    def getAllPerson(self):
        cursor = self._connection.cursor()
        sql = "SELECT PID, name FROM person_info WHERE activated = 1"
        cursor.execute(sql)
        result = tuple(cursor)
        return result

    def getSomePerson(self, PID):
        cursor = self._connection.cursor()
        sql = "SELECT PID, name FROM person_info WHERE activated = 1 AND PID = %s"
        param = (PID)
        cursor.execute(sql, param)
        result = tuple(cursor)
        return result

    def getAllSID(self):
        cursor = self._connection.cursor()
        sql = "SELECT SID, session_name, start_time, end_time FROM session_info"
        cursor.execute(sql)
        result = tuple(cursor)
        return result

    def getSomeSID(self, PID):
        cursor = self._connection.cursor()
        sql = "SELECT session_info.SID, session_name, start_time, end_time FROM session_info JOIN authorized_attendee ON session_info.SID = authorized_attendee.SID WHERE authorized_attendee.PID = %s"
        param = (PID)
        cursor.execute(sql, param)
        result = tuple(cursor)
        return result
    # session part end

    # history part
    def getAttendance(self, PID, SID, startDatetime: date, endDatetime: date, limit=50):
        cursor = self._connection.cursor()
        sql = "SELECT a.AID, a.PID, p.name, a.SID, s.session_name, a.check_time, a.checkIn FROM attendance a JOIN person_info p ON a.PID = p.PID JOIN session_info s ON a.SID = s.SID WHERE 1=1"

        param = {}

        if PID != None:
            sql += " AND a.PID = %(PID)s"
            param['PID'] = PID
        if SID != None:
            sql += " AND a.SID = %(SID)s"
            param['SID'] = SID
        if startDatetime != None:
            sql += " AND a.check_time >= %(startDatetime)s"
            param['startDatetime'] = startDatetime
        if endDatetime != None:
            sql += " AND a.check_time <= %(endDatetime)s"
            param['endDatetime'] = endDatetime

        sql += " ORDER BY a.check_time DESC LIMIT %(limit)s;"
        param['limit'] = limit

        cursor.execute(sql, param)
        result = tuple(cursor)
        return result

    def getCurrentSessions(self, PID, currentTime: datetime = None):
        cursor = self._connection.cursor()

        if currentTime == None:
            currentTime = datetime.now()
        sql = "SELECT s.SID, s.session_name, s.start_time, s.end_time FROM session_info s JOIN authorized_attendee a ON s.SID = a.SID WHERE PID = %(PID)s AND start_time >= %(cTime)s AND end_time <= %(cTime)s"
        param = {'PID': PID, 'cTime': currentTime}
        cursor.execute(sql, param)
        result = tuple(cursor)
        return result
    # history part end
