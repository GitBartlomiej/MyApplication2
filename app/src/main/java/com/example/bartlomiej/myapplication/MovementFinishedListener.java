package com.example.bartlomiej.myapplication;

import android.hardware.SensorEvent;

/**
 * Created by bartlomiej on 12.11.17.
 */

public interface MovementFinishedListener {
    void movementFinished(Movement movement, double totalRoad);
    void accelerationXYZvalues(SensorEvent event);
}
