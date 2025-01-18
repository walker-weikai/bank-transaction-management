FROM openjdk:21

#WORKDIR /Users/walker/workspace/myproject/bank-transaction-management/target/

COPY ./bank-transaction-management-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

