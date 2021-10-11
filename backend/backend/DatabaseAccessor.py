import mysql.connector
from datetime import datetime

DEFAULT_MYSQL_PARAM = {
    'host': 'localhost',
    'port': 3306,
    'user': 'cz3002',
    'password': 'cz3002@MySql',
    'database': 'cz3002',
}


class DatabaseAccessor:
    def __init__(self, mySqlParam=DEFAULT_MYSQL_PARAM):
        self._connection = mysql.connector.connect(**mySqlParam)

    def close(self):
        self._connection.close()

    # account part
    def registration(self, PID, name, plainPwd):
        cursor = self._connection.cursor()

        sql = "INSERT INTO person_info(PID, name, hashed_password, activated) VALUES (%(PID)s, %(name)s, SHA2(%(passwd)s, 256), 1);"
        param = {'PID': PID, 'name': name, 'passwd': plainPwd}
        cursor.execute(sql, param)

        return cursor.rowcourt > 0

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
        return cursor.rowcount > 0

    def getHashedPwd(self, PID, inputHashedPassword):
        cursor = self._connection.cursor()
        sql = "SELECT hashed_password FROM person_info WHERE PID = %(PID)s"
        param = {'PID': PID}
        cursor.execute(sql, param)
        res = cursor.fetchone()
        return res

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
        return cursor.rowcount > 0

    # account part end

    # check in part
    def getAttendance(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "SELECT * FROM attendance WHERE PID = %(PID)s AND SID = %(SID)s"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        result = cursor.fetchone()
        return result

    def takeAttendance(self, PID, SID, checkIn):
        cursor = self._connection.cursor()
        sql = "INSERT INTO attendance(PID, SID, checkIn) VALUES  (%(PID)s, %(SID)s, %(checkIn)s)"
        param = {'PID': PID, 'SID': SID, 'checkIn': checkIn}
        cursor.execute(sql, param)
        return cursor.rowcount > 0
    # check in part end

    # session part
    def addAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "INSERT INTO authorized_attendee(PID, SID) VALUES  (%(PID)s, %(SID)s)"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        return cursor.rowcount > 0

    def removeAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "DELETE FROM authorized_attendee(PID, SID) WHERE PID = %(PID)s AND SID = %(SID)s"
        param = {'PID': PID, 'SID': SID}
        cursor.execute(sql, param)
        return cursor.rowcount > 0

    def addSession(self, sessionName, creator, venue, startTime, endTime, nextSession):
        cursor = self._connection.cursor()
        sql = "INSERT INTO session_info(creator, session_name, venue, start_time, end_time, next_session) VALUES(%s, %s, %s, %s, %s, %s)"
        param = (creator, sessionName, venue, startTime, endTime, nextSession)
        cursor.execute(sql, param)
        return cursor.lastrowid

    def deleteSession(self, SID):
        cursor = self._connection.cursor()
        sql = "DELETE FROM session_info WHERE SID = %(SID)s"
        param = {'SID': SID}
        cursor.execute(sql, param)
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
        result = tuple(cursor)

    def getAllPerson(self):
        cursor = self._connection.cursor()
        sql = "SELECT PID, name FROM person_info WHERE activated = 1"
        cursor.execute(sql)
        result = tuple(cursor)
        return result
    # session part end

    # history part
    def getAttendance(self, PID, SID, startDate, endDate, limit=50):
        cursor = self._connection.cursor()
        sql = "SELECT * FROM attendance WHERE 1=1"
        param = {}

        if (PID):
            sql += " AND PID = %(PID)s"
            param['PID'] = PID
        if (SID):
            sql += " AND SID = %(SID)s"
            param['SID'] = SID
        if (startDate):
            sql += " AND check_time >= %(startDate)s"
            param['startDate'] = startDate
        if (endDate):
            sql += " AND check_time <= %(endDate)s"
            param['endDate'] = endDate

        sql += " ORDER BY check_time DESC LIMIT %d" % int(limit)

        cursor.execute(sql, param)
        result = tuple(cursor)
        return result

    def getCurrentSessions(self, PID):
        cursor = self._connection.cursor()

        currentTime = datetime.now.__str__()
        sql = "SELECT * FROM session_info NATURAL JOIN authorized_attendee WHERE PID = %(PID)s AND start_time >= %(cTime)s AND end_time <= %(cTime)s"
        param = {'PID': PID, 'cTime': currentTime}
        cursor.execute(sql, param)
        result = tuple(cursor)
        return result
    # history part end
