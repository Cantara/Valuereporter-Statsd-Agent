#!/bin/sh
NAME=piip
docker logs -f $(docker ps -aqf "name=$NAME")