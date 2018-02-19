package com.jtstegeman.cs4518_finalproject.etaSystem.learning;

import android.content.Context;
import android.content.SharedPreferences;

import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kyle on 2/19/18.
 */

public class ETALearningSystem {

    public static String WALKING_SPEED_PREF = "pref_walking_speed";
    // TODO: add more speeds

    private ETALearner mETALearner;

    private Map<UserActivity, Float> speeds;
    private SharedPreferences prefs;


    public ETALearningSystem(ETALearner etaLearner, SharedPreferences preferences) {
        mETALearner = etaLearner;
        speeds = new HashMap<>();
        speeds.put(UserActivity.WALKING, preferences.getFloat(WALKING_SPEED_PREF, 1.4f));
        prefs = preferences;
    }

    public void setETALearner(ETALearner etaLearner) {
        mETALearner = etaLearner;
    }

    /**
     * Updates the speed of the given activity.
     * @param averageSpeed The average speed of the last event of the user activity.
     * @param userActivity The last user activity.
     * @return The new speed of the given activity or -1 if the speed was unable to be updated.
     */
    public float learn(float averageSpeed, UserActivity userActivity){
        if(mETALearner != null){
            float newValue =  mETALearner.train(speeds.get(userActivity), averageSpeed, userActivity);
            if(speeds.containsKey(userActivity)){
                speeds.put(userActivity, newValue);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat(getPrefNameOfActivity(userActivity), newValue);
                editor.apply();
            }
            return newValue;
        }
        return -1;
    }

    private String getPrefNameOfActivity(UserActivity activity){
        switch (activity){
            case WALKING:
                return WALKING_SPEED_PREF;
        }
        return "";
    }
}
