package com.foodbooking.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationHelper {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{8,15}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty() && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPrice(String price) {
        try {
            BigDecimal value = new BigDecimal(price);
            return value.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidQuantity(String quantity) {
        try {
            int value = Integer.parseInt(quantity);
            return value > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getValidationMessage(String fieldName, String value, String validationType) {
        switch (validationType) {
            case "email":
                if (!isValidEmail(value)) {
                    return fieldName + " neteisingas. Įveskite teisingą el. pašto adresą.";
                }
                break;
            case "phone":
                if (!isValidPhone(value)) {
                    return fieldName + " neteisingas. Įveskite teisingą telefono numerį.";
                }
                break;
            case "password":
                if (!isValidPassword(value)) {
                    return fieldName + " turi būti bent 6 simbolių ilgio.";
                }
                break;
            case "price":
                if (!isValidPrice(value)) {
                    return fieldName + " turi būti teigiamas skaičius.";
                }
                break;
            case "quantity":
                if (!isValidQuantity(value)) {
                    return fieldName + " turi būti teigiamas sveikasis skaičius.";
                }
                break;
            case "required":
                if (!isNotEmpty(value)) {
                    return fieldName + " yra privalomas laukas.";
                }
                break;
        }
        return null;
    }
}
