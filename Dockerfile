FROM openjdk:11-jre-slim
MAINTAINER Bard Lind <bard.lind@gmail.com>
#RUN yum -y install yum-cron
#RUN yum -y update
#RUN yum -y install curl

# Install Application
RUN adduser piip
ADD build/libs/piip*.jar /home/piip/piip.jar
ADD docker/piip_override.properties /home/piip/piip-override.properties
RUN chown piip:piip /home/piip/piip-override.properties

EXPOSE 22500 21515 21516

WORKDIR "/home/piip"
# CMD [ \
#     "java", \
#     "-Xdebug", \
#     "-Xrunjdwp:transport=dt_socket,address=21515,server=y,suspend=n", \
#     "-Dcom.sun.management.jmxremote.port=21516", \
#     "-Dcom.sun.management.jmxremote.rmi.port=21516", \
#     "-Dcom.sun.management.jmxremote.ssl=false", \
#     "-Dcom.sun.management.jmxremote.local.only=false", \
#     "-Dcom.sun.management.jmxremote.authenticate=false", \
#     "-Djava.rmi.server.hostname=localhost", \
#     "-jar", \
#     "--add-modules java.xml.bind ", \
#     "piip.jar" \
# ]

# Send StatsD value statistics
ENV agent_jar_name=valuereporter-statsd-agent.jar
ADD valuereporter-statsd-agent.jar /home/piip/$agent_jar
ENV prefix=programinformasjon.piip.dev
ENV statsd_host=graphite.dmz.nrk.no
ENV java_agent=-javaagent:./$agent_jar_name=base_package:no.nrk.prf.piip,statsd_host:$statsd_host,prefix:$prefix
CMD java  $java_agent -jar piip.jar


