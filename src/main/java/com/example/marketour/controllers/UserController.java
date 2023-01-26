package com.example.marketour.controllers;

import com.example.marketour.model.dtos.RegisterUser;
import com.example.marketour.model.entities.City;
import com.example.marketour.model.entities.Country;
import com.example.marketour.model.entities.User;
import com.example.marketour.model.entities.UserType;
import com.example.marketour.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    Object login(@ModelAttribute("user") User requestUser, HttpServletRequest request) {
        var session = request.getSession(true);
        if (requestUser.getUsername() == null || requestUser.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username/password needed!");
        }
        final var user = userService.checkCredentialsExist(requestUser.getUsername(), requestUser.getPassword());
        if (user != null) {
            if (session.getAttribute("user") == null) {
                session.setAttribute("user", user);
            }
            return "redirect:/main";
        } else {
            // add attribute to the request to show the error message in the login html template
            session.setAttribute("errorMessage", "Username or password is invalid.");
            return "redirect:/login";
        }
    }


    @PostMapping("/register")
    Object register(@ModelAttribute("user") RegisterUser registerUser, HttpServletRequest request) {
        var tempUser = new User();
        tempUser.setUsername(registerUser.getUsername());
        tempUser.setPassword(registerUser.getPassword());
        tempUser.setCountry(Country.valueOf(registerUser.getCountry()));
        tempUser.setCity(City.valueOf(registerUser.getCity()));
        tempUser.setUserType(UserType.valueOf(registerUser.getUserType()));
        final var session = request.getSession(true);
        if (tempUser.getUsername() == null || tempUser.getUserType() == null || tempUser.getPassword() == null || tempUser.getUsername().isEmpty() || tempUser.getPassword().isEmpty()) {
            session.setAttribute("registerErrorMessage", "Username and password are required.");
            return "redirect:/register";
        }
        final var user = userService.checkUsernameExists(tempUser.getUsername());
        if (user != null) {
            session.setAttribute("registerErrorMessage", "Username already taken.");
            return "redirect:/register";
        } else {
            final var addedUser = userService.createUser(tempUser.getUsername(), tempUser.getPassword(), tempUser.getUserType(), 0.0, tempUser.getCity(), tempUser.getCountry());
            session.setAttribute("user", addedUser);
            return "redirect:/main";
        }
    }

    @PostMapping("/logout")
    ResponseEntity<Object> logout(HttpServletRequest request) {
        final var session = request.getSession();
        final var user = (User) session.getAttribute("user");
        if (user != null) {
            session.invalidate();
            return ResponseEntity.ok("User logged out!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User already logged out!");
        }
    }
}
