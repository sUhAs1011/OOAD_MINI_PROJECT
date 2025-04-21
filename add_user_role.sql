-- SQL script to add a role column to the users table
ALTER TABLE users ADD COLUMN role VARCHAR(10) DEFAULT 'voter';

-- Update existing admin users if needed
-- UPDATE users SET role = 'admin' WHERE username = 'admin';

-- For testing: insert an admin user if doesn't exist
INSERT IGNORE INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin'); 