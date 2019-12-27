FROM openjdk:8

COPY target/fb-login-demo-0.0.1-SNAPSHOT.jar  .
COPY keystore.p12 .

EXPOSE 8080

ENV FACEBOOK_APP_ID=""
ENV FACEBOOK_APP_SECRET=""

ENTRYPOINT ["java", "-jar", "fb-login-demo-0.0.1-SNAPSHOT.jar"]
