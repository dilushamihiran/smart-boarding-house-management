package com.boardinghouse.admin;

import com.boardinghouse.boardinghouse.BoardingHouse;
import com.boardinghouse.boardinghouse.BoardingHouseRepository;
import com.boardinghouse.boardinghouse.BoardingHouseStatus;
import com.boardinghouse.booking.BookingRepository;
import com.boardinghouse.complaint.Complaint;
import com.boardinghouse.complaint.ComplaintRepository;
import com.boardinghouse.complaint.ComplaintStatus;
import com.boardinghouse.room.RoomRepository;
import com.boardinghouse.user.Role;
import com.boardinghouse.user.User;
import com.boardinghouse.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final BoardingHouseRepository boardingHouseRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ComplaintRepository complaintRepository;

    public AdminController(UserRepository userRepository,
                           BoardingHouseRepository boardingHouseRepository,
                           RoomRepository roomRepository,
                           BookingRepository bookingRepository,
                           ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.boardingHouseRepository = boardingHouseRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.complaintRepository = complaintRepository;
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalStudents", userRepository.countByRole(Role.ROLE_STUDENT));
        model.addAttribute("totalOwners", userRepository.countByRole(Role.ROLE_OWNER));
        model.addAttribute("totalBoardingHouses", boardingHouseRepository.count());
        model.addAttribute("totalRooms", roomRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("totalComplaints", complaintRepository.count());
        return "admin/dashboard";
    }

    // ==================== USER MANAGEMENT ====================
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(user -> {
            if (user.getRole() != Role.ROLE_ADMIN) {
                user.setEnabled(!user.isEnabled());
                userRepository.save(user);
                ra.addFlashAttribute("success", "User status updated successfully!");
            }
        });
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(user -> {
            if (user.getRole() != Role.ROLE_ADMIN) {
                userRepository.delete(user);
                ra.addFlashAttribute("success", "User deleted successfully!");
            }
        });
        return "redirect:/admin/users";
    }

    // ==================== BOARDING HOUSE MANAGEMENT ====================
    @GetMapping("/boarding-houses")
    public String boardingHouses(Model model) {
        model.addAttribute("boardingHouses", boardingHouseRepository.findAll());
        return "admin/boarding-houses";
    }

    @PostMapping("/boarding-houses/{id}/approve")
    public String approveBoardingHouse(@PathVariable Long id, RedirectAttributes ra) {
        boardingHouseRepository.findById(id).ifPresent(bh -> {
            bh.setStatus(BoardingHouseStatus.APPROVED);
            boardingHouseRepository.save(bh);
            ra.addFlashAttribute("success", "Boarding house approved!");
        });
        return "redirect:/admin/boarding-houses";
    }

    @PostMapping("/boarding-houses/{id}/reject")
    public String rejectBoardingHouse(@PathVariable Long id, RedirectAttributes ra) {
        boardingHouseRepository.findById(id).ifPresent(bh -> {
            bh.setStatus(BoardingHouseStatus.REJECTED);
            boardingHouseRepository.save(bh);
            ra.addFlashAttribute("success", "Boarding house rejected!");
        });
        return "redirect:/admin/boarding-houses";
    }

    @PostMapping("/boarding-houses/{id}/delete")
    public String deleteBoardingHouse(@PathVariable Long id, RedirectAttributes ra) {
        boardingHouseRepository.deleteById(id);
        ra.addFlashAttribute("success", "Boarding house deleted!");
        return "redirect:/admin/boarding-houses";
    }

    // ==================== COMPLAINT MONITORING ====================
    @GetMapping("/complaints")
    public String complaints(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "admin/complaints";
    }

    @PostMapping("/complaints/{id}/resolve")
    public String resolveComplaint(@PathVariable Long id, RedirectAttributes ra) {
        complaintRepository.findById(id).ifPresent(complaint -> {
            complaint.setStatus(ComplaintStatus.RESOLVED);
            complaintRepository.save(complaint);
            ra.addFlashAttribute("success", "Complaint marked as resolved!");
        });
        return "redirect:/admin/complaints";
    }

    // ==================== REPORTS ====================
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("totalComplaints", complaintRepository.count());
        return "admin/reports";
    }
}
