package com.foodbooking.controller;

import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.model.Restaurant;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RestaurantsTabController implements Initializable {

    @FXML
    private TableView<Restaurant> restaurantsTable;

    @FXML
    private TableColumn<Restaurant, Integer> idColumn;

    @FXML
    private TableColumn<Restaurant, String> nameColumn;

    @FXML
    private TableColumn<Restaurant, String> addressColumn;

    @FXML
    private TableColumn<Restaurant, String> phoneColumn;

    @FXML
    private TableColumn<Restaurant, String> emailColumn;

    @FXML
    private TableColumn<Restaurant, String> descriptionColumn;

    @FXML
    private TableColumn<Restaurant, Boolean> activeColumn;

    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private ObservableList<Restaurant> restaurantsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Load data
        loadRestaurants();
    }

    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants;
            if (SessionManager.getInstance().isRestaurantOwner()) {
                // Restaurant owners see only their restaurants
                restaurants = restaurantDAO.getRestaurantsByOwnerId(
                        SessionManager.getInstance().getCurrentUser().getId());
            } else {
                // Admins and clients see all restaurants
                restaurants = restaurantDAO.getAllRestaurants();
            }
            restaurantsList.clear();
            restaurantsList.addAll(restaurants);
            restaurantsTable.setItems(restaurantsList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko užkrauti restoranų: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RestaurantDialog.fxml"));
            Parent root = loader.load();

            RestaurantDialogController controller = loader.getController();
            controller.setEditMode(false);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Pridėti naują restoraną");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadRestaurants();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        Restaurant selected = restaurantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite restoraną redagavimui!");
            return;
        }

        // Restaurant owners can only edit their own restaurants
        if (SessionManager.getInstance().isRestaurantOwner() &&
            !selected.getOwnerId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            AlertHelper.showError("Klaida", "Galite redaguoti tik savo restoraną!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RestaurantDialog.fxml"));
            Parent root = loader.load();

            RestaurantDialogController controller = loader.getController();
            controller.setEditMode(true);
            controller.setRestaurant(selected);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Redaguoti restoraną");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadRestaurants();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Restaurant selected = restaurantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite restoraną šalinimui!");
            return;
        }

        // Restaurant owners can only delete their own restaurants
        if (SessionManager.getInstance().isRestaurantOwner() &&
            !selected.getOwnerId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            AlertHelper.showError("Klaida", "Galite ištrinti tik savo restoraną!");
            return;
        }

        if (AlertHelper.showConfirmation("Patvirtinimas",
                "Ar tikrai norite ištrinti restoraną: " + selected.getName() + "?\n" +
                "Bus ištrinti ir visi jo meniu elementai!")) {
            try {
                if (restaurantDAO.deleteRestaurant(selected.getId())) {
                    AlertHelper.showSuccess("Sėkmė", "Restoranas sėkmingai ištrintas!");
                    loadRestaurants();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko ištrinti restorano!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Šalinimo klaida: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadRestaurants();
    }
}
