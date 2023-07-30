package com.example.repositories;

import com.example.entities.Booking;
import com.example.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.endDate >= :startDate " +
            "AND b.startDate <= :endDate")
    List<Booking> findConflictingBookings(Long roomId,
                                          LocalDate startDate,
                                          LocalDate endDate);

    Page<Booking> findAllByUserOrderByStartDateDesc(User user, Pageable pageable);
}

