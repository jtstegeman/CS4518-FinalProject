package com.jtstegeman.cs4518_finalproject.etaSystem.learning;

import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

/**
 * Created by kyle on 2/19/18.
 */

public class ExponentialMovingAverageLearner implements ETALearner {

    private float alpha;

    public ExponentialMovingAverageLearner(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public float train(float previousSpeed, float averageSpeed, UserActivity activity) {
        return averageSpeed * alpha + previousSpeed * (1 - alpha);
    }
}
