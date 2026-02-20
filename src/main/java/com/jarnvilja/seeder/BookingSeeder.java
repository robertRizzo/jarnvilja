package com.jarnvilja.seeder;

import com.jarnvilja.model.*;
import com.jarnvilja.repository.BookingRepository;
import com.jarnvilja.repository.TrainingClassRepository;
import com.jarnvilja.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(3)
public class BookingSeeder implements CommandLineRunner {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TrainingClassRepository trainingClassRepository;

    public BookingSeeder(BookingRepository bookingRepository, UserRepository userRepository, TrainingClassRepository trainingClassRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.trainingClassRepository = trainingClassRepository;
    }

    @Override
    public void run(String... args) throws Exception {


        Optional<User> optionalMember = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.ROLE_MEMBER)
                .findFirst();


        Optional<TrainingClass> optionalClass = trainingClassRepository.findAll()
                .stream()
                .findFirst();

        if (optionalMember.isPresent() && optionalClass.isPresent()) {
            User member = optionalMember.get();
            TrainingClass trainingClass = optionalClass.get();


            Booking booking = new Booking(member, trainingClass);
            booking.setBookingStatus(BookingStatus.CONFIRMED);

            bookingRepository.save(booking);

            System.out.println("Bokning skapad för medlem: " + member.getUsername());
        } else {
            System.out.println("Ingen member eller träningsklass hittades – bokning ej skapad.");
        }
    }
}
