-- Food Booking System Database Schema
-- MySQL Database

-- Create database
CREATE DATABASE IF NOT EXISTS food_booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_booking_db;

-- Drop existing tables (in correct order due to foreign keys)
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('CLIENT', 'RESTAURANT_OWNER', 'DRIVER', 'ADMINISTRATOR') NOT NULL,
    restaurant_id INT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Restaurants table
CREATE TABLE restaurants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    description TEXT,
    owner_id INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_owner_id (owner_id),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Menu items table
CREATE TABLE menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    available BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_category (category),
    INDEX idx_available (available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Orders table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    driver_id INT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'PICKED_UP', 'DELIVERING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    delivery_address VARCHAR(255) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP NULL,
    FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_client_id (client_id),
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Order items table
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    menu_item_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT,
    INDEX idx_order_id (order_id),
    INDEX idx_menu_item_id (menu_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Messages table
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data

-- Insert administrator
INSERT INTO users (username, password, email, full_name, phone_number, role, active)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@foodbooking.lt', 'Administratorius', '+37060000000', 'ADMINISTRATOR', TRUE);

-- Insert restaurant owners
INSERT INTO users (username, password, email, full_name, phone_number, role, active)
VALUES
('owner1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'owner1@restaurant.lt', 'Jonas Jonaitis', '+37060000001', 'RESTAURANT_OWNER', TRUE),
('owner2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'owner2@restaurant.lt', 'Petras Petraitis', '+37060000002', 'RESTAURANT_OWNER', TRUE);

-- Insert drivers
INSERT INTO users (username, password, email, full_name, phone_number, role, active)
VALUES
('driver1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'driver1@foodbooking.lt', 'Antanas Antanaitis', '+37060000003', 'DRIVER', TRUE),
('driver2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'driver2@foodbooking.lt', 'Kazys Kaziūnas', '+37060000004', 'DRIVER', TRUE);

-- Insert clients
INSERT INTO users (username, password, email, full_name, phone_number, role, active)
VALUES
('client1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client1@gmail.com', 'Ona Onaitė', '+37060000005', 'CLIENT', TRUE),
('client2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client2@gmail.com', 'Marija Marčiūnaitė', '+37060000006', 'CLIENT', TRUE);

-- Insert restaurants
INSERT INTO restaurants (name, address, phone_number, email, description, owner_id, active)
VALUES
('Picerija Roma', 'Gedimino pr. 1, Vilnius', '+37052000001', 'info@picerijjaroma.lt', 'Autentiška itališka pica ir makaronai', 2, TRUE),
('Sushi Bar', 'Vilniaus g. 10, Vilnius', '+37052000002', 'info@sushibar.lt', 'Šviežiausi sušiai ir azijietiški patiekalai', 3, TRUE);

-- Update users with restaurant_id
UPDATE users SET restaurant_id = 1 WHERE id = 2;
UPDATE users SET restaurant_id = 2 WHERE id = 3;

-- Insert menu items for Picerija Roma
INSERT INTO menu_items (restaurant_id, name, description, price, category, available)
VALUES
(1, 'Margarita', 'Klasikinė pica su pomidorais ir mocarela', 8.99, 'Pica', TRUE),
(1, 'Pepperoni', 'Pica su aštriu dešrele', 9.99, 'Pica', TRUE),
(1, 'Carbonara', 'Makaronai su kumpiu ir grietinės padažu', 7.99, 'Makaronai', TRUE),
(1, 'Tiramisu', 'Italų desertas su kava', 4.99, 'Desertai', TRUE);

-- Insert menu items for Sushi Bar
INSERT INTO menu_items (restaurant_id, name, description, price, category, available)
VALUES
(2, 'California Roll', 'Suši su krabais ir avokadu', 12.99, 'Suši', TRUE),
(2, 'Salmon Nigiri', 'Lašiša ant ryžių', 14.99, 'Suši', TRUE),
(2, 'Miso Sriuba', 'Tradicinė japonų sriuba', 3.99, 'Sriubos', TRUE),
(2, 'Green Tea Ice Cream', 'Žaliosios arbatos ledai', 5.99, 'Desertai', TRUE);

-- Insert sample orders
INSERT INTO orders (client_id, restaurant_id, status, total_amount, delivery_address, notes)
VALUES
(6, 1, 'PENDING', 18.98, 'Savanorių pr. 5-10, Vilnius', 'Skambinti prieš pristatymą'),
(7, 2, 'CONFIRMED', 27.98, 'Žirmūnų g. 20-15, Vilnius', NULL);

-- Insert order items
INSERT INTO order_items (order_id, menu_item_id, menu_item_name, price, quantity, subtotal)
VALUES
(1, 1, 'Margarita', 8.99, 1, 8.99),
(1, 2, 'Pepperoni', 9.99, 1, 9.99),
(2, 5, 'California Roll', 12.99, 1, 12.99),
(2, 6, 'Salmon Nigiri', 14.99, 1, 14.99);

-- Note: Default password for all users is 'password123'
