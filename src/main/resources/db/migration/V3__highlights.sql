CREATE TABLE highlights (
    id INT GENERATED ALWAYS AS IDENTITY,
    user_id VARCHAR NOT NULL,
    title_id INT NOT NULL,
    highlight_text VARCHAR NOT NULL,
    last_reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT highlights_fk_title FOREIGN KEY(title_id) REFERENCES titles(id) ON DELETE CASCADE,
    CONSTRAINT highlights_fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);