package com.foodbooking.controller;

import com.foodbooking.model.User;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.DatabaseConnection;
import com.foodbooking.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label userInfoLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private TabPane mainTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userInfoLabel.setText("Prisijungęs: " + currentUser.getFullName());
            roleLabel.setText("Rolė: " + currentUser.getRole().getDisplayName());

            // Load tabs based on user role
            loadTabs(currentUser);
        }
    }

    private void loadTabs(User user) {
        try {
            if (user.isAdministrator()) {
                // Admin sees all tabs
                addTab("Vartotojai", "/fxml/UsersTab.fxml");
                addTab("Restoranai", "/fxml/RestaurantsTab.fxml");
                addTab("Meniu", "/fxml/MenuItemsTab.fxml");
                addTab("Užsakymai", "/fxml/OrdersTab.fxml");
                addTab("Atšaukimo užklausos", "/fxml/CancellationRequestsTab.fxml");
            } else if (user.isRestaurantOwner()) {
                // Restaurant owner sees their restaurant's menu, orders, and cancellation requests
                addTab("Mano restoranas", "/fxml/RestaurantsTab.fxml");
                addTab("Meniu", "/fxml/MenuItemsTab.fxml");
                addTab("Užsakymai", "/fxml/OrdersTab.fxml");
                addTab("Atšaukimo užklausos", "/fxml/CancellationRequestsTab.fxml");
            } else if (user.isDriver()) {
                // Driver sees available orders and their orders
                addTab("Užsakymai", "/fxml/OrdersTab.fxml");
            } else if (user.isClient()) {
                // Client can view restaurants and menu (read-only), use shopping cart, and manage their orders
                addTab("Restoranai", "/fxml/RestaurantsTab.fxml");
                addTab("Meniu", "/fxml/MenuItemsTab.fxml");
                addTab("🛒 Krepšelis", "/fxml/ShoppingCartTab.fxml");
                addTab("Mano užsakymai", "/fxml/OrdersTab.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko užkrauti interfeiso: " + e.getMessage());
        }
    }

    private void addTab(String title, String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent content = loader.load();

        Tab tab = new Tab(title);
        tab.setContent(content);
        mainTabPane.getTabs().add(tab);
    }

    @FXML
    private void handleLogout() {
        if (AlertHelper.showConfirmation("Atsijungti", "Ar tikrai norite atsijungti?")) {
            SessionManager.getInstance().logout();
            DatabaseConnection.closeConnection();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) mainTabPane.getScene().getWindow();
                stage.setScene(new Scene(root, 600, 500));
                stage.setTitle("Prisijungimas");
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Nepavyko grįžti į prisijungimo langą: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExit() {
        if (AlertHelper.showConfirmation("Išeiti", "Ar tikrai norite išeiti iš programos?")) {
            DatabaseConnection.closeConnection();
            System.exit(0);
        }
    }

    @FXML
    private void handleAbout() {
        AlertHelper.showInfo("Apie programą",
            "Maisto Rezervavimo Sistema\n\n" +
            "Versija: 1.0\n" +
            "Sukurta naudojant: Java, JavaFX, MySQL\n\n" +
            "Sistema leidžia valdyti maisto užsakymus, \n" +
            "restoranų meniu ir pristatymą.");
    }
}
