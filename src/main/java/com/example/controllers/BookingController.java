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

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody NewBookingRequest request, Authentication authentication) {
        Booking createdBooking = bookingService.createBooking(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

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
