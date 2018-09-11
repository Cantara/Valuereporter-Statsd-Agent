#!/bin/sh

BASEDIR=$(pwd)

IMAGE_NAME=valuereporter-child-example

echo "Building Docker image $IMAGE_TITLE ($IMAGE_ID) from $BASEDIR."

docker run -it "$IMAGE_NAME" "$BASEDIR"
