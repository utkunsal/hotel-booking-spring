package com.example.services;

import com.example.controllers.BookingController;
import com.example.dtos.BookingDTO;
import com.example.entities.Booking;
import com.example.entities.Room;
import com.example.entities.User;
import com.example.repositories.BookingRepository;
import com.example.repositories.RoomRepository;
import com.example.responses.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    /**
     * Creates a new booking based on the provided request
     *
     * @param request           The request object containing booking details
     * @param authentication    The auth object
     * @return                  The created booking in BookingDTO
     */
    public BookingDTO createBooking(BookingController.NewBookingRequest request, Authentication authentication) {
        // get room by id
        Optional<Room> optionalRoom = roomRepository.findById(request.roomId());
        if (optionalRoom.isEmpty()) {
            throw new IllegalArgumentException("Room not found with ID: " + request.roomId());
        }
        Room room = optionalRoom.get();

        // check if the room is available
        if (!isRoomAvailable(room.getId(), request.startDate(), request.endDate())) {
            throw new IllegalStateException("Room is not available for the requested dates");
        }

        // create the booking
        User user = (User) authentication.getPrincipal();
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setUser(user);
        booking.setVerified(user.isVerified());

        // save the booking and return bookingDTO
        Booking savedBooking =  bookingRepository.save(booking);
        return convertBookingToDTO(savedBooking);
    }

    /**
     * Deletes the booking corresponding to the given booking id.
     *
     * @param bookingId         The id of the booking to remove
     * @param authentication    The auth object
     */
    public void removeBooking(Long bookingId, Authentication authentication) {
        // get booking
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
        Booking booking = optionalBooking.get();

        if (!booking.getUser().getEmail().equals(authentication.getName())){
            throw new AccessDeniedException("You are not authorized to remove this booking");
        }

        // delete the booking
        bookingRepository.delete(booking);
    }

    /**
     * Checks if the room is still not booked by anyone
     *
     * @param roomId        id of the room to check
     * @param startDate     Start date of the time period
     * @param endDate       End date of the time period
     * @return              True if the room is available
     */
    private boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(roomId, startDate, endDate);
        return conflictingBookings.isEmpty();
    }

    /**
     * Gets bookings of the given user
     * @param user  the user
     * @param page  the page number
     * @param size  size of bookings in a page
     * @return      returns a BookingResponse which includes bookings and page information
     */
    public BookingResponse getUserBookings(User user, int page, int size) {
        // get bookings
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingPage = bookingRepository.findAllByUserOrderByStartDateDesc(user, pageable);
        List<Booking> bookings = bookingPage.getContent();
        // return response
        return BookingResponse.builder()
                .bookings(bookings.stream().map(this::convertBookingToDTO).toList())
                .currentPage(bookingPage.getNumber())
                .totalPages(bookingPage.getTotalPages())
                .totalItems(bookingPage.getTotalElements())
                .build();
    }

    private BookingDTO convertBookingToDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        BeanUtils.copyProperties(booking, bookingDTO);
        return bookingDTO;
    }
}

