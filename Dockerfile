FROM openjdk:11-jre-slim
MAINTAINER Bard Lind <bard.lind@gmail.com>

ENV agent_jar_name=valuereporter-statsd-agent.jar
# Install Application
RUN adduser valuereporter
ADD target/valuereporter-statsd-agent-jar-with-dependencies.jar /home/valuereporter/$agent_jar_name
#ADD docker/valuereporter_override.properties /home/valuereporter/valuereporter_override.properties
#RUN chown valuereporter:valuereporter /home/valuereporter/valuereporter_override.properties
RUN chmod 755 /home/valuereporter/$agent_jar_name
ENV agent_jar_path=/home/valuereporter/$agent_jar_name


# Send StatsD value statistics
#ENV prefix=NEED-TOBE-SET
#ENV statsd_host=NEED-TOBE-SET
#ENV base_package=NEED-TOBE-SET
#ENV java_agent=-javaagent:./$agent_jar_name=base_package:$base_package,statsd_host:$statsd_host,prefix:$prefix
#CMD java  $java_agent -jar <your-service.jar>


