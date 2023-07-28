package com.example.repositories;

import com.example.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT h, ARRAY_AGG(r) " +
            "FROM Room r " +
            "JOIN r.hotel h " +
            "WHERE (:city IS NULL OR h.city = :city) " +
            "AND (:country IS NULL OR h.country = :country) " +
            "AND r.id NOT IN (" +
            "SELECT b.room.id " +
            "FROM Booking b " +
            "WHERE b.startDate <= :endDate " +
            "AND b.endDate >= :startDate) " +
            "AND r.capacity = :capacity " +
            "GROUP BY h")
    Page<Object[]> findAvailableRoomsGroupedByHotelId(@Param("city") String city,
                                                      @Param("country") String country,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate,
                                                      @Param("capacity") int capacity,
                                                      Pageable pageable);

    @Query("SELECT r " +
            "FROM Room r " +
            "JOIN r.hotel h " +
            "WHERE h.id = :hotelId " +
            "AND r.id NOT IN (" +
            "SELECT b.room.id " +
            "FROM Booking b " +
            "WHERE b.startDate <= :endDate " +
            "AND b.endDate >= :startDate) " +
            "AND r.capacity = :capacity")
    List<Room> findAvailableRoomsForHotel(@Param("hotelId") Long hotelId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("capacity") int capacity);
}

