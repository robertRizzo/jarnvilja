package com.jarnvilja.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "training_classes")
public class TrainingClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private DayOfWeek trainingDay;  // Exempel: MONDAY

    @Enumerated(EnumType.STRING)
    private Matta matta;

    private LocalTime startTime;    // Exempel: 17:00
    private LocalTime endTime;      // Exempel: 18:00

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private User trainer;  // En tränare per träningspass

    public TrainingClass() {}

    public TrainingClass(String title, String description, DayOfWeek trainingDay,Matta matta, LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.description = description;
        this.trainingDay = trainingDay;
        this.matta = matta;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @OneToMany(mappedBy = "trainingClass", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    public List<Booking> getBookings() {
        return bookings;
    }

    private BookingStatus status = BookingStatus.CANCELLED;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public DayOfWeek getTrainingDay() { return trainingDay; }
    public void setTrainingDay(DayOfWeek trainingDay) { this.trainingDay = trainingDay; }
    public Matta getMatta() {return matta;}
    public void setMatta(Matta matta) { this.matta = matta; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public User getTrainer() { return trainer; }
    public void setTrainer(User trainer) { this.trainer = trainer; }

    public void setBookings(List<Booking> bookings) {this.bookings = bookings;}
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
