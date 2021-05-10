CREATE SEQUENCE keywords_id_seq;
CREATE TABLE IF NOT EXISTS keywords (
    id INTEGER NOT NULL PRIMARY KEY DEFAULT nextval('keywords_id_seq'),
    keyword TEXT UNIQUE,
    category TEXT[]
);
ALTER SEQUENCE keywords_id_seq OWNED BY keywords.id;

/* keyword matches */
CREATE SEQUENCE keyword_matches_id_seq;
CREATE TABLE IF NOT EXISTS keyword_matches (
    id INTEGER NOT NULL PRIMARY KEY DEFAULT nextval('keyword_matches_id_seq'),
    hacker_news_item_id BIGINT NOT NULL,
    keyword TEXT NOT NULL,
    category TEXT NOT NULL,
    CONSTRAINT keyword_matches_unique_entry UNIQUE (hacker_news_item_id, keyword, category)
);
ALTER SEQUENCE keyword_matches_id_seq OWNED BY keyword_matches.id;

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

CREATE MATERIALIZED VIEW topic_list AS
    SELECT DISTINCT unnest(category) AS topic_name FROM keywords;
/* * Later we will need to refresh the materialized view concurrently.
   * In order to do so, we need to add a index on the materialized view.
   * Reference: https://www.postgresql.org/docs/9.4/sql-refreshmaterializedview.html
   */
CREATE UNIQUE INDEX ON topic_list (topic_name);

/*
 * A trigger on "public.keywords" table.
 * Once the table updated, trigger a refresh on topic_list materialized view.
 */
CREATE OR REPLACE FUNCTION refresh_topic_list() RETURNS trigger LANGUAGE plpgsql AS $$
    BEGIN
        REFRESH MATERIALIZED VIEW CONCURRENTLY topic_list;
        RETURN NULL;
    END;
$$;
CREATE TRIGGER trigger_refresh_topic_list
    AFTER INSERT OR UPDATE OR DELETE ON keywords
    FOR EACH STATEMENT EXECUTE PROCEDURE refresh_topic_list();
