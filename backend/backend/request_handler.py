from django.http import HttpResponse, HttpRequest, JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
from face_recognizer import FaceRecognizer
import logging
import traceback
import jsonschema
from common_request_util import *
from DatabaseAccessor import DatabaseAccessor
from Manager import *
from common_json_schema import AUTHENTICATION_OBJECT

log = logging.getLogger('Request Handler')

dbAccessor = DatabaseAccessor()
face_recognizer = FaceRecognizer(dbAccessor)
account_mng = AccountManager(dbAccessor, face_recognizer)

request_json_schema = {
    '$schema': 'http://json-schema.org/draft-06/schema#',
    'type': 'object',
    'properties': {
        'operation': {'type': 'string'},
        'auth': {'anyOf': [
            {'type': 'null'},
            AUTHENTICATION_OBJECT,
        ]},
        'param': {'type': 'object', 'additionalProperties': True}
    },
    'required': ['operation', 'auth', 'param'],
    'additionalProperties': False,
}
request_json_validator = jsonschema.Draft6Validator(request_json_schema)

OP_HANDLER = {}
NEED_AUTH = {}


def _default_handler(op_name, auth, param):
    return failed_response('unknown operation: ' + op_name)


@csrf_exempt
def handle_cz3002(request: HttpRequest):
    if request.method != 'POST':
        log.debug('Only POST method is used')
        return failed_response()

    try:
        req = json.loads(request.body)
    except json.decoder.JSONDecodeError:
        log.error('Cannot parse the request into JSON object.')
        return failed_response()

    if not request_json_validator.is_valid(req):
        log.error('Invlid request structure')
        return failed_response()

    operation = req['operation']
    auth = req['auth']
    param = req['param']

    handler = OP_HANDLER.get(operation)
    if handler == None:
        return _default_handler(operation, auth, param)

    if NEED_AUTH[operation]:
        if auth == None:
            return failed_response()

        pid = auth['username']
        hashed_pwd = auth['hashed_password']

        if not account_mng.authentication(pid, hashed_pwd):
            log.error('invalid authentication object')
            return failed_response()

    try:
        response = handler(operation, auth, param)
    except:
        log.error(
            'Error happend during executing operation: %s with param %s', operation, str(param))
        traceback.print_exc()
        response = failed_response()

    return response


def set_login_handler():
    global OP_HANDLER

    login_param_schema = {
        '$schema': 'http://json-schema.org/draft-06/schema#',
        'type': 'object',
        'properties': {
            'username': {'type': 'string'},
            'password': {'type': 'string'},
        },
        'required': ['username', 'password'],
        'additionalProperties': False,
    }

    login_param_validator = jsonschema.Draft6Validator(login_param_schema)

    def login_handler(op_name, auth, param):
        if not login_param_validator.is_valid(param):
            return failed_response()

        PID = param['username']
        password = param['password']

        auth_obj = account_mng.login(PID, password)

        if auth_obj == None:
            return failed_response('invalid username or password')
        else:
            return success_response({"auth": auth_obj})

    OP_HANDLER['login'] = login_handler
    NEED_AUTH['login'] = False


set_login_handler()


def handle_home(request: HttpRequest):
    return HttpResponse(b'It works')
