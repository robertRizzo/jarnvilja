package com.jarnvilja.seeder;

import com.jarnvilja.model.User;
import com.jarnvilja.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.jarnvilja.model.Role.*;

@Component
@Order(1)
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(ROLE_ADMIN);

            System.out.println("Admin password (encoded): " + admin.getPassword()); // Debug-utskrift

            User member = new User();
            member.setUsername("member");
            member.setEmail("robert.rizzo@hotmail.com");
            member.setPassword(passwordEncoder.encode("member123"));
            member.setRole(ROLE_MEMBER);

            System.out.println("Member password (encoded): " + member.getPassword()); // Debug-utskrift

            User trainer = new User();
            trainer.setUsername("trainer");
            trainer.setEmail("trainer@example.com");
            trainer.setPassword(passwordEncoder.encode("trainer123"));
            trainer.setRole(ROLE_TRAINER);

            System.out.println("Trainer password (encoded): " + trainer.getPassword()); // Debug-utskrift

            userRepository.save(admin);
            userRepository.save(member);
            userRepository.save(trainer);

            System.out.println("Tre användare skapades: admin, member, trainer");
        } else {
            System.out.println("Användare finns redan – seedning hoppades över.");
        }
    }
}
