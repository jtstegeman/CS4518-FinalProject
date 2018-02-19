package com.jtstegeman.cs4518_finalproject.etaSystem.learning;

import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

/**
 * Created by kyle on 2/19/18.
 */

public interface ETALearner {

    /**
     * Updates the speed of the given activity.
     * @param previousSpeed The previous average speed of the activity.
     * @param averageSpeed The average speed of the last event of the user activity.
     * @return The new speed of the given activity.
     */
    float train(float previousSpeed, float averageSpeed, UserActivity activity);
}
