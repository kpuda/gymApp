package com.gymPal.gymApp.entity;

import com.gymPal.gymApp.model.ExcerciseRepsAndWeight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long workoutOwner;
    private String workoutName;
    private boolean isPublic = false;
    @ManyToMany(mappedBy = "savedWorkouts")
    private Set<User> userSet;

    @ElementCollection
    Map<Long, ExcerciseRepsAndWeight> excerciseMap;
}
