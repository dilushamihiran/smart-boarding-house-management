package com.boardinghouse.room;

import com.boardinghouse.boardinghouse.BoardingHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByBoardingHouse(BoardingHouse boardingHouse);
    List<Room> findByAvailabilityTrue();
    List<Room> findByPriceLessThanEqual(Double price);
    List<Room> findByBoardingHouse_DistrictAndAvailabilityTrue(String district);
}
