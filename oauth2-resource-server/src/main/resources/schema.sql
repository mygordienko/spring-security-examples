CREATE TABLE IF NOT EXISTS interviews (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL,
    manager_email VARCHAR(255) NOT NULL,
    applicant_email VARCHAR(255) NOT NULL,
    result VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS demands (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL,
    author_email VARCHAR(255) NOT NULL,
    type VARCHAR(32) NOT NULL
);
