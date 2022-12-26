package com.example.marketour.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@ToString
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    @JsonIgnore
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    @JsonIgnore
    private UserType userType;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "city")
    private City city;
    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    private Country country;
    @Column(name = "tokens", nullable = false)
    @JsonIgnore
    private Long tokens;

    public boolean sameUser(User other) {
        return Objects.equals(other.getUserId(), userId);
    }
}