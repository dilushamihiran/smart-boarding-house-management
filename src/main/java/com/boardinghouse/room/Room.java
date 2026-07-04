package com.boardinghouse.room;

import com.boardinghouse.boardinghouse.BoardingHouse;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boarding_house_id", nullable = false)
    private BoardingHouse boardingHouse;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private boolean availability = true;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BoardingHouse getBoardingHouse() { return boardingHouse; }
    public void setBoardingHouse(BoardingHouse boardingHouse) { this.boardingHouse = boardingHouse; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RoomImage> getImages() { return images; }
    public void setImages(List<RoomImage> images) { this.images = images; }
}
