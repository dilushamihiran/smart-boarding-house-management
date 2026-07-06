package com.boardinghouse.complaint;

import com.boardinghouse.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUser(User user);
    List<Complaint> findByBoardingHouse_Owner(User owner);
}
