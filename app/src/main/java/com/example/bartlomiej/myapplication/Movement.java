package com.example.bartlomiej.myapplication;

/**
 * Created by bartlomiej on 12.11.17.
 */

public class Movement {
    private double road = 0;

    public double getRoad() {
        return road;
    }

    void addRoadFragment(double roadFragment){
        road += roadFragment;
    }
}
