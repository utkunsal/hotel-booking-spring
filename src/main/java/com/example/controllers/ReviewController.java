package com.example.controllers;

import com.example.dtos.ReviewDTO;
import com.example.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Gets reviews of a hotel
     * @param id    id of the hotel
     * @return      Returns a list of ReviewDTOs
     */
    @GetMapping("{hotelId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable("hotelId") Long id) {
        return ResponseEntity.ok(reviewService.getReviews(id));
    }

    /**
     * Creates a new review and saves it to database
     * @param request           The review information
     * @param authentication    Auth
     */
    @PostMapping
    public void createReview(@RequestBody NewReviewRequest request, Authentication authentication) {
        reviewService.createReview(request, authentication);
    }

    /**
     * Deletes the given review from database
     * @param id                id of the review
     * @param authentication    Auth
     */
    @DeleteMapping("{reviewId}")
    public void removeReview(@PathVariable("reviewId") Long id, Authentication authentication) {
        reviewService.removeReview(id, authentication);
    }

    public record NewReviewRequest(
            Long hotelId,
            String text,
            Integer stars,
            LocalDateTime date
    ) {}


}
