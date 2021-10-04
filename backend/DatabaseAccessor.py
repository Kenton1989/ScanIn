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

    def registration(self, PID, name, hashedPwd, facialVector):
        

        if (exist):
            return False
        else:
            sql_register = "INSERT INTO " #TODO: how to set values?
            return True
    
    def getUserInfo(self, PID):
        cursor = self._connection.cursor()

        sql_check = "SELECT * FROM person_info WHERE PID = %(PID)s"
        val_check = {'PID' : PID}
        result = cursor.execute(sql_check, val_check)
        return result

    def disableAccount(self, PID):
        cursor = self._connection.cursor()

        sql_check = "SELECT * FROM person_info WHERE PID = %(PID)s"
        val_check = {'PID' : PID}
        personal_info = cursor.execute(sql_check, val_check)

        if (len(personal_info) == 0):
            return False
        if (personal_info['activated']):
            sql_update = "UPDATE person_info SET activated = False WHERE PID = %(PID)s"
            val_update = {'PID' : PID}
            cursor.execute(sql_update, val_update);
            return True

    def addAuthorizedPerson(self, PID, SID):
        pass

    def removeAuthorizedPerson(self, PID, SID):
        pass

    def authentication(self, PID, inputHashedPassword):
        cursor = self._connection.cursor()
        sql_check = "SELECT hashed_password FROM person_info WHERE PID = %(PID)s"
        val_check = {'PID' : PID}
        hashed_password = cursor.execute(sql_check, val_check)
        
        return hashed_password == inputHashedPassword

    def getCurrentSessions(self):
        # self.getSessions(None, None, )
        pass

    def getSessions(self, PID, SID, startDate, endDate):
        pass


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
        pass

    


def columnGenerator():
    COLUMN_COUNT = 128
    for i in range(1, COLUMN_COUNT+1):  
        print("v" + str(i) + " DOUBLE PRECISION(64),")