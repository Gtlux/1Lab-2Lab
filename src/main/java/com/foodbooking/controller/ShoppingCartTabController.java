package com.foodbooking.controller;

import com.foodbooking.dao.OrderDAO;
import com.foodbooking.dao.OrderItemDAO;
import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.model.*;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ShoppingCartTabController implements Initializable {

    @FXML
    private Label cartStatusLabel;

    @FXML
    private VBox restaurantInfoBox;

    @FXML
    private Label restaurantNameLabel;

    @FXML
    private Label restaurantAddressLabel;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> nameColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> priceColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> subtotalColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private TextField deliveryAddressField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button checkoutButton;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Set default delivery address from user profile
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getFullName() != null) {
            deliveryAddressField.setPromptText("Įveskite pristatymo adresą...");
        }

        // Load cart
        loadCart();
    }

    private void loadCart() {
        ShoppingCart cart = SessionManager.getInstance().getShoppingCart();

        if (cart.isEmpty()) {
            cartStatusLabel.setText("Tuščias krepšelis");
            restaurantInfoBox.setVisible(false);
            restaurantInfoBox.setManaged(false);
            checkoutButton.setDisable(true);
            totalLabel.setText("0.00 €");
            cartItems.clear();
        } else {
            cartStatusLabel.setText(String.format("Prekių kiekis: %d", cart.getItemCount()));

            // Load restaurant info
            Restaurant restaurant = restaurantDAO.getRestaurantById(cart.getRestaurant().getId());
            if (restaurant != null) {
                restaurantInfoBox.setVisible(true);
                restaurantInfoBox.setManaged(true);
                restaurantNameLabel.setText("Restoranas: " + restaurant.getName());
                restaurantAddressLabel.setText("Adresas: " + restaurant.getAddress());
            }

            // Load cart items
            cartItems.clear();
            cartItems.addAll(cart.getItems());
            cartTable.setItems(cartItems);

            // Update total
            totalLabel.setText(String.format("%.2f €", cart.getTotal()));

            // Enable checkout if delivery address is provided
            checkoutButton.setDisable(false);
        }
    }

    @FXML
    private void handleChangeQuantity() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite prekę kiekio keitimui!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getQuantity()));
        dialog.setTitle("Keisti kiekį");
        dialog.setHeaderText("Keisti kiekį: " + selected.getMenuItemName());
        dialog.setContentText("Naujas kiekis:");

        dialog.showAndWait().ifPresent(quantityStr -> {
            try {
                int newQuantity = Integer.parseInt(quantityStr.trim());
                if (newQuantity < 0) {
                    AlertHelper.showError("Klaida", "Kiekis negali būti neigiamas!");
                    return;
                }

                if (newQuantity == 0) {
                    if (AlertHelper.showConfirmation("Patvirtinimas",
                            "Kiekis 0 pašalins prekę iš krepšelio. Ar tęsti?")) {
                        SessionManager.getInstance().getShoppingCart().updateQuantity(selected, newQuantity);
                        loadCart();
                    }
                } else {
                    SessionManager.getInstance().getShoppingCart().updateQuantity(selected, newQuantity);
                    loadCart();
                    AlertHelper.showSuccess("Sėkmė", "Kiekis pakeistas!");
                }
            } catch (NumberFormatException e) {
                AlertHelper.showError("Klaida", "Neteisingas kiekio formatas!");
            }
        });
    }

    @FXML
    private void handleRemove() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite prekę šalinimui!");
            return;
        }

        if (AlertHelper.showConfirmation("Patvirtinimas",
                "Ar tikrai norite pašalinti " + selected.getMenuItemName() + " iš krepšelio?")) {
            SessionManager.getInstance().getShoppingCart().removeItem(selected);
            loadCart();
            AlertHelper.showSuccess("Sėkmė", "Prekė pašalinta iš krepšelio!");
        }
    }

    @FXML
    private void handleClear() {
        if (SessionManager.getInstance().getShoppingCart().isEmpty()) {
            AlertHelper.showInfo("Informacija", "Krepšelis jau tuščias!");
            return;
        }

        if (AlertHelper.showConfirmation("Patvirtinimas",
                "Ar tikrai norite išvalyti visą krepšelį?")) {
            SessionManager.getInstance().clearCart();
            loadCart();
            AlertHelper.showSuccess("Sėkmė", "Krepšelis išvalytas!");
        }
    }

    @FXML
    private void handleRefresh() {
        loadCart();
    }

    @FXML
    private void handleCheckout() {
        ShoppingCart cart = SessionManager.getInstance().getShoppingCart();

        if (cart.isEmpty()) {
            AlertHelper.showError("Klaida", "Krepšelis tuščias!");
            return;
        }

        String deliveryAddress = deliveryAddressField.getText();
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            AlertHelper.showError("Klaida", "Prašome įvesti pristatymo adresą!");
            deliveryAddressField.requestFocus();
            return;
        }

        String notes = notesArea.getText();

        // Confirm order
        String confirmMessage = String.format(
            "Patvirtinti užsakymą?\n\n" +
            "Restoranas: %s\n" +
            "Prekių: %d\n" +
            "Suma: %.2f €\n" +
            "Pristatymas: %s",
            restaurantNameLabel.getText().replace("Restoranas: ", ""),
            cart.getItemCount(),
            cart.getTotal(),
            deliveryAddress
        );

        if (!AlertHelper.showConfirmation("Patvirtinti užsakymą", confirmMessage)) {
            return;
        }

        try {
            // Create order
            Order order = new Order(
                SessionManager.getInstance().getCurrentUser().getId(),
                cart.getRestaurant().getId(),
                deliveryAddress.trim()
            );
            order.setNotes(notes);
            order.setStatus(OrderStatus.PENDING);

            // Save order
            int orderId = orderDAO.createOrder(order);
            if (orderId <= 0) {
                AlertHelper.showError("Klaida", "Nepavyko sukurti užsakymo!");
                return;
            }

            // Save order items
            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem(
                    orderId,
                    cartItem.getMenuItem().getId(),
                    cartItem.getMenuItem().getName(),
                    cartItem.getMenuItem().getPrice(),
                    cartItem.getQuantity()
                );
                orderItemDAO.createOrderItem(orderItem);
            }

            // Update order total
            order.setId(orderId);
            order.calculateTotal();
            orderDAO.updateOrder(order);

            // Clear cart
            SessionManager.getInstance().clearCart();
            loadCart();

            // Clear form
            deliveryAddressField.clear();
            notesArea.clear();

            AlertHelper.showSuccess("Sėkmė",
                String.format("Užsakymas #%d sėkmingai sukurtas!\n\n" +
                    "Suma: %.2f €\n" +
                    "Statusas: Laukiama patvirtinimo\n\n" +
                    "Galite stebėti užsakymo eigą skirtuke 'Mano užsakymai'.",
                    orderId, order.getTotalAmount()));
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Užsakymo sukūrimo klaida: " + e.getMessage());
        }
    }
}
