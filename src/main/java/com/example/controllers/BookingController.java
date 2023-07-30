package com.example.controllers;


import com.example.dtos.BookingDTO;
import com.example.entities.User;
import com.example.responses.BookingResponse;
import com.example.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Gets bookings of the logged-in user
     * @param authentication    Auth
     * @param page              the page number
     * @param size              size of bookings in a page
     * @return                  Returns a BookingResponse which includes bookings and page information
     */
    @GetMapping("/user")
    public ResponseEntity<BookingResponse> getUserBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        BookingResponse bookingResponse = bookingService.getUserBookings((User) authentication.getPrincipal(), page, size);
        return ResponseEntity.ok(bookingResponse);
    }

    /**
     * Creates a new booking and saves it to database
     * @param request           The booking information
     * @param authentication    Auth
     * @return                  The created booking in BookingDTO
     */
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody NewBookingRequest request, Authentication authentication) {
        return ResponseEntity.ok(bookingService.createBooking(request, authentication));
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
