package com.gymPal.gymApp.service;

import com.gymPal.gymApp.model.ExcerciseModel;
import org.springframework.http.ResponseEntity;

public interface ExcerciseService {
    String addExcercise(ExcerciseModel excerciseModel);

    String generatePayload();

    ResponseEntity getAllExcercices();

}
