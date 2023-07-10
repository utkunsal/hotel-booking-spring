package com.example.services;

import com.example.controllers.ReviewController;
import com.example.dtos.ReviewDTO;
import com.example.entities.*;
import com.example.repositories.HotelRepository;
import com.example.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;

    /**
     * Gets reviews of a hotel
     * @param hotelId   id of the hotel
     * @return          returns a lists with ReviewDTOs
     */
    public List<ReviewDTO> getReviews(Long hotelId) {
        // get hotel by id
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
        if (optionalHotel.isEmpty()) {
            throw new IllegalArgumentException("Hotel not found with ID: " + hotelId);
        }
        Hotel hotel = optionalHotel.get();
        // get reviews
        List<Review> reviews = reviewRepository.findAllByHotel(hotel);
        return reviews.stream().map(this::convertReviewToDTO).toList();
    }

    /**
     * Creates a new review for a hotel
     * @param request           The review information
     * @param authentication    Auth
     * @return                  Returns the created review
     */
    public Review createReview(ReviewController.NewReviewRequest request, Authentication authentication) {
        // get hotel by id
        Optional<Hotel> optionalHotel = hotelRepository.findById(request.hotelId());
        if (optionalHotel.isEmpty()) {
            throw new IllegalArgumentException("Hotel not found with ID: " + request.hotelId());
        }
        Hotel hotel = optionalHotel.get();

        // create and save the review
        Review review = Review.builder()
                .stars(request.stars())
                .text(request.text())
                .date(request.date())
                .user((User) authentication.getPrincipal())
                .hotel(hotel)
                .build();
        return reviewRepository.save(review);
    }

    /**
     * Deletes the given review
     * @param reviewId          id of the review to delete
     * @param authentication    Auth
     */
    public void removeReview(Long reviewId, Authentication authentication) {
        // get review
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }
        Review review = optionalReview.get();

        if (!review.getUser().getEmail().equals(authentication.getName())){
            throw new AccessDeniedException("You are not authorized to remove this review");
        }

        // delete the review
        reviewRepository.delete(review);
    }

    public ReviewDTO convertReviewToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        BeanUtils.copyProperties(review, reviewDTO);
        reviewDTO.setUserDisplayName(review.getUser().getName());
        return reviewDTO;
    }
}
