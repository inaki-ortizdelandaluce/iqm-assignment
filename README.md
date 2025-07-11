# IQM Assignment

## Introduction
This Git repository is my response to the assignment provided by IQM on 10th January 2023. This assignment is part of an undergoing recruitment process for an open vacancy as Quantum Computer Infrastructure Engineer within the company. As requested, I commit to keep this repository and its contents strictly private among IQM and myself.

## Goal
The goal of the assignment is to implement an HTTP Server which simply echoes a message upon every input request, dockerise the resulting software and implement a reverse proxy in Docker Compose to route the incoming requests depending on the resource path.

Hereafter, I will list the different technical solutions proposed and will also detail the steps to be followed to reproduce the expected behaviour of the infrastructure. I have implemented solutions in two different programming languages, Java and Python, to take the chance to show different technical skills I may have, and to allow for further disgression on different topics like scalability, robustness and automation. The Java approach is slightly more complex, also due to the own complexity and verbosity of the language, while the Python one goes straight to the point.

## Step 1. Build an HTTP Echo Server

### 1.1 Java
The prerequisites to build, deploy, run and test the HTTP Echo Server are:
* Unix based operating system
* Java 1.8+
* Apache Maven 3.8+

To keep the solution as simple as possible I have decided to use just Maven and standard unix shell-commands to perform the complete software life-cycle. No spetial attention has been paid to unit and component testing for the same sake of simplicity, although placeholders allowing to inject and run the tests have been left in the directory structure.

The source code consists on two Java classes, one providing the command-line interface (_EchoHttpServerCmd.java_) and another with the HTTP Server implementation (_EchoHttpServer.java_). Given the user input is typically an enemy of software robusteness I have included a dedicated class to handle the input arguments using Apache Commons CLI library  although this is not strictly necessary. 

```
src/main/java/
└── iqm
    └── assessment
        ├── EchoHttpServer.java
        └── EchoHttpServerCmd.java
```

```
[...]

public class EchoHttpServer {
    
    [...]	
    
    public void start() {

	[...]
	
	HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 0);
	server.createContext("/", new EchoHttpHandler(this.message));
	server.setExecutor(null); 
	server.start();
	
	[...]
			
    }
    
    static class EchoHttpHandler implements HttpHandler {
    	
    	private String message;
    	
    	public EchoHttpHandler(String message) {
    		this.message = message;
    	}
    	
        @Override
        public void handle(HttpExchange e) throws IOException {
        	
            // set headers
            Headers headers = e.getResponseHeaders();
            headers.clear();
            headers.add("Content-Type", "text/plain; charset=utf-8");
        	
            e.sendResponseHeaders(200, message.length());
            
            // write response  
            OutputStream os = e.getResponseBody();
            os.write(message.getBytes());
            os.close();
            
        }
    }
}

```


Software dependencies are declared in the Maven's POM file located in the root directory (_pom.xml_) and the actual deployment is delegated using a dedicated maven plugin set (appassembler and assembly) and the corresponding configuration file (_assembly.xml_). With a few lines of configuration, the software can be built and packaged in a directory structure ready to be executed in any platform without major hassle. 

Once you have cloned the git repository under a root folder (_/path/to/repo_), and you open the terminal, there is on single step needed to compile, build, package and assemble the HTTP Echo Server:
```
$ cd /path/to/repo/root/folder
$ mvn clean package
```

This step generates a tar.gz file which can be then decompressed as follows:
```
$ cd target/
$ tar xfz echo-server-1.0.tar.gz
$ cd echo-server-1.0/bin/
```

The usage of the command-line application can be seen by using any of the help arguments (_--help_ or _-h_):
```
$ ./echo-server.sh --help
usage: echo-server.sh -m <text> -p <integer>
 -m,--message <text>   Message enclosed in double quotes, e.g. "Hello World"
 -p,--port <integer>   HTTP Server Port, e.g. 8080
```

Finally to start-up the server with a given port and message, e.g. port 9095 and message "Hello World":
```
$ ./echo-server.sh -p 9095 -m "Hello World" &
```

Once the server is started, a few lines are shown on the terminal console indicating the server has started up sucessfully. No specific logging mechanism other the printing out to the console has been envisaged towards a achieving a quick and simple solution. In case the port is already in use an exception is thrown and no specific error handling is provided for the same reasons.
```
Starting HTTP Server
  Port:9095
  Message:Hello World
HTTP Server started
```

Finally the telnet utility can be used to check the server behaves as expected:
```
$ telnet localhost 9095
Trying ::1...
Connected to localhost.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhost

Handling request
HTTP/1.1 200 OK
Date: Thu, 12 Jan 2023 15:33:58 GMT
Content-type: text/plain; charset=utf-8
Content-length: 11

Hello World
```


### 1.2 Python
The benefits of relying on existing building and automation tools have already been shown with the Java implementation. However, for scalability reasons related to the Docker image sizes that we will use later, it would be much better to have a Python implementation. Hence a bare borne alternative solution has been written in Python. For the sake of simplicity there will be no Python packaging in this case, neither a virtual environment set-up, just a simple script which gets the things done.

The prerequisites to build, deploy, run and test the HTTP Echo Server in Python are:
* Unix based operating system
* Python 3

The source code simply consists of a single python script (_echo_server.py_) which handles the input arguments and starts the HTTP Server with a dedicated request handler which simply echoes the input message, same as in the Java implementation.

```
import sys, getopt
from http.server import BaseHTTPRequestHandler, HTTPServer

class EchoHttpServer:

    def __init__(self, port, message):

        def get_handler(*args):
            EchoHttpHandler(message, *args)

        try:
            server = HTTPServer(('0.0.0.0', port), get_handler)
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
	self.wfile.write(bytes("\n", "utf-8"))
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
```

To run the server the following commands can be executed:
```
$ cd /path/to/repo/root/folder
$ python3 src/python/echo_server.py --port 8080 --message "Hello Python" &
```
Same as for the Java case, once the server is running, the telnet utility can be used to check the server behaves as expected:
```
$ telnet localhost 8080
Trying ::1...
telnet: connect to address ::1: Connection refused
Trying 127.0.0.1...
Connected to localhost.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhost

127.0.0.1 - - [13/Jan/2023 00:16:42] "GET / HTTP/1.1" 200 -
HTTP/1.0 200 OK
Server: BaseHTTP/0.6 Python/3.9.13
Date: Thu, 12 Jan 2023 23:16:42 GMT
Content-type: text/plain; charset=utf-8

Hello Python
```

## Step 2. Dockerise the HTTP Echo Server

### 2.1 Java
To dockerise the Java HTTP Echo Server I have chosen the official Amazon's Correto image, which unfortunately is too large compared with, for example, other Alpine-based images. This is one of the reasons why I have decided to provide an alternative implementation in Python, because even if it's not the scope of the exercise, I wanted to show that scalability and optimisation should always be a driving factor as far as software and system infrastrucure are concerned.

Thanks to the automation already achieved when building and packaging the Java application, building a container for the Java solution is very trivial: we just need to copy over the application assemble, define the configurable CLI arguments port and message as environment variables and expose the port to the host as the Dockerfile directives in file _Dockerfile.java_ show:
```
FROM amazoncorretto:latest
COPY target/appassembler/echo-server /root/echo-server
EXPOSE $PORT 
CMD /root/echo-server/bin/echo-server.sh -p "$PORT" -m "$MESSAGE"
```

To build the image the following commands should be executed from the terminal:
```
$ cd /path/to/repo/root/folder
$ docker build -t echo-server-java -f Dockerfile.java .
```

Once the docker image is built, the HTTP Echo Server container can be run as follows:
```
$ docker run -d --rm -e PORT=9000 -e MESSAGE="Hello World" --name echo-java echo-server-java
```
To get the IP address in which the service is running we should inspect the container with the following command:
```
$ docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' echo-java
172.17.0.2
```

Such that we can finally use the telnet utility to check whether the container is exposing the HHTP Echo Server as expected:
```
$ telnet 172.17.0.2 9000
Trying 172.17.0.2...
Connected to 172.17.0.2.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhost

HTTP/1.1 200 OK
Date: Thu, 12 Jan 2023 11:39:37 GMT
Content-type: text/plain; charset=utf-8
Content-length: 11

Hello World
```

Finally we should release the resources accordingly:
```
docker stop echo-java
```

### 2.2 Python
Alternatively to the Java-based container, it is also provided a dedicated Dockerfile (_Dockerfile.python_) to build an image with the Python HTTP Echo Server implementation described above.

```
FROM python:3.8.2-alpine
COPY src/python/echo_server.py .
ENV PYTHONUNBUFFERED=1
EXPOSE $PORT 
CMD python3 echo-server.py -p "$PORT" -m "$MESSAGE"
```
Similar steps as for the previos section can be executed to run this image:
```
$ cd /path/to/repo/root/folder
$ docker build -t echo-server-python -f Dockerfile.python .
$ docker run --rm -e PORT=9001 -e MESSAGE="Hello Python World" --name echo-python echo-server-python
$ docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' echo-python
172.17.0.2
```
It can be seen that the total size of the Python-based image is about a third of the Java-based image size. When dockerising an application it is of key importance to streamline the total size of the image to be able to fullfil further scalability requirements which may arise. Therefore this has proven that a Python-based implementation of the HTTP Echo Server has worth the effort.

Finally it can be checked that, using the Pyhon-based container, we get the same output we had for Java except for some minor differences on the HTTP headers:
```
$ telnet 172.17.0.2 9001
Trying 172.17.0.2...
Connected to 172.17.0.2.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhost

HTTP/1.0 200 OK
Server: BaseHTTP/0.6 Python/3.8.2
Date: Thu, 12 Jan 2023 11:39:37 GMT
Content-type: text/plain; charset=utf-8

Hello Python World
Connection closed by foreign host.
```
Finally we should release the resources accordingly:
```
docker stop echo-python
```

## Step 3. Docker Compose to route requests to different servers
Given that Dockerised images of the HTTP Echo Server (either in Python or Java) are already available, we can now build a reverse proxy to delegate the incoming requests to different servers depending on the resource path as requested in the assignment. For the reverse proxy I have chosen an NGINX image in Alpine given the size is very small and the NGINX configuration is pretty straight forward.

The code is available under the _docker_ folder and consists of a Docker Compose file (_docker-compose.yml_), a Dockerfile (_nignx/Dockerfile_) and an NGINX configuration file (_nginx/nginx.conf_), the latest o build the NGINX proxy image:
```
docker/
├── docker-compose.yml
└── nginx
    ├── Dockerfile
    └── nginx.conf
```

The Docker Compose file implements three services: the reverse proxy listening on port 9090, and two echo servers each one listening in a different port (i.e. 9091 and 9092) and providing a different message. As it can be seen in the compose file, the proxy is built as defined in the Dockerfile underneath the _nginx_ directory, which is where the magic of the reverse proxy redirection happens. 

```
services:
    proxy:
        container_name: "proxy"
        image: echo-proxy
        build: nginx
        ports:
            - 9090:9090
        restart: always
 
    echo1:
        container_name: "echo1"
        depends_on:
            - proxy
        image: echo-server-java:latest
        environment:
            PORT: 9091
            MESSAGE: "Hello from a"
        restart: always
 
    echo2:
        container_name: "echo2"
        depends_on:
            - proxy
        image: echo-server-java:latest
        environment:
            PORT: 9092
            MESSAGE: "Hello from b"
        restart: always
```

In the NGINX configuration file (_docker/nginx/nginx.conf_), I have declared two upstream servers corresponding to the echo servers running on port 9091 and 9092, and the proxy server listening on 9090 which is configured such that requests are redirected to either one or the other echo servers depending on the resource path (/a or /b):
```
worker_processes 1;
  
events { worker_connections 1024; }

http {

    sendfile on;

    upstream docker-echo1 {
        server echo1:9091;
    }

    upstream docker-echo2 {
        server echo2:9092;
    }
    
    proxy_set_header   Host $host;
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header   X-Forwarded-Host $server_name;
    
    server {
        listen 9090;
 
        location ^~ /a {
            proxy_pass         http://docker-echo1/;
            proxy_redirect     off;
        }

        location ^~ /b {
            proxy_pass         http://docker-echo2/;
            proxy_redirect     off;
        }

    }
}
```
To run the Docker Compose and check everything is working as expected, the following steps are needed:

* First we need to make sure that the ports declared in the compose file and the nginx configuration file are available. If this is not the case another set must be selected.
* Then we startup the compose in detached mode:
```
$ cd /path/to/repo/root/folder
$ cd docker
$ docker-compose up -d
```
* We can check whether the compose has been started up successfully and the echo servers are listening with the specified port and message using the following commands:
```
$ docker ps
$ docker-compose logs
```

* Finally, the telnet utility can be used to check the reverse proxy behaves as expected:
```
$ telnet localhost 9090
Trying 172.0.0.1...
Connected to 172.0.0.1.
Escape character is '^]'.
GET /a HTTP/1.1
Host: localhost


HTTP/1.1 200 OK
Server: nginx/1.23.3
Date: Thu, 12 Jan 2023 12:35:23 GMT
Content-Type: text/plain; charset=utf-8
Content-Length: 12
Connection: keep-alive

Hello from a

GET /b HTTP/1.1
Host: localhost


HTTP/1.1 200 OK
Server: nginx/1.23.3
Date: Thu, 12 Jan 2023 12:36:47 GMT
Content-Type: text/plain; charset=utf-8
Content-Length: 12
Connection: keep-alive

Hello from b
```
* And last, but not least, the resources are released shutting down the docker compose
```
$ docker-compose down
```
## Conclusion
I hope the steps shown above provide an overall idea on the implementation decisions taken. Also that the step-by-step guide to reproduce the results using the code provided has the level of detail you were expecting. It has been a very fun excercise, thanks for the opportunity! Please do not hesitate to contact me for any further clarification this guide or the accompanying code may require.

Iñaki Ortiz de Landaluce
