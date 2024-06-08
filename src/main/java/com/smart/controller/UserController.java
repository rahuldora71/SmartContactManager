package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import com.smart.dao.ContactRepository;
import com.smart.entities.Contact;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
  	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	//Method for adding common data to the model
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String username = principal.getName();
		System.out.println("Dashboard is working Correctly");
		System.out.println("Username is : " + username);
		User user = this.userRepository.getUserByUserName(username);
		model.addAttribute("user", user);
		System.out.println(user);
	}



	//home Dashboard handler
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "Dashboard-Smart Contact Manager");

		return"normal/user_dashboard";
	}




	// open add contact form handler
	@GetMapping("/add-contact")
	public String addContactForm(Model model ) {
		model.addAttribute("title", "Add Contact-Smart Contact Manager");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	//process contact form handler
	@PostMapping("/process-contact")
	public String processContactForm(@ModelAttribute Contact contact , @RequestParam("contact_image") MultipartFile file, Principal principal, HttpSession session) {
		try {


			String name = principal.getName();
			System.out.println("name is : " + name);
			User user = this.userRepository.getUserByUserName(name);
			//procwssing and uploading image file
			if(file.isEmpty()){
				// If the file is empty
				System.out.println("File is empty");
			}
			else {
				// uploading the file and update the name to contact
				System.out.println(contact.getcId());
				contact.setImage( contact.getPhone() +file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + contact.getPhone() + file.getOriginalFilename());
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);

			System.out.println("contact is : " + contact);
			System.out.println("Added to data base");
			// messege success
			session.setAttribute("message", new Message(contact.getName()+" "+contact.getSecondName()+" Successfully Added", "alert-success"));



			return "redirect:/user/add-contact";
		}catch (Exception e){
			System.out.println("ERROR"+e.getMessage());
			//error messege
			session.setAttribute("message", new Message("Something went wrong!!", "alert-danger"));

			return "redirect:/user/add-contact";
		}
	}
	//Show Contact Handler
	@GetMapping("/show-contacts")
	public String showContacts(Model model , Principal principal){
		model.addAttribute("title","Contacts-Smart Contact Manager");
		// Send Contacts list
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		List<Contact> contact = this.contactRepository.findContactByUser(user.getId());

		model.addAttribute("contacts",contact);

		return "normal/show_contacts";
	}
}
