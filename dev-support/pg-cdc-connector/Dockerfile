FROM confluentinc/cp-kafka:6.1.1

COPY ./connect /connect
COPY ./config  /config

ENTRYPOINT [ "/usr/bin/connect-standalone", "/config/worker.properties", "/config/connector.properties" ]