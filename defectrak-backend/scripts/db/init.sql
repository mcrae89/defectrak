DROP TABLE IF EXISTS priorities;
CREATE TABLE priorities (
    id SERIAL PRIMARY KEY,
    level VARCHAR(25) NOT NULL UNIQUE,
    status VARCHAR(25) NOT NULL DEFAULT 'active'
);

DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    role VARCHAR(25) NOT NULL UNIQUE,
    status VARCHAR(25) NOT NULL DEFAULT 'active'
);

DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL default 2,
    status VARCHAR(25) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES user_roles(id)
);

DROP TABLE IF EXISTS statuses;
CREATE TABLE statuses (
    id SERIAL PRIMARY KEY,
    status_label VARCHAR(25) NOT NULL UNIQUE,
    status VARCHAR(25) NOT NULL DEFAULT 'active'
);

DROP TABLE IF EXISTS bugs;
CREATE TABLE bugs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    priority_id INTEGER NOT NULL,
    status_id integer NOT NULL,
    assignee_user_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id INTEGER NOT NULL,
    CONSTRAINT fk_priority FOREIGN KEY (priority_id) REFERENCES priorities(id),
    CONSTRAINT fk_assignee FOREIGN KEY (assignee_user_id) REFERENCES users(id),
    CONSTRAINT fk_status FOREIGN KEY (status_id) REFERENCES statuses(id),
    CONSTRAINT fk_creator FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);