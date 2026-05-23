CREATE TABLE todos (
    id UUID PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description TEXT,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_todos_completed ON todos (completed);
CREATE INDEX idx_todos_created_at ON todos (created_at DESC);
