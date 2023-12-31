package com.example.controllers;

import com.example.dtos.HotelWithRoomsDTO;
import com.example.entities.Hotel;
import com.example.entities.Room;
import com.example.repositories.HotelRepository;
import com.example.repositories.RoomRepository;
import com.example.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final SearchService searchService;

    /**
     * Gets all available hotel locations,
     * Locations are in format "City, Country" or "Country"
     * @return A list with alphabetically sorted locations
     */
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getAllLocations(){
        Set<String> locations = new HashSet<>();
        List<Hotel> hotels = hotelRepository.findAll();
        hotels.forEach(hotel -> {
            locations.add(hotel.getCountry());
            locations.add(hotel.getCity()+", "+hotel.getCountry());
        });

        return ResponseEntity.ok(locations.stream().sorted((a, b) -> {
            String location1 = a.contains(",") ? a.substring(a.indexOf(",") + 2)+"a" : a;
            String location2 = b.contains(",") ? b.substring(b.indexOf(",") + 2)+"a" : b;
            return location1.compareTo(location2);
        }).toList());
    }

    /**
     * Gets available rooms for given hotel
     * @param hotelId       id of the hotel
     * @param startDate     The start date of search
     * @param endDate       The end date of search
     * @param capacity      Room capacity
     * @return              Returns a HotelWithRoomsDTO which includes hotel with available rooms
     */
    @GetMapping({"{hotelId}"})
    public ResponseEntity<HotelWithRoomsDTO> getAvailableRoomsForHotel(
            @PathVariable("hotelId") Long hotelId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("capacity") int capacity
    ) {
        HotelWithRoomsDTO availableRooms = searchService.getAvailableRoomsForHotel(hotelId, startDate, endDate, capacity);
        return ResponseEntity.ok(availableRooms);
    }

    /**
     * Creates new hotel and rooms and saves them to database
     * @param request The hotel and rooms information
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void addHotelWithRooms(@RequestBody NewHotelWithRoomsRequest request){

        // Create a new hotel
        Hotel hotel = new Hotel();
        hotel.setName(request.name());
        hotel.setCity(request.city());
        hotel.setCountry(request.country());
        hotel.setDescription(request.description());
        hotel.setImageUrls(request.imageUrls());

        // Save the hotel to db
        Hotel savedHotel = hotelRepository.save(hotel);

        // Create and add rooms to the hotel
        List<NewHotelWithRoomsRequest.NewRoomRequest> roomRequests = request.rooms();
        List<Room> rooms = new ArrayList<>();
        for (NewHotelWithRoomsRequest.NewRoomRequest roomRequest: roomRequests) {
            Room room = new Room();
            room.setNumber(roomRequest.number());
            room.setCapacity(roomRequest.capacity());
            room.setPrice(roomRequest.price());
            room.setHotel(savedHotel);
            room.setSize(roomRequest.size());
            room.setAmenities(roomRequest.amenities().stream().sorted().toList());
            rooms.add(room);
        }

        // Save rooms to db
        roomRepository.saveAll(rooms);
    }

    record NewHotelWithRoomsRequest(
            String name,
            String city,
            String country,
            String description,
            List<NewRoomRequest> rooms,
            List<String> imageUrls
    ) {
        record NewRoomRequest(
                String number,
                int capacity,
                double price,
                List<String> amenities,
                String size
        ) {
        }
    }

}
