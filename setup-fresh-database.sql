-- =====================================================
-- FOOD BOOKING SYSTEM - FRESH DATABASE SETUP
-- For Android App + REST API
-- =====================================================

-- Drop and recreate database
DROP DATABASE IF EXISTS food_booking_db;
CREATE DATABASE food_booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_booking_db;

-- =====================================================
-- TABLE: users
-- =====================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('CLIENT', 'RESTAURANT_OWNER', 'DRIVER', 'ADMINISTRATOR') NOT NULL,
    restaurant_id INT DEFAULT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: restaurants
-- =====================================================
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
    INDEX idx_owner_id (owner_id),
    INDEX idx_active (active),
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: menu_items
-- =====================================================
CREATE TABLE menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    available BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(255),
    peak_hour_price DECIMAL(10,2) COMMENT 'Price during peak hours',
    off_peak_price DECIMAL(10,2) COMMENT 'Price during off-peak hours',
    is_dynamic_pricing BOOLEAN DEFAULT FALSE COMMENT 'Whether dynamic pricing is enabled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_category (category),
    INDEX idx_available (available),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: orders
-- =====================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    driver_id INT DEFAULT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'PICKED_UP', 'DELIVERING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    delivery_address VARCHAR(255) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP NULL,
    INDEX idx_client_id (client_id),
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: order_items
-- =====================================================
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    menu_item_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal DECIMAL(10,2) NOT NULL,
    notes TEXT,
    INDEX idx_order_id (order_id),
    INDEX idx_menu_item_id (menu_item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: loyalty_points
-- =====================================================
CREATE TABLE loyalty_points (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE COMMENT 'Client user ID',
    points INT NOT NULL DEFAULT 0,
    total_earned INT NOT NULL DEFAULT 0 COMMENT 'Total points ever earned',
    total_spent INT NOT NULL DEFAULT 0 COMMENT 'Total points spent',
    tier ENUM('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') DEFAULT 'BRONZE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_points (points),
    INDEX idx_tier (tier),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: loyalty_transactions
-- =====================================================
CREATE TABLE loyalty_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_id INT DEFAULT NULL,
    points_change INT NOT NULL COMMENT 'Positive for earning, negative for spending',
    transaction_type ENUM('EARNED', 'SPENT', 'BONUS', 'EXPIRED') NOT NULL,
    description VARCHAR(255),
    balance_after INT NOT NULL COMMENT 'Points balance after this transaction',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: cancellation_requests
-- =====================================================
CREATE TABLE cancellation_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    client_id INT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_by INT DEFAULT NULL COMMENT 'Restaurant owner or admin who reviewed',
    review_note TEXT COMMENT 'Response from owner/admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    INDEX idx_order_id (order_id),
    INDEX idx_client_id (client_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: reviews
-- =====================================================
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id INT NOT NULL COMMENT 'User who wrote the review',
    reviewed_entity_type ENUM('RESTAURANT', 'DRIVER', 'CLIENT') NOT NULL,
    reviewed_entity_id INT NOT NULL COMMENT 'ID of restaurant, driver, or client',
    order_id INT DEFAULT NULL COMMENT 'Related order (optional)',
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewed_entity (reviewed_entity_type, reviewed_entity_id),
    INDEX idx_order_id (order_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: messages
-- =====================================================
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    INDEX idx_order_id (order_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sent_at (sent_at),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TEST DATA
-- Password for all users: password123
-- BCrypt hash: $2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S
-- =====================================================

-- 1. USERS (ID 1-10)
INSERT INTO users (username, password, email, full_name, phone_number, role, active) VALUES
-- Administrator
('admin', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'admin@foodbooking.lt', 'Administratorius', '+37060000000', 'ADMINISTRATOR', TRUE),

-- Clients (ID 2-4)
('jonas', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'jonas@example.com', 'Jonas Jonaitis', '+37060000001', 'CLIENT', TRUE),
('petras', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'petras@example.com', 'Petras Petraitis', '+37060000002', 'CLIENT', TRUE),
('agne', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'agne@example.com', 'Agnė Agnaitė', '+37060000003', 'CLIENT', TRUE),

-- Restaurant Owners (ID 5-7)
('pizza_owner', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'pizza@restaurant.lt', 'Pizza Savininkas', '+37060000010', 'RESTAURANT_OWNER', TRUE),
('burger_owner', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'burger@restaurant.lt', 'Burger Savininkas', '+37060000011', 'RESTAURANT_OWNER', TRUE),
('sushi_owner', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'sushi@restaurant.lt', 'Sushi Savininkas', '+37060000012', 'RESTAURANT_OWNER', TRUE),

-- Drivers (ID 8-9)
('driver1', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'driver1@delivery.lt', 'Vairuotojas Pirmas', '+37060000020', 'DRIVER', TRUE),
('driver2', '$2a$10$PRnpHrMBN6tGmBaOQ8xTgeda0aiqPvaKO/u9od1bfirVa4O161T1S', 'driver2@delivery.lt', 'Vairuotojas Antras', '+37060000021', 'DRIVER', TRUE);

-- 2. RESTAURANTS (ID 1-5)
INSERT INTO restaurants (name, address, phone_number, email, description, owner_id, active) VALUES
('Pizza Paradise', 'Vilniaus g. 10, Vilnius', '+37052345678', 'info@pizzaparadise.lt', 'Geriausios picos mieste!', 5, TRUE),
('Burger House', 'Gedimino pr. 25, Vilnius', '+37052345679', 'info@burgerhouse.lt', 'Sultingi burgeriai ir traškios bulvytės', 6, TRUE),
('Sushi Master', 'Konstitucijos pr. 7, Vilnius', '+37052345680', 'info@sushimaster.lt', 'Autentiški japonų patiekalai', 7, TRUE),
('Kebab King', 'Ozo g. 25, Vilnius', '+37052345681', 'info@kebabking.lt', 'Skanus kebabas ir falafel', 5, TRUE),
('Salad Bar', 'Savanorių pr. 15, Vilnius', '+37052345682', 'info@saladbar.lt', 'Švieži ir sveiki salotų rinkiniai', 6, TRUE);

-- 3. MENU ITEMS (ID 1-28)
-- Pizza Paradise (Restaurant ID 1)
INSERT INTO menu_items (restaurant_id, name, description, price, category, available) VALUES
(1, 'Margarita', 'Klasikinė pica su mocarela ir baziliku', 8.99, 'Pica', TRUE),
(1, 'Pepperoni', 'Pica su pepperoni dešra', 10.99, 'Pica', TRUE),
(1, 'Keturių sūrių', 'Mocarela, gorgonzola, parmezanas, feta', 11.99, 'Pica', TRUE),
(1, 'Hawaiian', 'Su kumpiu ir ananasais', 9.99, 'Pica', TRUE),
(1, 'Coca Cola 0.5L', 'Gaivusis gėrimas', 2.50, 'Gėrimai', TRUE),
(1, 'Sprite 0.5L', 'Gaivusis gėrimas', 2.50, 'Gėrimai', TRUE);

-- Burger House (Restaurant ID 2)
INSERT INTO menu_items (restaurant_id, name, description, price, category, available) VALUES
(2, 'Classic Burger', 'Jautienos mėsainis, salotos, pomidorai, svogūnai', 7.99, 'Burgeriai', TRUE),
(2, 'Cheese Burger', 'Su dvigubu sūriu', 8.99, 'Burgeriai', TRUE),
(2, 'Bacon Burger', 'Su traškiu bekonu', 9.99, 'Burgeriai', TRUE),
(2, 'French Fries', 'Traškios bulvytės', 3.50, 'Priedai', TRUE),
(2, 'Onion Rings', 'Kepti svogūnų žiedai', 4.50, 'Priedai', TRUE),
(2, 'Milkshake', 'Vanilės, šokolado arba braškių', 4.99, 'Gėrimai', TRUE);

-- Sushi Master (Restaurant ID 3)
INSERT INTO menu_items (restaurant_id, name, description, price, category, available) VALUES
(3, 'Californios roleliai 8vnt', 'Su krabais, avokadu, agurku', 12.99, 'Sushi', TRUE),
(3, 'Lašišos nigiri 6vnt', 'Švieži lašišos nigiri', 14.99, 'Sushi', TRUE),
(3, 'Miso sriuba', 'Tradicinė japonų sriuba', 5.50, 'Sriubos', TRUE),
(3, 'Edamame', 'Garinti sojos ankščiai su druska', 4.50, 'Užkandžiai', TRUE),
(3, 'Tempura', 'Kepti daržovių ir krevetės', 13.99, 'Užkandžiai', TRUE);

-- Kebab King (Restaurant ID 4)
INSERT INTO menu_items (restaurant_id, name, description, price, category, available) VALUES
(4, 'Kebab didelis', 'Su vištiena, daržovėmis, padažu', 6.50, 'Kebabai', TRUE),
(4, 'Kebab mažas', 'Mažesnis porcija', 4.50, 'Kebabai', TRUE),
(4, 'Falafel', 'Vegetariškas patiekalas', 5.50, 'Vegetariška', TRUE),
(4, 'Hummus su pita', 'Tradicinis hummus', 4.50, 'Užkandžiai', TRUE),
(4, 'Baklava', 'Saldus desertas', 3.50, 'Desertai', TRUE);

-- Salad Bar (Restaurant ID 5)
INSERT INTO menu_items (restaurant_id, name, description, price, category, available) VALUES
(5, 'Caesar salotos', 'Su vištiena, sūriu, skrebučiais', 8.50, 'Salotos', TRUE),
(5, 'Graikiškos salotos', 'Su feta sūriu, alyvuogėmis', 7.50, 'Salotos', TRUE),
(5, 'Smoothie bowl', 'Su vaisiais ir granola', 6.50, 'Desertai', TRUE),
(5, 'Fresh juice', 'Šviežiai spaustos sultys', 4.50, 'Gėrimai', TRUE);

-- 4. ORDERS (ID 1-3)
-- Jonas užsakymas (Pizza Paradise) - DELIVERED
INSERT INTO orders (client_id, restaurant_id, driver_id, status, total_amount, delivery_address, notes, created_at, delivered_at) VALUES
(2, 1, 8, 'DELIVERED', 21.98, 'Savanorių pr. 1-15, Vilnius', 'Skambinti prieš pristatant', '2025-12-07 15:30:00', '2025-12-07 16:15:00');

-- Petras užsakymas (Burger House) - PENDING
INSERT INTO orders (client_id, restaurant_id, status, total_amount, delivery_address, created_at) VALUES
(3, 2, 'PENDING', 16.49, 'Žalgirio g. 92-5, Vilnius', NOW());

-- Agnė užsakymas (Sushi Master) - PREPARING
INSERT INTO orders (client_id, restaurant_id, status, total_amount, delivery_address, notes, created_at) VALUES
(4, 3, 'PREPARING', 27.98, 'Konstitucijos pr. 26-88, Vilnius', 'Be imbiero', DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- 5. ORDER ITEMS
-- Jonas užsakymas (#1)
INSERT INTO order_items (order_id, menu_item_id, menu_item_name, price, quantity, subtotal) VALUES
(1, 1, 'Margarita', 8.99, 2, 17.98),
(1, 5, 'Coca Cola 0.5L', 2.50, 2, 5.00);

-- Petras užsakymas (#2)
INSERT INTO order_items (order_id, menu_item_id, menu_item_name, price, quantity, subtotal) VALUES
(2, 8, 'Cheese Burger', 8.99, 1, 8.99),
(2, 10, 'French Fries', 3.50, 2, 7.00);

-- Agnė užsakymas (#3)
INSERT INTO order_items (order_id, menu_item_id, menu_item_name, price, quantity, subtotal) VALUES
(3, 13, 'Californios roleliai 8vnt', 12.99, 2, 25.98),
(3, 15, 'Miso sriuba', 5.50, 1, 5.50);

-- 6. LOYALTY POINTS
INSERT INTO loyalty_points (user_id, points, total_earned, tier) VALUES
(2, 150, 220, 'SILVER'),  -- Jonas
(3, 50, 50, 'BRONZE'),    -- Petras
(4, 200, 280, 'GOLD');    -- Agnė

-- =====================================================
-- SETUP COMPLETE!
-- =====================================================
-- Test users (all passwords: password123):
--   Admin:     admin / password123
--   Clients:   jonas / password123
--              petras / password123
--              agne / password123
--   Owners:    pizza_owner / password123
--              burger_owner / password123
--              sushi_owner / password123
--   Drivers:   driver1 / password123
--              driver2 / password123
-- =====================================================
