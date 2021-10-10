from django.http import HttpResponse, HttpRequest, JsonResponse
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

def _failed_response(errMsg):
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


OP_HANDLER = {}

def handle_cz3002(request: HttpRequest):
    if request.method != 'POST':
        log.debug('Only POST method is used')

    try:
        req = json.loads(request.body)
    except json.decoder.JSONDecodeError:
        log.error('Cannot parse the request into JSON object.')
        return _failed_response('Invalid request.')

    operation = req['operation']
    auth = req['auth']
    param = req['param']

    handler = OP_HANDLER.get(operation, _default_handler)

    return handler(auth, operation)


def handle_home(request: HttpRequest):
    return HttpResponse(b'It works')
