package com.example.controllers;


import com.example.entities.Booking;
import com.example.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Creates a new booking and saves it to database
     * @param request           The booking information
     * @param authentication    Auth
     */
    @PostMapping
    public void createBooking(@RequestBody NewBookingRequest request, Authentication authentication) {
        bookingService.createBooking(request, authentication);
    }

    /**
     * Deletes a booking from database
     * @param id                The booking id to delete
     * @param authentication    Auth
     */
    @DeleteMapping("{bookingId}")
    public void removeBooking(@PathVariable("bookingId") Long id, Authentication authentication) {
        bookingService.removeBooking(id, authentication);
    }

    public record NewBookingRequest(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {}
}
