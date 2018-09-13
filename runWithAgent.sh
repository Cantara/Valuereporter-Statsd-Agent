#!/bin/sh
./update_service.sh
AGENT_JAR_NAME=valuereporter-statsd-agent.jar
PREFIX=NEED-TOBE-SET-GRAPHANA/GRAPHITE-style
STATSD_HOST=NEED-TOBE-SET
BASE_PACKAGE=NEED-TOBE-SET
JAVA_AGENT=-javaagent:./$AGENT_JAR_NAME=base_package:$BASE_PACKAGE,statsd_host:$STATSD_HOST,prefix:$PREFIX
java $JAVA_AGENT -jar build/libs/<your-service>.jar
