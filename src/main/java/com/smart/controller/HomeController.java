package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		User user = new User();
		user.setName("Rahul Dora");
		user.setEmail("rahuldoraksp02@gmail.com");
		userRepository.save(user);
		return"working";
	}
}
