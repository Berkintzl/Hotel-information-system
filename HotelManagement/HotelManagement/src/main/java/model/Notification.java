package model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private int hotelId;
    private String message;
    private Timestamp createdAt;

    public Notification(int id, int hotelId, String message, Timestamp createdAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

}
