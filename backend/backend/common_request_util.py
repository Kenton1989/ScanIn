from django.http import HttpResponse, HttpRequest, JsonResponse
from datetime import datetime, timezone
import base64
from PIL import Image
import io

import logging

log = logging.getLogger('my_app')

def success_response(returnVal={}):
    assert isinstance(returnVal, dict)
    res = {
        'success': True,
        'details': "",
        'return': returnVal,
    }
    response = JsonResponse(res)
    return response


def failed_response(errMsg='invalid request'):
    assert isinstance(errMsg, str)
    log.error('request failed: %s', errMsg)
    res = {
        'success': False,
        'details': errMsg,
        'return': {},
    }
    response = JsonResponse(res)
    return response


def parse_datetime(val):
    if val == None:
        return None
    if isinstance(val, str):
        return datetime.fromisoformat(val).astimezone(timezone.utc)
    if isinstance(val, datetime):
        return val.astimezone(timezone.utc)


def parse_image_string(val: str) -> Image.Image:
    data_bytes = base64.decodestring(val.encode('ascii'))
    data_stream = io.BytesIO(data_bytes)
    image = Image.open(data_stream, formats=['JPEG'])
    return image
