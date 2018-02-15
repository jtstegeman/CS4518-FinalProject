package com.jtstegeman.cs4518_finalproject.etaSystem;

/**
 * Created by kyle on 2/15/18.
 */

public class ETAFactory {

    public static ETASystem getDefaultETASystem(){
        return new ETASystem(new CrowFliesETAEstimator());
    }

}
