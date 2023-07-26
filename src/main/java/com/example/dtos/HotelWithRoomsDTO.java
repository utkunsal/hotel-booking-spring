package com.example.dtos;

import com.example.entities.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelWithRoomsDTO {
    private Hotel hotel;
    private List<RoomDTO> rooms;
    private int reviewCount;
    private Double avgRating;
}
