#!/bin/sh -ex

: ${1?"Usage: $0 <major|minor|patch>"}

latest=$(git describe --abbrev=0 || echo 0.0.0)
release=$(semver ${latest} -i $1 --preid RC)

./mvnw scm:check-local-modification clean deploy scm:tag -P release -D revision=${release} -D tag=${release}
