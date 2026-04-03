package com.hotel.dao;

import com.hotel.model.ActiveReservation;
import com.hotel.model.Reservation;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean makeReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (guest_id, room_id, check_in_date, check_out_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, reservation.getGuestId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(5, "ACTIVE");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelReservation(int reservationId, int roomId) {
        // Need to do this in a transaction preferably, or sequentially for simple setup
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // 1. Update reservation status
            String updateResStr = "UPDATE reservations SET status = 'CANCELLED' WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateResStr)) {
                pstmt.setInt(1, reservationId);
                pstmt.executeUpdate();
            }

            // 2. Mark room available
            String updateRoomStr = "UPDATE rooms SET is_available = true WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateRoomStr)) {
                pstmt.setInt(1, roomId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<ActiveReservation> getAllReservations() {
        List<ActiveReservation> list = new ArrayList<>();
        String sql = "SELECT * FROM active_reservations_view ORDER BY reservation_id DESC";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ActiveReservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("guest_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
