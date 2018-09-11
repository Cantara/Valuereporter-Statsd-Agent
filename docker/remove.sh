#!/bin/sh
echo stopping valuereporter-statsd-agent
docker stop valuereporter-statsd-agent
echo removing valuereporter-statsd-agent
docker rm valuereporter-statsd-agent
echo list active docker containers
docker ps
