package com.jarnvilja.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

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

    @GetMapping("/integritetspolicy")
    public String showPrivacyPolicyPage() {
        return "integritetspolicy";
    }

    @GetMapping("/om_projektet")
    public String showAboutProjectPage() {
        return "om_projektet";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
}
