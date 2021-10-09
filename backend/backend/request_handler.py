from django.http import HttpResponse, HttpRequest, JsonResponse
import json

def handle_cz3002(request: HttpRequest):
    body = request.body
    if len(body) < 2: body = '{"test":"3002"}'
    req = json.loads(body)
    req["msg"] = "It works!"
    return JsonResponse(req)


def handle_home(request: HttpRequest):
    return HttpResponse(b'It works')
