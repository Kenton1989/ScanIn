import mysql.connector
from datetime import datetime

DEFAULT_MYSQL_PARAM = {
    'host': 'localhost',
    'user': 'username',
    'password': 'password',
}

class DatabaseAccessor:
    def __init__(self, mySqlParam=DEFAULT_MYSQL_PARAM):
        self._connection = mysql.connector.connect(**mySqlParam)
        
    def close(self):
        self._connection.close()

    # account part
    def registration(self, PID, name, hashedPwd, facialVector):
        sql_register = "INSERT INTO " #TODO: how to set values?
        pass
    
    def getUserInfo(self, PID):
        cursor = self._connection.cursor()

        sql_check = "SELECT * FROM person_info WHERE PID = %(PID)s"
        val_check = {'PID' : PID}
        result = cursor.execute(sql_check, val_check)
        return result

    def disableAccount(self, PID):
        cursor = self._connection.cursor()
        sql_update = "UPDATE person_info SET activated = False WHERE PID = %(PID)s"
        val_update = {'PID' : PID}
        cursor.execute(sql_update, val_update);
        return True

    def getHashedPwd(self, PID, inputHashedPassword):
        cursor = self._connection.cursor()
        sql_check = "SELECT hashed_password FROM person_info WHERE PID = %(PID)s"
        val_check = {'PID' : PID}
        hashed_password = cursor.execute(sql_check, val_check)
        
        return hashed_password

    def getFacialVectors(self):
        cursor = self._connection.cursor() #TODO: format needs change?
        sql = "SELECT PID, facial_vector FROM person_info"
        result = cursor.execute(sql)
        return result
    # account part end

    # check in part
    def getAttendance(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "SELECT * FROM attendance WHERE PID = %(PID)s AND SID = %(SID)s"
        val = {'PID' : PID, 'SID' : SID}
        result = cursor.execute(sql, val)
        return result

    def takeAttendance(self, PID, SID, checkIn):
        cursor = self._connection.cursor()
        sql = "INSERT INTO attendance(PID, SID, checkIn) VALUES " ,\
            + "(%(PID)s, %(SID)s, %(checkIn)s)"
        val = {'PID' : PID, 'SID' : SID, 'checkIn' : checkIn}
        result = cursor.execute(sql, val)
        return result  
    # check in part end

    # session part
    def addAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "INSERT INTO authorized_attendee(PID, SID) VALUES " ,\
            + "(%(PID)s, %(SID)s)"
        val = {'PID' : PID, 'SID' : SID}
        result = cursor.execute(sql, val)

    def removeAuthorizedPerson(self, PID, SID):
        cursor = self._connection.cursor()
        sql = "DELETE FROM authorized_attendee(PID, SID) WHERE " ,\
            + "PID = %(PID)s AND SID = %(SID)s"
        val = {'PID' : PID, 'SID' : SID}
        result = cursor.execute(sql, val)

    # return SID
    def addSession(self, sessionName, creator, venue, startTime, endTime, nextSession):
        cursor = self._connection.cursor()
        sql = "INSERT INTO session_info(creator, session_name, venue, start_time, end_time, next_session)",\
            + " VALUES(%s, %s, %s, %s, %s, %s)"
        val = (sessionName, creator, venue, startTime, endTime, nextSession)
        cursor.execute(sql, val)

        sql_len = "SELECT COUNT(*) FROM session_info"
        length = cursor.execute(sql_len)
        return length

    def deleteSession(self, SID):
        cursor = self._connection.cursor()
        sql_del = "DELETE FROM session_info WHERE SID = %(SID)s"
        val_del = {'SID' : SID}
        result = cursor.execute(sql_del, val_del)

    def getNextSession(self, SID):
        cursor = self._connection.cursor()
        sql_sel = "SELECT next_session FROM session_info WHERE SID = %(SID)s"
        val_sel = {'SID' : SID}
        next_session = cursor.execute(sql_sel, val_sel)
        return next_session

    def updateSessionTime(self, SID, newStartTime, newEndTime):
        cursor = self._connection.cursor()
        sql = "UPDATE session_info " ,\
            + "SET start_time = %(sTime)s AND end_time = %(eTime)s" ,\
            + "WHERE SID = %(SID)s"
        val = {'sTime' : newStartTime, 'eTime' : newEndTime, 'SID' : SID}
        result = cursor.execute(sql, val)
    
    def getAllPerson(self):
        cursor = self._connection.cursor()
        sql = "SELECT PID, name FROM person_info WHERE activated = true"
        result = cursor.execute(sql)
        return result
    # session part end
    
    # history part
    def getAttendance(self, PID, SID, startDate, endDate):
        cursor = self._connection.cursor()
        sql = "SELECT * FROM attendance WHERE 1=1"
        val = {}
        
        if (PID):
            sql += " AND PID = %(PID)s"
            val['PID'] = PID
        if (SID):
            sql += " AND SID = %(SID)s"
            val['SID'] = SID
        if (startDate):
            sql += " AND check_time >= %(startDate)s"
            val['startDate'] = startDate
        if (endDate):
            sql += "AND check_time >= %(endDate)s"
            val['endDate'] = endDate

        result = cursor.execute(sql, val)
        return result

    def getCurrentSessions(self, PID):
        cursor = self._connection.cursor()

        currentTime = datetime.now.__str__()
        sql = "SELECT * FROM session_info NATURAL JOIN authorized_attendee",\
            + "WHERE PID = %(PID)s AND start_time >= %(cTime)s AND end_time <= %(cTime)s"
        val = {'PID' : PID, 'cTime' : currentTime} 
        result = cursor.execute(sql, val)
        return result
    # history part end
    


def columnGenerator():
    COLUMN_COUNT = 128
    for i in range(1, COLUMN_COUNT+1):  
        print("v" + str(i) + " DOUBLE PRECISION(64),")

