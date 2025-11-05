-- Extensions to Food Booking System Database Schema
-- Additional features: Reviews, Loyalty Points, Dynamic Pricing, Cancellation Requests

USE food_booking_db;

-- Cancellation requests table
CREATE TABLE IF NOT EXISTS cancellation_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    client_id INT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_by INT NULL COMMENT 'Restaurant owner or admin who reviewed',
    review_note TEXT NULL COMMENT 'Response from owner/admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order_id (order_id),
    INDEX idx_client_id (client_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add dynamic pricing fields to menu_items
ALTER TABLE menu_items
ADD COLUMN peak_hour_price DECIMAL(10, 2) NULL COMMENT 'Price during peak hours',
ADD COLUMN off_peak_price DECIMAL(10, 2) NULL COMMENT 'Price during off-peak hours',
ADD COLUMN is_dynamic_pricing BOOLEAN DEFAULT FALSE COMMENT 'Whether dynamic pricing is enabled';

-- Reviews table for restaurants, drivers, and clients
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id INT NOT NULL COMMENT 'User who wrote the review',
    reviewed_entity_type ENUM('RESTAURANT', 'DRIVER', 'CLIENT') NOT NULL,
    reviewed_entity_id INT NOT NULL COMMENT 'ID of restaurant, driver, or client',
    order_id INT NULL COMMENT 'Related order (optional)',
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewed_entity (reviewed_entity_type, reviewed_entity_id),
    INDEX idx_order_id (order_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Loyalty points table for tracking customer rewards
CREATE TABLE IF NOT EXISTS loyalty_points (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT 'Client user ID',
    points INT NOT NULL DEFAULT 0,
    total_earned INT NOT NULL DEFAULT 0 COMMENT 'Total points ever earned',
    total_spent INT NOT NULL DEFAULT 0 COMMENT 'Total points spent',
    tier ENUM('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') DEFAULT 'BRONZE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_points (user_id),
    INDEX idx_points (points),
    INDEX idx_tier (tier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Loyalty points transactions history
CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_id INT NULL,
    points_change INT NOT NULL COMMENT 'Positive for earning, negative for spending',
    transaction_type ENUM('EARNED', 'SPENT', 'BONUS', 'EXPIRED') NOT NULL,
    description VARCHAR(255),
    balance_after INT NOT NULL COMMENT 'Points balance after this transaction',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initialize loyalty points for existing clients
INSERT INTO loyalty_points (user_id, points, total_earned, total_spent, tier)
SELECT id, 0, 0, 0, 'BRONZE'
FROM users
WHERE role = 'CLIENT'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Sample reviews data
INSERT INTO reviews (reviewer_id, reviewed_entity_type, reviewed_entity_id, order_id, rating, comment) VALUES
(3, 'RESTAURANT', 1, NULL, 5, 'Puikus restoranas! Maistas skanus, aptarnavimas greitas.'),
(3, 'RESTAURANT', 2, NULL, 4, 'Geras maistas, bet kartais tenka ilgai laukti.'),
(5, 'RESTAURANT', 1, NULL, 5, 'Labai patiko! Rekomenduoju visiems.'),
(3, 'DRIVER', 4, NULL, 4, 'Vairuotojas malonus, pristatė laiku.');

-- Update menu items with dynamic pricing examples
UPDATE menu_items SET
    is_dynamic_pricing = TRUE,
    peak_hour_price = ROUND(price * 1.2, 2),
    off_peak_price = ROUND(price * 0.85, 2)
WHERE id IN (1, 2, 3, 4, 5);

-- Comments
-- Reviews:
--   - Clients can review restaurants and drivers after order completion
--   - Drivers can review clients
--   - Restaurant owners can review clients
--   - Admins can moderate reviews
--
-- Loyalty Points:
--   - Clients earn 1 point per 1 EUR spent
--   - Points can be redeemed for discounts
--   - Tiers: BRONZE (0-99), SILVER (100-499), GOLD (500-999), PLATINUM (1000+)
--   - Each tier provides increasing discount percentages
--
-- Dynamic Pricing:
--   - Peak hours: 11:00-14:00, 18:00-21:00 (use peak_hour_price)
--   - Off-peak hours: other times (use off_peak_price)
--   - Regular price used when is_dynamic_pricing = FALSE
