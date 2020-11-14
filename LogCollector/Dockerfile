FROM soft.luxx.cn/library/openjdk-18-el7:latest
WORKDIR /service
COPY target/log-collector-1.0.jar /service/log-collector.jar
COPY entrypoint.sh /service/entrypoint.sh
RUN chmod +x entrypoint.sh
ENV LANG zh_CN.UTF-8
ENTRYPOINT [ "./entrypoint.sh" ]
#CMD ["java", "-jar", "log-collector.jar","--spring.profiles.active=${ENV}"]