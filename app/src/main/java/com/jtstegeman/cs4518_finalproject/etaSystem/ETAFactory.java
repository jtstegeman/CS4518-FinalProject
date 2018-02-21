package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.content.SharedPreferences;

/**
 * Created by kyle on 2/15/18.
 */

public class ETAFactory {

    public static ETASystem getDefaultETASystem(SharedPreferences preferences){
        return new ETASystem(new CrowFliesETAEstimator(), preferences);
    }

}
