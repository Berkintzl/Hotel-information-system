-- User Roles (required for the system to work)
INSERT IGNORE INTO UserRole (id, role_name) VALUES (1, 'admin');
INSERT IGNORE INTO UserRole (id, role_name) VALUES (2, 'owner');
INSERT IGNORE INTO UserRole (id, role_name) VALUES (3, 'manager');
INSERT IGNORE INTO UserRole (id, role_name) VALUES (4, 'receptionist');

-- Create Admin User (without hotel_id)
INSERT IGNORE INTO User (username, password, role_id, hotel_id)
VALUES ('admin', 'admin123', (SELECT id FROM UserRole WHERE role_name = 'admin'), NULL);
