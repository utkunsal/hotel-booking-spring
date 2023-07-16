package com.example.repositories;

import com.example.entities.Hotel;
import com.example.entities.Review;
import com.example.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByHotelAndVerified(Hotel hotel, boolean verified, Pageable pageable);
    Page<Review> findAllByUser(User user, Pageable pageable);
    List<Review> findAllByUserAndHotelAndVerified(User user, Hotel hotel, boolean verified);
}
