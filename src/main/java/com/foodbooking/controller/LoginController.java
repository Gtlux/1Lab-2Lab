package com.foodbooking.controller;

import com.foodbooking.dao.UserDAO;
import com.foodbooking.model.User;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.DatabaseConnection;
import com.foodbooking.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showError("Klaida", "Užpildykite visus laukus!");
            return;
        }

        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            AlertHelper.showError("Duomenų bazės klaida",
                "Nepavyko prisijungti prie duomenų bazės!\n\n" +
                "Patikrinkite:\n" +
                "1. Ar paleista XAMPP su MySQL\n" +
                "2. Ar sukurta duomenų bazė (database/schema.sql)\n" +
                "3. Ar teisingi prisijungimo duomenys");
            return;
        }

        try {
            // Authenticate user
            User user = userDAO.authenticate(username, password);

            if (user != null) {
                if (!user.isActive()) {
                    AlertHelper.showError("Klaida", "Šis vartotojas yra neaktyvus!");
                    return;
                }

                // Set current user in session
                SessionManager.getInstance().setCurrentUser(user);

                // Load main window
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 700));
                stage.setTitle("Maisto Rezervavimo Sistema - " + user.getFullName());
            } else {
                AlertHelper.showError("Klaida", "Neteisingas vartotojo vardas arba slaptažodis!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Prisijungimo klaida: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 700));
            stage.setTitle("Registracija");
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti registracijos lango: " + e.getMessage());
        }
    }
}
