INSERT INTO user_roles (role)
VALUES
('admin'), ('general');

INSERT INTO users (username, role, email)
VALUES
('meadnl89', 'admin', 'meadnl89@gmail.com')
,('jlmead83','general', 'fake@email.com')
ON CONFLICT DO NOTHING;

INSERT INTO priorities (name, status)
VALUES
('low', 'Active'), ('medium', 'Active'), ('high', 'Active');

INSERT INTO statuses (status_label)
VALUES
('open'), ('closed');

INSERT INTO bugs (title, description, priority_id, status_id, assignee_id, created_by)
VALUES
('Bug 1', 'This is a bug', '1', '1', '2', '1'), ('Bug 2', 'This is a bug', '2', '1', '2', '1'), ('Bug 3', 'This is a bug', '3', '1', '2', '1');