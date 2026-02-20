package com.jarnvilja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private User member;  // Relation till User (medlem)

    @ManyToOne
    @JoinColumn(name = "training_class_id")
    private TrainingClass trainingClass;  // Relation till TrainingClass (Träningspass)

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private LocalDate bookingDate;
    private LocalDateTime bookingTimeStamp;

    // Standard konstruktor
    public Booking() {}

    // Konstruktor för skapande av bokning
    public Booking(User member, TrainingClass trainingClass) {
        this.member = member;
        this.trainingClass = trainingClass;
        this.bookingDate = LocalDate.now();
        this.bookingTimeStamp = LocalDateTime.now(); // Initialiserar bokningens tidsstämpel
        this.bookingStatus = BookingStatus.PENDING; // Sätter status till PENDING vid skapande
    }

    public boolean isCancelledByMember() {
        return BookingStatus.CANCELLED.equals(this.bookingStatus);
    }

    // Getter- och setter-metoder för alla fält
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getMember() { return member; }
    public void setMember(User member) { this.member = member; }

    public TrainingClass getTrainingClass() { return trainingClass; }
    public void setTrainingClass(TrainingClass trainingClass) { this.trainingClass = trainingClass; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalDateTime getBookingTimeStamp() { return bookingTimeStamp; }
    public void setBookingTimeStamp(LocalDateTime bookingTimeStamp) { this.bookingTimeStamp = bookingTimeStamp; }

    // Metod för att kontrollera om bokningen har gått ut (om den är äldre än 30 minuter och har status PENDING)
    public boolean isExpired() {
        return bookingStatus == BookingStatus.PENDING && bookingTimeStamp != null && bookingTimeStamp.isBefore(LocalDateTime.now().minusMinutes(30));
    }
}
