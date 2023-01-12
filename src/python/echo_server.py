import sys, getopt
from http.server import BaseHTTPRequestHandler, HTTPServer

class EchoHttpServer:

    def __init__(self, port, message):

        def get_handler(*args):
            EchoHttpHandler(message, *args)

        try:
            server = HTTPServer(('localhost', port), get_handler)
            print('Starting HTTP Server, use <Ctrl-C> to stop')
            server.serve_forever()
        except KeyboardInterrupt:
            server.socket.close()        


class EchoHttpHandler(BaseHTTPRequestHandler):
    
    def __init__(self, message, *args):
        self.message = message
        BaseHTTPRequestHandler.__init__(self, *args)
        

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/plain; charset=utf-8')
        self.end_headers()
        self.wfile.write(bytes(message, "utf-8"))
        return


if __name__ == '__main__':

    port = ''
    message = ''
    opts, args = getopt.getopt(sys.argv[1:],"hp:m:",["port=","message="])
        
    for opt, arg in opts:
        if opt == '-h':
            print('echo-server.py --port <integer> --message <text>')
            sys.exit()
        elif opt in ("-p", "--port"):
            port = arg
        elif opt in ("-m", "--message"):
            message = arg
        
    server = EchoHttpServer(int(port), message)


