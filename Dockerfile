FROM amazoncorretto:latest
COPY target/appassembler/echo-server /root/echo-server
EXPOSE $PORT 
CMD /root/echo-server/bin/echo-server.sh -p "$PORT" -m "$MESSAGE"