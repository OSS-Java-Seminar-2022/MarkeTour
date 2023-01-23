package com.example.marketour.services;

import com.example.marketour.model.entities.*;
import com.example.marketour.repositories.GuideTourRepository;
import com.example.marketour.repositories.TourRepository;
import com.example.marketour.repositories.TouristTourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.marketour.model.entities.Tour.filter;


@Service
public class TourService {
    private final TouristTourRepository touristTourRepository;
    private final GuideTourRepository guideTourRepository;
    private final TourRepository tourRepository;

    public TourService(TouristTourRepository touristTourRepository, GuideTourRepository guideTourRepository,
                       TourRepository tourRepository) {
        this.touristTourRepository = touristTourRepository;
        this.guideTourRepository = guideTourRepository;
        this.tourRepository = tourRepository;
    }

    public List<Tour> getAllTouristsTours(User user, Filter filter) {
        return touristTourRepository.findAll().stream().filter(tour -> tour.getTourist().sameUser(user) && user.getUserType() == UserType.tourist && filter(tour.getTour(), filter)).map(TouristTour::getTour).collect(Collectors.toList());
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public List<Tour> getAllGuideTours(User user, Filter filter) {
        return guideTourRepository.findAll().stream().filter(tour -> tour.getGuide().sameUser(user) && user.getUserType() == UserType.guide && filter(tour.getTour(), filter)).map(GuideTour::getTour).collect(Collectors.toList());
    }

    public List<Tour> getAllToursOnMarketplace(Filter filter) {
        return guideTourRepository.findAll().stream().map(GuideTour::getTour).filter(e -> e.isVisibleOnMarket() && filter(e, filter)).collect(Collectors.toList());
    }

    public Tour findById(Long tourId) {
        return tourRepository.findAll().stream().filter(tour -> tour.getTourId().equals(tourId)).findFirst().orElse(null);
    }

    public void addTouristTour(User user, Tour tour) {
        final var touristTour = new TouristTour();
        touristTour.setTourist(user);
        touristTour.setTour(tour);
        touristTour.setLastUsed(System.currentTimeMillis());
        touristTourRepository.save(touristTour);
    }

    public void addGuideTour(User user, Tour tour) {
        final var guideTour = new GuideTour();
        guideTour.setGuide(user);
        guideTour.setTour(tour);
        guideTour.setCreateTime(System.currentTimeMillis());
        tourRepository.save(tour);
        guideTourRepository.save(guideTour);
    }

}
