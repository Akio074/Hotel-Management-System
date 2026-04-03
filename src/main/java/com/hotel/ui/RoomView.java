package com.hotel.ui;

import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class RoomView extends BorderPane {

    private final RoomDAO roomDAO = new RoomDAO();
    private TableView<Room> table;
    private ObservableList<Room> roomData;

    public RoomView() {
        setPadding(new Insets(10));
        
        VBox formBox = createForm();
        setLeft(formBox);

        table = new TableView<>();
        setupTable();
        setCenter(table);

        loadData();
    }

    private VBox createForm() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(10, 20, 10, 10));
        vbox.setPrefWidth(300);

        Label title = new Label("Register New Room");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        TextField numberField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Single", "Double", "Suite", "Deluxe");
        typeBox.getSelectionModel().selectFirst();
        TextField priceField = new TextField();

        grid.add(new Label("Room Number:"), 0, 0);
        grid.add(numberField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Price ($):"), 0, 2);
        grid.add(priceField, 1, 2);

        Button saveBtn = new Button("Register Room");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            if (numberField.getText().isEmpty() || priceField.getText().isEmpty()) {
                App.showAlert(Alert.AlertType.ERROR, "Error", "Fill required fields.");
                return;
            }
            try {
                double price = Double.parseDouble(priceField.getText());
                Room r = new Room(0, numberField.getText(), typeBox.getValue(), price, true);
                if (roomDAO.addRoom(r)) {
                    App.showAlert(Alert.AlertType.INFORMATION, "Success", "Room registered.");
                    numberField.clear(); priceField.clear();
                    loadData();
                } else {
                    App.showAlert(Alert.AlertType.ERROR, "Error", "Failed to register room.");
                }
            } catch (NumberFormatException ex) {
                App.showAlert(Alert.AlertType.ERROR, "Error", "Invalid price.");
            }
        });

        vbox.getChildren().addAll(title, grid, saveBtn);
        return vbox;
    }

    private void setupTable() {
        TableColumn<Room, String> numCol = new TableColumn<>("Room No");
        numCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Room, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Room, Boolean> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        table.getColumns().addAll(numCol, typeCol, priceCol, availCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() {
        roomData = FXCollections.observableArrayList(roomDAO.getAllRooms());
        table.setItems(roomData);
    }
}
