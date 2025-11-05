package com.foodbooking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Maisto Rezervavimo Sistema - Prisijungimas");
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        com.foodbooking.util.DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
