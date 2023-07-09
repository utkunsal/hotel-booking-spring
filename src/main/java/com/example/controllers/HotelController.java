package com.example.controllers;

import com.example.entities.Hotel;
import com.example.entities.Room;
import com.example.repositories.HotelRepository;
import com.example.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @PostMapping
    public void addHotelWithRooms(@RequestBody NewHotelWithRoomsRequest request){

        // Create a new hotel
        Hotel hotel = new Hotel();
        hotel.setName(request.name());
        hotel.setCity(request.city());
        hotel.setCountry(request.country());

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
            rooms.add(room);
        }

        // Save rooms to db
        roomRepository.saveAll(rooms);
    }

    record NewHotelWithRoomsRequest(
            String name,
            String city,
            String country,
            List<NewRoomRequest> rooms
    ) {
        record NewRoomRequest(
                String number,
                int capacity,
                double price
        ) {
        }
    }

}
