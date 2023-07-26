package com.example.services;

import com.example.dtos.HotelWithRoomsDTO;
import com.example.dtos.RoomDTO;
import com.example.entities.Hotel;
import com.example.entities.Room;
import com.example.repositories.ReviewRepository;
import com.example.repositories.RoomRepository;
import com.example.responses.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Gets available hotels with rooms according to given parameters
     * @param city          City to search in, Optional.
     * @param country       Country to search in, Optional.
     * @param startDate     The start date of search
     * @param endDate       The end date of search
     * @param capacity      Room capacity
     * @param page          the page number
     * @param size          hotel count in a page
     * @return              Returns a SearchResponse which includes hotels with available rooms
     */
    public SearchResponse getAvailableHotelsWithRooms(String city, String  country, LocalDate startDate, LocalDate endDate, int capacity, int page, int size) {
        // get search results
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> resultPage = roomRepository.findAvailableRoomsGroupedByHotelId(city, country, startDate, endDate, capacity, pageable);
        List<Object[]> content = resultPage.getContent();
        // convert room ids to room objects and populate list of HotelWithRoomsDTOs
        List<HotelWithRoomsDTO> out = new ArrayList<>();
        for (Object[] item : content) {
            List<RoomDTO> rooms = new ArrayList<>();
            for(Long id:(Long[]) item[1])
                roomRepository.findById(id).ifPresent(room -> {
                    rooms.add(convertRoomToDTO(room));
                });
            Hotel hotel = (Hotel) item[0];
            // to sort according to price
            rooms.sort(Comparator.comparing(RoomDTO::getPrice));
            // to get review count
            int reviewCount = reviewRepository.getReviewCountByHotel(hotel);
            // to get avg rating
            Double avgRating = reviewRepository.getAverageRatingForHotel(hotel);
            out.add(new HotelWithRoomsDTO(hotel, rooms, reviewCount, avgRating));
        }
        // build return response
        return SearchResponse.builder()
                .results(out)
                .currentPage(resultPage.getNumber())
                .totalPages(resultPage.getTotalPages())
                .totalItems(resultPage.getTotalElements())
                .build();
    }

    private RoomDTO convertRoomToDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        BeanUtils.copyProperties(room, roomDTO);
        return roomDTO;
    }

}
