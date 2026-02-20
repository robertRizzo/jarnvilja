package com.jarnvilja.controller;

import com.jarnvilja.dto.BookingDTO;
import com.jarnvilja.dto.MemberProfileDTO;
import com.jarnvilja.dto.MembershipStatsDTO;
import com.jarnvilja.model.Booking;
import com.jarnvilja.model.TrainingClass;
import com.jarnvilja.model.User;
import com.jarnvilja.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/memberPage")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<User> createMember(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User createdUser  = memberService.createMember(user);
        return new ResponseEntity<>(createdUser , HttpStatus.CREATED);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<User> updateMember(@PathVariable Long memberId, @RequestBody User user) {
        User updatedUser  = memberService.updateMember(memberId, user);
        return new ResponseEntity<>(updatedUser , HttpStatus.OK);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<User> getMemberById(@PathVariable Long memberId) {
        Optional<User> user = Optional.ofNullable(memberService.getMemberById(memberId));
        return user.map(ResponseEntity::ok) // Om medlemmen finns, returnera 200 OK med medlemmen
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Annars returnera 404 NOT FOUND
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllMembers() {
        List<User> members = memberService.getAllMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getMemberByEmail(@PathVariable String email) {
        Optional<User> user = Optional.ofNullable(memberService.getMemberByEmail(email));
        return user.map(ResponseEntity::ok) // Om medlemmen finns, returnera 200 OK med medlemmen
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Annars returnera 404 NOT FOUND
    }

    @PatchMapping("/{memberId}/password")
    public ResponseEntity<User> updateMemberPassword(@PathVariable Long memberId, @RequestBody String newPassword) {
        User updatedUser  = memberService.updateMemberPassword(memberId, newPassword);
        return new ResponseEntity<>(updatedUser , HttpStatus.OK);
    }

    @PostMapping("/{memberId}/bookings")
    public String createBooking(@PathVariable Long memberId, @RequestParam Long trainingClassId, Model model, RedirectAttributes redirectAttributes) {
        // Kontrollera bokningsstatus
        String bookingMessage = memberService.checkBookingStatus(memberId, trainingClassId);

        if (bookingMessage != null) {
            // Om användaren redan har bokat passet, skicka meddelandet via RedirectAttributes
            redirectAttributes.addFlashAttribute("bookingMessage", bookingMessage);
            return "redirect:/memberPage";  // Omdirigera till medlemssidan med meddelandet
        }

        // Om inga problem, skapa bokningen
        memberService.createBooking(memberId, trainingClassId);

        return "redirect:/memberPage"; // Om bokningen skapades, omdirigera till medlemssidan
    }

    @PatchMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long bookingId) {
        Booking confirmedBooking = memberService.confirmBooking(bookingId);
        return new ResponseEntity<>(confirmedBooking, HttpStatus.OK);
    }

    @PostMapping("/bookings/{bookingId}")
    public RedirectView cancelBooking(@PathVariable Long bookingId, @RequestParam("_method") String method) {
        if ("delete".equalsIgnoreCase(method)) {
            memberService.cancelBooking(bookingId);
            return new RedirectView("/memberPage");
        }
        return new RedirectView("/error"); // Omdirigera till en fel-sida om metoden inte är DELETE
    }

    @GetMapping("/{userId}/bookings")
    public ResponseEntity<List<Booking>> getBookingsForMember(@PathVariable Long userId) {
        List<Booking> bookings = memberService.getBookingsForMember(userId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/{userId}/bookings/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookingsForMember(@PathVariable Long userId) {
        List<Booking> upcomingBookings = memberService.getUpcomingBookingsForMember(userId);
        return new ResponseEntity<>(upcomingBookings, HttpStatus.OK);
    }

    @GetMapping("/{userId}/bookings/past")
    public ResponseEntity<List<Booking>> getPastBookingsForMember(@PathVariable Long userId) {
        List<Booking> pastBookings = memberService.getPastBookingsForMember(userId);
        return new ResponseEntity<>(pastBookings, HttpStatus.OK);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long bookingId) {
        Booking booking = memberService.getBookingById(bookingId);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/available-classes")
    public ResponseEntity<List<TrainingClass>> getAvailableClasses() {
        List<TrainingClass> availableClasses = memberService.getAvailableClasses();
        return new ResponseEntity<>(availableClasses, HttpStatus.OK);
    }

    @GetMapping("/{memberId}/classes")
    public ResponseEntity<List<TrainingClass>> getAllClassesForMember(@PathVariable Long memberId) {
        List<TrainingClass> memberClasses = memberService.getAllClassesForMember(memberId);
        return new ResponseEntity<>(memberClasses, HttpStatus.OK);
    }


    @GetMapping("/{memberId}/profile")
    public ResponseEntity<MemberProfileDTO> getMemberProfile(@PathVariable Long memberId) {
        MemberProfileDTO memberProfile = memberService.getMemberProfile(memberId);
        return new ResponseEntity<>(memberProfile, HttpStatus.OK);
    }

    @GetMapping("/{memberId}/stats")
    public ResponseEntity<MembershipStatsDTO> getMembershipStats(@PathVariable Long memberId) {
        MembershipStatsDTO membershipStats = memberService.getMembershipStats(memberId);
        return new ResponseEntity<>(membershipStats, HttpStatus.OK);
    }
}


