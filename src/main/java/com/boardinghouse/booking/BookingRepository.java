package com.boardinghouse.booking;

import com.boardinghouse.room.Room;
import com.boardinghouse.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByRoom(Room room);
    List<Booking> findByRoom_BoardingHouse_Owner(User owner);
}
