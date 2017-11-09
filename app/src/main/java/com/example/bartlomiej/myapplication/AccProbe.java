package com.example.bartlomiej.myapplication;

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
    private double prevAcc = 0;

    int movementCounter = 0;

    public double road = 0;
    double roadBuff = 0;
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
    private Boolean onceStartProbing = false;

    public AccProbe(){
        accVec = new Vector<Double>(accBuffSteps);
        bufforForAccMean = new double[accMeanBuffRange];
        //początkowa inicjalizacja buffora
        for (int i = 0; i < bufforForAccMean.length; i++){
            bufforForAccMean[i] = 0;
        }

        bufforForAccMean = new double[accMeanBuffRange];
        //początkowa inicjalizacja buffora
        for (int i = 0; i < accMeanBuffRange; i++){
            bufforForAccMean[i] = 0;
        }

        accVec.clear();
        for (int i=0; i<accVec.capacity(); i++){
            accVec.add(0.0);
        }
    }

    public void startCountingProcedure(double accelerationX, double deltaTime, double prevAcc){
        produceAccMeanAndBuffForAcc(accelerationX);
        startProbing();
        countRoad(deltaTime, prevAcc);
        endProbing();
        movementCounting();
    }

    /**
    Funkcja sprawdza czy przyspieszenie przekroczyło zadaną wartość progową w określonej liczbie kroków czasowych.
    This function checks if acceleration exceed the set threshold value in set number of time steps.
     */
    void startProbing(){
        if (onceStartProbing == true && goodToStartMovement == false) return;
        int counter = 0;
        for (double acc : bufforForAccMean){
            if(acc > threshold){
                counter++;
            }
            if(counter == accBuffSteps){
                onceStartProbing = true;
                probingStarted = true;
            }
        }
//        probingStarted = false;
    }

    void endProbing(){
        if (goodToStartMovement == false) return;
        if (probingStarted == true) {
            int counter = 0;
            for (double acc : bufforForAccMean) {
                if ((acc > -threshold) && (acc < threshold)) {
                    counter++;
                }
                if (counter == accBuffSteps) {
                    probingEnded = true;
                    return;
                }
            }
        }
        else
            probingEnded = false;
    }

    void movementCounting(){
        if (probingStarted == true && probingEnded == true && goodToStartMovement == true){
            movementCounter++;
            meanRoad = road / movementCounter;
            probingStarted = probingEnded = false;
            onceStartProbing = false;
            velocity = 0;
            accMean = 0;
        }
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

        // aktualizacja accBuffRange
        for(int i = accBuffSteps -1; i>=1; i--){
            bufforForAccMean[i] = bufforForAccMean[i-1];
        }
        bufforForAccMean[0] = accMean;
    }

    public void countRoad(double deltaTime, double prevAcc){
        if(probingStarted==true && probingEnded == false && goodToStartMovement == true){
            velocity += accMean * deltaTime ;
            road += Math.abs(velocity * deltaTime);
            roadBuff += Math.abs(velocity * deltaTime);
        }
    }

    void xMovementSentinel (float[] acceleration){
        Boolean Y_ax_good, Z_ax_good;
        Y_ax_good = acceleration[1] > 9.7 - Y_axOffset && acceleration[1] < 9.7 + Y_axOffset;
        Z_ax_good = acceleration[2] > 0 - Z_axOffset && acceleration[2] < 0 + Z_axOffset;
        goodToStartMovement = Y_ax_good && Z_ax_good;
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
