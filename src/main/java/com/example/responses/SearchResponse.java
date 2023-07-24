package com.example.responses;

import com.example.dtos.HotelWithRoomsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private List<HotelWithRoomsDTO> results;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
