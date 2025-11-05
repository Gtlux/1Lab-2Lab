package com.foodbooking.controller;

import com.foodbooking.dao.MenuItemDAO;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MenuItemsTabController implements Initializable {

    @FXML
    private TableView<com.foodbooking.model.MenuItem> menuItemsTable;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, Integer> idColumn;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, String> nameColumn;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, String> descriptionColumn;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, BigDecimal> priceColumn;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, String> restaurantColumn;

    @FXML
    private TableColumn<com.foodbooking.model.MenuItem, Boolean> availableColumn;

    @FXML
    private ComboBox<Restaurant> restaurantFilterComboBox;

    @FXML
    private ComboBox<String> categoryFilterComboBox;

    @FXML
    private ComboBox<String> availabilityFilterComboBox;

    @FXML
    private TextField nameFilterField;

    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addToCartButton;

    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private ObservableList<com.foodbooking.model.MenuItem> menuItemsList = FXCollections.observableArrayList();
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

        // Hide all editing buttons for clients (they can only view menu)
        if (SessionManager.getInstance().isClient()) {
            addButton.setVisible(false);
            addButton.setManaged(false);
            editButton.setVisible(false);
            editButton.setManaged(false);
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            // Show add to cart button for clients only
            addToCartButton.setVisible(true);
            addToCartButton.setManaged(true);
        }

        setupFilters();
        loadMenuItems();
    }

    private void setupFilters() {
        // Setup restaurant filter
        loadRestaurantFilter();

        // Setup category filter
        categoryFilterComboBox.getItems().addAll(
            "Visi",
            "Užkandžiai",
            "Sriubos",
            "Pagrindiniai patiekalai",
            "Desertai",
            "Gėrimai",
            "Alkoholiniai gėrimai",
            "Salotos",
            "Picos",
            "Suši",
            "Kita"
        );
        categoryFilterComboBox.setValue("Visi");

        // Setup availability filter
        availabilityFilterComboBox.getItems().addAll("Visi", "Prieinami", "Neprieinami");
        availabilityFilterComboBox.setValue("Visi");
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
            List<com.foodbooking.model.MenuItem> menuItems;
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
        // Clients cannot add menu items
        if (SessionManager.getInstance().isClient()) {
            AlertHelper.showError("Klaida", "Klientai negali pridėti meniu elementų!");
            return;
        }

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
        com.foodbooking.model.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite meniu elementą redagavimui!");
            return;
        }

        // Clients cannot edit menu items
        if (SessionManager.getInstance().isClient()) {
            AlertHelper.showError("Klaida", "Klientai negali redaguoti meniu elementų!");
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
        com.foodbooking.model.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite meniu elementą šalinimui!");
            return;
        }

        // Clients cannot delete menu items
        if (SessionManager.getInstance().isClient()) {
            AlertHelper.showError("Klaida", "Klientai negali ištrinti meniu elementų!");
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
        try {
            // Start with all menu items based on user role
            List<com.foodbooking.model.MenuItem> menuItems;
            if (SessionManager.getInstance().isRestaurantOwner()) {
                List<Restaurant> ownRestaurants = restaurantDAO.getRestaurantsByOwnerId(
                        SessionManager.getInstance().getCurrentUser().getId());
                menuItems = new ArrayList<>();
                for (Restaurant restaurant : ownRestaurants) {
                    menuItems.addAll(menuItemDAO.getMenuItemsByRestaurantId(restaurant.getId()));
                }
            } else {
                menuItems = menuItemDAO.getAllMenuItems();
            }

            // Apply filters
            List<com.foodbooking.model.MenuItem> filteredItems = new ArrayList<>(menuItems);

            // Filter by restaurant
            Restaurant selectedRestaurant = restaurantFilterComboBox.getValue();
            if (selectedRestaurant != null) {
                filteredItems.removeIf(item -> !item.getRestaurantId().equals(selectedRestaurant.getId()));
            }

            // Filter by category
            String selectedCategory = categoryFilterComboBox.getValue();
            if (selectedCategory != null && !selectedCategory.equals("Visi")) {
                filteredItems.removeIf(item -> item.getCategory() == null ||
                    !item.getCategory().equalsIgnoreCase(selectedCategory));
            }

            // Filter by availability
            String selectedAvailability = availabilityFilterComboBox.getValue();
            if (selectedAvailability != null && !selectedAvailability.equals("Visi")) {
                boolean available = selectedAvailability.equals("Prieinami");
                filteredItems.removeIf(item -> item.getAvailable() != available);
            }

            // Filter by name
            String nameFilter = nameFilterField.getText();
            if (nameFilter != null && !nameFilter.trim().isEmpty()) {
                String lowerCaseFilter = nameFilter.toLowerCase().trim();
                filteredItems.removeIf(item -> !item.getName().toLowerCase().contains(lowerCaseFilter));
            }

            // Filter by price range
            String minPriceText = minPriceField.getText();
            String maxPriceText = maxPriceField.getText();

            if (minPriceText != null && !minPriceText.trim().isEmpty()) {
                try {
                    BigDecimal minPrice = new BigDecimal(minPriceText.trim());
                    filteredItems.removeIf(item -> item.getPrice().compareTo(minPrice) < 0);
                } catch (NumberFormatException e) {
                    AlertHelper.showError("Klaida", "Neteisingas minimalios kainos formatas!");
                    return;
                }
            }

            if (maxPriceText != null && !maxPriceText.trim().isEmpty()) {
                try {
                    BigDecimal maxPrice = new BigDecimal(maxPriceText.trim());
                    filteredItems.removeIf(item -> item.getPrice().compareTo(maxPrice) > 0);
                } catch (NumberFormatException e) {
                    AlertHelper.showError("Klaida", "Neteisingas maksimalios kainos formatas!");
                    return;
                }
            }

            // Update table
            menuItemsList.clear();
            menuItemsList.addAll(filteredItems);
            menuItemsTable.setItems(menuItemsList);

            AlertHelper.showInfo("Filtravimas", "Rasta " + filteredItems.size() + " meniu elementų.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Filtravimo klaida: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        restaurantFilterComboBox.setValue(null);
        categoryFilterComboBox.setValue("Visi");
        availabilityFilterComboBox.setValue("Visi");
        nameFilterField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        loadMenuItems();
    }

    @FXML
    private void handleAddToCart() {
        com.foodbooking.model.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite meniu elementą pridėjimui į krepšelį!");
            return;
        }

        if (!selected.getAvailable()) {
            AlertHelper.showError("Klaida", "Šis patiekalas šiuo metu neprieinamas!");
            return;
        }

        // Ask for quantity
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Kiekis");
        dialog.setHeaderText("Pridėti į krepšelį: " + selected.getName());
        dialog.setContentText("Kiekis:");

        dialog.showAndWait().ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    AlertHelper.showError("Klaida", "Kiekis turi būti teigiamas skaičius!");
                    return;
                }

                // Add to cart
                try {
                    SessionManager.getInstance().getShoppingCart().addItem(selected, quantity);
                    AlertHelper.showSuccess("Sėkmė",
                        String.format("%s (x%d) pridėta į krepšelį!\n\nKrepšelyje: %d prekė(-ių)",
                            selected.getName(), quantity,
                            SessionManager.getInstance().getShoppingCart().getItemCount()));
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Klaida", e.getMessage());
                }
            } catch (NumberFormatException e) {
                AlertHelper.showError("Klaida", "Neteisingas kiekio formatas!");
            }
        });
    }
}
