package web;

import model.Hotel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.HotelService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelRestController {
    private final HotelService hotelService;

    public HotelRestController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<Hotel> list() throws SQLException { return hotelService.getAllHotels(); }

    @GetMapping("/{id}")
    public Hotel get(@PathVariable int id) throws SQLException { return hotelService.getHotelById(id); }

    public static class CreateHotelRequest { public String name; public String address; }

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody CreateHotelRequest req) throws SQLException {
        int id = hotelService.createHotel(req.name, req.address);
        return ResponseEntity.ok(id);
    }
}
