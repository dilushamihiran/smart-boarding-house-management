package com.boardinghouse.room;

import com.boardinghouse.boardinghouse.BoardingHouseRepository;
import com.boardinghouse.boardinghouse.BoardingHouseStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RoomController {

    private final RoomRepository roomRepository;
    private final BoardingHouseRepository boardingHouseRepository;

    public RoomController(RoomRepository roomRepository, BoardingHouseRepository boardingHouseRepository) {
        this.roomRepository = roomRepository;
        this.boardingHouseRepository = boardingHouseRepository;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String district,
                         @RequestParam(required = false) Double maxPrice,
                         Model model) {
        List<Room> rooms;
        if (district != null && !district.isEmpty()) {
            rooms = roomRepository.findByBoardingHouse_DistrictAndAvailabilityTrue(district);
        } else if (maxPrice != null) {
            rooms = roomRepository.findByPriceLessThanEqual(maxPrice);
        } else {
            rooms = roomRepository.findByAvailabilityTrue();
        }
        model.addAttribute("rooms", rooms);
        model.addAttribute("district", district);
        model.addAttribute("maxPrice", maxPrice);
        return "search";
    }
}
