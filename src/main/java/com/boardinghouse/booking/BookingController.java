package com.boardinghouse.booking;

import com.boardinghouse.room.Room;
import com.boardinghouse.room.RoomRepository;
import com.boardinghouse.user.User;
import com.boardinghouse.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository,
                              RoomRepository roomRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    // Student Dashboard
    @GetMapping("/student/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String studentDashboard(Authentication auth, Model model) {
        User student = userRepository.findByEmail(auth.getName()).orElseThrow();
        List<Booking> myBookings = bookingRepository.findByUser(student);
        model.addAttribute("bookings", myBookings);
        model.addAttribute("studentName", student.getFullName());
        return "student/dashboard";
    }

    // Book a room
    @GetMapping("/student/book/{roomId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String showBookingForm(@PathVariable Long roomId, Model model) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        model.addAttribute("room", room);
        model.addAttribute("booking", new Booking());
        return "student/book-room";
    }

    @PostMapping("/student/book/{roomId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String bookRoom(@PathVariable Long roomId,
                            @RequestParam String checkInDate,
                            @RequestParam Integer duration,
                            Authentication auth, RedirectAttributes ra) {
        User student = userRepository.findByEmail(auth.getName()).orElseThrow();
        Room room = roomRepository.findById(roomId).orElseThrow();

        Booking booking = new Booking();
        booking.setUser(student);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.parse(checkInDate));
        booking.setDurationMonths(duration);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        ra.addFlashAttribute("success", "Booking request submitted!");
        return "redirect:/student/dashboard";
    }

    @PostMapping("/student/bookings/{id}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes ra) {
        bookingRepository.findById(id).ifPresent(b -> {
            b.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(b);
        });
        ra.addFlashAttribute("success", "Booking cancelled!");
        return "redirect:/student/dashboard";
    }

    // Owner: manage bookings
    @GetMapping("/owner/bookings")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String ownerBookings(Authentication auth, Model model) {
        User owner = userRepository.findByEmail(auth.getName()).orElseThrow();
        List<Booking> bookings = bookingRepository.findByRoom_BoardingHouse_Owner(owner);
        model.addAttribute("bookings", bookings);
        return "owner/bookings";
    }

    @PostMapping("/owner/bookings/{id}/approve")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String approveBooking(@PathVariable Long id, RedirectAttributes ra) {
        bookingRepository.findById(id).ifPresent(b -> {
            b.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(b);
            // Mark room as unavailable
            b.getRoom().setAvailability(false);
        });
        ra.addFlashAttribute("success", "Booking approved!");
        return "redirect:/owner/bookings";
    }

    @PostMapping("/owner/bookings/{id}/reject")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String rejectBooking(@PathVariable Long id, RedirectAttributes ra) {
        bookingRepository.findById(id).ifPresent(b -> {
            b.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(b);
        });
        ra.addFlashAttribute("success", "Booking rejected!");
        return "redirect:/owner/bookings";
    }
}
