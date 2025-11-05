package com.foodbooking.util;

import com.foodbooking.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
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
