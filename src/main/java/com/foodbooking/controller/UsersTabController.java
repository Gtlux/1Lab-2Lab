package com.foodbooking.controller;

import com.foodbooking.dao.UserDAO;
import com.foodbooking.model.User;
import com.foodbooking.model.UserRole;
import com.foodbooking.util.AlertHelper;
import com.foodbooking.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class UsersTabController implements Initializable {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> fullNameColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, UserRole> roleColumn;

    @FXML
    private TableColumn<User, Boolean> activeColumn;

    @FXML
    private TableColumn<User, LocalDateTime> createdAtColumn;

    @FXML
    private ComboBox<UserRole> roleFilterComboBox;

    private UserDAO userDAO = new UserDAO();
    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Populate role filter combo box
        roleFilterComboBox.getItems().addAll(UserRole.values());

        // Load data
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            usersList.clear();
            usersList.addAll(users);
            usersTable.setItems(usersList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko užkrauti vartotojų: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDialog.fxml"));
            Parent root = loader.load();

            UserDialogController controller = loader.getController();
            controller.setEditMode(false);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Pridėti naują vartotoją");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite vartotoją redagavimui!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDialog.fxml"));
            Parent root = loader.load();

            UserDialogController controller = loader.getController();
            controller.setEditMode(true);
            controller.setUser(selected);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Redaguoti vartotoją");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                loadUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Nepavyko atidaryti dialogo: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite vartotoją šalinimui!");
            return;
        }

        if (selected.getId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            AlertHelper.showError("Klaida", "Negalite ištrinti savo paties paskyros!");
            return;
        }

        if (AlertHelper.showConfirmation("Patvirtinimas",
                "Ar tikrai norite ištrinti vartotoją: " + selected.getFullName() + "?")) {
            try {
                if (userDAO.deleteUser(selected.getId())) {
                    AlertHelper.showSuccess("Sėkmė", "Vartotojas sėkmingai ištrintas!");
                    loadUsers();
                } else {
                    AlertHelper.showError("Klaida", "Nepavyko ištrinti vartotojo!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Klaida", "Šalinimo klaida: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    @FXML
    private void handleFilter() {
        UserRole selectedRole = roleFilterComboBox.getValue();
        if (selectedRole == null) {
            AlertHelper.showWarning("Įspėjimas", "Pasirinkite rolę filtravimui!");
            return;
        }

        try {
            List<User> users = userDAO.getUsersByRole(selectedRole);
            usersList.clear();
            usersList.addAll(users);
            usersTable.setItems(usersList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Klaida", "Filtravimo klaida: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        roleFilterComboBox.setValue(null);
        loadUsers();
    }
}
