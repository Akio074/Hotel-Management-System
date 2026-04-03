package com.hotel.ui;

import com.hotel.util.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        if (!DatabaseConnection.connect()) {
            System.out.println("No database connection established. Check if PostgreSQL is running and credentials are correct. Exiting...");
            System.exit(0);
        }

        primaryStage.setTitle("Hotel Management System");

        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab guestTab = new Tab("Guests", new GuestView());
        Tab roomTab = new Tab("Rooms", new RoomView());
        Tab reservationTab = new Tab("Reservations", new ReservationView());

        tabPane.getTabs().addAll(guestTab, roomTab, reservationTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 900, 600);
        String css = getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> DatabaseConnection.disconnect());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
