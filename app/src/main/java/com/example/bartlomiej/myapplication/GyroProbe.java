package com.example.bartlomiej.myapplication;

import android.hardware.SensorEvent;

/**
 * Created by bartlomiej on 13.11.17.
 */

public class GyroProbe {
    public boolean gyroGoodToStartMovement = false;
    public double XAxRotationOffset = 1;
    public double YAxRotationOffset = 1;
    public double ZAxRotationOffset = 1;
    public void angleSentinel(SensorEvent event){
        boolean goodRotationX = event.values[0] > 0 - XAxRotationOffset && event.values[0] < 0 + XAxRotationOffset;
        boolean goodRotationY = event.values[1] > 0 - YAxRotationOffset && event.values[1] < 0 + YAxRotationOffset;
        boolean goodRotationZ = event.values[2] > 0 - ZAxRotationOffset && event.values[2] < 0 + ZAxRotationOffset;
        gyroGoodToStartMovement = goodRotationX && goodRotationY && goodRotationZ;
    }
}
