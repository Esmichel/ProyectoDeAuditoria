-- Create users schema if it doesn't already exist
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS common;

-- Create users table
CREATE TABLE users.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);

-- Create roles table
CREATE TABLE users.roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Create permissions table
CREATE TABLE users.permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Create user_roles table (many-to-many relationship between users and roles)
CREATE TABLE users.user_roles (
    user_id BIGINT REFERENCES users.users(id) ON DELETE CASCADE,  -- Fixed the reference to users table
    role_id BIGINT REFERENCES users.roles(id) ON DELETE CASCADE,  -- Fixed the reference to roles table
    PRIMARY KEY (user_id, role_id)
);

-- Create role_permissions table (many-to-many relationship between roles and permissions)
CREATE TABLE users.role_permissions (
    role_id BIGINT REFERENCES users.roles(id) ON DELETE CASCADE,  -- Fixed the reference to roles table
    permission_id BIGINT REFERENCES users.permissions(id) ON DELETE CASCADE,  -- Fixed the reference to permissions table
    PRIMARY KEY (role_id, permission_id)
);

-- Insert roles into roles table
INSERT INTO users.roles (name, description) VALUES
('Admin', 'Administrator with full access to all features'),
('User', 'Regular user with limited access'),
('Manager', 'Manager with access to specific features');

-- Insert permissions into permissions table
INSERT INTO users.permissions (name, description) VALUES
('VIEW_DASHBOARD', 'View the dashboard'),
('EDIT_PROFILE', 'Edit user profile'),
('MANAGE_USERS', 'Manage users and roles'),
('VIEW_REPORTS', 'View reports');

-- Create a user (assuming a user with ID 1)
INSERT INTO users.users (username, password, email, active) 
VALUES ('admin_user', '5f4dcc3b5aa765d61d8327deb882cf99', 'admin@example.com', TRUE);

-- Assign roles to the created user (user_id = 1)
INSERT INTO users.user_roles (user_id, role_id) VALUES
(1, (SELECT id FROM users.roles WHERE name = 'Admin')),
(1, (SELECT id FROM users.roles WHERE name = 'Manager')); 

-- Assign all permissions to the 'Admin' role
INSERT INTO users.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM users.roles r
JOIN users.permissions p ON r.name = 'Admin'; 
