package com.ebix.easi.authorizationserver.model.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebix.easi.authorizationserver.dto.LoginDTO;
import com.ebix.easi.authorizationserver.exception.EasiBusinessException;
import com.ebix.easi.authorizationserver.model.entities.Authorization;
import com.ebix.easi.authorizationserver.model.entities.User;
import com.ebix.easi.authorizationserver.model.service.AuthorizationServerService;

@RestController
@RequestMapping("/authorization")
public class AuthorizationServerRestController {
	
	@Autowired
	private AuthorizationServerService service;
	
	@PostMapping(path="/register", produces = "application/json")
	public ResponseEntity<?> register(@RequestBody @Valid User user) {
		try {  
			service.createUserAccess(user);
			return ResponseEntity.status(HttpStatus.CREATED).build();
			
		} catch (EasiBusinessException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@PostMapping(path = "/login", produces = "application/json")
	public ResponseEntity<?> login(@RequestBody @Valid LoginDTO login) {
		try {
			Authorization authorization = service.login(login.getUsername(), login.getPassword());
			if(authorization != null) {
				return ResponseEntity.accepted().body(authorization.getJwt());
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@GetMapping(path = "/check", produces = "application/json")
	public ResponseEntity<?> validateToken(@RequestHeader("token") String token) {
		try {
			if(service.tokenIsValid(token)) {
				return ResponseEntity.ok().build();
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}

}
