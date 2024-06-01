package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	
	// public BCryptPasswordEncoder bCryptPasswordEncoder() {
	// 	return new BCryptPasswordEncoder();
	// }
	@Autowired
	private UserRepository userRepository;

	// home handler
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";

	}

	// about handler
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-Smart Contact Manager");

		return "about";
	}

	// signup handler
	@GetMapping("/signup")
	public String signup(Model model, HttpSession session) {
		Object message = session.getAttribute("message");
		model.addAttribute("title", "SignUp-Smart Contact Manager");

		if (message != null) {
			model.addAttribute("message", message);
			session.removeAttribute("message");
		}
		model.addAttribute("user", new User());
		return "signup";
	}

	// this handler for registring user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		model.addAttribute("title", "SignUp-Smart Contact Manager");

		
		
		try {
			
			if (result.hasErrors()) {
			System.out.println("Error = "+result.toString());
			model.addAttribute(user);
			
			return"signup";}
				
			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			
			
			user.setRole("USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			System.out.println("this is the password befor bcrypt password encoder : "+user.getPassword());
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			System.out.println("this is the password after bcrypt password encoder : "+user.getPassword());
			System.out.println("Agreement : " + agreement);
//			System.out.println("User:::::"+user);
			User save = this.userRepository.save(user);
			System.out.println(save);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Register !!", "alert-success"));

			return "redirect:/signup";
		}
		catch (DataIntegrityViolationException e) {
			model.addAttribute("user", user);
	        session.setAttribute("message", new Message("Email already registered!", "alert-danger"));
	        return "signup";
	    }
		catch (Exception e) {

			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}


	//handler for custom login

	@GetMapping("/login")
	public String loginhandler(Model model) {
		model.addAttribute("title", "Login-Smart Contact Manager");
		return "login";
	}
	

}
