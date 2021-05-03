FROM alpine

RUN apk --no-cache add curl

COPY ./schema /schema
COPY ./register-schema.sh /register-schema.sh
COPY ./run.sh /run.sh

ENTRYPOINT [ "/run.sh" ]
