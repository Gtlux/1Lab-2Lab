-- Reset all test user passwords to "password123"
-- Run PasswordHashGenerator.java first to get the correct hash, then replace HASH_HERE with actual hash

-- TEMPORARY SOLUTION: Update with a known BCrypt hash for "password123"
-- Hash generated with BCrypt.hashpw("password123", BCrypt.gensalt(10))

UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'admin';
UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'owner1';
UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'owner2';
UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'driver1';
UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'driver2';
UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'client1';
UPDATE users SET password = '$2a$10$rRYQ9u9h5KRZh5vGHNdZKO8YZ6pHlXqYJX6HJYX.xLGXKGE5wXEOi' WHERE username = 'client2';
