package com.example.services;

import com.example.controllers.ReviewController;
import com.example.dtos.ReviewDTO;
import com.example.responses.ReviewResponse;
import com.example.entities.*;
import com.example.repositories.HotelRepository;
import com.example.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * @param page      the page number
     * @param size      size of bookings in a page
     * @return          Returns a ReviewResponse which includes ReviewDTOs and page information
     */
    public ReviewResponse getReviews(Long hotelId, Authentication authentication, int page, int size) {
        // get hotel by id
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
        if (optionalHotel.isEmpty()) {
            throw new IllegalArgumentException("Hotel not found with ID: " + hotelId);
        }
        Hotel hotel = optionalHotel.get();
        // get reviews
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAllByHotelAndVerified(hotel, true, pageable);
        // also get user's unverified reviews and combine them
        if (page == 0 && authentication != null){
            reviewDTOs.addAll(reviewRepository
                    .findAllByUserAndHotelAndVerified((User) authentication.getPrincipal(), hotel, false)
                    .stream().map(this::convertReviewToDTO).toList());
        }
        reviewDTOs.addAll(reviewPage.map(this::convertReviewToDTO).getContent());
        // return reviews
        return ReviewResponse.builder()
                .reviews(reviewDTOs)
                .currentPage(reviewPage.getNumber())
                .totalPages(reviewPage.getTotalPages())
                .totalItems(reviewPage.getTotalElements())
                .build();
    }

    /**
     * Gets reviews of the given user
     * @param user  the user to get reviews of
     * @param page  the page number
     * @param size  size of bookings in a page
     * @return      Returns a ReviewResponse which includes ReviewDTOs and page information
     */
    public ReviewResponse getUserReviews(User user, int page, int size) {
        // get reviews
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAllByUser(user, pageable);
        Page<ReviewDTO> reviewDTOPage =  reviewPage.map(this::convertReviewToDTO);
        // return reviews
        return ReviewResponse.builder()
                .reviews(reviewDTOPage.getContent())
                .currentPage(reviewDTOPage.getNumber())
                .totalPages(reviewDTOPage.getTotalPages())
                .totalItems(reviewDTOPage.getTotalElements())
                .build();
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
        User user = (User) authentication.getPrincipal();
        Review review = Review.builder()
                .stars(request.stars())
                .text(request.text())
                .date(request.date())
                .user(user)
                .hotel(hotel)
                .verified(user.isVerified())
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
        reviewDTO.setHotelName(review.getHotel().getName());
        return reviewDTO;
    }
}
