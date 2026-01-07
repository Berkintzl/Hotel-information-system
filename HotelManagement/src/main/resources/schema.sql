CREATE TABLE IF NOT EXISTS Hotel (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS UserRole (
  id INT PRIMARY KEY,
  role_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS User (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role_id INT NOT NULL,
  hotel_id INT NOT NULL,
  FOREIGN KEY (role_id) REFERENCES UserRole(id),
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clients (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  hotel_id INT NOT NULL,
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clientratings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  client_id INT NOT NULL,
  rating INT NOT NULL,
  hotel_id INT NOT NULL,
  FOREIGN KEY (client_id) REFERENCES clients(id),
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rooms (
  id INT AUTO_INCREMENT PRIMARY KEY,
  room_number VARCHAR(50) NOT NULL,
  room_category VARCHAR(255) NOT NULL,
  is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
  hotel_id INT NOT NULL,
  reservation_number VARCHAR(255),
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id),
  UNIQUE KEY unique_room_per_hotel (room_number, hotel_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  client_id INT NOT NULL,
  reservation_number VARCHAR(255) NOT NULL UNIQUE,
  room_number VARCHAR(50) NOT NULL,
  cancellation_type VARCHAR(255),
  room_category VARCHAR(255) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
  hotel_id INT NOT NULL,
  FOREIGN KEY (client_id) REFERENCES clients(id),
  FOREIGN KEY (room_number) REFERENCES rooms(room_number),
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  hotel_id INT NOT NULL,
  message VARCHAR(1000) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS additionalservices (
  id INT AUTO_INCREMENT PRIMARY KEY,
  service_type VARCHAR(255) NOT NULL,
  season VARCHAR(255) NOT NULL,
  usage_count INT NOT NULL,
  hotel_id INT NOT NULL,
  FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
) ENGINE=InnoDB;
