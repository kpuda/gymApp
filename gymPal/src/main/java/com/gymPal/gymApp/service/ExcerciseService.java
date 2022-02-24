package com.gymPal.gymApp.service;

import com.gymPal.gymApp.model.ExcerciseModel;

public interface ExcerciseService {
    String addExcercise(ExcerciseModel excerciseModel);

    String generatePayload();

}
