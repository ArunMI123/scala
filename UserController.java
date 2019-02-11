package com.demo.mvp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.demo.mvp.models.User;
import com.demo.mvp.repositories.UserRepository;
import com.demo.mvp.services.UserService;
import com.demo.mvp.util.CustomErrorType;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
	private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser()
    {
    	List<User> users = userService.findAllUser(); 
    	if(users.isEmpty())
    		return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
    	
    	return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }
    
	@GetMapping(value = "/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") int id) {
		User user = userService.findById(id);
		if (user == null) {
			return new ResponseEntity<>(new CustomErrorType("User with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
//	@RequestMapping(value = "/user/{id}",method=RequestMethod.GET)
//	@ResponseBody
//	public User getUser(@PathVariable("id") Integer id) {
//		return userService.findById(id);
//	}

    @PostMapping(value = "/user")
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		if (userService.isUserExist(user).equals(user.getFirstName())) {
			return new ResponseEntity<>(new CustomErrorType("Unable to create. A User with name " + 
			user.getFirstName() + " already exist."),HttpStatus.CONFLICT);
		}
		userService.createUser(user);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

    @PutMapping(value = "/user/{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody User user) {
		User currentUser = userService.findById(id);

		if (currentUser == null) {
			return new ResponseEntity<>(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setEmail(user.getEmail());

		userService.updateUser(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}
    
    @DeleteMapping(value = "/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
		
		User user = userService.findById(id);
		if (user == null) {
			return new ResponseEntity<>(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		userService.deleteUserById(id);
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}
    
    @DeleteMapping(value = "/user")
	public ResponseEntity<User> deleteAllUsers() {

		userService.deleteAllUsers();
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}
    
}
