FROM openjdk

COPY staticanalyzer.jar /usr/local/staticanalyzer/staticanalyzer.jar

WORKDIR /usr/local/staticanalyzer/
CMD ["java", "-jar", "/usr/local/staticanalyzer/staticanalyzer.jar", "--spring.profiles.active=prod"]

EXPOSE 8080
