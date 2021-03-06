
FROM stakater/oracle-jdk:8u152-alpine-3.7
ARG artifactId
ARG version
ENV jarname=$artifactId-$version
WORKDIR /s

ADD target/classes /usr/local/searchVolume/


# setting log directory
ENV LOG_HOME /var/log

### By default start the API server ###
###USER appuser
ADD target/${jarname}.jar /opt/searchVolume.jar

ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-Dfile.encoding=UTF-8", "-Duser.timezone=Europe/Berlin", "-jar", "/opt/searchVolume.jar"]
EXPOSE 8080
