package com.jarnvilja.controller;

import com.jarnvilja.model.TrainingClass;
import com.jarnvilja.model.User;
import com.jarnvilja.repository.TrainingClassRepository;
import com.jarnvilja.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/trainerPage")
public class TrainerPageController {

    private final UserRepository userRepository;
    private final TrainingClassRepository trainingClassRepository;

    public TrainerPageController(UserRepository userRepository, TrainingClassRepository trainingClassRepository) {
        this.userRepository = userRepository;
        this.trainingClassRepository = trainingClassRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public String trainerPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User trainer = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        List<TrainingClass> classes = trainingClassRepository.findByTrainerId(trainer.getId());
        long totalBookings = classes.stream()
                .mapToLong(tc -> tc.getBookings() != null ? tc.getBookings().size() : 0)
                .sum();

        model.addAttribute("trainer", trainer);
        model.addAttribute("classes", classes);
        model.addAttribute("totalBookings", totalBookings);

        return "trainerPage";
    }
}
