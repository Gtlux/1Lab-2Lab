package com.foodbooking.controller;

import com.foodbooking.dao.RestaurantDAO;
import com.foodbooking.dao.UserDAO;
import com.foodbooking.model.Restaurant;
import com.foodbooking.model.User;
import com.foodbooking.model.UserRole;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserDialogController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private ComboBox<Restaurant> restaurantComboBox;

    @FXML
    private VBox restaurantBox;

    @FXML
    private CheckBox activeCheckBox;

    private UserDAO userDAO = new UserDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private User user;
    private boolean editMode = false;
    private boolean saved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate role combo box
        roleComboBox.getItems().addAll(UserRole.values());

        // Show restaurant combo when RESTAURANT_OWNER is selected
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == UserRole.RESTAURANT_OWNER) {
                loadRestaurants();
                restaurantBox.setVisible(true);
                restaurantBox.setManaged(true);
            } else {
                restaurantBox.setVisible(false);
                restaurantBox.setManaged(false);
            }
        });
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
        restaurantComboBox.getItems().clear();
        restaurantComboBox.getItems().addAll(restaurants);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (editMode) {
            passwordField.setPromptText("Palikite tuščią, jei nenorite keisti");
        }
    }

    public void setUser(User user) {
        this.user = user;
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        fullNameField.setText(user.getFullName());
        phoneField.setText(user.getPhoneNumber());
        roleComboBox.setValue(user.getRole());
        activeCheckBox.setSelected(user.isActive());

        if (user.getRestaurantId() != null) {
            Restaurant restaurant = restaurantDAO.getRestaurantById(user.getRestaurantId());
            if (restaurant != null) {
                restaurantComboBox.setValue(restaurant);
            }
        }
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        UserRole role = roleComboBox.getValue();

        // Validation
        if (!ValidationHelper.isNotEmpty(username) || !ValidationHelper.isNotEmpty(email) ||
            !ValidationHelper.isNotEmpty(fullName) || !ValidationHelper.isNotEmpty(phone) || role == null) {
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

        if (!editMode && !ValidationHelper.isValidPassword(password)) {
            AlertHelper.showError("Klaida", "Slaptažodis turi būti bent 6 simbolių ilgio!");
            return;
        }

        try {
            if (editMode) {
                // Update existing user
                user.setUsername(username);
                if (!password.isEmpty()) {
                    user.setPassword(password);
                }
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPhoneNumber(phone);
                user.setRole(role);
                user.setActive(activeCheckBox.isSelected());

                if (role == UserRole.RESTAURANT_OWNER && restaurantComboBox.getValue() != null) {
                    user.setRestaurantId(restaurantComboBox.getValue().getId());
                } else {
                    user.setRestaurantId(null);
                }

                boolean success = password.isEmpty() ?
                        userDAO.updateUser(user) :
                        userDAO.updateUser(user) && userDAO.updatePassword(user.getId(), password);

                if (success) {
                    AlertHelper.showSuccess("Sėkmė", "Vartotojas sėkmingai atnaujintas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko atnaujinti vartotojo!");
                }
            } else {
                // Create new user
                User newUser = new User(username, password, email, fullName, phone, role);
                newUser.setActive(activeCheckBox.isSelected());

                if (role == UserRole.RESTAURANT_OWNER && restaurantComboBox.getValue() != null) {
                    newUser.setRestaurantId(restaurantComboBox.getValue().getId());
                }

                if (userDAO.createUser(newUser)) {
                    AlertHelper.showSuccess("Sėkmė", "Vartotojas sėkmingai sukurtas!");
                    saved = true;
                    closeDialog();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko sukurti vartotojo!");
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
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
