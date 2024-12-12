package service;

import DAO.HotelDAO;
import model.Hotel;

import java.sql.SQLException;
import java.util.List;

public class HotelService {
    private final HotelDAO hotelDAO;

    public HotelService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }



    public int createHotel(String name, String address) throws SQLException {
        Hotel hotel = new Hotel(0, name, address); // 0 as a placeholder for the ID
        return hotelDAO.createHotel(hotel);
    }

    public List<Hotel> getAllHotels() throws SQLException {
        return hotelDAO.getAllHotels();
    }

    public Hotel getHotelById(int id) throws SQLException {
        return hotelDAO.getHotelById(id);
    }
}
