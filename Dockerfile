FROM java:8
WORKDIR /opt/app
COPY target/ddns.jar /opt/app/ddns.jar
CMD java -jar ddns.jar