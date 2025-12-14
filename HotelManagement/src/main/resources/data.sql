INSERT IGNORE INTO UserRole (id, role_name) VALUES (1, 'admin');
INSERT IGNORE INTO UserRole (id, role_name) VALUES (2, 'owner');
INSERT IGNORE INTO UserRole (id, role_name) VALUES (3, 'manager');
INSERT IGNORE INTO UserRole (id, role_name) VALUES (4, 'receptionist');
INSERT IGNORE INTO Hotel (name, address) VALUES ('Default Hotel', 'London, UK');
INSERT IGNORE INTO User (username, password, role_id, hotel_id)
VALUES ('admin', 'admin123', (SELECT id FROM UserRole WHERE role_name = 'admin'), (SELECT id FROM Hotel WHERE name = 'Default Hotel'));
