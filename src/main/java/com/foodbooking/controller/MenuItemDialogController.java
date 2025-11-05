package com.foodbooking.controller;

import com.foodbooking.dao.MenuItemDAO;
import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.model.Restaurant;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import com.foodbooking.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MenuItemDialogController implements Initializable {

    @FXML
    private ComboBox<Restaurant> restaurantComboBox;

    @FXML
    private VBox restaurantBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField priceField;

    @FXML
    private TextField categoryField;

    @FXML
    private CheckBox availableCheckBox;

    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private com.foodbooking.model.MenuItem menuItem;
    private boolean editMode = false;
    private boolean saved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        loadRestaurants();
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants;
        if (SessionManager.getInstance().isRestaurantOwner()) {
            restaurants = restaurantDAO.getRestaurantsByOwnerId(
                    SessionManager.getInstance().getCurrentUser().getId());
            if (restaurants.size() == 1) {
                restaurantComboBox.setValue(restaurants.get(0));
                restaurantBox.setVisible(false);
                restaurantBox.setManaged(false);
            }
        } else {
            restaurants = restaurantDAO.getAllRestaurants();
        }
        restaurantComboBox.getItems().clear();
        restaurantComboBox.getItems().addAll(restaurants);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (editMode) {
            restaurantComboBox.setDisable(true);
        }
    }

    public void setMenuItem(com.foodbooking.model.MenuItem menuItem) {
        this.menuItem = menuItem;
        Restaurant restaurant = restaurantDAO.getRestaurantById(menuItem.getRestaurantId());
        if (restaurant != null) {
            restaurantComboBox.setValue(restaurant);
        }
        nameField.setText(menuItem.getName());
        descriptionArea.setText(menuItem.getDescription());
        priceField.setText(menuItem.getPrice().toString());
        categoryField.setText(menuItem.getCategory());
        availableCheckBox.setSelected(menuItem.isAvailable());
    }

    @FXML
    private void handleSave() {
        Restaurant restaurant = restaurantComboBox.getValue();
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceStr = priceField.getText().trim();
        String category = categoryField.getText().trim();

        if (restaurant == null) {
            AlertHelper.showError("Klaida", "Pasirinkite restoraną!");
            return;
        }

        if (!ValidationHelper.isNotEmpty(name) || !ValidationHelper.isNotEmpty(priceStr) ||
            !ValidationHelper.isNotEmpty(category)) {
            AlertHelper.showError("Klaida", "Užpildykite visus privalomus laukus!");
            return;
        }

        if (!ValidationHelper.isValidPrice(priceStr)) {
            AlertHelper.showError("Klaida", "Neteisingas kainos formatas!");
            return;
        }

        BigDecimal price = new BigDecimal(priceStr);

        try {
            if (editMode) {
                menuItem.setName(name);
                menuItem.setDescription(description);
                menuItem.setPrice(price);
                menuItem.setCategory(category);
                menuItem.setAvailable(availableCheckBox.isSelected());

                if (menuItemDAO.updateMenuItem(menuItem)) {
                    AlertHelper.showSuccess("Sėkmė", "Meniu elementas sėkmingai atnaujintas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko atnaujinti meniu elemento!");
                }
            } else {
                com.foodbooking.model.MenuItem newMenuItem = new com.foodbooking.model.MenuItem(restaurant.getId(), name, description, price, category);
                newMenuItem.setAvailable(availableCheckBox.isSelected());

                if (menuItemDAO.createMenuItem(newMenuItem)) {
                    AlertHelper.showSuccess("Sėkmė", "Meniu elementas sėkmingai sukurtas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko sukurti meniu elemento!");
                }
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
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
