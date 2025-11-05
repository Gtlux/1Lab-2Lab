package com.foodbooking.controller;

import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.dao.UserDAO;
import com.foodbooking.model.Restaurant;
import com.foodbooking.model.User;
import com.foodbooking.model.UserRole;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RestaurantDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<User> ownerComboBox;

    @FXML
    private VBox ownerBox;

    @FXML
    private CheckBox activeCheckBox;

    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private UserDAO userDAO = new UserDAO();
    private Restaurant restaurant;
    private boolean editMode = false;
    private boolean saved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ownerComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() + " (" + user.getUsername() + ")" : "";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        if (SessionManager.getInstance().isRestaurantOwner()) {
            ownerBox.setVisible(false);
            ownerBox.setManaged(false);
        } else {
            loadOwners();
        }
    }

    private void loadOwners() {
        List<User> owners = userDAO.getUsersByRole(UserRole.RESTAURANT_OWNER);
        ownerComboBox.getItems().clear();
        ownerComboBox.getItems().addAll(owners);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        nameField.setText(restaurant.getName());
        addressField.setText(restaurant.getAddress());
        phoneField.setText(restaurant.getPhoneNumber());
        emailField.setText(restaurant.getEmail());
        descriptionArea.setText(restaurant.getDescription());
        activeCheckBox.setSelected(restaurant.isActive());

        if (!SessionManager.getInstance().isRestaurantOwner()) {
            User owner = userDAO.getUserById(restaurant.getOwnerId());
            if (owner != null) {
                ownerComboBox.setValue(owner);
            }
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (!ValidationHelper.isNotEmpty(name) || !ValidationHelper.isNotEmpty(address) ||
            !ValidationHelper.isNotEmpty(phone) || !ValidationHelper.isNotEmpty(email)) {
            AlertHelper.showError("Klaida", "Užpildykite visus privalomus laukus!");
            return;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            AlertHelper.showError("Klaida", "Neteisingas el. pašto formatas!");
            return;
        }

        if (!ValidationHelper.isValidPhone(phone)) {
            AlertHelper.showError("Klaida", "Neteisingas telefono numerio formatas!");
            return;
        }

        Integer ownerId;
        if (SessionManager.getInstance().isRestaurantOwner()) {
            ownerId = SessionManager.getInstance().getCurrentUser().getId();
        } else {
            if (ownerComboBox.getValue() == null) {
                AlertHelper.showError("Klaida", "Pasirinkite restorano savininką!");
                return;
            }
            ownerId = ownerComboBox.getValue().getId();
        }

        try {
            if (editMode) {
                restaurant.setName(name);
                restaurant.setAddress(address);
                restaurant.setPhoneNumber(phone);
                restaurant.setEmail(email);
                restaurant.setDescription(description);
                restaurant.setActive(activeCheckBox.isSelected());

                if (restaurantDAO.updateRestaurant(restaurant)) {
                    AlertHelper.showSuccess("Sėkmė", "Restoranas sėkmingai atnaujintas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko atnaujinti restorano!");
                }
            } else {
                Restaurant newRestaurant = new Restaurant(name, address, phone, email, description, ownerId);
                newRestaurant.setActive(activeCheckBox.isSelected());

                if (restaurantDAO.createRestaurant(newRestaurant)) {
                    AlertHelper.showSuccess("Sėkmė", "Restoranas sėkmingai sukurtas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko sukurti restorano!");
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
