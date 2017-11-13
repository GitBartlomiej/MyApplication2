package com.example.bartlomiej.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;

/**
 * Created by bartlomiej on 11.11.17.
 */

public class Calculation {

    private final AccProbe accProbe;
    private final GyroProbe gyroProbe;
    private double startTime;
    private SensorManager mSensorManager;
    MovementFinishedListener movementFinishedListener;
    double currentAccelerationX;
    public double prevTimeInSec = 0;
    private double currentTimeInSec;

    public Calculation(SensorManager mSensorManager, MovementFinishedListener movementFinishedListener) {
        this.mSensorManager = mSensorManager;
        this.movementFinishedListener = movementFinishedListener;
        gyroProbe = new GyroProbe();
        accProbe = new AccProbe(movementFinishedListener, gyroProbe);
        initListeners();
    }

    private void initListeners(){
        mSensorManager.registerListener(new AccelerometerListener(),
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

        mSensorManager.registerListener(new AccelerometerLinearListener(),
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_GAME);

        mSensorManager.registerListener(new GyroscopeListener(),
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);

//        mSensorManager.registerListener(this,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//                SensorManager.SENSOR_DELAY_GAME);
    }

    public void start(){
        startTime = SystemClock.uptimeMillis();
    }

    public void stop(){
        startTime = 0;
    }

    public void reset(){
        accProbe.totalRoad = 0;
        accProbe.movementCounter = 0;
        accProbe.accMean = 0;
        accProbe.meanRoad = 0;
        for(int i = 0; i <= accProbe.bufforForAccMean.length-1; i++){
           accProbe.bufforForAccMean[i] = 0.0;
        }
    }

    public void update(){
        double currentTimeInMillisec = SystemClock.uptimeMillis() - startTime;
        currentTimeInSec = currentTimeInMillisec /1000;

        double deltaTime = currentTimeInSec - prevTimeInSec;

        accProbe.startCountingProcedure(currentAccelerationX, deltaTime);

//            dataSave.saveDataToFile(currentTimeInSec, roadCounter.totalRoad, roadCounter.velocity, acc,
//                    roadCounter.accMean, roadCounter.bigAccMean);

        if(accProbe.movementCounter!=accProbe.movementCounterBuff){
            accProbe.movementCounterBuff = accProbe.movementCounter;
        }
        prevTimeInSec = currentTimeInSec;
    }

    public AccProbe getAccProbe() {
        return accProbe;
    }

    private class AccelerometerListener extends BaseSensorListener{
        @Override
        public void onSensorChanged(SensorEvent event){
            accProbe.xMovementSentinel(event.values);
//            if(movementFinishedListener != null)
//                movementFinishedListener.accelerationXYZvalues(event);
        }
    }

    private class AccelerometerLinearListener extends BaseSensorListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentAccelerationX = event.values[0];
        }
    }

    private class GyroscopeListener extends BaseSensorListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            gyroProbe.angleSentinel(event);
            if(movementFinishedListener != null)
                movementFinishedListener.accelerationXYZvalues(event);
        }
    }
}


