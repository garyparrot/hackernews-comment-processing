CREATE SEQUENCE keywords_id_seq;
CREATE TABLE IF NOT EXISTS keywords (
    id INTEGER NOT NULL PRIMARY KEY DEFAULT nextval('keywords_id_seq'),
    keyword TEXT UNIQUE,
    category TEXT[]
);
ALTER SEQUENCE keywords_id_seq OWNED BY keywords.id;

INSERT INTO keywords (keyword, category)
    VALUES ('opensource', '{"opensource", "foss"}'),
           ('ubuntu', '{"linux", "opensource"}'),
           ('arch', '{"linux", "opensource"}'),
           ('manjaro', '{"linux", "opensource"}'),
           ('gentoo', '{"linux", "opensource"}'),
           ('postgres', '{"database", "opensource", "postgres"}'),
           ('neo4j', '{"database", "opensource"}'),
           ('mongodb', '{"database", "opensource"}'),
           ('redis', '{"database", "opensource"}'),
           ('sqlite', '{"database", "opensource"}'),
           ('mariadb', '{"database", "opensource"}'),
           ('cockroachdb', '{"database", "opensource"}'),
           ('mysql', '{"database"}'),
           ('oracledb', '{"database"}'),
           ('ai', '{"big-data"}'),
           ('ds', '{"big-data"}'),
           ('kafka', '{"big-data", "stream-processing", "message-broker"}'),
           ('rabbitmq', '{"message-broker"}'),
           ('activemq', '{"message-broker"}'),
           ('mqtt', '{"message-broker"}'),
           ('hadoop', '{"big-data"}'),
           ('hbase', '{"big-data", "database"}'),
           ('hive', '{"big-data", "database"}'),
           ('cassandra', '{"big-data", "database"}'),
           ('hdfs', '{"big-data", "database"}'),
           ('spark', '{"big-data", "stream-processing"}'),
           ('flink', '{"big-data", "stream-processing"}'),
           ('java', '{"java"}'),
           ('scala', '{"java"}'),
           ('groovy', '{"java"}'),
           ('kotlin', '{"java"}'),
           ('tomcat', '{"java"}'),
           ('jlink', '{"java"}'),
           ('maven', '{"java"}'),
           ('gradle', '{"java"}'),
           ('jvm', '{"java"}');