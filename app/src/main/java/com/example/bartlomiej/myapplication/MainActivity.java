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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xText, yText, zText, roadText;
    private GraphView graphView;
    private GridLabelRenderer gridLabelRenderer;
    private Sensor mySensor;
    private SensorManager SM;
    private RoadCounter roadCounter;
    private Button startButton, stopButton, resizeButton;
    private float[] acc;
    long startTime = 0L, timeInMilliseconds = 0L, updateTime = 0L, timeSwapBuff = 0L;
    Handler customHandler = new Handler();
    ExecutorService updateTimeThreadExecutor = Executors.newSingleThreadExecutor();
    LineGraphSeries<DataPoint> graphSeries;

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

        startButton = (Button)findViewById(R.id.START);
        stopButton  = (Button)findViewById(R.id.STOP);
        resizeButton  = (Button)findViewById(R.id.Resize);

        graphView = (GraphView)findViewById(R.id.graph);
        graphSeries  = new LineGraphSeries<DataPoint>();
        graphView.getViewport().setYAxisBoundsManual(true);
//        graphView.getViewport().setMinY(-5);
//        graphView.getViewport().setMaxY(+5);
        graphView.getViewport().setScalableY(true);
        graphView.getViewport().setScrollableY(true);
//        graphView.getViewport().setScrollable(true);
        gridLabelRenderer = graphView.getGridLabelRenderer();
        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimeThread, 0);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                updateTimeThreadFuture.cancel(true);
                customHandler.removeCallbacks(updateTimeThread);
                startTime = 0;
                roadCounter.reset();
            }
        });
    }

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
//                    + "pochodna przyspieszenia: " + roadCounter.da_by_dt + "\n"
//                    + "średnia pochodna przyspieszenia: " + roadCounter.da_by_dtMean + "\n"
//                    + "getHighestValueX: " + graphSeries.getHighestValueX()+ "\n"
                      + "Srednia z przyspieszenia  : " + roadCounter.accMean+ "\n"
                      + "Srednia z przyspieszenia2: " + roadCounter.bigAccMean+ "\n"
                      +  "Licznik" + roadCounter.signChangeCounter);
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(secs-10000);
            graphView.getViewport().setMaxX(secs);
            graphView.getViewport().setMinY(roadCounter.accMean - 1.5);
            graphView.getViewport().setMaxY(roadCounter.accMean + 1.5);
            graphSeries.appendData(new DataPoint(timeInMilliseconds, roadCounter.accMean), true, 900);
            graphView.addSeries(graphSeries);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX)
                        return super.formatLabel((int)(value/1000), isValueX)+ "s";
                    else
                        return super.formatLabel(value, isValueX);
                }
            });

            if(graphSeries.getHighestValueX() % 10000 == 0){
//                customHandler.postDelayed(onReset, 0);
            }
            customHandler.postDelayed(this, 0);
        }
    };

    Runnable onReset = new Runnable(){
        public void run(){
            graphSeries.resetData(generateData());
            customHandler.postDelayed(this, 10000);
        }
    };

    private DataPoint[] generateData() {
        int count = 1;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i+10;
            DataPoint v = new DataPoint(timeInMilliseconds-10000+i,7);
            values[i] = v;
        }
        return values;
    }
    Future updateTimeThreadFuture = updateTimeThreadExecutor.submit(updateTimeThread);


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
