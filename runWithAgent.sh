#!/bin/sh
./update_service.sh
agent_jar_name=valuereporter-statsd-agent.jar
prefix=programinformasjon.piip.dev
statsd_host=graphite.dmz.nrk.no
java_agent=-javaagent:./$agent_jar_name=base_package:no.nrk.prf.piip,statsd_host:$statsd_host,prefix:$prefix
java $java_agent -jar build/libs/piip-0.1.1-SNAPSHOT.jar
