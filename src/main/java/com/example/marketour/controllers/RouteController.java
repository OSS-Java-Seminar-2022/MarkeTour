package com.example.marketour.controllers;

import com.example.marketour.model.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class RouteController {
    private final TourController tourController;
    private final ImageController imageController;
    private final UserController userController;
    private final TourPageController tourPageController;

    public RouteController(TourController tourController, ImageController imageController, UserController userController, TourPageController tourPageController) {
        this.tourController = tourController;
        this.imageController = imageController;
        this.userController = userController;
        this.tourPageController = tourPageController;
    }

    @GetMapping("/")
    public String indexPage() {
        return "home";
    }

    @GetMapping(value = "/login")
    String login(@ModelAttribute("user") User user) {
        return "login";
    }

    @GetMapping(value = "/pageCreate")
    String pageCreate(Model model, HttpServletRequest httpServletRequest, @RequestParam Map<String, String> params) {
        final var page = new TourPage();
        final var location = new Location();
        final var tour = new Tour();
        //Adding page to the existing tour
        if (params.containsKey("tourId")) {
            tour.setTourId(Long.valueOf(params.get("tourId")));
            final var pages = tourPageController.getAllTourPages(Long.valueOf(params.get("tourId"))).getBody();
            if (pages != null && !pages.isEmpty()) {
                model.addAttribute("page", pages.size());
            } else {
                model.addAttribute("page", 0);
            }
        }
        //Adding page for the new tour
        else {
            tour.setDescription(params.get("description"));
            tour.setPrice(Double.valueOf(params.get("price")));
            tour.setName(params.get("name"));
            tour.setVisibleOnMarket(false);
            model.addAttribute("page", 0);
        }
        page.setLocation(location);
        page.setImage(new Image());
        page.setAudio(new Audio());
        final var session = httpServletRequest.getSession(true);
        session.setAttribute("tour", tour);
        model.addAttribute("tour", tour);
        model.addAttribute("tourPage", page);

        return "pageCreate";
    }

    @GetMapping(value = "/newTour")
    String newTour(Model model, HttpServletRequest httpServletRequest) {
        final var user = (User) httpServletRequest.getSession().getAttribute("user");
        model.addAttribute("user", user);
        return "newTour";
    }

    @GetMapping(value = "/register")
    String register(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("cities", City.values());
        model.addAttribute("countries", Country.values());
        model.addAttribute("userTypes", UserType.values());
        return "register";
    }

    @GetMapping(value = "/main")
    String main(HttpServletRequest httpServletRequest, Model model) throws IOException {
        final var userSpecificTours = tourController.getAllToursOfThisUser(httpServletRequest, model);
        final var allToursOnMarket = tourController.getAllToursOnMarket(httpServletRequest, model);
        final var allTours = tourController.getAllTours(httpServletRequest);
        final var imageMap = ((ArrayList<Tour>) allTours.getBody()).stream().map(tour -> Map.entry(tour.getTourId(), Objects.requireNonNull(Base64.getEncoder().encodeToString(Objects.requireNonNull(imageController.getFirstPageImage(tour.getTourId()).getBody()).getData())))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final var user = (User) httpServletRequest.getSession().getAttribute("user");
        model.addAttribute("userTours", userSpecificTours.getBody());
        model.addAttribute("allTours", ((ArrayList<Tour>) allToursOnMarket.getBody()).stream().filter(tour -> !((ArrayList<Tour>) userSpecificTours.getBody()).stream().map(tour1 -> tour1.getTourId()).collect(Collectors.toList()).contains(tour.getTourId())).collect(Collectors.toList()));
        model.addAttribute("imageMap", imageMap);
        if (user.getImage() != null) {
            model.addAttribute("userAvatar", Base64.getEncoder().encodeToString(user.getImage().getData()));
        } else {
            File newFile = new File("src/main/resources/static/img/covo.png");
            BufferedImage originalImage = ImageIO.read(newFile);
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", oStream);
            byte[] imageInByte = oStream.toByteArray();
            model.addAttribute("userAvatar", Base64.getEncoder().encodeToString(imageInByte));

        }
        model.addAttribute("user", user);
        return "main";
    }

    @GetMapping(value = "/logout")
    String logout(HttpServletRequest request) {
        final var response = userController.logout(request);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "redirect:/";
        } else {
            //TODO error handling
            return "redirect:/";
        }
    }

}