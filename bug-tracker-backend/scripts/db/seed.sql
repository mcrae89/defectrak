INSERT INTO user_roles (role)
VALUES
('admin'), ('general');

INSERT INTO users (email, role_id)
VALUES
('meadnl89@gmail.com', 1)
,('fake@email.com',2)
ON CONFLICT DO NOTHING;

INSERT INTO priorities (name, status)
VALUES
('low', 'Active'), ('medium', 'Active'), ('high', 'Active');

INSERT INTO statuses (status_label)
VALUES
('open'), ('closed');

INSERT INTO bugs (title, description, priority_id, status_id, assignee_user_id, created_by_user_id)
VALUES
('Bug 1', 'This is a bug', 1, 1, 2, 1), ('Bug 2', 'This is a bug', 2, 1, 2, 1), ('Bug 3', 'This is a bug', 3, 1, 2, 1);