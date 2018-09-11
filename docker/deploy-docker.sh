#!/bin/sh

BASEDIR=$(pwd)

IMAGE_TAG=$(git rev-parse HEAD)
#IMAGE_TAG=latest
IMAGE_NAME=dtr.nrk.no/programinfo/piip
#IMAGE_NAME=piip
IMAGE_TITLE="Authenticated access PRF."

echo "Building Docker image $IMAGE_TITLE ($IMAGE_ID) from $BASEDIR."

docker build -t "$IMAGE_NAME" "$BASEDIR" || exit 1

docker tag "$IMAGE_NAME" "$IMAGE_NAME:$IMAGE_TAG" || exit 1

docker push "$IMAGE_NAME:$IMAGE_TAG" || exit 1
docker push "$IMAGE_NAME:latest" || exit 1
