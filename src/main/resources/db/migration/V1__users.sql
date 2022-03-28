CREATE TABLE users (
    id VARCHAR,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    daily_highlights_review_count INT NOT NULL,
    highlights_review_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);