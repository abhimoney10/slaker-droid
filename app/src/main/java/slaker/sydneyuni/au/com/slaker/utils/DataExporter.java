package slaker.sydneyuni.au.com.slaker.utils;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataExporter {
    public void exportCsv(ArrayList<ArrayList<String>> areas, String experimentName) {

        File location = new File(Environment.getExternalStorageDirectory() + "/Slakes/");
        if(!location.exists()){
            location.mkdir();
        }

        String csv = Environment.getExternalStorageDirectory() + "/Slakes/data_"+ experimentName + ".csv";

        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv), ',');
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String[]> data = new ArrayList<>();


        data.add(new String[] {"1","2","3","4","5","6","7","8","9","10",
                "11","12","13","14","15","16","17","18","19",
                "20","21","22","23","24","25","26","27","28","29","30",
                "32","34","36","38","40","42","44","46","48","50",
                "54","58","62","66","70","78","86","110","150",
                "210","280","380","480","600"});

        for (int i = 0; i < areas.size(); i++) {
            String[] stringArray = areas.get(i).toArray(new String[0]);
            data.add(stringArray);
        }


        if (writer != null) {
            writer.writeAll(data);
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
