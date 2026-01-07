package model;

public class Room {
    private int id;
    private String roomCategory;
    private boolean isOccupied;
    private String roomNumber;
    private int hotelId;
    private String reservationNumber;

    public Room(int id, String roomCategory, boolean isOccupied, String roomNumber, int hotelId, String reservationNumber) {
        this.id = id;
        this.roomCategory = roomCategory;
        this.isOccupied = isOccupied;
        this.roomNumber = roomNumber;
        this.hotelId = hotelId;
        this.reservationNumber = reservationNumber;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }

    public boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getStatus() {
        return isOccupied ? "Occupied" : "Available";
    }
    @Override
    public String toString() {
        return roomNumber + " - " + roomCategory + " (" + getStatus() + ")";
    }
}
