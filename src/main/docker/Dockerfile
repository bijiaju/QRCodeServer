FROM hub.c.163.com/r00txx/java8
ENV LC_ALL en_US.UTF-8
MAINTAINER bee byq_work@163.com
VOLUME /home/bee
ADD docker_base-0.0.1-SNAPSHOT.jar app.jar
RUN touch /app.jar
EXPOSE 8080
#日志中文
ENTRYPOINT ["java", "-Dfile.encoding=UTF8","-Dsun.jnu.encoding=UTF8", "-jar", "app.jar"]
