package com.boardinghouse.config;

import com.boardinghouse.boardinghouse.BoardingHouse;
import com.boardinghouse.boardinghouse.BoardingHouseRepository;
import com.boardinghouse.boardinghouse.BoardingHouseStatus;
import com.boardinghouse.booking.Booking;
import com.boardinghouse.booking.BookingRepository;
import com.boardinghouse.booking.BookingStatus;
import com.boardinghouse.complaint.Complaint;
import com.boardinghouse.complaint.ComplaintRepository;
import com.boardinghouse.complaint.ComplaintStatus;
import com.boardinghouse.room.Room;
import com.boardinghouse.room.RoomRepository;
import com.boardinghouse.user.Role;
import com.boardinghouse.user.User;
import com.boardinghouse.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardingHouseRepository boardingHouseRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ComplaintRepository complaintRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           BoardingHouseRepository boardingHouseRepository,
                           RoomRepository roomRepository,
                           BookingRepository bookingRepository,
                           ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.boardingHouseRepository = boardingHouseRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return; // Don't re-seed

        // ==================== USERS ====================
        // Admin
        User admin = createUser("System Admin", "admin@gmail.com", "0712345678", "admin123", Role.ROLE_ADMIN);

        // Owners
        User owner1 = createUser("Kamal Perera", "kamal@gmail.com", "0771234567", "owner123", Role.ROLE_OWNER);
        User owner2 = createUser("Nimal Silva", "nimal@gmail.com", "0772345678", "owner123", Role.ROLE_OWNER);

        // Students
        User student1 = createUser("Sahan Fernando", "sahan@gmail.com", "0761234567", "student123", Role.ROLE_STUDENT);
        User student2 = createUser("Amaya Kumari", "amaya@gmail.com", "0762345678", "student123", Role.ROLE_STUDENT);
        User student3 = createUser("Dinesh Rajapaksha", "dinesh@gmail.com", "0763456789", "student123", Role.ROLE_STUDENT);

        // ==================== BOARDING HOUSES ====================
        BoardingHouse bh1 = createBoardingHouse(owner1, "Sunshine Boarding", "No. 45, Galle Road", "Colombo",
                "A comfortable boarding house near the university with AC rooms and free WiFi.", BoardingHouseStatus.APPROVED);

        BoardingHouse bh2 = createBoardingHouse(owner1, "Green View Hostel", "No. 12, Kandy Road", "Kandy",
                "Peaceful environment with garden view. Close to bus stop.", BoardingHouseStatus.APPROVED);

        BoardingHouse bh3 = createBoardingHouse(owner2, "City Stay", "No. 78, Main Street", "Galle",
                "Modern facilities with 24/7 security. Walking distance to campus.", BoardingHouseStatus.APPROVED);

        BoardingHouse bh4 = createBoardingHouse(owner2, "New Haven", "No. 33, Temple Road", "Matara",
                "New boarding house with excellent facilities.", BoardingHouseStatus.PENDING);

        // ==================== ROOMS ====================
        Room r1 = createRoom(bh1, "Room A1 - Single", 1, 15000.0, true);
        Room r2 = createRoom(bh1, "Room A2 - Double", 2, 25000.0, true);
        Room r3 = createRoom(bh1, "Room A3 - Triple", 3, 30000.0, false);

        Room r4 = createRoom(bh2, "Room B1 - Single", 1, 12000.0, true);
        Room r5 = createRoom(bh2, "Room B2 - Double", 2, 20000.0, true);

        Room r6 = createRoom(bh3, "Room C1 - Single AC", 1, 18000.0, true);
        Room r7 = createRoom(bh3, "Room C2 - Double AC", 2, 28000.0, true);
        Room r8 = createRoom(bh3, "Room C3 - Suite", 2, 35000.0, true);

        // ==================== BOOKINGS ====================
        createBooking(student1, r1, LocalDate.now().plusDays(5), 6, BookingStatus.APPROVED);
        createBooking(student2, r4, LocalDate.now().plusDays(10), 12, BookingStatus.PENDING);
        createBooking(student3, r6, LocalDate.now().plusDays(3), 6, BookingStatus.APPROVED);
        createBooking(student1, r7, LocalDate.now().plusDays(15), 3, BookingStatus.PENDING);

        // ==================== COMPLAINTS ====================
        createComplaint(student1, bh1, "Water Issue", "Hot water is not working in the bathroom for 2 days.", ComplaintStatus.OPEN);
        createComplaint(student3, bh3, "Noisy Neighbors", "The neighboring room plays loud music after 10 PM.", ComplaintStatus.IN_PROGRESS);
        createComplaint(student2, bh2, "WiFi Problem", "WiFi connection keeps dropping every few minutes.", ComplaintStatus.RESOLVED);

        System.out.println("=========================================");
        System.out.println(" SAMPLE DATA LOADED SUCCESSFULLY!");
        System.out.println(" Admin: admin@gmail.com / admin123");
        System.out.println(" Owner: kamal@gmail.com / owner123");
        System.out.println(" Student: sahan@gmail.com / student123");
        System.out.println("=========================================");
    }

    private User createUser(String name, String email, String phone, String password, Role role) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private BoardingHouse createBoardingHouse(User owner, String name, String address, String district, String desc, BoardingHouseStatus status) {
        BoardingHouse bh = new BoardingHouse();
        bh.setOwner(owner);
        bh.setName(name);
        bh.setAddress(address);
        bh.setDistrict(district);
        bh.setDescription(desc);
        bh.setStatus(status);
        return boardingHouseRepository.save(bh);
    }

    private Room createRoom(BoardingHouse bh, String name, int capacity, double price, boolean available) {
        Room room = new Room();
        room.setBoardingHouse(bh);
        room.setRoomName(name);
        room.setCapacity(capacity);
        room.setPrice(price);
        room.setAvailability(available);
        return roomRepository.save(room);
    }

    private void createBooking(User user, Room room, LocalDate checkIn, int months, BookingStatus status) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setDurationMonths(months);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    private void createComplaint(User user, BoardingHouse bh, String subject, String desc, ComplaintStatus status) {
        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setBoardingHouse(bh);
        complaint.setSubject(subject);
        complaint.setDescription(desc);
        complaint.setStatus(status);
        complaintRepository.save(complaint);
    }
}
