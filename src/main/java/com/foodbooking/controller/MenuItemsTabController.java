package com.foodbooking.controller;

import com.foodbooking.dao.MenuItemDAO;
import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.model.MenuItem;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MenuItemsTabController implements Initializable {

    @FXML
    private TableView<MenuItem> menuItemsTable;

    @FXML
    private TableColumn<MenuItem, Integer> idColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> descriptionColumn;

    @FXML
    private TableColumn<MenuItem, BigDecimal> priceColumn;

    @FXML
    private TableColumn<MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<MenuItem, String> restaurantColumn;

    @FXML
    private TableColumn<MenuItem, Boolean> availableColumn;

    @FXML
    private ComboBox<Restaurant> restaurantFilterComboBox;

    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private ObservableList<MenuItem> menuItemsList = FXCollections.observableArrayList();
    private Map<Integer, String> restaurantNamesCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Custom cell value factory for restaurant name
        restaurantColumn.setCellValueFactory(cellData -> {
            Integer restaurantId = cellData.getValue().getRestaurantId();
            String restaurantName = restaurantNamesCache.computeIfAbsent(restaurantId, id -> {
                Restaurant restaurant = restaurantDAO.getRestaurantById(id);
                return restaurant != null ? restaurant.getName() : "Unknown";
            });
            return new javafx.beans.property.SimpleStringProperty(restaurantName);
        });

        // Setup restaurant filter combo box
        restaurantFilterComboBox.setConverter(new StringConverter<Restaurant>() {
            @Override
            public String toString(Restaurant restaurant) {
                return restaurant != null ? restaurant.getName() : "";
            }

            @Override
            public Restaurant fromString(String string) {
                return null;
            }
        });

        loadRestaurantFilter();
        loadMenuItems();
    }

    private void loadRestaurantFilter() {
        List<Restaurant> restaurants;
        if (SessionManager.getInstance().isRestaurantOwner()) {
            restaurants = restaurantDAO.getRestaurantsByOwnerId(
                    SessionManager.getInstance().getCurrentUser().getId());
        } else {
            restaurants = restaurantDAO.getAllRestaurants();
        }
        restaurantFilterComboBox.getItems().clear();
        restaurantFilterComboBox.getItems().addAll(restaurants);
    }

    private void loadMenuItems() {
        try {
            List<MenuItem> menuItems;
            if (SessionManager.getInstance().isRestaurantOwner()) {
                // Restaurant owners see only their restaurant's menu items
                List<Restaurant> ownRestaurants = restaurantDAO.getRestaurantsByOwnerId(
                        SessionManager.getInstance().getCurrentUser().getId());
                menuItems = FXCollections.observableArrayList();
                for (Restaurant restaurant : ownRestaurants) {
                    menuItems.addAll(menuItemDAO.getMenuItemsByRestaurantId(restaurant.getId()));
                }
            } else {
                menuItems = menuItemDAO.getAllMenuItems();
            }
            menuItemsList.clear();
            menuItemsList.addAll(menuItems);
            menuItemsTable.setItems(menuItemsList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko užkrauti meniu: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuItemDialog.fxml"));
            Parent root = loader.load();

            MenuItemDialogController controller = loader.getController();
            controller.setEditMode(false);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Pridėti naują meniu elementą");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadMenuItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite meniu elementą redagavimui!");
            return;
        }

        // Restaurant owners can only edit their own menu items
        if (SessionManager.getInstance().isRestaurantOwner()) {
            Restaurant restaurant = restaurantDAO.getRestaurantById(selected.getRestaurantId());
            if (restaurant == null || !restaurant.getOwnerId().equals(
                    SessionManager.getInstance().getCurrentUser().getId())) {
                AlertHelper.showError("Klaida", "Galite redaguoti tik savo restorano meniu!");
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuItemDialog.fxml"));
            Parent root = loader.load();

            MenuItemDialogController controller = loader.getController();
            controller.setEditMode(true);
            controller.setMenuItem(selected);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Redaguoti meniu elementą");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadMenuItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite meniu elementą šalinimui!");
            return;
        }

        // Restaurant owners can only delete their own menu items
        if (SessionManager.getInstance().isRestaurantOwner()) {
            Restaurant restaurant = restaurantDAO.getRestaurantById(selected.getRestaurantId());
            if (restaurant == null || !restaurant.getOwnerId().equals(
                    SessionManager.getInstance().getCurrentUser().getId())) {
                AlertHelper.showError("Klaida", "Galite ištrinti tik savo restorano meniu!");
                return;
            }
        }

        if (AlertHelper.showConfirmation("Patvirtinimas",
                "Ar tikrai norite ištrinti meniu elementą: " + selected.getName() + "?")) {
            try {
                if (menuItemDAO.deleteMenuItem(selected.getId())) {
                    AlertHelper.showSuccess("Sėkmė", "Meniu elementas sėkmingai ištrintas!");
                    loadMenuItems();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko ištrinti meniu elemento!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Šalinimo klaida: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadMenuItems();
    }

    @FXML
    private void handleFilter() {
        Restaurant selectedRestaurant = restaurantFilterComboBox.getValue();
        if (selectedRestaurant == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite restoraną filtravimui!");
            return;
        }

        try {
            List<MenuItem> menuItems = menuItemDAO.getMenuItemsByRestaurantId(selectedRestaurant.getId());
            menuItemsList.clear();
            menuItemsList.addAll(menuItems);
            menuItemsTable.setItems(menuItemsList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Filtravimo klaida: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        restaurantFilterComboBox.setValue(null);
        loadMenuItems();
    }
}
