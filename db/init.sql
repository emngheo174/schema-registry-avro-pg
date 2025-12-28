CREATE TABLE IF NOT EXISTS schemas (
  id SERIAL PRIMARY KEY,
  subject TEXT NOT NULL,
  version INT NOT NULL,
  schema TEXT NOT NULL,
  fingerprint TEXT NOT NULL UNIQUE
);

CREATE UNIQUE INDEX IF NOT EXISTS uniq_subject_version
ON schemas(subject, version);
