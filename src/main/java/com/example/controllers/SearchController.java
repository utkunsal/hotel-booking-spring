package com.example.controllers;

import com.example.responses.SearchResponse;
import com.example.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * Searches for available rooms in the given conditions
     * @param city          City to search in, Optional.
     * @param country       Country to search in, Optional.
     * @param startDate     The start date of search
     * @param endDate       The end date of search
     * @param capacity      Room capacity
     * @param page          the page number
     * @param size          hotel count in a page
     * @return              Returns a SearchResponse which includes hotels with available rooms
     */
    @GetMapping
    public ResponseEntity<SearchResponse> findAvailableRooms(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("capacity") int capacity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SearchResponse availableRooms = searchService.getAvailableHotelsWithRooms(city, country, startDate, endDate, capacity, page, size);
        return ResponseEntity.ok(availableRooms);
    }
}

