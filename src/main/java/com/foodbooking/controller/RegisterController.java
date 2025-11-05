package com.foodbooking.controller;

import com.foodbooking.dao.UserDAO;
import com.foodbooking.model.User;
import com.foodbooking.model.UserRole;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<UserRole> roleComboBox;

    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().addAll(UserRole.CLIENT, UserRole.RESTAURANT_OWNER, UserRole.DRIVER);
        roleComboBox.setValue(UserRole.CLIENT);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        UserRole role = roleComboBox.getValue();

        String errorMessage = validateInput(username, password, confirmPassword, email, fullName, phone, role);
        if (errorMessage != null) {
            AlertHelper.showError("Validacijos klaida", errorMessage);
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            AlertHelper.showError("Klaida", "Vartotojo vardas jau užimtas!");
            return;
        }

        try {
            User newUser = new User(username, password, email, fullName, phone, role);

            if (userDAO.createUser(newUser)) {
                AlertHelper.showSuccess("Sėkmė", "Registracija sėkminga! Dabar galite prisijungti.");
                handleBack();
            } else {
                AlertHelper.showError("Klaida", "Nepavyko sukurti vartotojo!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Registracijos klaida: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Prisijungimas");
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko grįžti: " + e.getMessage());
        }
    }

    private String validateInput(String username, String password, String confirmPassword,
                                  String email, String fullName, String phone, UserRole role) {
        if (!ValidationHelper.isNotEmpty(username)) {
            return "Vartotojo vardas privalomas!";
        }

        if (!ValidationHelper.isValidPassword(password)) {
            return "Slaptažodis turi būti bent 6 simbolių ilgio!";
        }

        if (!password.equals(confirmPassword)) {
            return "Slaptažodžiai nesutampa!";
        }

        if (!ValidationHelper.isValidEmail(email)) {
            return "Neteisingas el. pašto formatas!";
        }

        if (!ValidationHelper.isNotEmpty(fullName)) {
            return "Pilnas vardas privalomas!";
        }

        if (!ValidationHelper.isValidPhone(phone)) {
            return "Neteisingas telefono numerio formatas!";
        }

        if (role == null) {
            return "Pasirinkite vartotojo tipą!";
        }

        return null;
    }
}
