package com.example.controllers;

import com.example.entities.User;
import com.example.responses.ReviewResponse;
import com.example.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Gets reviews of a hotel
     * @param id    id of the hotel
     * @param page  the page number
     * @param size  size of reviews in a page
     * @return      Returns a ReviewResponse which includes reviews and page information
     */
    @GetMapping("{hotelId}")
    public ResponseEntity<ReviewResponse> getReviews(
            @PathVariable("hotelId") Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reviewService.getReviews(id, authentication, page, size));
    }

    /**
     * Gets reviews of the logged-in user
     * @param authentication    Auth
     * @param page              the page number
     * @param size              size of reviews in a page
     * @return                  Returns a ReviewResponse which includes reviews and page information
     */
    @GetMapping("/user")
    public ResponseEntity<ReviewResponse> getReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getUserReviews((User) authentication.getPrincipal(), page, size));
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
