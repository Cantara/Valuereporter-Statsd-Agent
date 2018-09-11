FROM baardl/valuereporter-statsd-agent
MAINTAINER Bard Lind <bard.lind@gmail.com>

#ENV agent_jar_name=valuereporter-statsd-agent.jar


# Send StatsD value statistics
#ENV prefix=NEED-TOBE-SET
#ENV statsd_host=NEED-TOBE-SET
#ENV base_package=NEED-TOBE-SET
#ENV java_agent=-javaagent:./$agent_jar_name=base_package:$base_package,statsd_host:$statsd_host,prefix:$prefix
#CMD java  $java_agent -jar <your-service.jar>
#CMD "echo Agent jar: $agent_jar_name"
ENTRYPOINT echo "Print env inherited from cantara/valuereporter-statsd-agent: agent_jar_path, $agent_jar_path"
#docker run -it <image>

