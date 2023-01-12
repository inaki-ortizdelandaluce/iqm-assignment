# IQM Assignment

## Introduction
This Git repository is my response to the assignment provided by IQM on 10th January 2023. This assignment is part of an undergoing recruitment process for an open vacancy as Quantum Computer Infrastructure Engineer within the company. As requested, I commit to keep this repository and its contents strictly privately among IQM and myself.

## Goal
The goal of the assignment is to implement an HTTP Server which simply echoes the input requests, dockerise the resulting software and implement a reverse proxy in Docker Compose to route the incoming request depending on the resource path.

Hereafter, I will list the different technical solutions proposed and will also detail the steps to be followed to reproduce the expected behaviour of the infrastructure. I have implemented solutions in two different programming languages, Java and Python, to take the chance to show different technical skills I may have, and to allow for further disgression on different topics like scalability, robustness and automation. The Java approach is slightly more complex, also due to the own complexity of the language, while the Python one goes straight to the point before containerisation takes place.

## Step 1. Build an HTTP Echo Server

### 1.1 Java
The prerequisites to build, deploy, run and test the HTTP Echo Server are:
* Unix based operating system
* Java 1.8+
* Apache Maven 3.8+

To keep the solution as simple as possible I have decided to use just Maven and standard unix shell-commands to perform the complete software life-cycle. No spetial attention has been paid to unit and component testing for the same sake of simplicity, although placeholders allowing to inject and run the tests have been left in the directory structure.

The source code consists on two Java classes, one providing the command-line interface (_EchoHttpServerCmd.java_) and another with the HTTP Server implementation (_EchoHttpServer.java_). Given the user input is typically and enemy of software robusteness I have included a dedicated class to handle the input arguments simply and nicely using Apache Commons CLI library  although this is not strictly necessary. 

```
src/main/java/
└── iqm
    └── assessment
        ├── EchoHttpServer.java
        └── EchoHttpServerCmd.java

2 directories, 2 files
```

Software dependencies are declared in the Maven's POM file located in the root directory (_pom.xml_) and the actual deployment is delegated using a dedicated maven plugin set (appassembler and assembly) and the corresponding configuration file (_assembly.xml_). With a few lines of configuration, the software can be built and packaged in a directory structure ready to be executed in any platform without major hassle. 

Once you have cloned the git repository under a root folder (_/path/to/repo_), and you open the terminal, there is on single step needed to compile, build, package and assemble the HTTP Echo Server as follows:
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

Once the server is started, a few lines are shown on the terminal console indicating the server has started up sucessfully. No specific logging mechanism other the printing out to the console has been envisaged towards a achieving a quick and simple solution. In case the port is already in use and exception is thrown and no specific exception handling is provided for the same reasons.
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
_TBW_


## Java Implementation


Requirements:
Java
Maven
Docker
tar
telnet
Ports available

LINUX
=====
1) 
$ mvn clean package
$ cd target/
$ tar xfz echo-server.tar.gz
$ cd echo-server-1.0/bin/
$ ./echo-server.sh -p 9095 -m "Hello World" &
$ telnet localhost 9095
Trying ::1...
Connected to localhost.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhost

Handling request
HTTP/1.1 200 OK
Date: Thu, 12 Jan 2023 12:44:09 GMT
Content-type: text/plain; charset=utf-8
Content-length: 11

Hello World
$ ps auxwww | grep echo-server
$ kill -9 <pid>

2) 
$ mvn clean package
$ scp -r Dockerfile docker target/appassembler ssh.esac.esa.int:.
$ scp -r Dockerfile appassembler docker bcops@bpcspsdockdev02.evsp.lan:/home/bcops/deleteme/
$ ssh bcops@bpcspsdockdev02.evsp.lan
$ mv appassembler target/

$ docker build -t echo-server .
$ docker run -d --rm -e PORT=9000 -e MESSAGE="Hello World" --name echo-server-test echo-server
$ docker logs echo-server-test
$ docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' echo-server-test
172.17.0.2
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
$ docker stop echo-server-test

3)
cd docker
docker-compose up -d (docker-compose up --build)
docker ps
docker-compose logs
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

$ telnet localhost 9090
Trying 172.0.0.1...
Connected to 172.0.0.1.
Escape character is '^]'.
GET /b HTTP/1.1
Host: localhost

HTTP/1.1 200 OK
Server: nginx/1.23.3
Date: Thu, 12 Jan 2023 12:36:47 GMT
Content-Type: text/plain; charset=utf-8
Content-Length: 12
Connection: keep-alive

Hello from b

$ docker-compose stop
$ docker-compose down
