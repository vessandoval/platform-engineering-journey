FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /build

COPY app/pom.xml .
COPY app/src ./src

RUN mvn -q clean package

FROM tomcat:10.1-jdk17-temurin

WORKDIR /usr/local/tomcat

#Esta línea podría requerirse en caso de que Tomcat no arranque mediante catalina.sh/startup.sh 
#RUN echo "org.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource" >> /usr/local/tomcat/conf/catalina.properties   

COPY --from=builder /build/target/laboratory-04.war \
                    /usr/local/tomcat/webapps/laboratory-04.war


ADD https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.10/postgresql-42.7.10.jar \
    /usr/local/tomcat/lib/postgresql.jar

#Esta línea parece que no será necesaria por que no figura en la última actualización sugerida por chatGPT
#Esta línea es la que se encuentra en el archivo Dockerfile original, pero no es necesaria según la última actualización sugerida por chatGPT. Por lo tanto, se puede eliminar para evitar confusiones y mantener el archivo limpio y actualizado.
#ADD https://jdbc.postgresql.org/download/postgresql-42.7.7.jar /usr/local/tomcat/lib/postgresql.jar

#COPY /app/src/main/webapp/META-INF/context.xml /usr/local/tomcat/conf/Catalina/localhost/laboratory-04.xml
