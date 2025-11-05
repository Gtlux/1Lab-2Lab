package com.foodbooking.controller;

import com.foodbooking.dao.CancellationRequestDAO;
import com.foodbooking.dao.OrderDAO;
import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.model.CancellationRequest;
import com.foodbooking.model.CancellationRequest.CancellationStatus;
import com.foodbooking.model.Order;
import com.foodbooking.model.OrderStatus;
import com.foodbooking.model.Restaurant;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class CancellationRequestsTabController implements Initializable {

    @FXML
    private TableView<CancellationRequest> requestsTable;

    @FXML
    private TableColumn<CancellationRequest, Integer> idColumn;

    @FXML
    private TableColumn<CancellationRequest, Integer> orderIdColumn;

    @FXML
    private TableColumn<CancellationRequest, String> restaurantColumn;

    @FXML
    private TableColumn<CancellationRequest, String> clientColumn;

    @FXML
    private TableColumn<CancellationRequest, String> reasonColumn;

    @FXML
    private TableColumn<CancellationRequest, CancellationStatus> statusColumn;

    @FXML
    private TableColumn<CancellationRequest, LocalDateTime> createdAtColumn;

    @FXML
    private TableColumn<CancellationRequest, String> reviewerColumn;

    @FXML
    private RadioButton pendingRadio;

    @FXML
    private RadioButton allRadio;

    private CancellationRequestDAO cancellationRequestDAO = new CancellationRequestDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private ObservableList<CancellationRequest> requestsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        restaurantColumn.setCellValueFactory(new PropertyValueFactory<>("restaurantName"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        reviewerColumn.setCellValueFactory(new PropertyValueFactory<>("reviewerName"));

        // Add listener to radio buttons
        pendingRadio.setOnAction(e -> loadRequests());
        allRadio.setOnAction(e -> loadRequests());

        // Load data
        loadRequests();
    }

    private void loadRequests() {
        try {
            List<CancellationRequest> requests;

            if (pendingRadio.isSelected()) {
                // Load only pending requests
                if (SessionManager.getInstance().isAdministrator()) {
                    requests = cancellationRequestDAO.getAllPendingRequests();
                } else if (SessionManager.getInstance().isRestaurantOwner()) {
                    // Get owner's restaurant IDs
                    List<Restaurant> ownRestaurants = restaurantDAO.getRestaurantsByOwnerId(
                        SessionManager.getInstance().getCurrentUser().getId());

                    requests = FXCollections.observableArrayList();
                    for (Restaurant restaurant : ownRestaurants) {
                        requests.addAll(cancellationRequestDAO.getPendingRequestsByRestaurantId(restaurant.getId()));
                    }
                } else {
                    requests = FXCollections.observableArrayList();
                }
            } else {
                // Load all requests (would need a new DAO method for this)
                requests = cancellationRequestDAO.getAllPendingRequests();
            }

            requestsList.clear();
            requestsList.addAll(requests);
            requestsTable.setItems(requestsList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko užkrauti užklausų: " + e.getMessage());
        }
    }

    @FXML
    private void handleApprove() {
        CancellationRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užklausą patvirtinimui!");
            return;
        }

        if (selected.getStatus() != CancellationStatus.PENDING) {
            AlertHelper.showWarning("Įspėjimas", "Galima patvirtinti tik laukiančias užklausas!");
            return;
        }

        // Show confirmation dialog with note option
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Patvirtinti atšaukimą");
        dialog.setHeaderText(String.format(
            "Patvirtinti užsakymo #%d atšaukimą?\n\nKlientas: %s\nPriežastis: %s",
            selected.getOrderId(), selected.getClientName(), selected.getReason()));
        dialog.setContentText("Pastaba klientui (neprivaloma):");

        dialog.showAndWait().ifPresent(note -> {
            try {
                // Approve cancellation request
                if (cancellationRequestDAO.approveCancellationRequest(
                        selected.getId(),
                        SessionManager.getInstance().getCurrentUser().getId(),
                        note.trim().isEmpty() ? "Patvirtinta" : note.trim())) {

                    // Update order status to CANCELLED
                    Order order = orderDAO.getOrderById(selected.getOrderId());
                    if (order != null) {
                        order.updateStatus(OrderStatus.CANCELLED);
                        orderDAO.updateOrder(order);
                    }

                    AlertHelper.showSuccess("Sėkmė",
                        String.format("Užsakymo #%d atšaukimas patvirtintas!\n\n" +
                            "Klientas gaus pranešimą apie sprendimą.",
                            selected.getOrderId()));
                    loadRequests();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko patvirtinti užklausos!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Patvirtinimo klaida: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleReject() {
        CancellationRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užklausą atmetimui!");
            return;
        }

        if (selected.getStatus() != CancellationStatus.PENDING) {
            AlertHelper.showWarning("Įspėjimas", "Galima atmesti tik laukiančias užklausas!");
            return;
        }

        // Show confirmation dialog with reason requirement
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Atmesti atšaukimą");
        dialog.setHeaderText(String.format(
            "Atmesti užsakymo #%d atšaukimą?\n\nKlientas: %s\nPriežastis: %s",
            selected.getOrderId(), selected.getClientName(), selected.getReason()));
        dialog.setContentText("Atmetimo priežastis (privaloma):");

        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                AlertHelper.showError("Klaida", "Prašome nurodyti atmetimo priežastį!");
                return;
            }

            try {
                if (cancellationRequestDAO.rejectCancellationRequest(
                        selected.getId(),
                        SessionManager.getInstance().getCurrentUser().getId(),
                        reason.trim())) {

                    AlertHelper.showSuccess("Sėkmė",
                        String.format("Užsakymo #%d atšaukimas atmestas!\n\n" +
                            "Klientas gaus pranešimą apie sprendimą.",
                            selected.getOrderId()));
                    loadRequests();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko atmesti užklausos!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Atmetimo klaida: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleView() {
        CancellationRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite užklausą peržiūrai!");
            return;
        }

        // Show detailed information
        StringBuilder info = new StringBuilder();
        info.append("=== ATŠAUKIMO UŽKLAUSA #").append(selected.getId()).append(" ===\n\n");
        info.append("Užsakymas: #").append(selected.getOrderId()).append("\n");
        info.append("Restoranas: ").append(selected.getRestaurantName()).append("\n");
        info.append("Klientas: ").append(selected.getClientName()).append("\n\n");
        info.append("Priežastis:\n").append(selected.getReason()).append("\n\n");
        info.append("Statusas: ").append(selected.getStatus().getDisplayName()).append("\n");
        info.append("Pateikta: ").append(selected.getCreatedAt()).append("\n\n");

        if (selected.getReviewerName() != null) {
            info.append("Peržiūrėjo: ").append(selected.getReviewerName()).append("\n");
            info.append("Peržiūrėta: ").append(selected.getReviewedAt()).append("\n");
            if (selected.getReviewNote() != null) {
                info.append("\nPastaba:\n").append(selected.getReviewNote()).append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atšaukimo užklausos informacija");
        alert.setHeaderText(null);
        alert.setContentText(info.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        loadRequests();
    }
}
