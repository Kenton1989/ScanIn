import mysql.connector

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

    def authentication(self, PID, inputHashedPassword):
        cursor = self._connection.cursor()
        sql_check = "SELECT hashed_password FROM person_info WHERE PID = %(PID)s"
        val_check = {'PID' : PID}
        hashed_password = cursor.execute(sql_check, val_check)
        
        return hashed_password == inputHashedPassword  
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
    def addSession(self, startTime, endTime):
        pass

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
    # session part end
    
    # history part
    def getSessions(self, PID, SID, startDate, endDate):
        pass

    def getCurrentSessions(self):
        # self.getSessions(None, None, )
        pass
    # history part end
    


def columnGenerator():
    COLUMN_COUNT = 128
    for i in range(1, COLUMN_COUNT+1):  
        print("v" + str(i) + " DOUBLE PRECISION(64),")