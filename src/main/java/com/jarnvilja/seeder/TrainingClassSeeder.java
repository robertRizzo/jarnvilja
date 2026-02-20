package com.jarnvilja.seeder;

import com.jarnvilja.model.Matta;
import com.jarnvilja.model.Role;
import com.jarnvilja.model.TrainingClass;
import com.jarnvilja.model.User;
import com.jarnvilja.repository.TrainingClassRepository;
import com.jarnvilja.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static com.jarnvilja.model.Role.ROLE_ADMIN;
import static com.jarnvilja.model.Role.ROLE_TRAINER;

@Component
@Order(2)
public class TrainingClassSeeder implements CommandLineRunner {

    private final TrainingClassRepository trainingClassRepository;
    private final UserRepository userRepository;

    public TrainingClassSeeder(TrainingClassRepository trainingClassRepository, UserRepository userRepository) {
        this.trainingClassRepository = trainingClassRepository;
        this.userRepository = userRepository;
    }

    private void createOrUpdateTrainingClass(String title, String description, DayOfWeek day, Matta matta, LocalTime startTime, LocalTime endTime, User trainer) {
        Optional<TrainingClass> existingClass = trainingClassRepository.findByTitleAndTrainingDayAndStartTime(title, day, startTime);

        if (existingClass.isEmpty()) {
            // Skapa en ny klass om den inte finns
            TrainingClass trainingClass = new TrainingClass();
            trainingClass.setTitle(title);
            trainingClass.setDescription(description);
            trainingClass.setTrainingDay(day);
            trainingClass.setMatta(matta);
            trainingClass.setStartTime(startTime);
            trainingClass.setEndTime(endTime);
            trainingClass.setTrainer(trainer);
            trainingClassRepository.save(trainingClass);
            System.out.println("Träningsklass '" + title + "' för " + day + " kl. " + startTime + " har skapats.");
        } else {
            // Om klassen redan finns, kan du välja att uppdatera den eller ignorera
            System.out.println("Träningsklass med titeln '" + title + "' för " + day + " kl. " + startTime + " finns redan.");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // Skapa tränare
        User trainer1 = new User();
        trainer1.setUsername("Göran ");
        trainer1.setEmail("goran@example.com");
        trainer1.setRole(ROLE_ADMIN);

        User trainer2 = new User();
        trainer2.setUsername("Hanna \"Kroknäsa\" Karlsson");
        trainer2.setEmail("hanna@example.com");
        trainer2.setRole(ROLE_TRAINER);

        User trainer3 = new User();
        trainer3.setUsername("Fanny \"Stenpanna\" Berg");
        trainer3.setEmail("fanny@example.com");
        trainer3.setRole(ROLE_TRAINER);

        User trainer4 = new User();
        trainer4.setUsername("Micke \"Huvudskada\" Andersson");
        trainer4.setEmail("micke@example.com");
        trainer4.setRole(ROLE_TRAINER);

        User trainer5 = new User();
        trainer5.setUsername("Tony McClinch");
        trainer5.setEmail("tony@example.com");
        trainer5.setRole(ROLE_TRAINER);

        User trainer6 = new User();
        trainer6.setUsername("Kettlebell-Kajsa");
        trainer6.setEmail("kajsa@example.com");
        trainer6.setRole(Role.ROLE_TRAINER);

        User trainer7 = new User();
        trainer7.setUsername("Bella \"Strypnacke\" Johansson");
        trainer7.setEmail("bella@example.com");
        trainer7.setRole(Role.ROLE_TRAINER);

        User trainer8 = new User();
        trainer8.setUsername("Leif \"Benlåset\"");
        trainer8.setEmail("leif@example.com");
        trainer8.setRole(Role.ROLE_TRAINER);



        trainer1 = userRepository.save(trainer1);
        trainer2 = userRepository.save(trainer2);
        trainer3 = userRepository.save(trainer3);
        trainer4 = userRepository.save(trainer4);
        trainer5 = userRepository.save(trainer5);
        trainer6 = userRepository.save(trainer6);
        trainer7 = userRepository.save(trainer7);
        trainer8 = userRepository.save(trainer8);

        // Kontrollera om träningsklasserna redan finns
        if (trainingClassRepository.count() == 0) {
            // Skapa träningsklasser enligt schemat
            createOrUpdateTrainingClass("Thaiboxning", "Thaiboxning", DayOfWeek.MONDAY, Matta.MATTA_1, LocalTime.of(12, 0), LocalTime.of(13, 15), trainer1);
            createOrUpdateTrainingClass("Barn Thaiboxning", "Barn Thaiboxning", DayOfWeek.MONDAY, Matta.MATTA_1, LocalTime.of(16, 0), LocalTime.of(17, 15), trainer3);
            createOrUpdateTrainingClass("Nybörjare Thaiboxning", "Nybörjare Thaiboxning", DayOfWeek.MONDAY, Matta.MATTA_1, LocalTime.of(17, 30), LocalTime.of(18, 55), trainer3);
            createOrUpdateTrainingClass("Forts/Avanc Thaiboxning", "Forts/Avanc Thaiboxning", DayOfWeek.MONDAY, Matta.MATTA_1, LocalTime.of(19, 0), LocalTime.of(20, 15), trainer5);
            createOrUpdateTrainingClass("Sparring Thaiboxning", "Sparring Thaiboxning", DayOfWeek.MONDAY, Matta.MATTA_1, LocalTime.of(20, 15), LocalTime.of(21, 15), trainer5);

            // Skapa träningsklasser enligt schemat för Matta 2 – BJJ & Fys
            createOrUpdateTrainingClass("Fys – Morgonpass", "Fys – Morgonpass", DayOfWeek.MONDAY, Matta.MATTA_2,LocalTime.of(6, 30), LocalTime.of(7, 30), trainer6);
            createOrUpdateTrainingClass("BJJ Lunchpass", "BJJ Lunchpass", DayOfWeek.MONDAY, Matta.MATTA_2, LocalTime.of(12, 0), LocalTime.of(13, 15), trainer7);
            createOrUpdateTrainingClass("Barn BJJ", "Barn BJJ", DayOfWeek.MONDAY, Matta.MATTA_2, LocalTime.of(16, 0), LocalTime.of(17, 15), trainer7);
            createOrUpdateTrainingClass("Nybörjare BJJ", "Nybörjare BJJ", DayOfWeek.MONDAY, Matta.MATTA_2,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer7);
            createOrUpdateTrainingClass("Forts/Avanc BJJ", "Forts/Avanc BJJ", DayOfWeek.MONDAY, Matta.MATTA_2,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer7);
            createOrUpdateTrainingClass("Sparring BJJ", "Sparring BJJ", DayOfWeek.MONDAY, Matta.MATTA_2,LocalTime.of(20, 15), LocalTime.of(21, 15), trainer8);

            // Tisdag
            createOrUpdateTrainingClass("Boxning", "Boxning", DayOfWeek.TUESDAY, Matta.MATTA_1,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer2);
            createOrUpdateTrainingClass("Barn Boxning", "Barn Boxning", DayOfWeek.TUESDAY, Matta.MATTA_1,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer4);
            createOrUpdateTrainingClass("Nybörjare Boxning", "Nybörjare Boxning", DayOfWeek.TUESDAY, Matta.MATTA_1,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer4);
            createOrUpdateTrainingClass("Forts/Avanc Boxning", "Forts/Avanc Boxning", DayOfWeek.TUESDAY, Matta.MATTA_1,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer2);
            createOrUpdateTrainingClass("Sparring Boxning", "Sparring Boxning", DayOfWeek.TUESDAY, Matta.MATTA_1,LocalTime.of(20, 15), LocalTime.of(21, 15), null);

            createOrUpdateTrainingClass("BJJ Lunchpass", "BJJ Lunchpass", DayOfWeek.TUESDAY, Matta.MATTA_2,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer7);
            createOrUpdateTrainingClass("Barn BJJ", "Barn BJJ", DayOfWeek.TUESDAY, Matta.MATTA_2,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer7);
            createOrUpdateTrainingClass("Nybörjare BJJ", "Nybörjare BJJ", DayOfWeek.TUESDAY, Matta.MATTA_2,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer7);
            createOrUpdateTrainingClass("Forts/Avanc BJJ", "Forts/Avanc BJJ", DayOfWeek.TUESDAY, Matta.MATTA_2,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer7);
            createOrUpdateTrainingClass("Sparring BJJ", "Sparring BJJ", DayOfWeek.TUESDAY, Matta.MATTA_2,LocalTime.of(20, 15), LocalTime.of(21, 15), trainer8);

            // Onsdag
            createOrUpdateTrainingClass("Thaiboxning", "Thaiboxning", DayOfWeek.WEDNESDAY, Matta.MATTA_1,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer1);
            createOrUpdateTrainingClass("Barn Thaiboxning", "Barn Thaiboxning", DayOfWeek.WEDNESDAY, Matta.MATTA_1,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer3);
            createOrUpdateTrainingClass("Nybörjare Thaiboxning", "Nybörjare Thaiboxning", DayOfWeek.WEDNESDAY, Matta.MATTA_1,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer3);
            createOrUpdateTrainingClass("Forts/Avanc Thaiboxning", "Forts/Avanc Thaiboxning", DayOfWeek.WEDNESDAY, Matta.MATTA_1,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer5);
            createOrUpdateTrainingClass("Sparring Thaiboxning", "Sparring Thaiboxning", DayOfWeek.WEDNESDAY, Matta.MATTA_1,LocalTime.of(20, 15), LocalTime.of(21, 15), null);

            createOrUpdateTrainingClass("Fys – Morgonpass", "Fys – Morgonpass", DayOfWeek.WEDNESDAY, Matta.MATTA_2,LocalTime.of(6, 30), LocalTime.of(7, 30), trainer6);
            createOrUpdateTrainingClass("BJJ Lunchpass", "BJJ Lunchpass", DayOfWeek.WEDNESDAY, Matta.MATTA_2,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer7);
            createOrUpdateTrainingClass("Barn BJJ", "Barn BJJ", DayOfWeek.WEDNESDAY, Matta.MATTA_2,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer7);
            createOrUpdateTrainingClass("Nybörjare BJJ", "Nybörjare BJJ", DayOfWeek.WEDNESDAY, Matta.MATTA_2,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer7);
            createOrUpdateTrainingClass("Forts/Avanc BJJ", "Forts/Avanc BJJ", DayOfWeek.WEDNESDAY, Matta.MATTA_2,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer7);
            createOrUpdateTrainingClass("Sparring BJJ", "Sparring BJJ", DayOfWeek.WEDNESDAY, Matta.MATTA_2,LocalTime.of(20, 15), LocalTime.of(21, 15), trainer8);

            // Torsdag
            createOrUpdateTrainingClass("Boxning", "Boxning", DayOfWeek.THURSDAY, Matta.MATTA_1,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer2);
            createOrUpdateTrainingClass("Barn Boxning", "Barn Boxning", DayOfWeek.THURSDAY, Matta.MATTA_1,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer4);
            createOrUpdateTrainingClass("Nybörjare Boxning", "Nybörjare Boxning", DayOfWeek.THURSDAY, Matta.MATTA_1,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer4);
            createOrUpdateTrainingClass("Forts/Avanc Boxning", "Forts/Avanc Boxning", DayOfWeek.THURSDAY, Matta.MATTA_1,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer2);
            createOrUpdateTrainingClass("Sparring Boxning", "Sparring Boxning", DayOfWeek.THURSDAY, Matta.MATTA_1,LocalTime.of(20, 15), LocalTime.of(21, 15), null);

            createOrUpdateTrainingClass("BJJ Lunchpass", "BJJ Lunchpass", DayOfWeek.THURSDAY, Matta.MATTA_2,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer7);
            createOrUpdateTrainingClass("Barn BJJ", "Barn BJJ", DayOfWeek.THURSDAY, Matta.MATTA_2,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer7);
            createOrUpdateTrainingClass("Nybörjare BJJ", "Nybörjare BJJ", DayOfWeek.THURSDAY, Matta.MATTA_2,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer7);
            createOrUpdateTrainingClass("Forts/Avanc BJJ", "Forts/Avanc BJJ", DayOfWeek.THURSDAY, Matta.MATTA_2,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer7);
            createOrUpdateTrainingClass("Sparring BJJ", "Sparring BJJ", DayOfWeek.THURSDAY, Matta.MATTA_2,LocalTime.of(20, 15), LocalTime.of(21, 15), trainer8);

            // Fredag
            createOrUpdateTrainingClass("Thaiboxning", "Thaiboxning", DayOfWeek.FRIDAY, Matta.MATTA_1,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer1);
            createOrUpdateTrainingClass("Barn Thaiboxning", "Barn Thaiboxning", DayOfWeek.FRIDAY, Matta.MATTA_1,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer3);
            createOrUpdateTrainingClass("Nybörjare Thaiboxning", "Nybörjare Thaiboxning", DayOfWeek.FRIDAY, Matta.MATTA_1,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer3);
            createOrUpdateTrainingClass("Forts/Avanc Thaiboxning", "Forts/Avanc Thaiboxning", DayOfWeek.FRIDAY, Matta.MATTA_1,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer5);
            createOrUpdateTrainingClass("Sparring Thaiboxning", "Sparring Thaiboxning", DayOfWeek.FRIDAY, Matta.MATTA_1,LocalTime.of(20, 15), LocalTime.of(21, 15), null);

            createOrUpdateTrainingClass("Fys – Morgonpass", "Fys – Morgonpass", DayOfWeek.FRIDAY, Matta.MATTA_2,LocalTime.of(6, 30), LocalTime.of(7, 30), trainer6);
            createOrUpdateTrainingClass("BJJ Lunchpass", "BJJ Lunchpass", DayOfWeek.FRIDAY, Matta.MATTA_2,LocalTime.of(12, 0), LocalTime.of(13, 15), trainer7);
            createOrUpdateTrainingClass("Barn BJJ", "Barn BJJ", DayOfWeek.FRIDAY, Matta.MATTA_2,LocalTime.of(16, 0), LocalTime.of(17, 15), trainer7);
            createOrUpdateTrainingClass("Nybörjare BJJ", "Nybörjare BJJ", DayOfWeek.FRIDAY, Matta.MATTA_2,LocalTime.of(17, 30), LocalTime.of(18, 45), trainer7);
            createOrUpdateTrainingClass("Forts/Avanc BJJ", "Forts/Avanc BJJ", DayOfWeek.FRIDAY, Matta.MATTA_2,LocalTime.of(19, 0), LocalTime.of(20, 15), trainer7);
            createOrUpdateTrainingClass("Sparring BJJ", "Sparring BJJ", DayOfWeek.FRIDAY, Matta.MATTA_2,LocalTime.of(20, 15), LocalTime.of(21, 15), trainer8);

            // Lördag
            createOrUpdateTrainingClass("Boxning", "Boxning - Alla nivåer", DayOfWeek.SATURDAY, Matta.MATTA_1,LocalTime.of(8, 0), LocalTime.of(9, 30), trainer2);
            createOrUpdateTrainingClass("Thaiboxning", "Thaiboxning - Alla nivåer", DayOfWeek.SATURDAY, Matta.MATTA_1,LocalTime.of(10, 0), LocalTime.of(12, 0), trainer1);


            createOrUpdateTrainingClass("BJJ", "BJJ - Alla nivåer", DayOfWeek.SATURDAY, Matta.MATTA_2,LocalTime.of(8, 0), LocalTime.of(9, 30), trainer7);
            createOrUpdateTrainingClass("BJJ", "BJJ - Alla nivåer", DayOfWeek.SATURDAY, Matta.MATTA_2,LocalTime.of(10, 0), LocalTime.of(12, 0), trainer8);

            // Söndag
            createOrUpdateTrainingClass("Boxning", "Boxning - Alla nivåer", DayOfWeek.SUNDAY, Matta.MATTA_1,LocalTime.of(8, 0), LocalTime.of(9, 30), trainer4);
            createOrUpdateTrainingClass("Thaiboxning", "Thaiboxning - Alla nivåer", DayOfWeek.SUNDAY, Matta.MATTA_1,LocalTime.of(10, 0), LocalTime.of(12, 0), trainer1);


            createOrUpdateTrainingClass("BJJ", "BJJ - Alla nivåer", DayOfWeek.SUNDAY, Matta.MATTA_2,LocalTime.of(8, 0), LocalTime.of(9, 30), trainer7);
            createOrUpdateTrainingClass("BJJ", "BJJ - Alla nivåer", DayOfWeek.SUNDAY, Matta.MATTA_2,LocalTime.of(10, 0), LocalTime.of(12, 0), trainer8);


            System.out.println("Träningsklasser har skapats.");
        } else {
            System.out.println("Träningsklasser finns redan – seedning hoppades över.");
        }
    }

}
