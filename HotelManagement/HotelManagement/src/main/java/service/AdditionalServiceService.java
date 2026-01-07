package service;

import DAO.AdditionalServiceDAO;
import model.AdditionalService;

import java.sql.SQLException;
import java.util.List;

public class AdditionalServiceService {
    private final AdditionalServiceDAO additionalServiceDAO;

    public AdditionalServiceService(AdditionalServiceDAO additionalServiceDAO) {
        this.additionalServiceDAO = additionalServiceDAO;
    }

    public void addAdditionalService(AdditionalService service) throws SQLException {
        additionalServiceDAO.createAdditionalService(service);
    }

    public List<AdditionalService> getAdditionalServicesByHotelId(int hotelId) throws SQLException {
        return additionalServiceDAO.getAdditionalServicesByHotelId(hotelId);
    }
}
