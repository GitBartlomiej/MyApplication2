package com.example.bartlomiej.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

/**
 * Created by bartlomiej on 11.11.17.
 */

public abstract class BaseSensorListener implements SensorEventListener {

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
