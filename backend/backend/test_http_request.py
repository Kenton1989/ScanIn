from datetime import datetime, timezone

from urllib.request import Request, urlopen
import json
import base64

URL = 'http://104.248.151.223:3002/cz3002/'

ADMIN_AUTH = {
    'username': 'admin',
    'hashed_password': '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'
}

def send(opName, params={}, auth=None):
    header = {
        'Content-Type': 'application/json'
    }

    data = {
        'auth': auth,
        'operation': opName,
        'param': params,
    }

    data_byte = json.dumps(data).encode('utf8')
    print('Sending')
    print('URL:', URL)
    print('Method:', 'POST')
    print('Content-Type:', 'application/json')
    print('Body:')
    print(data_byte)
    print()
    req = Request(URL, data_byte, headers=header, method='POST')

    response = urlopen(req)

    reply = json.load(response)
    print('Response:')
    print(json.dumps(reply))
    print()
    return reply


def img_to_base64(filename):
    with open(filename, 'rb') as img:
        databyte = img.read()

    return base64.b64encode(databyte).decode('ascii')


def test_login(username='admin', pwd='password'):
    print(send(
        opName='login',
        params={
            'username': username,
            'password': pwd,
        },
        auth=None
    ))


def test_get_history_param(auth):
    print(send(
        opName='get_valid_history_param',
        params={},
        auth=auth
    ))


def test_get_history(pid=None, sid=None, beg=None, end=None, max_num=10):
    param = {
        'pid': pid,
        'sid': sid,
        'beg_time': beg,
        'end_time': end,
        'max_num': max_num,
    }
    print(send(
        opName='get_history',
        params=param,
        auth=ADMIN_AUTH
    ))

def test_add_session(name, venue, beg: datetime, end: datetime, rep, period, unit, attendees):
    param = {
        'session_name': name,
        'venue': venue,
        'beg_time': beg.isoformat(),
        'end_time': end.isoformat(),
        'repeat': rep,
        'period': period,
        'period_unit': unit,
        'attendees': attendees,
    }
    print(send(
        opName='add_session',
        params=param,
        auth=ADMIN_AUTH
    ))


def test_get_last_history(auth, sid):
    print(send(
        opName='get_last_history',
        params={
            'sid': sid
        },
        auth=auth
    ))


def test_check_in_out(auth, sid):
    print(send(
        opName='check_in_out',
        params={
            'sid': sid
        },
        auth=auth
    ))


def test_get_attendees():
    print(send(
        opName='get_attendees',
        params={},
        auth=ADMIN_AUTH
    ))


if __name__ == '__main__':
    auth = {'username': 'U1922499K', 'hashed_password':
            'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f'}
    # test_login('U1922499K', '12345678')
    test_get_history_param(ADMIN_AUTH)
    # end = datetime.fromisoformat('2021-10-17T22:45:30+08:00')
    # beg = datetime.fromisoformat('2021-10-17T22:44:30+08:00')
    # test_get_history(beg=beg.isoformat(), end=end.isoformat())
    # test_get_attendees()
    # test_get_last_history(auth, 5)
    # test_check_in_out(auth, 5)
    # test_add_session(
    #     name='Everlasting',
    #     venue='Earth',
    #     beg=datetime(2000, 1, 1),
    #     end=datetime(2100, 1, 1),
    #     rep=1,
    #     period=1,
    #     unit='day',
    #     attendees=['U1922499K'])
