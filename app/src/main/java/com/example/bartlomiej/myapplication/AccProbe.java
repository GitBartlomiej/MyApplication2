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
    // liczba kroków czsowych po któryych zostanie zatwierdzony pomiar
    public int accBuffRange = 3;
    // próg wartości przyspieszenia, powyżej której następuje liczenie kroków czasowych i
    // ewentualne odpalenie liczenia drogi
    public double threshold = 0.20;
    // bufor dla przyspieszenia dla 3 kroków do przodu
    double[] bufforForAcc;
    // bufor dla meanAcc o pojemnosci 3
    double[] bufforForAccMean;
    public int accMeanBuffRange = 3;
    Vector<Float> accVec;
    public double accMean;
    public Boolean probingStarted = false;
    public Boolean probingEnded = false;
    Boolean checkExit = true;
    int movementCounter = 0;
    //TODO tu trzeba tez dac buffor dla dla osi Y i dla Z
    private double Y_axOffset = 0.5;
    private double Z_axOffset = 0.7;
    Boolean goodToStartMovement = false;

    public AccProbe(){
        accVec = new Vector<Float>(accMeanBuffRange);
        bufforForAcc = new double[accBuffRange];
        //początkowa inicjalizacja buffora
        for (int i = 0; i < bufforForAcc.length; i++){
            bufforForAcc[i] = 0;
        }

        bufforForAccMean = new double[accMeanBuffRange];
        //początkowa inicjalizacja buffora
        for (int i = 0; i < accMeanBuffRange; i++){
            bufforForAccMean[i] = 0;
        }

        accVec.clear();
        for (int i=0; i<accVec.capacity(); i++){
            accVec.add(0.0f);
        }
    }

    /**
    Funkcja sprawdza czy przyspieszenie przekroczyło zadaną wartość progową w określonej liczbie kroków czasowych.
    This function checks if acceleration exceed the set threshold value in set number of time steps.
     */
    void startProbing(){
        int counter = 0;
        for (double acc : bufforForAcc){
            if(acc > threshold){
                counter++;
            }
            if(counter == accBuffRange){
                probingStarted = true;
            }
        }
        //zaznaczyć że od tego momentu czasowego rozpoczyna się pomiar

    }

    void endProbing(){
        if (probingStarted == false) return;
        int counter = 0;
        for (double acc : bufforForAcc){
            if ((acc > -threshold) && (acc < threshold)){
                counter++;
            }
            if (counter == accBuffRange){
                probingEnded = true;
                return;
            }
        }
        probingEnded =false;
    }
    void movementCounting(){
        if (probingStarted == true && probingEnded == true){
            movementCounter++;
            probingStarted = probingEnded = false;
        }
    }

    void xMovementSentinel (float[] acceleration){
        Boolean Y_ax_good, Z_ax_good;
        Y_ax_good = acceleration[1] > 9.7 - Y_axOffset && acceleration[1] < 9.7 + Y_axOffset ? true : false;
        Z_ax_good = acceleration[2] > 0   - Z_axOffset && acceleration[2] < 0   + Z_axOffset ? true : false;
        goodToStartMovement = Y_ax_good && Z_ax_good ? true : false;
    }

    /**
     * Funkcja badająca zmiennośc przyspieszenia. Czy jest rosnące czy nie.
     * Function which probe the changeability of acceleration. It checks if function of acceleration
     * is increasing or decreasing.
     * @return true jeśli funkcja przyspieszenia jest rosnąca
     */
    Boolean increasingFunction(){
        for(int i = accMeanBuffRange -1; i>=1; i--){
            bufforForAccMean[i] = bufforForAccMean[i-1];
        }
        bufforForAccMean[0] = accMean;
        if((bufforForAccMean[0] > bufforForAccMean[1]) && ((bufforForAccMean[1] > bufforForAccMean[2]))){
            return true;
        }
        return false;
    }

    /**
     * Funkcja licząca śrdnie przyspieszenie na podstawie 3 punktów pomiarowych
     * Function which counting mean acceleration base on 3 measure points
     * @param acceleration
     */
    void produceAccMeanAndBuffForaAcc (float[] acceleration){
        for (int i = accVec.capacity()-1; i >= 1; i--){
            accVec.set(i, accVec.elementAt(i-1));
        }
        accVec.set(0, acceleration[0]);
        double sumOfAcc = 0;
        for (double acc : accVec) {
            sumOfAcc += acc;
        }
        accMean = sumOfAcc / accMeanBuffRange;

        // aktualizacja accBuffRange
        for(int i = accBuffRange -1; i>=1; i--){
            bufforForAcc[i] = bufforForAcc[i-1];
        }
        bufforForAcc[0] = accMean;

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
