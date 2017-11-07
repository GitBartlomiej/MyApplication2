package com.example.bartlomiej.myapplication;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by bartlomiej on 30.10.17.
 */

public class DataSave {
    String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    String folderPath = "IMUMUS";
    DateFormat df;
    Date dateobj;
    public String fileName;
    String filePath;
    File f;
    CSVWriter writer;
    Boolean once = false;

    public DataSave(){
        df = new SimpleDateFormat("-dd-MM-yy-HH-mm-ss");
        dateobj = new Date();
        fileName = "AnalysisData" + df.format(dateobj) + ".txt";
        filePath = baseDir + File.separator + folderPath + File.separator + fileName;
        f = new File(filePath);
        if (f.exists()  && !f.isDirectory()){
            f.delete();
        }
    }

    public void saveDataToFile(double time, double road, double velocity, float[] acc, double accMean, double bigMean){
        if(f.exists()  && !f.isDirectory()){
            FileWriter mFileWriter = null;
            try {
                mFileWriter = new FileWriter(filePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);
        }
        else{
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] data =  new String[8];
        if(once == false){
            data[0] = "time";
            data[1] = "road";
            data[2] = "velocity";
            data[3] = "acc_X";
            data[4] = "acc_Y";
            data[5] = "acc_Z";
            data[6] = "accMean";
            data[7] = "bigMean";
            once = true;
        }
        else {
            data[0] =  Double.toString(time);
            data[1] =  Double.toString(road);
            data[2] =  Double.toString(velocity);
            data[3] =  Double.toString(acc[0]);
            data[4] =  Double.toString(acc[1]);
            data[5] =  Double.toString(acc[2]);
            data[6] =  Double.toString(accMean);
            data[7] =  Double.toString(bigMean);
        }
        try {
            writer.writeNext(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
