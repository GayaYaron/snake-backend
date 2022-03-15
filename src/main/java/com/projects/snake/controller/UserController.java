package com.projects.snake.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projects.snake.controller.model.LoginBody;
import com.projects.snake.exception.NotFoundException;
import com.projects.snake.model.Design;
import com.projects.snake.model.User;
import com.projects.snake.service.UserService;
import com.projects.snake.service.response.LoginResponse;


@Controller
@RequestMapping(value = "user")
public class UserController {
	@Autowired
	private UserService service;
	
	@PostMapping(value = "/register")
	public ResponseEntity<LoginResponse> Register(@RequestBody LoginBody loginBody) {
		return ResponseEntity.ok(service.register(loginBody.getNickname(), loginBody.getPassword()));
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginBody loginBody) {
		return ResponseEntity.ok(service.login(loginBody.getNickname(), loginBody.getPassword()));
	}
	
	@PutMapping(value = "/chosen_design")
	public ResponseEntity<Void> setChosenDesign(@RequestParam Integer designId) {
		service.setChosenDesign(designId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping()
	public ResponseEntity<User> getUserDetail() {
		Optional<User> optionalUser = service.getUser();
		if(optionalUser.isEmpty()) {
			throw new NotFoundException("user");
		}
		return ResponseEntity.ok(optionalUser.get());
	}
	
	@PostMapping(value = "/buy_color_pack")
	public ResponseEntity<Void> buyColorPack(@RequestParam Integer colorId) {
		service.buyColorPack(colorId);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(value = "/design") 
	public ResponseEntity<Void> addDesign(@RequestBody Design design) {
		service.addDesign(design);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping(value = "/design")
	public ResponseEntity<Design> getChosenDesign() {
		return ResponseEntity.ok(service.getChosenDesign());
	}
	
	@DeleteMapping(value = "/design")
	public ResponseEntity<Void> deleteDesign(@RequestParam Integer designId) {
		service.deleteDesign(designId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping(value = "/design/all")
	public ResponseEntity<List<Design>> getAllDesigns() {
		return ResponseEntity.ok(service.getUserDesigns());
	}
	
	@PutMapping(value = "/coins")
	public ResponseEntity<Void> addCoins(@RequestParam int amount) {
		service.addCoins(amount);
		return ResponseEntity.ok().build();
	}
}
