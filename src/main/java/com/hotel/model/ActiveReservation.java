package com.hotel.model;

import java.time.LocalDate;

public class ActiveReservation {
    private int reservationId;
    private int guestId;
    private String guestName;
    private int roomId;
    private String roomNumber;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;

    public ActiveReservation(int reservationId, int guestId, String guestName, int roomId, String roomNumber, String roomType, LocalDate checkInDate, LocalDate checkOutDate, String status) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public int getReservationId() { return reservationId; }
    public int getGuestId() { return guestId; }
    public String getGuestName() { return guestName; }
    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public String getStatus() { return status; }
}
