package service;

import DAO.ClientRatingDAO;
import model.ClientRating;

import java.sql.SQLException;
import java.util.List;

public class ClientRatingService {
    private final ClientRatingDAO clientRatingDAO;

    public ClientRatingService(ClientRatingDAO clientRatingDAO) {
        this.clientRatingDAO = clientRatingDAO;
    }

    public void addClientRating(ClientRating rating) throws SQLException {
        clientRatingDAO.createClientRating(rating);
    }

    public List<ClientRating> getClientRatingsByHotelId(int hotelId) throws SQLException {
        return clientRatingDAO.getClientRatingsByHotelId(hotelId);
    }
}
