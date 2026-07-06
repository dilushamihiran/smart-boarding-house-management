package com.boardinghouse.complaint;

import com.boardinghouse.boardinghouse.BoardingHouse;
import com.boardinghouse.boardinghouse.BoardingHouseRepository;
import com.boardinghouse.boardinghouse.BoardingHouseStatus;
import com.boardinghouse.user.User;
import com.boardinghouse.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final BoardingHouseRepository boardingHouseRepository;
    private final UserRepository userRepository;

    public ComplaintController(ComplaintRepository complaintRepository,
                                BoardingHouseRepository boardingHouseRepository,
                                UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.boardingHouseRepository = boardingHouseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/student/complaints")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String myComplaints(Authentication auth, Model model) {
        User student = userRepository.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("complaints", complaintRepository.findByUser(student));
        model.addAttribute("boardingHouses", boardingHouseRepository.findByStatus(BoardingHouseStatus.APPROVED));
        return "student/complaints";
    }

    @PostMapping("/student/complaints/add")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String submitComplaint(@RequestParam Long boardingHouseId,
                                   @RequestParam String subject,
                                   @RequestParam String description,
                                   Authentication auth, RedirectAttributes ra) {
        User student = userRepository.findByEmail(auth.getName()).orElseThrow();
        BoardingHouse bh = boardingHouseRepository.findById(boardingHouseId).orElseThrow();

        Complaint complaint = new Complaint();
        complaint.setUser(student);
        complaint.setBoardingHouse(bh);
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setStatus(ComplaintStatus.OPEN);
        complaintRepository.save(complaint);

        ra.addFlashAttribute("success", "Complaint submitted!");
        return "redirect:/student/complaints";
    }

    @GetMapping("/owner/complaints")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String ownerComplaints(Authentication auth, Model model) {
        User owner = userRepository.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("complaints", complaintRepository.findByBoardingHouse_Owner(owner));
        return "owner/complaints";
    }

    @PostMapping("/owner/complaints/{id}/resolve")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String resolveComplaint(@PathVariable Long id, RedirectAttributes ra) {
        complaintRepository.findById(id).ifPresent(c -> {
            c.setStatus(ComplaintStatus.RESOLVED);
            complaintRepository.save(c);
        });
        ra.addFlashAttribute("success", "Complaint resolved!");
        return "redirect:/owner/complaints";
    }
}
