package com.example.marketour.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tour")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Tour implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tour_id")
    private Long tourId;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "start_location_id", referencedColumnName = "location_id")
    private Location startLocation;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "end_location_id", referencedColumnName = "location_id")
    private Location endLocation;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "visible_on_market", nullable = false)
    private boolean visibleOnMarket;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "guide_tour_id")
    @JsonIgnore
    private GuideTour guideTour;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tourist_tour_id")
    @JsonIgnore
    private TouristTour touristTour;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TourPage> tourPages;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TourReview> tourReviews;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transaction> transactions;


    public static boolean filter(Tour tour, Filter filter) {
        return filter == null || ((filter.city == null || tour.city.equals(filter.city)) &&
                (filter.country == null || tour.country.equals(filter.country)) &&
                (filter.priceRange == null || filter.priceRange.contains(tour.price)));
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
