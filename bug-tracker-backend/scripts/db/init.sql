--CREATE BUG TABLE
DROP TABLE IF EXISTS priorities;
CREATE TABLE priorities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(100) NOT NULL
);

DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    role VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS statuses;
CREATE TABLE statuses (
    id SERIAL PRIMARY KEY,
    status_label VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS bugs;
CREATE TABLE bugs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    priority_ID INTEGER NOT NULL,
    status_id integer NOT NULL,
    assignee_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    CONSTRAINT fk_priority FOREIGN KEY (priority_ID) REFERENCES priorities(id),
    CONSTRAINT fk_assignee FOREIGN KEY (assignee_id) REFERENCES users(id),
    CONSTRAINT fk_status FOREIGN KEY (status_id) REFERENCES statuses(id)
);