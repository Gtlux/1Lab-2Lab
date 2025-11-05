package com.foodbooking.controller;

import com.foodbooking.dao.*;
import com.foodbooking.model.Order;
import com.foodbooking.model.OrderItem;
import com.foodbooking.model.Restaurant;
import com.foodbooking.model.User;
import com.foodbooking.model.UserRole;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import com.foodbooking.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderDialogController implements Initializable {

    @FXML
    private ComboBox<User> clientComboBox;

    @FXML
    private VBox clientBox;

    @FXML
    private ComboBox<Restaurant> restaurantComboBox;

    @FXML
    private TextField deliveryAddressField;

    @FXML
    private TextArea notesArea;

    @FXML
    private ComboBox<com.foodbooking.model.MenuItem> menuItemComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<OrderItem> orderItemsTable;

    @FXML
    private TableColumn<OrderItem, String> itemNameColumn;

    @FXML
    private TableColumn<OrderItem, BigDecimal> itemPriceColumn;

    @FXML
    private TableColumn<OrderItem, Integer> itemQuantityColumn;

    @FXML
    private TableColumn<OrderItem, BigDecimal> itemSubtotalColumn;

    @FXML
    private Label totalLabel;

    private UserDAO userDAO = new UserDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();

    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private boolean saved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        orderItemsTable.setItems(orderItems);

        setupClientComboBox();
        setupRestaurantComboBox();
        setupMenuItemComboBox();

        if (SessionManager.getInstance().isClient()) {
            clientBox.setVisible(false);
            clientBox.setManaged(false);
        }

        quantityField.setText("1");
    }

    private void setupClientComboBox() {
        clientComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() + " (" + user.getUsername() + ")" : "";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        List<User> clients = userDAO.getUsersByRole(UserRole.CLIENT);
        clientComboBox.getItems().addAll(clients);
    }

    private void setupRestaurantComboBox() {
        restaurantComboBox.setConverter(new StringConverter<Restaurant>() {
            @Override
            public String toString(Restaurant restaurant) {
                return restaurant != null ? restaurant.getName() : "";
            }

            @Override
            public Restaurant fromString(String string) {
                return null;
            }
        });

        List<Restaurant> restaurants = restaurantDAO.getActiveRestaurants();
        restaurantComboBox.getItems().addAll(restaurants);

        restaurantComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMenuItems(newVal.getId());
            }
        });
    }

    private void setupMenuItemComboBox() {
        menuItemComboBox.setConverter(new StringConverter<com.foodbooking.model.MenuItem>() {
            @Override
            public String toString(com.foodbooking.model.MenuItem item) {
                return item != null ? item.getName() + " - " + item.getPrice() + " €" : "";
            }

            @Override
            public com.foodbooking.model.MenuItem fromString(String string) {
                return null;
            }
        });
    }

    private void loadMenuItems(int restaurantId) {
        List<com.foodbooking.model.MenuItem> menuItems = menuItemDAO.getAvailableMenuItems(restaurantId);
        menuItemComboBox.getItems().clear();
        menuItemComboBox.getItems().addAll(menuItems);
    }

    @FXML
    private void handleAddItem() {
        com.foodbooking.model.MenuItem selectedItem = menuItemComboBox.getValue();
        String quantityStr = quantityField.getText().trim();

        if (selectedItem == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite patiekalą!");
            return;
        }

        if (!ValidationHelper.isValidQuantity(quantityStr)) {
            AlertHelper.showError("Klaida", "Neteisingas kiekis!");
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        OrderItem orderItem = new OrderItem(
                null,
                selectedItem.getId(),
                selectedItem.getName(),
                selectedItem.getPrice(),
                quantity
        );

        orderItems.add(orderItem);
        updateTotal();

        menuItemComboBox.setValue(null);
        quantityField.setText("1");
    }

    @FXML
    private void handleRemoveItem() {
        OrderItem selected = orderItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite elementą šalinimui!");
            return;
        }

        orderItems.remove(selected);
        updateTotal();
    }

    private void updateTotal() {
        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText(String.format("%.2f €", total));
    }

    @FXML
    private void handleSave() {
        Restaurant restaurant = restaurantComboBox.getValue();
        String deliveryAddress = deliveryAddressField.getText().trim();

        if (restaurant == null) {
            AlertHelper.showError("Klaida", "Pasirinkite restoraną!");
            return;
        }

        if (!ValidationHelper.isNotEmpty(deliveryAddress)) {
            AlertHelper.showError("Klaida", "Įveskite pristatymo adresą!");
            return;
        }

        if (orderItems.isEmpty()) {
            AlertHelper.showError("Klaida", "Pridėkite bent vieną patiekalą!");
            return;
        }

        Integer clientId;
        if (SessionManager.getInstance().isClient()) {
            clientId = SessionManager.getInstance().getCurrentUser().getId();
        } else {
            if (clientComboBox.getValue() == null) {
                AlertHelper.showError("Klaida", "Pasirinkite klientą!");
                return;
            }
            clientId = clientComboBox.getValue().getId();
        }

        try {
            Order order = new Order(clientId, restaurant.getId(), deliveryAddress);
            order.setNotes(notesArea.getText().trim());
            order.setOrderItems(new ArrayList<>(orderItems));
            order.calculateTotal();

            if (orderDAO.createOrder(order)) {
                boolean allItemsSaved = true;
                for (OrderItem item : orderItems) {
                    item.setOrderId(order.getId());
                    if (!orderItemDAO.createOrderItem(item)) {
                        allItemsSaved = false;
                        break;
                    }
                }

                if (allItemsSaved) {
                    AlertHelper.showSuccess("Sėkmė", "Užsakymas sėkmingai sukurtas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko išsaugoti visų užsakymo elementų!");
                }
            } else {
                AlertHelper.showError("Klaida", "Nepavyko sukurti užsakymo!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Išsaugojimo klaida: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) deliveryAddressField.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
