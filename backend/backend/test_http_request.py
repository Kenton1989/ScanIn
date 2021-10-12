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


if __name__ == '__main__':
    print(send('get_something', {}, {'username': 'aba', 'password': 'hey'}))
