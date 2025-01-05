package service;

import DAO.ReservationDAO;
import DAO.RoomDAO;
import model.Reservation;
import model.Room;

import java.sql.SQLException;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
    }

    public void addReservation(Reservation reservation) throws SQLException {
        reservationDAO.createReservation(reservation);
    }

    public void cancelReservation(String reservationNumber, String cancellationType) throws SQLException {
        Reservation reservation = reservationDAO.getReservationByNumber(reservationNumber);
        if (reservation != null && !reservation.isCancelled()) {
            reservationDAO.cancelReservation(reservationNumber, cancellationType);
            Room room = roomDAO.getRoomByRoomNumber(reservation.getRoomNumber());
            if (room != null) {
                room.setIsOccupied(false);
                room.setReservationNumber(null);
                roomDAO.updateRoomStatus(room.getRoomNumber(), false);
            }
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
