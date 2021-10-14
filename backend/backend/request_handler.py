from django.http import HttpResponse, HttpRequest, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from datetime import datetime, timedelta
import json
from face_recognizer import FaceRecognizer
import logging
import traceback
import jsonschema
from common_request_util import *
from DatabaseAccessor import DatabaseAccessor
from Manager import *
from common_json_schema import *

log = logging.getLogger('Request Handler')

dbAccessor = DatabaseAccessor()
face_recognizer = FaceRecognizer(dbAccessor)

account_mng = AccountManager(dbAccessor, face_recognizer)
history_mng = HistoryManager(dbAccessor)
session_mng = SessionManager(dbAccessor)
check_in_mng = CheckInManager(dbAccessor, face_recognizer)

request_json_schema = {
    '$schema': 'http://json-schema.org/draft-07/schema#',
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
request_json_validator = jsonschema.Draft7Validator(request_json_schema)

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

        if not account_mng.verifyAuthentication(auth):
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


def register_handler(name, param_schema, need_auth, handler):
    global OP_HANDLER

    param_schema['$schema'] = 'http://json-schema.org/draft-07/schema#'
    param_validator = jsonschema.Draft7Validator(param_schema)

    def verify_and_handle(op_name, auth, param):
        if not param_validator.is_valid(param):
            return failed_response('invalid parameters')

        if need_auth and auth == None:
            return failed_response('authentication is required')

        return handler(op_name, auth, param)

    OP_HANDLER[name] = verify_and_handle
    NEED_AUTH[name] = need_auth


def login_handler(op_name, auth, param):
    PID = param['username']
    password = param['password']

    auth_obj = account_mng.login(PID, password)

    if auth_obj == None:
        return failed_response('invalid username or password')
    else:
        return success_response({"auth": auth_obj})


register_handler(
    name='login',
    param_schema={
        'type': 'object',
        'properties': {
            'username': {'type': 'string'},
            'password': {'type': 'string'},
        },
        'required': ['username', 'password'],
    },
    need_auth=False,
    handler=login_handler)


def get_valid_history_param_handler(op_name, auth, param):
    pid = account_mng.getPidFromAuth(auth)
    users = history_mng.preloadPID(pid)
    sessions = history_mng.preloadSID(pid)
    return success_response({
        'users': users,
        'sessions': sessions,
    })


register_handler(
    name='get_valid_history_param',
    param_schema=EMPTY_JSON_OBJECT,
    need_auth=True,
    handler=get_valid_history_param_handler
)


def get_history_handler(name, auth, param):
    pid = param.get('pid', None)
    sid = param.get('sid', None)
    beg_time = parse_datetime(param.get('beg_time', None))
    end_time = parse_datetime(param.get('end_time', None))
    max_num = param.get('max_num', None)

    if max_num == None:
        max_num = 30

    max_num = min(max_num, 1000)

    res = history_mng.getHistory(pid, sid, beg_time, end_time, max_num)
    return success_response({
        'histories': res
    })


register_handler(
    name='get_history',
    param_schema={
        'type': 'object',
        'properties': {
            'pid': {'anyOf': [
                {'type': 'null'},
                {'type': 'string'},
            ]},
            'sid': {'anyOf': [
                {'type': 'null'},
                {'type': 'integer'},
            ]},
            'beg_time': {'anyOf': [
                {'type': 'null'},
                {'type': 'date-time'},
            ]},
            'end_time': {'anyOf': [
                {'type': 'null'},
                {'type': 'date-time'},
            ]},
            'max_num': {'anyOf': [
                {'type': 'null'},
                {'type': 'integer', "minimum": 0, }
            ]},
        },
    },
    need_auth=True,
    handler=get_history_handler)


def get_attendees_handler(name, auth, param):
    pid = account_mng.getPidFromAuth(auth)
    res = account_mng.getAllUser(pid)
    return success_response({
        'users': res
    })


register_handler(
    name='get_attendees',
    param_schema=EMPTY_JSON_OBJECT,
    need_auth=True,
    handler=get_attendees_handler
)

TIME_UNIT_MAP = {
    'day': timedelta(days=1),
    'week': timedelta(weeks=1),
}


def add_session_handler(name, auth, param):
    s_name = param['session_name']
    venue = param['venue']
    beg_time = parse_datetime(param['beg_time'])
    end_time = parse_datetime(param['end_time'])
    repeat = param['repeat']
    period = param['period']
    period_unit = param['period_unit']
    attendees = param['attendees']
    creator = account_mng.getPidFromAuth(auth)

    period_delta = period * TIME_UNIT_MAP[period_delta]
    session_mng.addSession(repeat, period_delta, attendees,
                           s_name, creator, venue, beg_time, end_time)

    return success_response()


register_handler(
    name='add_session',
    param_schema={
        "type": "object",
        "properties": {
            "session_name": {'type': 'string'},
            "venue": {'type': 'string'},
            "beg_time": {'type': 'date-time'},
            "end_time": {'type': 'date-time'},
            "repeat": {'type': 'integer'},
            "period": {'type': 'integer'},
            "period_unit": {'enum': ['day', 'week']},
            "attendees": {
                'type': 'array',
                'items': {'type': 'string'}
            },

        },
        "required": ["session_name", "venue", "beg_time", "end_time", "repeat", "period", "period_unit", "attendees"]
    },
    need_auth=True,
    handler=add_session_handler)


def register_user_handler(name, auth, param):
    pid = param['pid']
    name = param['name']
    pwd = param['password']
    front_face = parse_image_string(param['front_face'])

    try:
        account_mng.registerAccount(pid, name, pwd, [front_face])
    except ManagerError as e:
        return failed_response(str(e))

    return success_response()

register_handler(
    name='register_user',
    param_schema={
        'type': 'object',
        'properties': {
            'pid': {'type': 'string'},
            'name': {'type': 'string'},
            'password': {'type': 'string'},
            'front_face': IMAGE_STRING
        },
        'required': ['pid', 'name', 'password', 'front_face']
    },
    need_auth=False,
    handler=register_user_handler)


def recognized_face_handler(name, auth, param):
    face = parse_image_string(param['face'])
    want_session = bool(param.get('want_session', False))

    pid = check_in_mng.recognition(face)
    if pid == None:
        return failed_response('unknown face')

    ret_auth = account_mng.genAuthentication(pid)
    user_info = account_mng.getUserInfo(pid)
    sessions = None
    if want_session:
        sessions = session_mng.getCurrentSessions(pid)

    return success_response({
        'user': user_info,
        'auth': ret_auth,
        'sessions': sessions,
    })


register_handler(
    name='recognize_face',
    param_schema={
        'type': 'object',
        'properties': {
            'face': IMAGE_STRING,
            'want_session': {
                'anyOf': [
                    {'type': 'boolean'},
                    {'type': 'null'}
                ]
            }
        },
        'required': ['face']
    },
    need_auth=False,
    handler=recognized_face_handler)


def get_last_history_handler(name, auth, param):
    sid = param['sid']
    pid = account_mng.getPidFromAuth(auth)
    last = history_mng.getLastHistory(pid, sid)
    return success_response({
        'last_history': last
    })


register_handler(
    name='get_last_history',
    param_schema={
        'type': 'object',
        'properties': {
            'sid': {'type': 'integer'},
        },
        'required': ['sid']
    },
    need_auth=True,
    handler=get_last_history_handler)


def check_in_out_handler(name, auth, param):
    sid = param['sid']
    pid = account_mng.getPidFromAuth(auth)
    res = check_in_mng.checkIn(pid, sid, checkIn)
    if res == None:
        return failed_response()
    else:
        return success_response({
            'new_history': res
        })


register_handler(
    name='check_in_out',
    param_schema={
        'type': 'object',
        'properties': {
            'sid': {'type': 'integer'},
        },
        'required': ['sid']
    },
    need_auth=True,
    handler=check_in_out_handler)


def handle_home(request: HttpRequest):
    return HttpResponse(b'It works')
