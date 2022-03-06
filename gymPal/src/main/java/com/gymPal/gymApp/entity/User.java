package com.gymPal.gymApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;
    @Column(length = 60)
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles= new ArrayList<>();

    private boolean isEnabled=false;

    @ElementCollection
    private Set<Long> savedWorkouts;

    public User(String username, String email, String password, boolean isEnabled, Set<Long> savedWorkouts) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
        this.savedWorkouts = savedWorkouts;
    }
}
