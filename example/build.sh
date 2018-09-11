#!/bin/sh

BASEDIR=$(pwd)

IMAGE_TAG=latest
IMAGE_NAME=valuereporter-child-example
IMAGE_TITLE="Image extending the valuereporter-statsd-agent"

echo "Building Docker image $IMAGE_TITLE ($IMAGE_ID) from $BASEDIR."

docker build -t "$IMAGE_NAME" "$BASEDIR" --no-cache|| exit 1

docker tag "$IMAGE_NAME" "$IMAGE_NAME:$IMAGE_TAG" || exit 1