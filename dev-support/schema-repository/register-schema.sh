#!/bin/sh

SCHEMA_REGISTRY_IP_PORT="${SCHEMA_REGISTRY_IP_PORT:-localhost:8081}"

register() {
    SCHEMA_CONTENT="$(cat $2 | tr '\r' ' ' | tr '\n' ' ' | sed 's/\"/\\\"/g')"
    SCHEMA_CONTENT_WRAPPERD='{ "schema": "'"$SCHEMA_CONTENT"'"}'

    debug "SCHEMA_CONTENT: " "$SCHEMA_CONTENT"
    debug "WRAPPED: " "$SCHEMA_CONTENT_WRAPPERD"

    RESULT="$(curl --silent -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        --data "$SCHEMA_CONTENT_WRAPPERD" \
        http://"$SCHEMA_REGISTRY_IP_PORT"/subjects/"$SCHEMA_NAME"/versions)"
    if [ $? -eq 0 ]; then
        echo "[Ok] schema $SCHEMA_NAME updated, server response: $RESULT"
    else
        echo "[No] failed to update $SCHEMA_NAME"
        exit 1
    fi
}

debug() {
    if [ "$DEBUG" ]; then
        echo $@
    fi
}

main() {

    sleep "${WAIT:-0}"

    for SCHEMA in "$@"
    do
        SCHEMA_NAME="$(echo "$SCHEMA" | sed 's/\.avro$//g' | sed 's/^.*\///g')"
        register "$SCHEMA_NAME" "$SCHEMA"
    done
}

main $@
