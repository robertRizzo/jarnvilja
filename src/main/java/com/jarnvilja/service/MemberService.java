package com.jarnvilja.service;

import com.jarnvilja.dto.MemberProfileDTO;
import com.jarnvilja.dto.MembershipStatsDTO;
import com.jarnvilja.model.Booking;
import com.jarnvilja.model.BookingStatus;
import com.jarnvilja.model.TrainingClass;
import com.jarnvilja.model.User;
import com.jarnvilja.repository.BookingRepository;
import com.jarnvilja.repository.TrainingClassRepository;
import com.jarnvilja.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TrainingClassRepository trainingClassRepository;
    private final EmailService emailService;

    @Autowired
    public MemberService(UserRepository userRepository, BookingRepository bookingRepository, TrainingClassRepository trainingClassRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.trainingClassRepository = trainingClassRepository;
        this.emailService = emailService;
    }

    // Hantera medlem:
    public void registerUser (User user) {
        createMember(user);
    }

    public User createMember(User user) {
        return userRepository.save(user);
    }

    public User updateMember(Long memberId, User updatedMember) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setUsername(updatedMember.getUsername());
        member.setEmail(updatedMember.getEmail());
        return userRepository.save(member);
    }

    public void deleteMember(Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        userRepository.delete(member);
    }

    public User getMemberById(Long memberId) {
        return userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public List<User> getAllMembers() {
        return userRepository.findAll();
    }

    public User getMemberByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public User updateMemberPassword(Long memberId, String newPassword) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setPassword(newPassword);
        return userRepository.save(member); // Returnera den uppdaterade medlemmen
    }

    public User getMemberByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }


    // Bokningar:


    public String checkBookingStatus(Long memberId, Long trainingClassId) {
        // Kontrollera om passet har startat
        TrainingClass trainingClass = trainingClassRepository.findById(trainingClassId)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        if (trainingClass.getTrainingDay().equals(DayOfWeek.from(LocalDate.now())) &&
                trainingClass.getStartTime().isBefore(LocalTime.now())) {
            return "This training class has already started.";
        }

        // Kontrollera om medlemmen redan har bokat samma pass för dagen
        List<Booking> existingBookings = bookingRepository.findByMemberIdAndTrainingClassIdAndBookingDate(
                memberId, trainingClassId, LocalDate.now());

        // Filtrera bort avbokade bokningar
        existingBookings = existingBookings.stream()
                .filter(booking -> !booking.getBookingStatus().equals(BookingStatus.CANCELLED))
                .toList();

        if (!existingBookings.isEmpty()) {
            return "You have already booked this training class for today.";
        }

        return null;
    }

    public Booking createBooking(Long memberId, Long trainingClassId) {
        // Hämta användaren och träningsklassen
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        TrainingClass trainingClass = trainingClassRepository.findById(trainingClassId)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        // Kontrollera om passet har startat
        if (trainingClass.getTrainingDay().equals(DayOfWeek.from(LocalDate.now())) &&
                trainingClass.getStartTime().isBefore(LocalTime.now())) {
            throw new RuntimeException("Cannot book this training class, it has already started.");
        }

        // Skapa och spara bokningen
        Booking booking = new Booking(user, trainingClass);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookingTimeStamp(LocalDateTime.now());

        emailService.sendEmail(user.getEmail(), "Bokning bekräftad",
                "Hej " + user.getUsername() + ",\n\n" +
                        "Din bokning för träningspasset " + trainingClass.getTitle() +
                        " den " + trainingClass.getTrainingDay() + " kl. " +
                        trainingClass.getStartTime() + " har bekräftats.\n\n" +
                        "Vänliga hälsningar,\n" +
                        "Järnvilja");

        return bookingRepository.save(booking);
    }


    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not pending and cannot be confirmed");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public void expirePendingBookings() {
        List<Booking> pendingBookings = bookingRepository.findPendingBookingsBefore(LocalDateTime.now().minusMinutes(30));

        for (Booking booking : pendingBookings) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
        }
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.CANCELLED);

        User user = booking.getMember(); // eller hur du hämtar användaren
        TrainingClass trainingClass = booking.getTrainingClass(); // och träningspasset

        emailService.sendEmail(
                user.getEmail(),
                "Bekräftelse på avbokning",
                "Hej " + user.getUsername() + ",\n\n" +
                        "Du har nu avbokat din plats till träningspasset \"" + trainingClass.getTitle() + "\" " +
                        trainingClass.getTrainingDay() + " kl. " + trainingClass.getStartTime() + ".\n\n" +
                        "Vi hoppas få se dig en annan gång!\n\n" +
                        "Vänliga hälsningar,\n" +
                        "Järnvilja"
        );


        bookingRepository.save(booking);
    }


    public List<Booking> getBookingsForMember(Long userId) {
        return bookingRepository.findByMemberId(userId);
    }


    public List<Booking> getUpcomingBookingsForMember(Long userId) {
        return bookingRepository.findUpcomingBookingsForMember(userId, LocalDate.now());
    }

    public List<Booking> getPastBookingsForMember(Long userId) {
        return bookingRepository.findPastBookingsForMember(userId, LocalDate.now());
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }


    // Träningspass:

    public List<TrainingClass> getAvailableClasses() {
        return trainingClassRepository.findAll();
    }

    public List<TrainingClass> searchAvailableClasses(String search) {
        return trainingClassRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
    }

    public List<TrainingClass> sortClasses(List<TrainingClass> classes, String sort) {
        switch (sort) {
            case "title":
                classes.sort(Comparator.comparing(TrainingClass::getTitle));
                break;
            case "description":
                classes.sort(Comparator.comparing(TrainingClass::getDescription));
                break;
            case "trainingDay":
                classes.sort(Comparator.comparing(TrainingClass::getTrainingDay));
                break;
            case "startTime":
                classes.sort(Comparator.comparing(TrainingClass::getStartTime));
                break;
            case "endTime":
                classes.sort(Comparator.comparing(TrainingClass::getEndTime));
                break;
            default:
                break;
        }
        return classes;
    }

    public List<TrainingClass> getAllClassesForMember(Long memberId) {
        List<Booking> bookings = bookingRepository.findByMemberId(memberId);
        return bookings.stream()
                .map(Booking::getTrainingClass)
                .collect(Collectors.toList());
    }

    public MemberProfileDTO getMemberProfile(Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return new MemberProfileDTO(member.getId(), member.getUsername(), member.getEmail());
    }

    public MembershipStatsDTO getMembershipStats(Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<Booking> bookings = bookingRepository.findByMemberId(memberId);

        int totalBookings = bookings.size();

        return new MembershipStatsDTO(member.getId(), totalBookings);
    }


}
