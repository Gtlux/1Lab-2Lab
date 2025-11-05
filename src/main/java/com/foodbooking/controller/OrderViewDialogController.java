package com.foodbooking.controller;

import com.foodbooking.dao.OrderItemDAO;
import com.foodbooking.model.Order;
import com.foodbooking.model.OrderItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OrderViewDialogController implements Initializable {

    @FXML
    private Label orderIdLabel;

    @FXML
    private Label clientLabel;

    @FXML
    private Label restaurantLabel;

    @FXML
    private Label driverLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label notesLabel;

    @FXML
    private Label createdLabel;

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

    private OrderItemDAO orderItemDAO = new OrderItemDAO();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    public void setOrder(Order order) {
        orderIdLabel.setText("#" + order.getId());
        clientLabel.setText(order.getClientName() != null ? order.getClientName() : "N/A");
        restaurantLabel.setText(order.getRestaurantName() != null ? order.getRestaurantName() : "N/A");
        driverLabel.setText(order.getDriverName() != null ? order.getDriverName() : "Nepriskirtas");
        statusLabel.setText(order.getStatus().getDisplayName());
        addressLabel.setText(order.getDeliveryAddress());
        notesLabel.setText(order.getNotes() != null && !order.getNotes().isEmpty() ? order.getNotes() : "Nėra");
        createdLabel.setText(order.getCreatedAt().format(dateFormatter));
        totalLabel.setText(String.format("%.2f €", order.getTotalAmount()));

        List<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(order.getId());
        orderItemsTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) orderIdLabel.getScene().getWindow();
        stage.close();
    }
}
