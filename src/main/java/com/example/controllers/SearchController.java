package com.example.controllers;

import com.example.entities.Room;
import com.example.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final RoomRepository roomRepository;

    @GetMapping
    public ResponseEntity<List<Room>> findAvailableRooms(@RequestParam("city") String city,
                                                         @RequestParam("country") String country,
                                                         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                         @RequestParam("capacity") int capacity) {
        List<Room> availableRooms = roomRepository.findAvailableRooms(city, country, startDate, endDate, capacity);
        return ResponseEntity.ok(availableRooms);
    }
}

