package com.hotel.ui;

import com.hotel.dao.GuestDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.ActiveReservation;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ReservationView extends BorderPane {

    private final ReservationDAO resDAO = new ReservationDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private TableView<ActiveReservation> table;
    
    // Form fields
    private ComboBox<Guest> guestBox;
    private ComboBox<Room> roomBox;
    private DatePicker checkInPicker;
    private DatePicker checkOutPicker;

    public ReservationView() {
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
        vbox.setPrefWidth(350);

        Label title = new Label("Make a Reservation");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        guestBox = new ComboBox<>();
        roomBox = new ComboBox<>();
        guestBox.setPrefWidth(200);
        roomBox.setPrefWidth(200);
        
        checkInPicker = new DatePicker(LocalDate.now());
        checkOutPicker = new DatePicker(LocalDate.now().plusDays(1));
        checkInPicker.setPrefWidth(200);
        checkOutPicker.setPrefWidth(200);

        Button reloadBtn = new Button("↻ Refresh Lists");
        reloadBtn.setOnAction(e -> refreshDropDowns());

        grid.add(new Label("Guest:"), 0, 0);
        grid.add(guestBox, 1, 0);
        grid.add(new Label("Available Room:"), 0, 1);
        grid.add(roomBox, 1, 1);
        grid.add(new Label("Check In:"), 0, 2);
        grid.add(checkInPicker, 1, 2);
        grid.add(new Label("Check Out:"), 0, 3);
        grid.add(checkOutPicker, 1, 3);

        Button resBtn = new Button("Reserve");
        resBtn.setMaxWidth(Double.MAX_VALUE);
        resBtn.setOnAction(e -> handleReservation());

        Button cancelResBtn = new Button("Cancel Selected Reservation");
        cancelResBtn.setStyle("-fx-text-fill: red;");
        cancelResBtn.setMaxWidth(Double.MAX_VALUE);
        cancelResBtn.setOnAction(e -> handleCancellation());

        vbox.getChildren().addAll(title, reloadBtn, grid, resBtn, new Separator(), cancelResBtn);
        return vbox;
    }

    private void handleReservation() {
        Guest guest = guestBox.getValue();
        Room room = roomBox.getValue();
        LocalDate in = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();

        if (guest == null || room == null || in == null || out == null) {
            App.showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        if (out.isBefore(in) || out.isEqual(in)) {
            App.showAlert(Alert.AlertType.ERROR, "Error", "Check-out must be after check-in date.");
            return;
        }

        Reservation res = new Reservation(0, guest.getId(), room.getId(), in, out, "ACTIVE");
        if (resDAO.makeReservation(res)) {
            // Also set room to unavailable
            roomDAO.updateRoomAvailability(room.getId(), false);
            App.showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation successful!");
            loadData();
        } else {
            App.showAlert(Alert.AlertType.ERROR, "Error", "Reservation failed.");
        }
    }

    private void handleCancellation() {
        ActiveReservation selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            App.showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reservation to cancel.");
            return;
        }
        if ("CANCELLED".equals(selected.getStatus())) {
            App.showAlert(Alert.AlertType.INFORMATION, "Info", "This reservation is already cancelled.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setContentText("Are you sure you want to cancel the reservation for " + selected.getGuestName() + "?");
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                if (resDAO.cancelReservation(selected.getReservationId(), selected.getRoomId())) {
                    App.showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation cancelled.");
                    loadData();
                } else {
                    App.showAlert(Alert.AlertType.ERROR, "Error", "Cancellation failed.");
                }
            }
        });
    }

    private void setupTable() {
        TableColumn<ActiveReservation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        TableColumn<ActiveReservation, String> guestCol = new TableColumn<>("Guest Name");
        guestCol.setCellValueFactory(new PropertyValueFactory<>("guestName"));

        TableColumn<ActiveReservation, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        
        TableColumn<ActiveReservation, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<ActiveReservation, LocalDate> inCol = new TableColumn<>("Check In");
        inCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        TableColumn<ActiveReservation, LocalDate> outCol = new TableColumn<>("Check Out");
        outCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        
        TableColumn<ActiveReservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, guestCol, roomCol, typeCol, inCol, outCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void loadData() {
        refreshDropDowns();
        table.setItems(FXCollections.observableArrayList(resDAO.getAllReservations()));
    }
    
    private void refreshDropDowns() {
        guestBox.setItems(FXCollections.observableArrayList(guestDAO.getAllGuests()));
        roomBox.setItems(FXCollections.observableArrayList(roomDAO.getAvailableRooms()));
    }
}
