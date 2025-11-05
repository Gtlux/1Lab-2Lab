package com.foodbooking.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate BCrypt password hashes for testing
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        String plainPassword = "password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        System.out.println("Plain password: " + plainPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
        System.out.println();
        System.out.println("Verification: " + BCrypt.checkpw(plainPassword, hashedPassword));
    }
}
