package com.boardinghouse.boardinghouse;

import com.boardinghouse.user.User;
import com.boardinghouse.user.UserRepository;
import com.boardinghouse.room.Room;
import com.boardinghouse.room.RoomRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/owner")
@PreAuthorize("hasRole('ROLE_OWNER')")
public class BoardingHouseController {

    private final BoardingHouseRepository boardingHouseRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BoardingHouseController(BoardingHouseRepository boardingHouseRepository,
                                    RoomRepository roomRepository,
                                    UserRepository userRepository) {
        this.boardingHouseRepository = boardingHouseRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String ownerDashboard(Authentication auth, Model model) {
        User owner = userRepository.findByEmail(auth.getName()).orElseThrow();
        List<BoardingHouse> myHouses = boardingHouseRepository.findByOwner(owner);
        model.addAttribute("boardingHouses", myHouses);
        model.addAttribute("ownerName", owner.getFullName());
        return "owner/dashboard";
    }

    @GetMapping("/boarding-houses/add")
    public String showAddForm(Model model) {
        model.addAttribute("boardingHouse", new BoardingHouse());
        return "owner/add-boarding-house";
    }

    @PostMapping("/boarding-houses/add")
    public String addBoardingHouse(@ModelAttribute BoardingHouse boardingHouse,
                                    Authentication auth, RedirectAttributes ra) {
        User owner = userRepository.findByEmail(auth.getName()).orElseThrow();
        boardingHouse.setOwner(owner);
        boardingHouse.setStatus(BoardingHouseStatus.PENDING);
        boardingHouseRepository.save(boardingHouse);
        ra.addFlashAttribute("success", "Boarding house submitted for approval!");
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/boarding-houses/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Authentication auth) {
        BoardingHouse bh = boardingHouseRepository.findById(id).orElseThrow();
        model.addAttribute("boardingHouse", bh);
        return "owner/edit-boarding-house";
    }

    @PostMapping("/boarding-houses/{id}/edit")
    public String updateBoardingHouse(@PathVariable Long id,
                                      @ModelAttribute BoardingHouse updated,
                                      RedirectAttributes ra) {
        boardingHouseRepository.findById(id).ifPresent(bh -> {
            bh.setName(updated.getName());
            bh.setAddress(updated.getAddress());
            bh.setDistrict(updated.getDistrict());
            bh.setDescription(updated.getDescription());
            boardingHouseRepository.save(bh);
        });
        ra.addFlashAttribute("success", "Boarding house updated!");
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/boarding-houses/{id}/delete")
    public String deleteBoardingHouse(@PathVariable Long id, RedirectAttributes ra) {
        boardingHouseRepository.deleteById(id);
        ra.addFlashAttribute("success", "Boarding house deleted!");
        return "redirect:/owner/dashboard";
    }

    // Room Management
    @GetMapping("/boarding-houses/{bhId}/rooms")
    public String listRooms(@PathVariable Long bhId, Model model) {
        BoardingHouse bh = boardingHouseRepository.findById(bhId).orElseThrow();
        List<Room> rooms = roomRepository.findByBoardingHouse(bh);
        model.addAttribute("boardingHouse", bh);
        model.addAttribute("rooms", rooms);
        return "owner/rooms";
    }

    @GetMapping("/boarding-houses/{bhId}/rooms/add")
    public String showAddRoomForm(@PathVariable Long bhId, Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("bhId", bhId);
        return "owner/add-room";
    }

    @PostMapping("/boarding-houses/{bhId}/rooms/add")
    public String addRoom(@PathVariable Long bhId, @ModelAttribute Room room, RedirectAttributes ra) {
        BoardingHouse bh = boardingHouseRepository.findById(bhId).orElseThrow();
        room.setBoardingHouse(bh);
        room.setAvailability(true);
        roomRepository.save(room);
        ra.addFlashAttribute("success", "Room added successfully!");
        return "redirect:/owner/boarding-houses/" + bhId + "/rooms";
    }

    @PostMapping("/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId, RedirectAttributes ra) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        Long bhId = room.getBoardingHouse().getId();
        roomRepository.delete(room);
        ra.addFlashAttribute("success", "Room deleted!");
        return "redirect:/owner/boarding-houses/" + bhId + "/rooms";
    }

    @PostMapping("/rooms/{roomId}/toggle-availability")
    public String toggleAvailability(@PathVariable Long roomId, RedirectAttributes ra) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        room.setAvailability(!room.isAvailability());
        roomRepository.save(room);
        Long bhId = room.getBoardingHouse().getId();
        ra.addFlashAttribute("success", "Availability updated!");
        return "redirect:/owner/boarding-houses/" + bhId + "/rooms";
    }
}
