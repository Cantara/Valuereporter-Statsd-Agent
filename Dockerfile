FROM openjdk:11-jre-slim
MAINTAINER Bard Lind <bard.lind@gmail.com>

ENV AGENT_JAR_NAME=valuereporter-statsd-agent.jar
# Install Application
RUN adduser valuereporter
ADD target/valuereporter-statsd-agent-jar-with-dependencies.jar /home/valuereporter/$AGENT_JAR_NAME
#ADD docker/valuereporter_override.properties /home/valuereporter/valuereporter_override.properties
#RUN chown valuereporter:valuereporter /home/valuereporter/valuereporter_override.properties
RUN chmod 755 /home/valuereporter/$AGENT_JAR_NAME
ENV AGENT_JAR_PATH=/home/valuereporter/$AGENT_JAR_NAME


# Send StatsD value statistics
#$AGENT_JAR_PATH is inherited from the baseimage at cantara/valuereporter-statsd-agent
#$PREFIX is used by Grafana to separate your data from the other services.
#$STATSD_HOST hostname to StatsD service -assuming standard port for now
#$BASE_PACKAGE the java package structure you want to monitor eg. no.cantara.demo

#ENV PREFIX=NEED-TOBE-SET
#ENV STATSD_HOST=NEED-TOBE-SET
#ENV BASE_PACKAGE=NEED-TOBE-SET
#ENV JAVA_AGENT=-javaagent:$AGENT_JAR_PATH=base_package:$BASE_PACKAGE,statsd_host:$STATSD_HOST,prefix:$PREFIX
#CMD java $JAVA_AGENT -jar <your-service.jar>


