package service;

import DAO.ReservationDAO;
import DAO.RoomDAO;
import javax.sql.DataSource;
import model.Reservation;
import model.Room;

import java.sql.SQLException;
import java.util.UUID;
import java.util.List;
import java.sql.Connection;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final DataSource dataSource;

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO, DataSource dataSource) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.dataSource = dataSource;
    }

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.dataSource = null;
    }

    public void addReservation(Reservation reservation) throws SQLException {
        if (reservation.getReservationNumber() == null || reservation.getReservationNumber().trim().isEmpty()) {
            reservation.setReservationNumber("R-" + reservation.getHotelId() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }
        if (reservation.getRoomCategory() == null || reservation.getRoomCategory().trim().isEmpty()) {
            Room room = roomDAO.getRoomByRoomNumber(reservation.getRoomNumber());
            if (room != null) {
                reservation.setRoomCategory(room.getRoomCategory());
            }
        }
        if (dataSource == null) {
            reservationDAO.createReservation(reservation);
            roomDAO.setRoomReservation(reservation.getRoomNumber(), reservation.getReservationNumber(), true);
            return;
        }
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            ReservationDAO rdao = new ReservationDAO(conn);
            RoomDAO roomDaoConn = new RoomDAO(conn);
            rdao.createReservation(reservation);
            roomDaoConn.setRoomReservation(reservation.getRoomNumber(), reservation.getReservationNumber(), true);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public void cancelReservation(String reservationNumber, String cancellationType) throws SQLException {
        Reservation reservation = reservationDAO.getReservationByNumber(reservationNumber);
        if (reservation != null && !reservation.isCancelled()) {
            reservationDAO.cancelReservation(reservationNumber, cancellationType);
            roomDAO.setRoomReservation(reservation.getRoomNumber(), null, false);
        }
    }


    public List<Room> getAvailableRoomsByHotelId(int hotelId) throws SQLException {
        return roomDAO.getAvailableRoomsByHotelId(hotelId);
    }

    public void updateRoomStatus(Room room) throws SQLException {
        roomDAO.updateRoomStatus(room.getRoomNumber(), room.getIsOccupied());
    }

    public List<Reservation> getReservationsByHotelId(int hotelId) throws SQLException {
        return reservationDAO.getReservationsByHotelId(hotelId);
    }
}
