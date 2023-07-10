package com.example.repositories;

import com.example.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r " +
            "JOIN r.hotel h " +
            "WHERE (:city IS NULL OR h.city = :city) " +
            "AND h.country = :country " +
            "AND r.id NOT IN (" +
            "SELECT b.room.id " +
            "FROM Booking b " +
            "WHERE b.startDate <= :endDate " +
            "AND b.endDate >= :startDate) " +
            "AND r.capacity = :capacity")
    List<Room> findAvailableRooms(@Param("city") String city,
                                  @Param("country") String country,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("capacity") int capacity);

}

