#!/bin/sh
echo stopping piip
docker stop piip
echo removing piip
docker rm piip
echo list active docker containers
docker ps
