#!/bin/sh
./update_service.sh
agent_jar_name=valuereporter-statsd-agent.jar
prefix=NEED-TOBE-SET-GRAPHANA/GRAPHITE-style
statsd_host=NEED-TOBE-SET
base_package=NEED-TOBE-SET
java_agent=-javaagent:./$agent_jar_name=base_package:$base_package,statsd_host:$statsd_host,prefix:$prefix
java $java_agent -jar build/libs/<your-service>.jar
