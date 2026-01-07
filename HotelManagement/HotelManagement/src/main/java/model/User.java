package model;

public class User {
    private int id;
    private String username;
    private String password;
    private int roleId;
    private int hotelId;
    private String roleName;

    public User(int id, String username, String password, int roleId, int hotelId, String roleName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.hotelId = hotelId;
        this.roleName = roleName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
