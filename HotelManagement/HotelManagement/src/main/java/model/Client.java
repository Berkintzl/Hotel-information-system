package model;

public class Client {
    private int id;
    private String name;
    private String email;
    private int hotelId;

    public Client(int id, String name, String email, int hotelId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hotelId = hotelId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
}
