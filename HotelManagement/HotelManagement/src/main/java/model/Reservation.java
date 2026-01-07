package model;

import java.sql.Date;

public class Reservation {
    private int id;
    private int clientId;
    private String reservationNumber;
    private String roomNumber;
    private String cancellationType;
    private String roomCategory;
    private Date startDate;
    private Date endDate;
    private boolean isCancelled;
    private int hotelId;

    public Reservation(int id, int clientId, String reservationNumber, String roomNumber, String cancellationType, String roomCategory, Date startDate, Date endDate, boolean isCancelled, int hotelId) {
        this.id = id;
        this.clientId = clientId;
        this.reservationNumber = reservationNumber;
        this.roomNumber = roomNumber;
        this.cancellationType = cancellationType;
        this.roomCategory = roomCategory;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCancelled = isCancelled;
        this.hotelId = hotelId;
    }

    // Getter ve setter yöntemleri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCancellationType() {
        return cancellationType;
    }

    public void setCancellationType(String cancellationType) {
        this.cancellationType = cancellationType;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
}
