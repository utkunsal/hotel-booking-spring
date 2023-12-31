package com.example.repositories;

import com.example.entities.Hotel;
import com.example.entities.Review;
import com.example.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByHotelAndVerifiedOrderByDateDesc(Hotel hotel, boolean verified, Pageable pageable);
    Page<Review> findAllByUserOrderByDateDesc(User user, Pageable pageable);
    List<Review> findAllByUserAndHotelAndVerifiedOrderByDateDesc(User user, Hotel hotel, boolean verified);
    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotel = :hotel AND r.verified = true")
    int getReviewCountByHotel(@Param("hotel") Hotel hotel);
    @Query("SELECT AVG(r.stars) FROM Review r WHERE r.hotel = :hotel AND r.verified = true")
    Double getAverageRatingForHotel(@Param("hotel") Hotel hotel);
}
