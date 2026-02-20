package com.jarnvilja.service;

import com.jarnvilja.dto.BookingStatsDTO;
import com.jarnvilja.dto.MemberStatsDTO;
import com.jarnvilja.model.*;
import com.jarnvilja.repository.BookingRepository;
import com.jarnvilja.repository.TrainingClassRepository;
import com.jarnvilja.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrainingClassRepository trainingClassRepository;
    private final BookingRepository bookingRepository;

    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder, TrainingClassRepository trainingClassRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.trainingClassRepository = trainingClassRepository;
        this.bookingRepository = bookingRepository;
    }


    // Hantera användare

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRole(updatedUser.getRole());
            return userRepository.save(existingUser);
        }
        return null;
    }


    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "User " + id + " deleted";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findUsersByRole(role);
    }

    public User assignRoleToUser(Long id, Role role) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            return userRepository.save(user);
        }
        return null;
    }

    public User resetUserPassword(Long id, String newPassword) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        }
        return null;
    }




    // Hantera tränare

    public TrainingClass assignTrainerToClass(Long classId, Long trainerId) {
        TrainingClass trainingClass = trainingClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        trainingClass.setTrainer(trainer);  // Sätter en ny tränare istället för att lägga till i en lista

        return trainingClassRepository.save(trainingClass);  // Sparar uppdateringen
    }


    public String removeTrainerFromClass(Long classId, Long trainerId) {
        Optional<TrainingClass> trainingClassOpt = trainingClassRepository.findById(classId);

        if (trainingClassOpt.isPresent()) {
            TrainingClass trainingClass = trainingClassOpt.get();

            if (trainingClass.getTrainer() != null && trainingClass.getTrainer().getId().equals(trainerId)) {
                trainingClass.setTrainer(null);  // Ta bort tränaren genom att sätta den till null
                trainingClassRepository.save(trainingClass);
                return "Trainer " + trainerId + " removed from the class: " + trainingClass.getTitle();
            } else {
                return "Trainer " + trainerId + " is not assigned to this class.";
            }
        }
        return "Training class not found.";
    }


    public Optional<User> getTrainerFromClass(Long classId) {
        Optional<TrainingClass> trainingClassOpt = trainingClassRepository.findById(classId);
        if (trainingClassOpt.isPresent()) {
            User trainer = trainingClassOpt.get().getTrainer();
            return Optional.ofNullable(trainer);
        }
        return Optional.empty();
    }

    public List<User> getAllTrainers() {
        return userRepository.findUsersByRole(Role.ROLE_TRAINER);
    }



    // Hantera Bokningar
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public String deleteBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            bookingRepository.deleteById(bookingId);
            return "Booking " + bookingId + " deleted";
        }
        return "Booking " + bookingId + " not found";
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> cancelAllBookingsForClass(Long trainingClassId) {
        List<Booking> bookings = bookingRepository.findByTrainingClassId(trainingClassId);

        for (Booking booking : bookings) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
        }
        return bookingRepository.saveAll(bookings);
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
         return bookingRepository.findByBookingStatus(status);
    }

    public void removeExpiredBookings() {
        List<Booking> allBookings = bookingRepository.findAll();  // Hämta alla bokningar
        List<Booking> expiredBookings = allBookings.stream()
                .filter(Booking::isExpired)  // Filtrera ut bokningar som är expired
                .collect(Collectors.toList());

        if (!expiredBookings.isEmpty()) {
            bookingRepository.deleteAll(expiredBookings);  // Ta bort de utlöpta bokningarna
        }
    }



    public BookingStatsDTO getBookingStats() {
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
        long cancelledBookings = bookingRepository.countByBookingStatus(BookingStatus.CANCELLED);
        long pendingBookings = bookingRepository.countByBookingStatus(BookingStatus.PENDING);
        long cancelledBookingsByMember = bookingRepository.countByBookingStatus((BookingStatus.CANCELLED_BY_MEMBER));
        long expiredBookings = bookingRepository.countByBookingStatus(BookingStatus.EXPIRED);
        String mostPopularClass = bookingRepository.findMostPopularClass();

        return new BookingStatsDTO(totalBookings, confirmedBookings, cancelledBookings, pendingBookings, cancelledBookingsByMember, expiredBookings, mostPopularClass);
    }

    public MemberStatsDTO getMemberStats() {
        long totalMembers = userRepository.count();
        long activeMembers = bookingRepository.countActiveMembers();
        long inactiveMembers = totalMembers - activeMembers;
        long mostActiveMemberId = bookingRepository.findMostActiveMemberId();

        return new MemberStatsDTO(totalMembers, activeMembers, inactiveMembers, mostActiveMemberId);
    }

    public String getTotalBookingsForClass(Long classId) {
        Long count = bookingRepository.countBookingsForClass(classId);
        return "Total bookings for class " + classId + ": " + count;
    }

    public List<Booking> getBookingsByPeriod(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findBookingsByDateBetween(startDate, endDate);
    }

    public List<Booking> getAllBookingsForMember(Long memberId) {
        return bookingRepository.findMemberId(memberId);
    }

    public Map<String, Long> getClassStats() {
        List<Object[]> results = bookingRepository.getClassStats();
        Map<String, Long> classStats = new HashMap<>();

        for (Object[] result : results) {
            classStats.put((String) result[0], (Long) result[1]);
        }
        return classStats;
    }
}

