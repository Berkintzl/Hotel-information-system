package model;

public class AdditionalService {
    private int id;
    private String serviceType;
    private String season;
    private int usageCount;
    private int hotelId; // Yeni alan

    public AdditionalService(int id, String serviceType, String season, int usageCount, int hotelId) {
        this.id = id;
        this.serviceType = serviceType;
        this.season = season;
        this.usageCount = usageCount;
        this.hotelId = hotelId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
}
