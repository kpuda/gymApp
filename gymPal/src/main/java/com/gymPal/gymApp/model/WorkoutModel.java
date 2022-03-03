package com.gymPal.gymApp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

//@Entity
@Getter
@Setter
@Data
public class WorkoutModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ElementCollection
    Map<Long, ExcerciseRepsAndWeight> excerciseMap;

}
