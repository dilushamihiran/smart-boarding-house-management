package com.boardinghouse.boardinghouse;

import com.boardinghouse.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardingHouseRepository extends JpaRepository<BoardingHouse, Long> {
    List<BoardingHouse> findByStatus(BoardingHouseStatus status);
    List<BoardingHouse> findByOwner(User owner);
    long countByStatus(BoardingHouseStatus status);
}
