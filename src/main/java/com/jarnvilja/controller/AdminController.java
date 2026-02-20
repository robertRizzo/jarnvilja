package com.jarnvilja.controller;

import com.jarnvilja.dto.BookingStatsDTO;
import com.jarnvilja.dto.MemberStatsDTO;
import com.jarnvilja.dto.TrainingClassStatsDTO;
import com.jarnvilja.model.*;
import com.jarnvilja.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/adminPage")
public class AdminController {

    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }



    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = adminService.createUser(user);
        if (createdUser == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
       return adminService.deleteUser(id);
    }

    /*
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

     */


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserId(@PathVariable Long id) {
        User user = adminService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping("/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = adminService.getUsersByRole(role);

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @PatchMapping("/{id}/role")
    public ResponseEntity<User> assignRoleToUser (@PathVariable Long id, @RequestBody Role role) {
        User updatedUser  = adminService.assignRoleToUser (id, role);
        if (updatedUser  == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUser , HttpStatus.OK);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<User> resetUserPassword(@PathVariable Long id, @RequestBody String newPassword) {
        User updatedUser = adminService.resetUserPassword(id, newPassword);

        if (updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUser , HttpStatus.OK);
    }

    @PostMapping("/classes/{classId}/trainer/{trainerId}")
    public ResponseEntity<TrainingClass> assignTrainerToClass(@PathVariable Long classId, @PathVariable Long trainerId) {
        TrainingClass updatedTrainingClass = adminService.assignTrainerToClass(classId, trainerId);
        if (updatedTrainingClass == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedTrainingClass, HttpStatus.OK);
    }

    @DeleteMapping("/classes/{classId}/trainer/{trainerId}")
    public ResponseEntity<String> removeTrainerFromClass(@PathVariable Long classId, @PathVariable Long trainerId) {
        String result = adminService.removeTrainerFromClass(classId, trainerId);

        if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); // Returnera 404 om klassen eller tränaren inte hittas
        } else if (result.contains("removed")) {
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT); // Returnera 204 No Content om tränaren tas bort
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST); // Returnera 400 Bad Request om tränaren inte var tilldelad
        }
    }

    @GetMapping("/classes/{classId}/trainer")
    public ResponseEntity<User> getTrainerFromClass(@PathVariable Long classId) {
        Optional<User> trainerOpt = adminService.getTrainerFromClass(classId);

        if (trainerOpt.isPresent()) {
            return new ResponseEntity<>(trainerOpt.get(), HttpStatus.OK); // Returnera 200 OK med tränaren
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returnera 404 om klassen inte hittas eller ingen tränare är tilldelad
    }

    @GetMapping("/trainers")
    public ResponseEntity<List<User>> getAllTrainers() {
        List<User> trainers = adminService.getAllTrainers();
        return new ResponseEntity<>(trainers, HttpStatus.OK); // Returnera 200 OK med listan av tränare
    }



    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = adminService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK); // Returnera 200 OK med listan av bokningar
    }


    @PostMapping("/deleteBooking/{bookingId}")
    public RedirectView deleteBooking(@PathVariable Long bookingId, @RequestParam("_method") String method) {
        if ("delete".equalsIgnoreCase(method)) {
            String result = adminService.deleteBooking(bookingId);

            if (result.contains("not found")) {
                // Hantera fallet där bokningen inte hittas, kanske logga ett meddelande
                return new RedirectView("/adminPage?error=notfound");
            }

            // Om bokningen tas bort, omdirigera till adminPage med ett meddelande
            return new RedirectView("/adminPage?success=deleted");
        }

        // Om metoden inte är tillåten, omdirigera till adminPage med ett felmeddelande
        return new RedirectView("/adminPage?error=methodnotallowed");
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        try {
            Booking booking = adminService.getBookingById(id);
            return new ResponseEntity<>(booking, HttpStatus.OK); // Returnera 200 OK med bokningen
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returnera 404 om bokningen inte hittas
        }
    }


    @PatchMapping("/classes/{trainingClassId}/bookings")
    public ResponseEntity<List<Booking>> cancelAllBookingsForClass(@PathVariable Long trainingClassId) {
        List<Booking> cancelledBookings = adminService.cancelAllBookingsForClass(trainingClassId);
        return new ResponseEntity<>(cancelledBookings, HttpStatus.OK); // Returnera 200 OK med de avbokade bokningarna
    }


    @GetMapping("/bookings/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<Booking> bookings = adminService.getBookingsByStatus(status);
        return new ResponseEntity<>(bookings, HttpStatus.OK); // Returnera 200 OK med bokningarna
    }


    @DeleteMapping("/bookings/expired")
    public ResponseEntity<String> removeExpiredBookings() {
        adminService.removeExpiredBookings();
        return new ResponseEntity<>("Expired bookings removed", HttpStatus.OK); // Returnera 200 OK med ett meddelande
    }

    @GetMapping("/booking-stats")
    public ResponseEntity<BookingStatsDTO> getBookingStats() {
        try {
            // Anropa tjänsten för att hämta bokningsstatistik
            BookingStatsDTO stats = adminService.getBookingStats();
            // Returnera statistiken med HTTP-status 200 OK
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Logga felet (kan använda en logger här)
            // Returnera en 500 Internal Server Error om något går fel
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<MemberStatsDTO> getMemberStats() {
        try {
            // Anropa tjänsten för att hämta medlemsstatistik
            MemberStatsDTO stats = adminService.getMemberStats();
            // Returnera statistiken med HTTP-status 200 OK
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Logga felet (kan använda en logger här)
            // Returnera en 500 Internal Server Error om något går fel
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/classes/{classId}/bookings/total")
    public ResponseEntity<Long> getTotalBookingsForClass(@PathVariable Long classId) {
        try {
            // Anropa tjänsten för att hämta totala bokningar för klassen
            Long totalBookings = Long.valueOf(adminService.getTotalBookingsForClass(classId));
            // Returnera det totala antalet bokningar med HTTP-status 200 OK
            return ResponseEntity.ok(totalBookings);
        } catch (Exception e) {
            // Logga felet (kan använda en logger här)
            // Returnera en 500 Internal Server Error om något går fel
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/period")
    public ResponseEntity<List<Booking>> getBookingsByPeriod(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Anropa tjänsten för att hämta bokningar inom den angivna perioden
            List<Booking> bookings = adminService.getBookingsByPeriod(startDate, endDate);
            // Returnera bokningarna med HTTP-status 200 OK
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            // Logga felet (kan använda en logger här)
            // Returnera en 500 Internal Server Error om något går fel
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{memberId}/bookings")
    public ResponseEntity<List<Booking>> getAllBookingsForMember(@PathVariable Long memberId) {
        try {
            // Anropa tjänsten för att hämta alla bokningar för medlemmen
            List<Booking> bookings = adminService.getAllBookingsForMember(memberId);
            // Returnera bokningarna med HTTP-status 200 OK
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            // Logga felet (kan använda en logger här)
            // Returnera en 500 Internal Server Error om något går fel
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/classes/stats")
    public ResponseEntity<List<TrainingClassStatsDTO>> getClassStats() {
        try {
            // Anropa tjänsten för att hämta statistik för klasser
            Map<String, Long> classStatsMap = adminService.getClassStats();
            List<TrainingClassStatsDTO> classStats = classStatsMap.entrySet().stream()
                    .map(entry -> new TrainingClassStatsDTO(Long.valueOf(entry.getKey()), entry.getValue().intValue()))
                    .collect(Collectors.toList());
            // Returnera statistiken med HTTP-status 200 OK
            return ResponseEntity.ok(classStats);
        } catch (Exception e) {
            // Logga felet (kan använda en logger här)
            // Returnera en 500 Internal Server Error om något går fel
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/editUser/{id}")
    public String editUser (@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id);
        if (user == null) {
            return "error";
        }
            model.addAttribute("user", user);
            return "editUser";
        }

    @PostMapping("/editUser/{id}")
    public String updateUser (@PathVariable Long id, @ModelAttribute User user) {
        adminService.updateUser (id, user); // Uppdatera användaren
        return "redirect:/adminPage"; // Omdirigera tillbaka till admin-sidan efter uppdatering
    }
}
