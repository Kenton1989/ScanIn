from urllib.request import Request, urlopen
import json

URL = 'http://104.248.151.223:3002/cz3002/'


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
    print('Sending', data, 'to', URL)
    req = Request(URL, data_byte, headers=header, method='POST')

    response = urlopen(req)

    print('Loading response')
    reply = json.load(response)

    return reply


def test_login():
    print(send(
        opName='login',
        params={
            'username': 'admin',
            'password': 'password',
        },
        auth=None
    ))


def test_get_history():
    print(send(
        opName='get_valid_history_param',
        params={},
        auth={
            'username': 'admin',
            'hashed_password': '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'
        }
    ))


def test_get_history():
    param = {
        'pid': 'admin',
        'sid': 4,
        'beg_time': None,
        'end_time': None,
        'max_num': 10,
    }
    print(send(
        opName='get_history',
        params={},
        auth={
            'username': 'admin',
            'hashed_password': '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'
        }
    ))


def test_get_last_history():
    print(send(
        opName='get_last_history',
        params={
            'sid': 4
        },
        auth={
            'username': 'admin',
            'hashed_password': '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'
        }
    ))



if __name__ == '__main__':
    # test_login()
    # test_get_history_param()
    test_get_last_history()
