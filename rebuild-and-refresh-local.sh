#!/bin/sh
./buildFatJar.sh
./docker/build.sh
./docker/remove.sh
./docker/run.sh