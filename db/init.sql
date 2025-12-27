DROP TABLE IF EXISTS schemas;

CREATE TABLE schemas (
    id SERIAL PRIMARY KEY,
    subject TEXT NOT NULL,
    version INT NOT NULL,
    schema_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (subject, version)
);
