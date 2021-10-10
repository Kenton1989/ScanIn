from django.http import HttpResponse, HttpRequest, JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
from face_recognizer import FaceRecognizer
import logging

log = logging.getLogger('Request Handler')


def _success_response(returnVal):
    assert isinstance(returnVal, dict)
    res = {
        'success': True,
        'details': "",
        'return': returnVal,
    }
    response = JsonResponse(res)
    return response


def _failed_response(errMsg='invalid request.'):
    assert isinstance(errMsg, str)
    res = {
        'success': False,
        'details': errMsg,
        'return': {},
    }
    response = JsonResponse(res)
    return response


def _default_handler(auth, param):
    return _failed_response('Unknown operation.')

def _valid_auth_format(auth):
    if auth == None: return True
    if not isinstance(auth, dict): return False
    if len(auth) != 2: return False
    if not isinstance(auth['username'], str): return False
    if not isinstance(auth['password'], str): return False
    return True


OP_HANDLER = {}


@csrf_exempt
def handle_cz3002(request: HttpRequest):
    if request.method != 'POST':
        log.debug('Only POST method is used')
        return _failed_response()

    try:
        req = json.loads(request.body)
    except json.decoder.JSONDecodeError:
        log.error('Cannot parse the request into JSON object.')
        return _failed_response()

    operation = req['operation']
    auth = req['auth']
    param = req['param']
    
    try:
        assert isinstance(operation, str)
        assert _valid_auth_format(auth)
        assert isinstance(param, dict)
    except:
        log.error('Invlid request structure')
        return _failed_response()

    handler = OP_HANDLER.get(operation, _default_handler)

    return handler(auth, operation)


def handle_home(request: HttpRequest):
    return HttpResponse(b'It works')
