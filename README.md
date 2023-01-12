# iqm-assessment

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