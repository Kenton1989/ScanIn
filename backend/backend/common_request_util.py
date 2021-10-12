from django.http import HttpResponse, HttpRequest, JsonResponse
from datetime import datetime
import base64
from PIL import Image
import io

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
        return datetime.fromisoformat(val)
    if isinstance(val, datetime):
        return val

def parse_image_string(val: str) -> Image:
    data_bytes = base64.decodestring(val)
    data_stream = io.BytesIO(data_bytes)
    image = Image.open(data_stream, formats=['JPEG'])
    return image