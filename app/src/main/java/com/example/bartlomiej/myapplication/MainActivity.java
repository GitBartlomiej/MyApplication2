package com.example.bartlomiej.myapplication;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MovementFinishedListener{
    private TextView xText, yText, zText, roadText, roadLAPText, roadLAPdiffText;
    private Button startButton, stopButton, resetButton;
    double  updateTime = 0.0;
    Handler customHandler = new Handler();
    // To moze sie jeszcze przydać zostawić
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

        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                calculation.start();
                customHandler.postDelayed(updateTimeThread, 0);
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
                    + "accGoodToStartMovement: " + accProbe.accGoodToStartMovement + "\n"
                    + "movement Counter: " + accProbe.movementCounter + "\n"
                    + "isCounting: " + accProbe.isCounting+ "\n"
                    + "DROGA: " + accProbe.meanRoad + "\n"
            );
            customHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void movementFinished(Movement movement, double totalRoad) {
        roadLAPText.append("\n" + String.valueOf(totalRoad));
        roadLAPdiffText.append("\n" + String.valueOf(movement.getRoad()));
    }

    @Override
    public void accelerationXYZvalues(SensorEvent event) {
        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);
    }
}
