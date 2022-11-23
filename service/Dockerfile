FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR extracted
ADD ./build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

# docker build -t olmago-contract:1.0.0 .
# docker run -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=prod" -e "DB_SERVER=127.0.0.1" -e "DB_PORT=3306" -e "KAFKA_BROKER_SERVER=127.0.0.1" -e "KAFKA_BROKER_IP=9093" olmago-contract:1.0.0