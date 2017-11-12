package com.example.bartlomiej.myapplication;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, MovementFinishedListener{
    private TextView xText, yText, zText, roadText, roadLAPText, roadLAPdiffText;
    private Button startButton, stopButton, resetButton;
    double  updateTime = 0.0;
    Handler customHandler = new Handler();
//    private DataSave dataSave = new DataSave();
    private Calculation calculation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculation = new Calculation((SensorManager)getSystemService(SENSOR_SERVICE), this);

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        roadText = (TextView)findViewById(R.id.Road);
        roadLAPText = (TextView)findViewById(R.id.RoadLAP);
        roadLAPdiffText = (TextView)findViewById(R.id.roadLAPdiff);

        startButton = (Button)findViewById(R.id.START);
        stopButton  = (Button)findViewById(R.id.STOP);
        resetButton = (Button)findViewById(R.id.Reset);

////        graphView = (GraphView)findViewById(R.id.graph);
//        graphSeries  = new LineGraphSeries<DataPoint>();
//
//        graphView.getViewport().setXAxisBoundsManual(true);
//        graphView.getViewport().setYAxisBoundsManual(true);
////        graphView.getViewport().setMinY(-5);
////        graphView.getViewport().setMaxY(+5);
//        graphView.getViewport().setScalableY(true);
//        graphView.getViewport().setScrollableY(true);
////        graphView.getViewport().setScrollable(true);
//        gridLabelRenderer = graphView.getGridLabelRenderer();

        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                calculation.start();

                customHandler.postDelayed(updateTimeThread, 0);
//                customHandler.postDelayed(updateChartThread,0);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                calculation.stop();
                customHandler.removeCallbacks(updateTimeThread);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                calculation.reset();
                roadLAPText.setText("Cała Droga");
                roadLAPdiffText.setText("Odcinki");
            }
        });
    }

    Runnable updateTimeThread = new Runnable(){
        public void run(){
            calculation.update();

            AccProbe accProbe = calculation.getAccProbe();

            roadText.setText("aktualny czas: " + updateTime + "\n"
                    + "Srednia z przyspieszenia: " + accProbe.accMean + "\n"
                    + "goodToStartMovement: " + accProbe.goodToStartMovement + "\n"
                    + "movement Counter: " + accProbe.movementCounter + "\n"
                    + "isCounting: " + accProbe.isCounting+ "\n"
                    + "DROGA: " + accProbe.meanRoad + "\n"
            );
            customHandler.postDelayed(this, 100);
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                xText.setText("X: " + sensorEvent.values[0]);
                yText.setText("Y: " + sensorEvent.values[1]);
                zText.setText("Z: " + sensorEvent.values[2]);
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                break;

            case Sensor.TYPE_GYROSCOPE:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void movementFinished(Movement movement, double totalRoad) {
        roadLAPText.append("\n" + String.valueOf(totalRoad));
        roadLAPdiffText.append("\n" + String.valueOf(movement.getRoad()));
    }
}
