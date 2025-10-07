CREATE TABLE IF NOT EXISTS flyway_schema_history
(
    installed_rank INT                     NOT NULL,
    version        VARCHAR(50),
    description    VARCHAR(200),
    type           VARCHAR(20)             NOT NULL,
    script         VARCHAR(1000)           NOT NULL,
    installed_by   VARCHAR(100)            NOT NULL,
    installed_on   TIMESTAMP DEFAULT now() NOT NULL,
    execution_time INT                     NOT NULL,
    success        BOOLEAN                 NOT NULL
);

CREATE TABLE IF NOT EXISTS test_table
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
