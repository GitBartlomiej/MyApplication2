package com.example.bartlomiej.myapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by bartlomiej on 06.11.17.
 * In this class acceleration of mobile phone is study.
 * Acceleration is probe and its dependency from time.
 * Road is counting in this class by double integration of acceleration by time
 * Road in this case is a function which is also a third degree polynomial
 */

public class AccProbe {
    // próg wartości przyspieszenia, powyżej której następuje liczenie kroków czasowych i
    // ewentualne odpalenie liczenia drogi
    public double threshold = 0.15;

    // Do liczenia sredniej
    Vector<Double> accVec;
    // do sprawdzania w 3 krokach
    double[] bufforForAccMean;

    public int accBuffSteps = 2;
    public int accMeanBuffRange = 2;

    public double accMean;


    int movementCounter = 0;

    public double totalRoad = 0;
    double meanRoad = 0;
    double velocity = 0;
    double prevRoadBuff = 0;
    int movementCounterBuff = 0;

    //TODO tu trzeba tez dac buffor dla dla osi Y i dla Z
    private double Y_axOffset = 0.7;
    private double Z_axOffset = 3.8;

    public Boolean probingStarted = false;
    public Boolean probingEnded = false;
    public Boolean goodToStartMovement = false;
    public boolean isCounting = false;

    private List<Movement> movements = new ArrayList<>();
    private MovementFinishedListener movementFinishedListener;

    public AccProbe(MovementFinishedListener movementFinishedListener){
        this.movementFinishedListener = movementFinishedListener;

        accVec = new Vector<Double>(accMeanBuffRange);
        bufforForAccMean = new double[accBuffSteps];
        //początkowa inicjalizacja buffora
        for (int i = 0; i < bufforForAccMean.length; i++){
            bufforForAccMean[i] = 0;
        }

        bufforForAccMean = new double[accBuffSteps];
        //początkowa inicjalizacja buffora
        for (int i = 0; i < accBuffSteps; i++){
            bufforForAccMean[i] = 0;
        }

        accVec.clear();
        for (int i=0; i<accVec.capacity(); i++){
            accVec.add(0.0);
        }
    }

    public void startCountingProcedure(double accelerationX, double deltaTime){
        produceAccMeanAndBuffForAcc(accelerationX);
        if(canStartCounting()){
            isCounting = true;
            movements.add(new Movement());
        }
        if(isCounting)
            countRoad(deltaTime);
        if(canStopCounting()){
            isCounting = false;
            movementCounter++;
            meanRoad = totalRoad / movementCounter;
            velocity = 0;
            accMean = 0;
            if(movementFinishedListener != null)
                movementFinishedListener.movementFinished(getLastMovement(), totalRoad);
        }
    }

    /**
    Funkcja sprawdza czy przyspieszenie przekroczyło zadaną wartość progową w określonej liczbie kroków czasowych.
    This function checks if acceleration exceed the set threshold value in set number of time steps.
     */
    boolean canStartCounting(){
        if (!goodToStartMovement || isCounting) return false;
        int counter = 0;
        for (double acc : bufforForAccMean){
            if(acc > threshold){
                counter++;
            }
        }
        return counter == accBuffSteps;
    }

    boolean canStopCounting(){
        if (goodToStartMovement == false || !isCounting) return false;
        int counter = 0;
        for (double acc : bufforForAccMean) {
            if ((acc > -threshold) && (acc < threshold)) {
                counter++;
            }
        }
        return counter == accBuffSteps;
    }

    /**
     * Funkcja licząca śrdnie przyspieszenie na podstawie 3 punktów pomiarowych
     * Function which counting mean acceleration base on 3 measure points
     * @param acceleration
     */
    void produceAccMeanAndBuffForAcc(double acceleration){
        for (int i = accMeanBuffRange - 1; i >= 1; i--){
            accVec.set(i, accVec.elementAt(i-1));
        }
        accVec.set(0, acceleration);
        double sumOfAcc = 0;
        for (double acc : accVec) {
            sumOfAcc += acc;
        }
        accMean = sumOfAcc / accMeanBuffRange;

        for(int i = accBuffSteps -1; i>=1; i--){
            bufforForAccMean[i] = bufforForAccMean[i-1];
        }
        bufforForAccMean[0] = accMean;
    }

    private void countRoad(double deltaTime){
        velocity += accMean * deltaTime ;
        double ds = Math.abs(velocity * deltaTime);
        getLastMovement().addRoadFragment(ds);
        totalRoad += ds;
    }

    void xMovementSentinel (float[] acceleration){
        Boolean Y_ax_good, Z_ax_good;
        Y_ax_good = acceleration[1] > 9.7 - Y_axOffset && acceleration[1] < 9.7 + Y_axOffset;
        Z_ax_good = acceleration[2] > 0 - Z_axOffset && acceleration[2] < 0 + Z_axOffset;
        goodToStartMovement = Y_ax_good && Z_ax_good;
    }

    Movement getLastMovement(){
        return movements.get(movements.size()-1);
    }

    /**
     * Liczenie minimum globalnego danego wykresu
     */
    void minimumOfFunction(){
    }

    /**
     * Liczenie maksimum globalnego danego wykresu
     */
    void maximumOfFunction(){
    }
}
