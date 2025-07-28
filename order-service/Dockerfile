FROM eclipse-temurin:21-jre
WORKDIR /work/

COPY target/quarkus-app/lib/ /work/lib/
COPY target/quarkus-app/quarkus-run.jar /work/
COPY target/quarkus-app/app/ /work/app/
COPY target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 9091
CMD ["java", "-jar", "quarkus-run.jar"]