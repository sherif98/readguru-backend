CREATE TABLE tags (
    id VARCHAR,
    user_id INT NOT NULL,
    number_of_highlights INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tags_highlights (
    highlight_id INT,
    tag_id VARCHAR,
    PRIMARY KEY (highlight_id, tag_id),
    CONSTRAINT fk_highlight FOREIGN KEY(highlight_id) REFERENCES highlights(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY(tag_id) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);