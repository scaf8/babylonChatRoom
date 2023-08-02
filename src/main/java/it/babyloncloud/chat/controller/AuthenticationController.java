package it.babyloncloud.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.babyloncloud.chat.controller.service.CredentialsService;
import it.babyloncloud.chat.controller.session.SessionData;
import it.babyloncloud.chat.model.Credentials;
import it.babyloncloud.chat.model.User;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private SessionData sessionData;
	
	@GetMapping("/checkUserStatus")
	@ResponseBody
	public boolean checkUserStatus(@RequestParam String username) {
		Credentials credentials = this.credentialsService.getCredentials(username);
		if(credentials != null)
			return credentials.getUser().getBlocked();
		return false;
	}

	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		boolean isLogged = false;
		Credentials credentials = this.sessionData.getLoggedCredentials();
		if(credentials != null) {
			isLogged = true;
			model.addAttribute("username", credentials.getUsername());
		}
		model.addAttribute("isLogged", isLogged);
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser.html";
	}

	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin.html";
	}

	@GetMapping(value = "/") 
	public String index(Model model) {
		boolean isLogged = false;
		User user = this.sessionData.getLoggedUser();
		if(user != null) {
			if(user.getBlocked()) {
				model.addAttribute("blockedMessage", "Questo account non è autorizzato ad accedere");
				return "formLogin.html";
			}
			else {
				isLogged = true;
				model.addAttribute("username", user.getUsername());
			}
		}
		model.addAttribute("isLogged", isLogged);
		return "index.html";
	}

	@PostMapping(value = { "/register" })
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult userBindingResult, @Valid
			@ModelAttribute("credentials") Credentials credentials,
			BindingResult credentialsBindingResult,
			Model model) {

		// se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
		if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
			credentials.setUser(user);
			try{
				credentialsService.saveCredentials(credentials);
				model.addAttribute("user", user);
				return "registrationSuccessful.html";
			}
			catch (DataIntegrityViolationException e) {
				model.addAttribute("errorMessage", "L'username e/o la mail inseriti esistono già");
				return "formRegisterUser.html";
			}
			catch (Exception e) {
				model.addAttribute("errorMessage", "Si è verificato un errore durante la registrazione");
				return "formRegisterUser.html";
			}
		}

		return "formRegisterUser.html";
	}

//	private boolean checkUser(Model model) {
//		boolean isLogged = false;
//
//		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
//		Credentials inputCredentials = this.credentialsService.getCredentials(loggedInUser.getName());
//		if(inputCredentials != null) {
//			if(!inputCredentials.getUser().getBlocked()) {
//				isLogged = true;
//				model.addAttribute("username", loggedInUser.getName());
//			}
//		}
//		model.addAttribute("isLogged", isLogged);
//		return isLogged;
//	}
	
//	private void checkUser(Model model) {
//		boolean isLogged = false;
//		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
//		if(this.credentialsService.getCredentials(loggedInUser.getName()) != null) {
//			isLogged = true;
//			model.addAttribute("username", loggedInUser.getName());
//		}
//		model.addAttribute("isLogged", isLogged);
//	}

}
