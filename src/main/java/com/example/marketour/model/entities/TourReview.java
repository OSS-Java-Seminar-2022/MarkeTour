package com.example.marketour.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "tour_review")
@Getter
@Setter
public class TourReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tour_review_id")
    private Long tourReviewId;

    @OneToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "text", nullable = false)
    private String text;


    @Column(name = "rate")
    private Long rate;

    //millis since epoch
    @Column(name = "time", nullable = false)
    private Long time;
}