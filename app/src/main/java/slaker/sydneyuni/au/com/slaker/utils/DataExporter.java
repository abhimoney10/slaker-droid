package slaker.sydneyuni.au.com.slaker.utils;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class DataExporter {
    public void exportCsv(List<String> areas, String experimentName) {

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
        String[] stringArray = areas.toArray(new String[0]);
        writer.writeNext(stringArray);
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
