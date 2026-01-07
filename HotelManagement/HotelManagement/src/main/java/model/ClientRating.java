package model;

public class ClientRating {
    private int id;
    private int clientId;
    private int rating;
    private int hotelId;

    public ClientRating(int id, int clientId, int rating, int hotelId) {
        this.id = id;
        this.clientId = clientId;
        this.rating = rating;
        this.hotelId = hotelId;
    }

    // Getters and Setters
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
}
