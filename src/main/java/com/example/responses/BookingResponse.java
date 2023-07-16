package com.example.responses;

import com.example.entities.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private List<Booking> bookings;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
