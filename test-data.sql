-- TEST DUOMENYS FOOD BOOKING SISTEMAI
-- Paleisti šį scriptą MySQL phpMyAdmin arba MySQL Workbench

USE food_booking;

-- 1. IŠVALYTI SENUS DUOMENIS (Optional)
-- DELETE FROM order_items;
-- DELETE FROM orders;
-- DELETE FROM menu_items;
-- DELETE FROM restaurants;
-- DELETE FROM users WHERE role IN ('CLIENT', 'DRIVER', 'RESTAURANT_OWNER');

-- 2. VARTOTOJAI
-- Slaptažodis visiems: "password123" (bcrypt hash)
INSERT INTO users (username, password, email, full_name, phone_number, role, active, created_at, updated_at) VALUES
-- Klientai
('jonas', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'jonas@example.com', 'Jonas Jonaitis', '+37060000001', 'CLIENT', TRUE, NOW(), NOW()),
('petras', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'petras@example.com', 'Petras Petraitis', '+37060000002', 'CLIENT', TRUE, NOW(), NOW()),
('agne', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'agne@example.com', 'Agnė Agnaitė', '+37060000003', 'CLIENT', TRUE, NOW(), NOW()),

-- Restoranų savininkai
('pizza_owner', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'pizza@restaurant.lt', 'Pizza Savininkas', '+37060000010', 'RESTAURANT_OWNER', TRUE, NOW(), NOW()),
('burger_owner', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'burger@restaurant.lt', 'Burger Savininkas', '+37060000011', 'RESTAURANT_OWNER', TRUE, NOW(), NOW()),
('sushi_owner', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'sushi@restaurant.lt', 'Sushi Savininkas', '+37060000012', 'RESTAURANT_OWNER', TRUE, NOW(), NOW()),

-- Vairuotojai
('driver1', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'driver1@delivery.lt', 'Vairuotojas Pirmas', '+37060000020', 'DRIVER', TRUE, NOW(), NOW()),
('driver2', '$2a$10$rZ7QJ5Wq0xG4XmZQJ5Wq0OqZ7QJ5Wq0xG4XmZQJ5Wq0xG4XmZQJ5W', 'driver2@delivery.lt', 'Vairuotojas Antras', '+37060000021', 'DRIVER', TRUE, NOW(), NOW());

-- 3. RESTORANAI
INSERT INTO restaurants (name, address, phone_number, email, description, owner_id, active, created_at, updated_at) VALUES
('Pizza Paradise', 'Vilniaus g. 10, Vilnius', '+37052345678', 'info@pizzaparadise.lt', 'Geriausios picos mieste!',
    (SELECT id FROM users WHERE username = 'pizza_owner'), TRUE, NOW(), NOW()),

('Burger House', 'Gedimino pr. 25, Vilnius', '+37052345679', 'info@burgerhouse.lt', 'Sultingi burgeriai ir traškios bulvytės',
    (SELECT id FROM users WHERE username = 'burger_owner'), TRUE, NOW(), NOW()),

('Sushi Master', 'Konstitucijos pr. 7, Vilnius', '+37052345680', 'info@sushimaster.lt', 'Autentiški japonų patiekalai',
    (SELECT id FROM users WHERE username = 'sushi_owner'), TRUE, NOW(), NOW()),

('Kebab King', 'Ozo g. 25, Vilnius', '+37052345681', 'info@kebabking.lt', 'Skanus kebabas ir falafel',
    (SELECT id FROM users WHERE username = 'pizza_owner'), TRUE, NOW(), NOW()),

('Salad Bar', 'Savanorių pr. 15, Vilnius', '+37052345682', 'info@saladbar.lt', 'Švieži ir sveiki salotų rinkiniai',
    (SELECT id FROM users WHERE username = 'burger_owner'), TRUE, NOW(), NOW());

-- 4. MENIU PATIEKALAI

-- Pizza Paradise meniu
INSERT INTO menu_items (name, description, price, category, restaurant_id, available, created_at, updated_at) VALUES
('Margarita', 'Klasikinė pica su mocarela ir bazilik', 8.99, 'Pica',
    (SELECT id FROM restaurants WHERE name = 'Pizza Paradise'), TRUE, NOW(), NOW()),
('Pepperoni', 'Pica su pepperoni dešra', 10.99, 'Pica',
    (SELECT id FROM restaurants WHERE name = 'Pizza Paradise'), TRUE, NOW(), NOW()),
('Keturių sūrių', 'Mocarela, gorgonzola, parmezanas, feta', 11.99, 'Pica',
    (SELECT id FROM restaurants WHERE name = 'Pizza Paradise'), TRUE, NOW(), NOW()),
('Hawaian', 'Su kumpiu ir ananasais', 9.99, 'Pica',
    (SELECT id FROM restaurants WHERE name = 'Pizza Paradise'), TRUE, NOW(), NOW()),
('Coca Cola 0.5L', 'Gaivusis gėrimas', 2.50, 'Gėrimai',
    (SELECT id FROM restaurants WHERE name = 'Pizza Paradise'), TRUE, NOW(), NOW());

-- Burger House meniu
INSERT INTO menu_items (name, description, price, category, restaurant_id, available, created_at, updated_at) VALUES
('Classic Burger', 'Jautienos mėsainis, salotos, pomidorai, svogūnai', 7.99, 'Burgeriai',
    (SELECT id FROM restaurants WHERE name = 'Burger House'), TRUE, NOW(), NOW()),
('Cheese Burger', 'Su dvigubu sūriu', 8.99, 'Burgeriai',
    (SELECT id FROM restaurants WHERE name = 'Burger House'), TRUE, NOW(), NOW()),
('Bacon Burger', 'Su traškiu bekonu', 9.99, 'Burgeriai',
    (SELECT id FROM restaurants WHERE name = 'Burger House'), TRUE, NOW(), NOW()),
('French Fries', 'Traškios bulvytės', 3.50, 'Priedai',
    (SELECT id FROM restaurants WHERE name = 'Burger House'), TRUE, NOW(), NOW()),
('Onion Rings', 'Kepti svogūnų žiedai', 4.50, 'Priedai',
    (SELECT id FROM restaurants WHERE name = 'Burger House'), TRUE, NOW(), NOW());

-- Sushi Master meniu
INSERT INTO menu_items (name, description, price, category, restaurant_id, available, created_at, updated_at) VALUES
('Californios roleliai 8vnt', 'Su krabais, avokadu, agurku', 12.99, 'Sushi',
    (SELECT id FROM restaurants WHERE name = 'Sushi Master'), TRUE, NOW(), NOW()),
('Lašišos nigiri 6vnt', 'Švieži lašišos nigiri', 14.99, 'Sushi',
    (SELECT id FROM restaurants WHERE name = 'Sushi Master'), TRUE, NOW(), NOW()),
('Miso sriuba', 'Tradicinė japonų sriuba', 5.50, 'Sriubos',
    (SELECT id FROM restaurants WHERE name = 'Sushi Master'), TRUE, NOW(), NOW()),
('Edamame', 'Garinti sojos ankščiai su druska', 4.50, 'Užkandžiai',
    (SELECT id FROM restaurants WHERE name = 'Sushi Master'), TRUE, NOW(), NOW());

-- Kebab King meniu
INSERT INTO menu_items (name, description, price, category, restaurant_id, available, created_at, updated_at) VALUES
('Kebab didelis', 'Su vištiena, daržovėmis, padažu', 6.50, 'Kebabai',
    (SELECT id FROM restaurants WHERE name = 'Kebab King'), TRUE, NOW(), NOW()),
('Falafel', 'Vegetariškas patiekalas', 5.50, 'Vegetariška',
    (SELECT id FROM restaurants WHERE name = 'Kebab King'), TRUE, NOW(), NOW()),
('Hummus su pita', 'Tradicinis hummus', 4.50, 'Užkandžiai',
    (SELECT id FROM restaurants WHERE name = 'Kebab King'), TRUE, NOW(), NOW());

-- Salad Bar meniu
INSERT INTO menu_items (name, description, price, category, restaurant_id, available, created_at, updated_at) VALUES
('Caesar salotos', 'Su vištiena, sūriu, skrebučiais', 8.50, 'Salotos',
    (SELECT id FROM restaurants WHERE name = 'Salad Bar'), TRUE, NOW(), NOW()),
('Graikiškos salotos', 'Su feta sūriu, alyvuogėmis', 7.50, 'Salotos',
    (SELECT id FROM restaurants WHERE name = 'Salad Bar'), TRUE, NOW(), NOW()),
('Smoothie bowl', 'Su vaisiais ir granola', 6.50, 'Desertas',
    (SELECT id FROM restaurants WHERE name = 'Salad Bar'), TRUE, NOW(), NOW());

-- 5. TEST UŽSAKYMAI
INSERT INTO orders (client_id, restaurant_id, delivery_address, status, total_amount, notes, created_at, updated_at) VALUES
((SELECT id FROM users WHERE username = 'jonas'),
 (SELECT id FROM restaurants WHERE name = 'Pizza Paradise'),
 'Savanorių pr. 1-15, Vilnius', 'DELIVERED', 21.98, 'Skambinti prieš pristatant',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

((SELECT id FROM users WHERE username = 'petras'),
 (SELECT id FROM restaurants WHERE name = 'Burger House'),
 'Žalgirio g. 92-5, Vilnius', 'PENDING', 16.49, NULL,
 NOW(), NOW()),

((SELECT id FROM users WHERE username = 'agne'),
 (SELECT id FROM restaurants WHERE name = 'Sushi Master'),
 'Konstitucijos pr. 26-88, Vilnius', 'PREPARING', 27.98, 'Be imbiero',
 DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- 6. UŽSAKYMŲ PATIEKALAI
-- Jonas užsakymas (Pizza Paradise)
INSERT INTO order_items (order_id, menu_item_id, name, price, quantity) VALUES
(1, (SELECT id FROM menu_items WHERE name = 'Margarita' LIMIT 1), 'Margarita', 8.99, 2),
(1, (SELECT id FROM menu_items WHERE name = 'Coca Cola 0.5L' LIMIT 1), 'Coca Cola 0.5L', 2.50, 2);

-- Petras užsakymas (Burger House)
INSERT INTO order_items (order_id, menu_item_id, name, price, quantity) VALUES
(2, (SELECT id FROM menu_items WHERE name = 'Classic Burger' LIMIT 1), 'Classic Burger', 7.99, 1),
(2, (SELECT id FROM menu_items WHERE name = 'French Fries' LIMIT 1), 'French Fries', 3.50, 1),
(2, (SELECT id FROM menu_items WHERE name = 'Coca Cola 0.5L' LIMIT 1), 'Coca Cola 0.5L', 2.50, 2);

-- Agnė užsakymas (Sushi Master)
INSERT INTO order_items (order_id, menu_item_id, name, price, quantity) VALUES
(3, (SELECT id FROM menu_items WHERE name = 'Californios roleliai 8vnt' LIMIT 1), 'Californios roleliai 8vnt', 12.99, 1),
(3, (SELECT id FROM menu_items WHERE name = 'Lašišos nigiri 6vnt' LIMIT 1), 'Lašišos nigiri 6vnt', 14.99, 1);

-- PABAIGA
SELECT 'Test duomenys sėkmingai įterpti!' AS status;
SELECT COUNT(*) AS restaurants_count FROM restaurants;
SELECT COUNT(*) AS menu_items_count FROM menu_items;
SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS users_count FROM users WHERE role IN ('CLIENT', 'DRIVER', 'RESTAURANT_OWNER');
