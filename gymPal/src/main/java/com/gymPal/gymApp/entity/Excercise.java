package com.gymPal.gymApp.entity;

import com.gymPal.gymApp.enums.BodyPart;
import com.gymPal.gymApp.enums.ExcerciseIntesitivity;
import com.gymPal.gymApp.enums.ExcerciseRank;
import com.gymPal.gymApp.enums.WeightType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Excercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String excerciseName;
    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;
    @Enumerated(EnumType.STRING)
    private ExcerciseIntesitivity intesitivity;
    @Enumerated(EnumType.STRING)
    private ExcerciseRank excerciseRank;
    @Enumerated(EnumType.STRING)
    private WeightType weightType;


    public Excercise(String name, BodyPart bodyPart, ExcerciseIntesitivity intesitivity, ExcerciseRank excerciseRank, WeightType weightType) {
        this.excerciseName = name;
        this.bodyPart = bodyPart;
        this.intesitivity = intesitivity;
        this.excerciseRank = excerciseRank;
        this.weightType = weightType;
    }
}
