CREATE TABLE titles (
    id INT GENERATED ALWAYS AS IDENTITY,
    user_id VARCHAR NOT NULL,
    title_name VARCHAR NOT NULL,
    author VARCHAR NOT NULL,
    cover VARCHAR NOT NULL,
    number_of_highlights INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);