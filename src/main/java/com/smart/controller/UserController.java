package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.smart.dao.ContactRepository;
import com.smart.entities.Contact;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
				contact.setImage("contact_profile_default.png");
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
	//per page  n=5
	//current page =0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page, Model model , Principal principal){
		model.addAttribute("title","Contacts-Smart Contact Manager");
		if (page<=0){
			page = 0;
		}
		// Send Contacts list
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		int pageSize=4;
		// it have current page number and page size
		Pageable pageable= PageRequest .of(page,pageSize);
		Page<Contact> contact = this.contactRepository.findContactByUser(user.getId(),pageable);
		model.addAttribute("contacts",contact);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contact.getTotalPages());
		return "normal/show_contacts";
	}

	// Showing particular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") int cId, Model model,Principal principal){
		model.addAttribute("title","Contact Details-Smart Contact Manager");
		System.out.println(cId);
		try {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact= contactOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (contact.getUser().getId()==user.getId()) {
			model.addAttribute("contact", contact);
		}
		}catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			//error messege
			model.addAttribute("contact", null);

		}
		return "normal/contact_detail";
	}

	//handler to delete contact
	@GetMapping("/show-contacts/{cId}/delete")
	public String deleteContact(@PathVariable("cId") Integer cId, Principal principal , HttpSession session) {
		try {
			Optional<Contact> contactOptional = this.contactRepository.findById(cId);
			Contact contact= contactOptional.get();
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			if (contact.getUser().getId()==user.getId()) {
				System.out.println("contact image  is : " + contact.getImage());
				//delete the profile image
				if (!contact.getImage().equals("contact_profile_default.png")) {
					System.out.println(contact.getImage());
					File saveFile = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + contact.getImage());
					Files.deleteIfExists(path);
				}

				this.contactRepository.deleteById(cId);

				session.setAttribute("message", new Message(contact.getName() + " " + contact.getSecondName() + " Successfully Deleted", "alert-success"));
			}/*else {
			            session.setAttribute("message", new Message("You can't delete this contact", "alert-danger"));
		}*/
		}catch (Exception e) {
            System.out.println("ERROR" + e.getMessage());
            //error messege
            session.setAttribute("message", new Message("Something went wrong!!", "alert-danger"));
        }
		return "redirect:/user/show-contacts/0";
	}

	//Open Update Form Handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId")int cId, Model model){
		model.addAttribute("title","Update-Smart Contact Manager");

		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}
	//save updated contact
	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("contact_image") MultipartFile file, Principal principal, HttpSession session) {

		try {
			Optional<Contact> contactOptional = this.contactRepository.findById(contact.getcId());
			Contact previous_contact=contactOptional.get();
			if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/images").getFile();
				if (!previous_contact.getImage().equals("contact_profile_default.png")) {
					Path deletepath = Paths.get(saveFile.getAbsolutePath() + File.separator + previous_contact.getImage());
					Files.deleteIfExists(deletepath);
				}
				contact.setImage( contact.getPhone() +file.getOriginalFilename());
				Path savepath = Paths.get(saveFile.getAbsolutePath() + File.separator + contact.getPhone() + file.getOriginalFilename());
				Files.copy(file.getInputStream(),savepath, StandardCopyOption.REPLACE_EXISTING);
			}else {


			contact.setImage(previous_contact.getImage());
			}
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message(contact.getName() + " " + contact.getSecondName() + " Successfully Updated", "alert-success"));

		}catch (Exception e)
		{
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!!", "alert-danger"));
		}

		return "redirect:/user/"+contact.getcId()+"/contact";
	}

	//Profile Handler
	@GetMapping("/profile")
	public  String yourProfile(Model model, Principal principal) {
		model.addAttribute("title", "Profile-Smart Contact Manager");

		User user = this.userRepository.getUserByUserName(principal.getName());
		model.addAttribute("user",user);



		return "normal/profile";
	}

	//open Setting handler
	@GetMapping("/setting")
	public String settingHandler(Model model){
		model.addAttribute("title", "Setting-Smart Contact Manager");

		return "normal/settings";
	}
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword ,@RequestParam("newPassword") String newPassword,@RequestParam("confirmPassword") String confirmPassword, Principal principal, HttpSession session) {
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		if (this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword()))
		{
			System.out.println("old password is correct");
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));

			this.userRepository.save(currentUser);
			            session.setAttribute("message", new Message("Password Successfully Changed", "alert-success"));

			if (newPassword != confirmPassword){
				session.setAttribute("message", new Message("New Password and Confirm Password is not same", "alert-danger"));
                return "redirect:/user/setting";
			}else {
				                session.setAttribute("message", new Message("Password Successfully Changed", "alert-success"));
				return "redirect:/user/index";			}
		}
		else {
			System.out.println("old password is not correct");
			            session.setAttribute("message", new Message("Old Password is not correct", "alert-danger"));
			return "redirect:/user/setting";
		}

	}

}
