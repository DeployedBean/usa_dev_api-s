package com.spring.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.auth.configurations.SecurityConfiguration;
import com.spring.auth.entities.RequestDto;
import com.spring.auth.entities.Responsedto;
import com.spring.auth.entities.Signupresponse;
import com.spring.auth.entities.User;
import com.spring.auth.service.AuthService;
import com.spring.auth.service.JwtServiceImpl;
import com.spring.auth.utils.CustomException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	SecurityConfiguration securityconfig;
	
	@Autowired
	AuthService authService;
	
	@Autowired
	JwtServiceImpl jwtServiceImpl;
	
	@PostMapping("/signup")
	public ResponseEntity<Signupresponse> signup(@RequestBody Signupresponse signupresponse) throws CustomException {
		try {
			if (signupresponse != null) {
				return authService.signup(signupresponse);
			} else {
				CustomException ce = new CustomException();
				ce.setMessage("Null");
				ce.setCode(10);
				// ce.printStackTrace();
				throw ce;
			}
		} catch (Exception e) {
			CustomException ce = new CustomException();
			if (e.getMessage().contains("Error")) {
				ce.setMessage(e.getMessage());
			} else {
				ce.setMessage(e.getMessage());
			}
			throw ce;
		}
	}
	@PostMapping(value = "/login")
	public Responsedto createAuthenticationToken(@RequestBody RequestDto obj) throws Exception {
		try {
			boolean matches = securityconfig.passwordEncoder().matches("123456","$2a$10$s8bcBomKx8quOwMvuzw3o.RjueDoI1x4juFjCbEChogKI7Uzp87vK");
			System.out.println(matches);
			Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                		obj.getUsername(),
	                		obj.getPassword()
	                )
	        );
			User user= (User) authentication.getPrincipal();
			String token = jwtServiceImpl.generateAccessToken(user);
			 return new Responsedto(user.getUserid().toString(),"Bearer " + token);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@GetMapping("/isjwtexpired")
	public Boolean isExpired(@RequestParam(name = "jwt") String jwtToken) {
		return jwtServiceImpl.isExpired(jwtToken);
	}
	
	@GetMapping(value = "/verify")
	public Boolean verifsyEmail() {
		return true;
	}
	
	
}
