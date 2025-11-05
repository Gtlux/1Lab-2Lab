package com.foodbooking.controller;

import com.foodbooking.dao.CancellationRequestDAO;
import com.foodbooking.dao.OrderDAO;
import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.model.CancellationRequest;
import com.foodbooking.model.Order;
import com.foodbooking.model.OrderStatus;
import com.foodbooking.model.Restaurant;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersTabController implements Initializable {

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> idColumn;

    @FXML
    private TableColumn<Order, String> clientColumn;

    @FXML
    private TableColumn<Order, String> restaurantColumn;

    @FXML
    private TableColumn<Order, String> driverColumn;

    @FXML
    private TableColumn<Order, OrderStatus> statusColumn;

    @FXML
    private TableColumn<Order, BigDecimal> totalAmountColumn;

    @FXML
    private TableColumn<Order, String> deliveryAddressColumn;

    @FXML
    private TableColumn<Order, LocalDateTime> createdAtColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button changeStatusButton;

    @FXML
    private Button requestCancellationButton;

    @FXML
    private Button deleteButton;

    private OrderDAO orderDAO = new OrderDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private CancellationRequestDAO cancellationRequestDAO = new CancellationRequestDAO();
    private ObservableList<Order> ordersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        restaurantColumn.setCellValueFactory(new PropertyValueFactory<>("restaurantName"));
        driverColumn.setCellValueFactory(new PropertyValueFactory<>("driverName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        deliveryAddressColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Configure buttons based on user role
        if (SessionManager.getInstance().isClient()) {
            // Clients can create orders and request cancellation, but cannot delete
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            changeStatusButton.setVisible(false);
            changeStatusButton.setManaged(false);
            requestCancellationButton.setVisible(true);
            requestCancellationButton.setManaged(true);
        } else {
            // Other roles (admin, restaurant owner, driver) cannot create new orders directly
            // But they can change status and delete
            addButton.setVisible(false);
            addButton.setManaged(false);
            requestCancellationButton.setVisible(false);
            requestCancellationButton.setManaged(false);
        }

        // Load data
        loadOrders();
    }

    private void loadOrders() {
        try {
            List<Order> orders = new ArrayList<>();

            if (SessionManager.getInstance().isClient()) {
                // Clients see their own orders
                orders = orderDAO.getOrdersByClientId(SessionManager.getInstance().getCurrentUser().getId());
            } else if (SessionManager.getInstance().isRestaurantOwner()) {
                // Restaurant owners see orders for their restaurants
                List<Restaurant> ownRestaurants = restaurantDAO.getRestaurantsByOwnerId(
                        SessionManager.getInstance().getCurrentUser().getId());
                for (Restaurant restaurant : ownRestaurants) {
                    orders.addAll(orderDAO.getOrdersByRestaurantId(restaurant.getId()));
                }
            } else if (SessionManager.getInstance().isDriver()) {
                // Drivers see available orders and their assigned orders
                orders.addAll(orderDAO.getAvailableOrdersForDrivers());
                orders.addAll(orderDAO.getOrdersByDriverId(SessionManager.getInstance().getCurrentUser().getId()));
            } else if (SessionManager.getInstance().isAdministrator()) {
                // Admins see all orders
                orders = orderDAO.getAllOrders();
            }

            ordersList.clear();
            ordersList.addAll(orders);
            ordersTable.setItems(ordersList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko užkrauti užsakymų: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        // Only clients and admins can create orders
        if (!SessionManager.getInstance().isClient() && !SessionManager.getInstance().isAdministrator()) {
            AlertHelper.showError("Klaida", "Tik klientai gali kurti užsakymus!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderDialog.fxml"));
            Parent root = loader.load();

            OrderDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Naujas užsakymas");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadOrders();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleView() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užsakymą peržiūrai!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderViewDialog.fxml"));
            Parent root = loader.load();

            OrderViewDialogController controller = loader.getController();
            controller.setOrder(selected);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Užsakymo informacija");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangeStatus() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užsakymą!");
            return;
        }

        // Check permissions
        if (SessionManager.getInstance().isClient() &&
            !selected.getClientId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            AlertHelper.showError("Klaida", "Galite keisti tik savo užsakymų statusą!");
            return;
        }

        if (SessionManager.getInstance().isRestaurantOwner()) {
            Restaurant restaurant = restaurantDAO.getRestaurantById(selected.getRestaurantId());
            if (restaurant == null || !restaurant.getOwnerId().equals(
                    SessionManager.getInstance().getCurrentUser().getId())) {
                AlertHelper.showError("Klaida", "Galite keisti tik savo restorano užsakymų statusą!");
                return;
            }
        }

        // Show status change dialog
        ChoiceDialog<OrderStatus> dialog = new ChoiceDialog<>(selected.getStatus(), OrderStatus.values());
        dialog.setTitle("Keisti užsakymo statusą");
        dialog.setHeaderText("Pasirinkite naują statusą");
        dialog.setContentText("Statusas:");

        dialog.showAndWait().ifPresent(newStatus -> {
            // Handle driver assignment
            if (newStatus == OrderStatus.PICKED_UP && SessionManager.getInstance().isDriver()) {
                if (orderDAO.assignDriver(selected.getId(), SessionManager.getInstance().getCurrentUser().getId())) {
                    AlertHelper.showSuccess("Sėkmė", "Užsakymas priskirtas jums!");
                    loadOrders();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko priskirti užsakymo!");
                }
            } else {
                selected.updateStatus(newStatus);
                if (orderDAO.updateOrder(selected)) {
                    AlertHelper.showSuccess("Sėkmė", "Užsakymo statusas pakeistas!");
                    loadOrders();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko pakeisti statuso!");
                }
            }
        });
    }

    @FXML
    private void handleDelete() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užsakymą šalinimui!");
            return;
        }

        // Only admins and order owners can delete
        if (!SessionManager.getInstance().isAdministrator() &&
            !selected.getClientId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            AlertHelper.showError("Klaida", "Negalite ištrinti šio užsakymo!");
            return;
        }

        if (AlertHelper.showConfirmation("Patvirtinimas",
                "Ar tikrai norite ištrinti užsakymą #" + selected.getId() + "?")) {
            try {
                if (orderDAO.deleteOrder(selected.getId())) {
                    AlertHelper.showSuccess("Sėkmė", "Užsakymas sėkmingai ištrintas!");
                    loadOrders();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko ištrinti užsakymo!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Šalinimo klaida: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRequestCancellation() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užsakymą atšaukimui!");
            return;
        }

        // Only clients can request cancellation of their own orders
        if (!SessionManager.getInstance().isClient() ||
            !selected.getClientId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            AlertHelper.showError("Klaida", "Galite atšaukti tik savo užsakymus!");
            return;
        }

        // Cannot cancel already delivered or cancelled orders
        if (selected.getStatus() == OrderStatus.DELIVERED || selected.getStatus() == OrderStatus.CANCELLED) {
            AlertHelper.showError("Klaida", "Negalima atšaukti jau pristatyto arba atšaukto užsakymo!");
            return;
        }

        // Check if there's already a pending cancellation request
        CancellationRequest existingRequest = cancellationRequestDAO.getCancellationRequestByOrderId(selected.getId());
        if (existingRequest != null && existingRequest.getStatus() == CancellationRequest.CancellationStatus.PENDING) {
            AlertHelper.showWarning("Įspėjimas",
                "Šio užsakymo atšaukimo užklausa jau pateikta!\n\n" +
                "Statusas: Laukiama restorano savininko patvirtinimo");
            return;
        }

        // Show cancellation reason dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Atšaukimo užklausa");
        dialog.setHeaderText("Prašome nurodyti atšaukimo priežastį");
        dialog.setContentText("Priežastis:");

        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                AlertHelper.showError("Klaida", "Prašome nurodyti atšaukimo priežastį!");
                return;
            }

            // Create confirmation dialog
            String confirmMessage = String.format(
                "Ar tikrai norite pateikti užsakymo #%d atšaukimo užklausą?\n\n" +
                "Priežastis: %s\n\n" +
                "Restorano savininkas peržiūrės ir priims sprendimą.",
                selected.getId(), reason
            );

            if (AlertHelper.showConfirmation("Patvirtinimas", confirmMessage)) {
                // Create cancellation request
                CancellationRequest request = new CancellationRequest(
                    selected.getId(),
                    SessionManager.getInstance().getCurrentUser().getId(),
                    reason.trim()
                );

                int requestId = cancellationRequestDAO.createCancellationRequest(request);
                if (requestId > 0) {
                    AlertHelper.showSuccess("Sėkmė",
                        "Atšaukimo užklausa sėkmingai pateikta!\n\n" +
                        "Užklausa #" + requestId + "\n" +
                        "Restorano savininkas gaus pranešimą ir priims sprendimą.\n\n" +
                        "Galite stebėti užklausos statusą užsakymų sąraše.");
                    loadOrders();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko pateikti atšaukimo užklausos!");
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadOrders();
    }
}
