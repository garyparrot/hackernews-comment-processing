#!/usr/bin/env bash

# USAGE: sh download-connector.sh

BASEDIR=$(dirname "$0")
CONNECTOR_DIR="${BASEDIR}/connect"
PG_CONNECTOR_URL="https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/1.4.2.Final/debezium-connector-postgres-1.4.2.Final-plugin.tar.gz"
PG_CONNECTOR_TAR_PATH="${CONNECTOR_DIR}/debezium-connector-postgres-1.4.2.tar.gz"

function DEBUG {
  # shellcheck disable=SC2068
  echo $@
}

DEBUG "[###] Create connector folder"
mkdir -p "$CONNECTOR_DIR"

DEBUG "[###] Download PostgreSQL connector if content not exists"
curl "$PG_CONNECTOR_URL" > "$PG_CONNECTOR_TAR_PATH"

DEBUG "[###] Extract tarball"
tar xvf "$PG_CONNECTOR_TAR_PATH" -C "$CONNECTOR_DIR"