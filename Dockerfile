FROM rubix-docker-dev.artifacts.tabdigital.com.au/rubix-atlas-docker-base:1.0-1504150830
WORKDIR /app
COPY target/my-utility-1.0-SNAPSHOT.jar my-utility-1.0-SNAPSHOT.jar
EXPOSE 8182
ENTRYPOINT java -jar my-utility-1.0-SNAPSHOT.jar