package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@Controller
public class ForgotController {
    //email Id form open Handler
    @RequestMapping("/forgot-password")
    public String openEmailForm(Model model) {
        model.addAttribute("title", "Forgot Password");

        return "forgot-password";
    }
    @PostMapping("/send-otp")
    @ResponseBody
    public String otpProcess(@RequestParam("email") String email, Model model) {
        model.addAttribute("title","Forgot Password");
        //generate OTP of 4 digit
        Random random = new Random();
        int otp = random.nextInt(8999) + 1000;

        System.out.println("OTP"+otp);
        model.addAttribute("email", email);
        return "OTP : "+otp;
    }
}
