package web;

import model.AdditionalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AdditionalServiceService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/services")
public class AdditionalServiceRestController {
    private final AdditionalServiceService additionalServiceService;

    public AdditionalServiceRestController(AdditionalServiceService additionalServiceService) {
        this.additionalServiceService = additionalServiceService;
    }

    @GetMapping
    public List<AdditionalService> list(@PathVariable int hotelId) throws SQLException {
        return additionalServiceService.getAdditionalServicesByHotelId(hotelId);
    }

    public static class CreateAdditionalServiceRequest {
        public String serviceType;
        public String season;
        public int usageCount;
    }

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable int hotelId, @RequestBody CreateAdditionalServiceRequest req) throws SQLException {
        additionalServiceService.addAdditionalService(new AdditionalService(0, req.serviceType, req.season, req.usageCount, hotelId));
        return ResponseEntity.ok().build();
    }
}
