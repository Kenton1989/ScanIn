from django.http import HttpResponse, HttpRequest
import json

def handle_cz3002(request: HttpRequest):
    body = request.body
    if len(body) < 2: body = '{"test":"3002"}'
    req = json.loads(body)
    
    return HttpResponse(bytes('It works (CZ3002) \n' + json.dumps(req), encoding='utf8'))


def handle_home(request: HttpRequest):
    return HttpResponse(b'It works')
