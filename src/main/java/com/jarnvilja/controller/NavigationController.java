package com.jarnvilja.controller;

import com.jarnvilja.model.*;
import com.jarnvilja.service.AdminService;
import com.jarnvilja.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import com.jarnvilja.repository.UserRepository;
import com.jarnvilja.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NavigationController {

    private final MemberService memberService;
    private final AdminService adminService;

    public NavigationController(MemberService memberService, AdminService adminService) {
        this.memberService = memberService;
        this.adminService = adminService;
    }

    @GetMapping("/index")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/kontakt")
    public String showContactPage() {
        return "kontakt";
    }

    @GetMapping("/om_klubben")
    public String showAboutPage() {
        return "om_klubben";
    }

    @GetMapping("/traningsschema")
    public String showTrainingSchedulePage() {
        return "traningsschema";
    }

    @GetMapping("/tranare")
    public String showTrainersPage() {
        return "tranare";
    }

    @GetMapping("/bli_medlem")
    public String showJoinUsPage() {
        return "bli_medlem";
    }

    @GetMapping("/faq")
    public String showFAQPage() {
        return "faq";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @PostMapping("/login")
    public String processLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/adminPage";
            } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MEMBER"))) {
                return "redirect:/memberPage";
            }
        }
        return "redirect:/index";
    }

    @GetMapping("/memberPage")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public String memberPage(Model model,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) String sort,
                             @RequestParam(required = false) boolean reset) {

        // Hämta användarinformation
        String username = userDetails.getUsername();
        User member = memberService.getMemberByUsername(username);

        model.addAttribute("member", member);
        model.addAttribute("memberId", member.getId());
        model.addAttribute("username", username);

        // Hämta tillgängliga klasser
        List<TrainingClass> trainingClasses = memberService.getAvailableClasses();

        // Om reset är true, återställ till den ursprungliga listan
        if (!reset) {
            if (search != null && !search.isEmpty()) {
                trainingClasses = memberService.searchAvailableClasses(search);
            }

            if (sort != null) {
                trainingClasses = memberService.sortClasses(trainingClasses, sort);
            }
        }

        // Dela upp klasserna per matta
        List<TrainingClass> matta1Pass = trainingClasses.stream()
                .filter(tc -> tc.getMatta() == Matta.MATTA_1)
                .collect(Collectors.toList());

        List<TrainingClass> matta2Pass = trainingClasses.stream()
                .filter(tc -> tc.getMatta() == Matta.MATTA_2)
                .collect(Collectors.toList());

        // Hämta bokningar för medlemmen
        List<Booking> bookings = memberService.getBookingsForMember(member.getId());
        List<Booking> activeBookings = bookings.stream()
                .filter(booking -> !booking.getBookingStatus().equals(BookingStatus.CANCELLED))
                .collect(Collectors.toList());
        model.addAttribute("trainingClasses", trainingClasses);
        model.addAttribute("matta1Pass", matta1Pass);
        model.addAttribute("matta2Pass", matta2Pass);
        model.addAttribute("bookings", activeBookings);
        model.addAttribute("daysOfWeek", DayOfWeek.values());

        return "memberPage";
    }

    @GetMapping("/adminPage")
    public String adminPage(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("bookings", adminService.getAllBookings());
        return "adminPage";
    }
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
}