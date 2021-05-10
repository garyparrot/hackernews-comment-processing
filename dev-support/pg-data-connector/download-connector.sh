#!/usr/bin/env bash

# USAGE: sh download-connector.sh

BASEDIR=$(dirname "$0")
CONNECTOR_DIR="${BASEDIR}/connect"
PG_JDBC_DRIVER_URL="https://jdbc.postgresql.org/download/postgresql-42.2.20.jar"
PG_JDBC_DRIVER_JAR_PATH="${CONNECTOR_DIR}/postgresql-42.2.20.jar"

PG_CONNECTOR_URL="https://d1i4a15mxbxib1.cloudfront.net/api/plugins/confluentinc/kafka-connect-jdbc/versions/10.1.1/confluentinc-kafka-connect-jdbc-10.1.1.zip"
PG_CONNECTOR_ZIP_PATH="${CONNECTOR_DIR}/confluentinc-kafka-connect-jdbc-10.1.1.zip"

AVRO_CONVERTER_URL="https://d1i4a15mxbxib1.cloudfront.net/api/plugins/confluentinc/kafka-connect-avro-converter/versions/5.5.4/confluentinc-kafka-connect-avro-converter-5.5.4.zip"
AVRO_CONVERTER_ZIP_PATH="${CONNECTOR_DIR}/confluentinc-kafka-connect-avro-converter-5.5.4.zip"

function DEBUG {
  # shellcheck disable=SC2068
  echo $@
}

DEBUG "[###] Create connector folder"
mkdir -p "$CONNECTOR_DIR"

DEBUG "[###] Download PostgreSQL connector if content not exists"
curl "$PG_CONNECTOR_URL" > "$PG_CONNECTOR_ZIP_PATH"
curl "$AVRO_CONVERTER_URL" > "$AVRO_CONVERTER_ZIP_PATH"
curl "$PG_JDBC_DRIVER_URL" > "$PG_JDBC_DRIVER_JAR_PATH"

DEBUG "[###] Extract tarball"
unzip "$PG_CONNECTOR_ZIP_PATH" -d "$CONNECTOR_DIR"
unzip "$AVRO_CONVERTER_ZIP_PATH" -d "$CONNECTOR_DIR"
