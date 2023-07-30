package com.example.dtos;

import com.example.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean verified;
}
