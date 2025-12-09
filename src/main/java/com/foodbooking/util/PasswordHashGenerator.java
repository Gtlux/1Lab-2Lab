package com.foodbooking.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate BCrypt password hashes
 * Run this to get hashed passwords for database
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String password = "password123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("\nSQL UPDATE komanda:");
        System.out.println("UPDATE users SET password = '" + hash + "' WHERE username IN ('jonas', 'petras', 'agne', 'pizza_owner', 'burger_owner', 'sushi_owner', 'driver1', 'driver2');");
    }
}
