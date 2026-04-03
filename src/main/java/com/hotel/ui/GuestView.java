package com.hotel.ui;

import com.hotel.dao.GuestDAO;
import com.hotel.model.Guest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GuestView extends BorderPane {

    private final GuestDAO guestDAO = new GuestDAO();
    private TableView<Guest> table;
    private ObservableList<Guest> guestData;

    public GuestView() {
        setPadding(new Insets(10));
        
        // Form on Left
        VBox formBox = createForm();
        setLeft(formBox);

        // Table in Center
        table = new TableView<>();
        setupTable();
        setCenter(table);

        loadData();
    }

    private VBox createForm() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(10, 20, 10, 10));
        vbox.setPrefWidth(300);

        Label title = new Label("Register New Guest");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        Button saveBtn = new Button("Register Guest");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                App.showAlert(Alert.AlertType.ERROR, "Error", "Name cannot be empty.");
                return;
            }
            Guest g = new Guest(0, nameField.getText(), phoneField.getText(), emailField.getText());
            if (guestDAO.addGuest(g)) {
                App.showAlert(Alert.AlertType.INFORMATION, "Success", "Guest registered.");
                nameField.clear(); phoneField.clear(); emailField.clear();
                loadData();
            } else {
                App.showAlert(Alert.AlertType.ERROR, "Error", "Failed to register guest.");
            }
        });

        vbox.getChildren().addAll(title, grid, saveBtn);
        return vbox;
    }

    private void setupTable() {
        TableColumn<Guest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Guest, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Guest, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Guest, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() {
        guestData = FXCollections.observableArrayList(guestDAO.getAllGuests());
        table.setItems(guestData);
    }
}
