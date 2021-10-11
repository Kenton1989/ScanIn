from django.http import HttpResponse, HttpRequest, JsonResponse


def success_response(returnVal):
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
