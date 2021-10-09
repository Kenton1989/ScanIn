from django.http import HttpResponse

def handle_cz3002(request):
    return HttpResponse(b'It works (CZ3002)')

    
def handle_home(request):
    return HttpResponse(b'It works (Home)')