package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.content.Context;

/**
 * Created by kyle on 2/15/18.
 */

public class ETAFactory {

    public static ETASystem getDefaultETASystem(Context context){
        return new ETASystem(new CrowFliesETAEstimator(), context);
    }

}
