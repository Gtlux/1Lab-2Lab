package com.foodbooking.mobile.util;

import com.foodbooking.mobile.models.CartItem;
import com.foodbooking.mobile.models.MenuItem;
import com.foodbooking.mobile.models.Restaurant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private static ShoppingCart instance;
    private Restaurant restaurant;
    private List<CartItem> items;

    private ShoppingCart() {
        items = new ArrayList<>();
    }

    public static ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void addItem(MenuItem menuItem, int quantity) {
        if (restaurant == null) {
            restaurant = new Restaurant();
            restaurant.setId(menuItem.getRestaurantId());
            restaurant.setName(menuItem.getRestaurantName());
        } else if (!restaurant.getId().equals(menuItem.getRestaurantId())) {
            throw new IllegalArgumentException(
                    "Negalite pridėti patiekalų iš skirtingų restoranų! Pirma išvalykite krepšelį.");
        }

        for (CartItem item : items) {
            if (item.getMenuItem().getId().equals(menuItem.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        items.add(new CartItem(menuItem, quantity));
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        if (items.isEmpty()) {
            restaurant = null;
        }
    }

    public void updateQuantity(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(item);
        } else {
            item.setQuantity(newQuantity);
        }
    }

    public void clear() {
        items.clear();
        restaurant = null;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<CartItem> getItems() {
        return items;
    }
}
