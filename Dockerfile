#see https://github.com/docker-library/tomcat/blob/master/9.0/jre10-slim/Dockerfile

FROM tomcat:9.0.11-jre10

VOLUME ["/ebx-repository-h2"]

# docker volume ls
# docker volume create ebx-repository-h2
# docker volume inspect ebx-repository-h2-oauth2google
# docker volume rm ebx-repository-h2

ENV CATALINA_HOME /usr/local/tomcat
WORKDIR $CATALINA_HOME

RUN keytool -genkey -noprompt \
 -alias tomcat \
 -keyalg RSA \
 -dname "CN=helloworld, OU=ID, O=ON, L=OAuthSample, S=WithTomcat, C=US" \
 -keystore $CATALINA_HOME/.keystore \
 -storepass "ebx tomcat password" \
 -keypass "ebx tomcat password"

COPY context/ebx.xml ${CATALINA_HOME}/conf/Catalina/localhost/ebx.xml
COPY context.xml conf/context.xml

COPY server.xml $CATALINA_HOME/conf/server.xml
COPY context/ebx.xml ${CATALINA_HOME}/conf/Catalina/localhost/ebx.xml

ENV EBX_HOME /ebx-repository-h2
RUN mkdir -p ${EBX_HOME}

COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/lib/ebx.jar $CATALINA_HOME/lib/
COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/lib/lib-h2/h2-1.3.170.jar $CATALINA_HOME/lib/

# COPY ebx/third_party_libs/activation-1.1.1.jar $CATALINA_HOME/lib/
COPY dev-local/third_party_libs/javax.mail-api-1.4.7.jar $CATALINA_HOME/lib/
COPY dev-local/third_party_libs/google-http-client-1.24.1.jar $CATALINA_HOME/lib/
COPY dev-local/third_party_libs/google-http-client-jackson2-1.24.1.jar $CATALINA_HOME/lib/
COPY dev-local/third_party_libs/slf4j-api-1.7.25.jar $CATALINA_HOME/lib/
COPY dev-local/third_party_libs/slf4j-simple-1.7.25.jar $CATALINA_HOME/lib/

COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/webapps/wars-packaging/ebx.war $CATALINA_HOME/webapps/
COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/webapps/wars-packaging/ebx-root-1.0.war $CATALINA_HOME/webapps/
COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/webapps/wars-packaging/ebx-manager.war $CATALINA_HOME/webapps/
COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/webapps/wars-packaging/ebx-dma.war $CATALINA_HOME/webapps/
COPY --from=316054198708.dkr.ecr.us-east-1.amazonaws.com/ebx:5.8.1.1067-0027 /data/ebx/ebx.software/webapps/wars-packaging/ebx-dataservices.war $CATALINA_HOME/webapps/

### PROJECT
COPY ebx.properties ${EBX_HOME}/ebx.properties
COPY bin/*.jar $CATALINA_HOME/lib/

ENV JAVAJDK10FIX --add-modules java.xml.ws
ENV EBX_OPTS="-Debx.home=${EBX_HOME} -Debx.properties=${EBX_HOME}/ebx.properties"
ENV JAVA_OPTS="${EBX_OPTS} ${JAVA_OPTS} -Dorg.ops4j.pax.logging.DefaultServiceLog.level=WARN -Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Slf4jLogger ${JAVAJDK10FIX}"
ENV CATALINA_OPTS ""

EXPOSE 8080
CMD ["catalina.sh", "run"]
