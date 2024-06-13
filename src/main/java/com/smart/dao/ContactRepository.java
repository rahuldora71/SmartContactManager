package com.smart.dao;

import com.smart.entities.Contact;
import com.smart.entities.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	//pagination
	@Query("from Contact as c where c.user.id=:userId")
	//Pageable have two perameters = 1. page number, 2. size of page
	public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable );

//	This method is for searching contact by name
//	public List<Contact> findByNameContainingAndUser(String name, User user);

	@Query("from Contact as c where (c.name like %:keyword% or c.secondName like %:keyword%) and c.user = :user")
	public List<Contact> searchByNameOrSecondNameAndUser(@Param("keyword") String keyword, @Param("user") User user);


}
