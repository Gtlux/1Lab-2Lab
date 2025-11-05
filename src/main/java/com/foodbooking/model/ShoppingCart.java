package com.foodbooking.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private Restaurant restaurant;
    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(MenuItem menuItem, int quantity) {
        if (restaurant == null) {
            restaurant = new Restaurant();
            restaurant.setId(menuItem.getRestaurantId());
        } else if (!restaurant.getId().equals(menuItem.getRestaurantId())) {
            throw new IllegalArgumentException(
                "Negalite pridėti patiekalų iš skirtingų restoranų! Pirma išvalykite krepšelį.");
        }

        for (CartItem cartItem : items) {
            if (cartItem.getMenuItem().getId().equals(menuItem.getId())) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                return;
            }
        }

        items.add(new CartItem(menuItem, quantity));
    }

    public void removeItem(CartItem cartItem) {
        items.remove(cartItem);
        if (items.isEmpty()) {
            restaurant = null;
        }
    }

    public void updateQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
        }
    }

    public void clear() {
        items.clear();
        restaurant = null;
    }

    public BigDecimal getTotal() {
        return items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
