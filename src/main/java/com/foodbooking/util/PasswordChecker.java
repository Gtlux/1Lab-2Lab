package com.foodbooking.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to check if a BCrypt hash matches a plain password
 */
public class PasswordChecker {

    public static void main(String[] args) {
        String plainPassword = "password123";

        String hashFromDatabase = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        System.out.println("Testing password verification...");
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Hash from DB: " + hashFromDatabase);
        System.out.println();

        boolean matches = BCrypt.checkpw(plainPassword, hashFromDatabase);
        System.out.println("Does it match? " + matches);

        if (!matches) {
            System.out.println();
            System.out.println("Hash does NOT match! Generating new hash...");
            String newHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
            System.out.println("New hash: " + newHash);
            System.out.println();
            System.out.println("Copy this hash and replace in schema.sql");
        } else {
            System.out.println();
            System.out.println("Hash is CORRECT! Problem might be elsewhere.");
        }
    }
}
