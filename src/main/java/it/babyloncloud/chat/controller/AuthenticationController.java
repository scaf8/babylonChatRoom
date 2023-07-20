package it.babyloncloud.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.babyloncloud.chat.controller.service.CredentialsService;
import it.babyloncloud.chat.model.Credentials;
import it.babyloncloud.chat.model.User;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;

	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		this.checkUser(model);
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
		this.checkUser(model);
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
				model.addAttribute("errorMessage", "L'username inserito esiste già");
				return "formRegisterUser.html";
			} 
			catch (Exception e) {
				model.addAttribute("errorMessage", "Si è verificato un errore durante la registrazione");
				return "formRegisterUser.html";
			}
		}

		return "formRegisterUser.html";
	}

	private void checkUser(Model model) {
		boolean isLogged = false;
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		if(this.credentialsService.getCredentials(loggedInUser.getName()) != null) {
			isLogged = true;
			model.addAttribute("username", loggedInUser.getName());
		}
		model.addAttribute("isLogged", isLogged);
	}

}
