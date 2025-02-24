INSERT INTO user_roles (role)
VALUES
('admin'), ('general');

INSERT INTO users (email,first_name, last_name, password, role_id)
VALUES
('meadnl89@gmail.com', 'Nathan', 'Mead', '$2a$10$ipi/FA2Bqq0Y69rl8T.IneoOT7wA7TNycWjamZ55PFKZiUxS0CoEK',1)
,('fake@email.com', 'Jayme', 'Mead', '$2a$10$ipi/FA2Bqq0Y69rl8T.IneoOT7wA7TNycWjamZ55PFKZiUxS0CoEK',2)
ON CONFLICT DO NOTHING;

INSERT INTO priorities (level)
VALUES
('low'), ('medium'), ('high');

INSERT INTO statuses (status_label)
VALUES
('open'), ('closed');

INSERT INTO bugs (title, description, priority_id, status_id, assignee_user_id, created_by_user_id)
VALUES
('Bug 1', 'This is a bug', 1, 1, 2, 1), ('Bug 2', 'This is a bug', 2, 1, 2, 1), ('Bug 3', 'This is a bug', 3, 1, 2, 1);