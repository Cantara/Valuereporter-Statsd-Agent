#!/bin/sh

Echo will rebuild and refresh Docker.
./docker/build.sh
./docker/remove.sh
./docker/run.sh

Echo Docker is rebuilt, reusing the java fat jar from latest gradle build.

