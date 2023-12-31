package com.example.responses;

import com.example.dtos.ReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private List<ReviewDTO> reviews;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
