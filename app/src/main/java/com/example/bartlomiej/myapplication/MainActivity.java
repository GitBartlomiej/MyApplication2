package com.example.bartlomiej.myapplication;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xText, yText, zText, roadText;
//    private EditText buffText;
    private Sensor mySensor;
    private SensorManager SM;
    private RoadCounter roadCounter;
    private Button startButton, stopButton, resizeButton;
    private float[] acc;
    long startTime = 0L, timeInMilliseconds = 0L, updateTime = 0L, timeSwapBuff = 0L;
    Handler customHandler = new Handler();
    ExecutorService updateTimeThreadExecutor =  Executors.newSingleThreadExecutor();
    Runnable updateTimeThread = new Runnable(){
        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            roadCounter.CountRoad(acc, timeInMilliseconds);
            int secs = (int)(updateTime/1000);
            int mins = secs/60;
            secs %= 60;
            int milliseconds = (int)(updateTime%1000);
            roadText.setText("Przyspieszenie a="+acc[0] +"\n"
//                    +String.format("%2d", secs) + ":"
//                    + String.format("%3d", milliseconds) + "\n"
//                    + "Przebyta droga: " + roadCounter.road+"m" + "\n"
//                    + "aktualny czas: " + timeInMilliseconds + "\n"
                    + "pochodna przyspieszenia: " + roadCounter.da_by_dt + "\n"
                    + "średnia pochodna przyspieszenia: " + roadCounter.da_by_dtMean + "\n"
                    +  "Licznik" + roadCounter.signChangeCounter);
//            roadText.setText(String.format("%2d", secs) + ":" + String.format("%3d", milliseconds));
            customHandler.postDelayed(this, 0);
           }
    };
    Future updateTimeThreadFuture = updateTimeThreadExecutor.submit(updateTimeThread);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roadCounter = new RoadCounter();
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_GAME);

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        roadText = (TextView)findViewById(R.id.Road);
//        buffText = (EditText)findViewById(R.id.BufforTEXT);

        startButton = (Button)findViewById(R.id.START);
        stopButton  = (Button)findViewById(R.id.STOP);
        resizeButton  = (Button)findViewById(R.id.Resize);

        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimeThread,0);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                updateTimeThreadFuture.cancel(true);
                customHandler.removeCallbacks(updateTimeThread);
//                roadText.setText("Przebuta droga: " + roadCounter.road+"m"
//                                + "aktualny czas: " + timeInMilliseconds);
                startTime=0;
                roadCounter.reset();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xText.setText("X: " + sensorEvent.values[0]);
        yText.setText("Y: " + sensorEvent.values[1]);
        zText.setText("Z: " + sensorEvent.values[2]);
        acc = sensorEvent.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
