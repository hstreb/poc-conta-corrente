FROM openjdk:17.0.2

COPY build/libs/transacoes-0.0.1.jar app.jar

CMD ["java", "-XX:MaxRAMPercentage=80.0", "-Dfile.encoding=UTF8", "-Duser.country=BR", "-Duser.language=pt", "-Duser.timezone=America/Sao_Paulo", "-jar", "app.jar"]
