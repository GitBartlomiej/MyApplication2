package com.example.bartlomiej.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private TextView xText, yText, zText, roadText;
    private GraphView graphView;
    private GridLabelRenderer gridLabelRenderer;
    private Sensor mySensor;
    private SensorManager SM;
//    private RoadCounter roadCounter;
    private Button startButton, stopButton, resizeButton;
    private float[] acc;
    double startTime = 0.0, timeInMilliseconds = 0.0, updateTime = 0.0;
    Handler customHandler = new Handler();
    ExecutorService updateTimeThreadExecutor = Executors.newSingleThreadExecutor();
    LineGraphSeries<DataPoint> graphSeries;
    int secs=0;
    private DataSave dataSave;
    private AccProbe accProbe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        roadCounter = new RoadCounter();
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_GAME);
        dataSave = new DataSave();
        accProbe = new AccProbe();

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        roadText = (TextView)findViewById(R.id.Road);

        startButton = (Button)findViewById(R.id.START);
        stopButton  = (Button)findViewById(R.id.STOP);
        resizeButton  = (Button)findViewById(R.id.Resize);

        graphView = (GraphView)findViewById(R.id.graph);
        graphSeries  = new LineGraphSeries<DataPoint>();

        graphView.getViewport().setXAxisBoundsManual(true);
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
//                customHandler.postDelayed(updateChartThread,0);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                updateTimeThreadFuture.cancel(true);
                customHandler.removeCallbacks(updateTimeThread);
                startTime = 0;
//                roadCounter.reset();
            }
        });
        resizeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                roadCounter.signChangeCounter = 1;
//                roadCounter.accMean = 0;
//                roadCounter.road = 0;
            }
        });
    }

    Runnable updateTimeThread = new Runnable(){
        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeInMilliseconds /1000;
//            roadCounter.CountRoad(acc, updateTime);
            accProbe.startProbing();
            accProbe.endProbing();
            accProbe.movementCounting();
//            dataSave.saveDataToFile(updateTime, roadCounter.road, roadCounter.velocity, acc,
//                    roadCounter.accMean, roadCounter.bigAccMean);
            secs = (int) (updateTime/1000);
            secs %= 60;
            roadText.setText("Przyspieszenie a = " + acc[0] +"\n"
                    + "aktualny czas: " + updateTime + "\n"
                    + "Srednia z przyspieszenia  : " + accProbe.accMean+ "\n"
                    + "goodToStartMovement: " + accProbe.goodToStartMovement + "\n"
                    + "movement Counter: " + accProbe.movementCounter + "\n"
                    + "probingStarted: " + accProbe.probingStarted + "\n"
                    + "probingEnded: " + accProbe.probingEnded + "\n");
            customHandler.postDelayed(this, 50);
        }
    };

//    Runnable updateChartThread = new Runnable() {
//        @Override
//        public void run() {
//            graphView.getViewport().setMinX(secs-10000);
//            graphView.getViewport().setMaxX(secs);
//            graphView.getViewport().setMinY(roadCounter.accMean - 1.5);
//            graphView.getViewport().setMaxY(roadCounter.accMean + 1.5);
//            graphSeries.appendData(new DataPoint(timeInMilliseconds, roadCounter.accMean), true, 900);
//            graphView.addSeries(graphSeries);
//            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
//                @Override
//                public String formatLabel(double value, boolean isValueX) {
//                    if(isValueX)
//                        return super.formatLabel((int)(value/1000), isValueX)+ "s";
//                    else
//                        return super.formatLabel(value, isValueX);
//                }
//            });
//            customHandler.postDelayed(this, 200);
//        }
//    };

    Future updateTimeThreadFuture = updateTimeThreadExecutor.submit(updateTimeThread);

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accProbe.produceAccMeanAndBuffForaAcc(sensorEvent.values);
        accProbe.xMovementSentinel(sensorEvent.values);
        acc = sensorEvent.values;
        xText.setText("X: " + sensorEvent.values[0]);
        yText.setText("Y: " + sensorEvent.values[1]);
        zText.setText("Z: " + sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
