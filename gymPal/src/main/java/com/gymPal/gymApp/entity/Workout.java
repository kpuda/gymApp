package com.gymPal.gymApp.entity;

import com.gymPal.gymApp.model.ExcerciseRepsAndWeight;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
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

    @ElementCollection
    private Set<Long> userSet;

    @ElementCollection
    List<ExcerciseRepsAndWeight> excerciseList;
}
