package com.foodbooking.util;

import com.foodbooking.model.ShoppingCart;
import com.foodbooking.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private ShoppingCart shoppingCart;

    private SessionManager() {
        this.shoppingCart = new ShoppingCart();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
        shoppingCart.clear();
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void clearCart() {
        shoppingCart.clear();
    }

    public boolean isClient() {
        return currentUser != null && currentUser.isClient();
    }

    public boolean isRestaurantOwner() {
        return currentUser != null && currentUser.isRestaurantOwner();
    }

    public boolean isDriver() {
        return currentUser != null && currentUser.isDriver();
    }

    public boolean isAdministrator() {
        return currentUser != null && currentUser.isAdministrator();
    }
}
