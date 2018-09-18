package slaker.sydneyuni.au.com.slaker.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import slaker.sydneyuni.au.com.slaker.R;

public class CustomAdapter extends BaseAdapter{

    public Context c;


    public String[] names =
            {
                    "Before you start",
                    "About this app",
                    "Preparing your samples",
                    "Setting your experiment details",
                    "Image recognition",
                    "Taking the first picture",
                    "Start your experiment",
                    "Results"



            };
    public String[] details=
            {       "Slakes uses the open-source library 'OpenCV' (Open Source Computer Vision Library, BSD license), in order to measure the area of soil aggregates",
                    "Slakes is the mobile version of the soil aggregate stability methodology presented by Fajardo et al ,(2016)* \n" +
                            "see : http://www.sciencedirect.com/science/article/pii/S0167198716300952",
                    "Make sure the soil samples are air dried for at least 24 hours and their sizes are between 2-15 mm.",
                    "Select the number of soil samples to be used (ideally 3 samples).And the name of your project, the app will save the results with that name in your phone's internal storage.",
                    "Make sure that the soil samples are against a white background.",
                    "Place the soil samples inside an EMPTY Petri dish. Make sure they are properly recognized by your phone, you can do this by touching the screen as shown in the picture.",
                    "After taking the first picture, you may proceed with the experiment. Place the soil samples inside a Petri dish FULL OF WATER. Immediately after press the 'Start' button. Make sure the soil samples don't touch each other otherwise they will be considered as a single piece of soil",
                    "Once the experiment is finished, the result is expressed as a Slaking index coefficient, where values less than 3 will indicate high stability. Between 3 to 7 moderate and higher than 7 low stability."
            };

    public int[] imageSmall ={R.drawable.inst0,R.drawable.inst1,R.drawable.inst2,R.drawable.inst3,R.drawable.inst4,R.drawable.inst5,R.drawable.inst6,R.drawable.inst7};
    public int[] imageBig ={R.drawable.logopencv,R.drawable.inst1big,R.drawable.inst2big,R.drawable.inst3big,R.drawable.inst4big,R.drawable.inst5big,R.drawable.inst6big,R.drawable.inst7big};


    public CustomAdapter(Context ctx){
        this.c=ctx;
    }


    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int pos) {
        return names[pos];
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.activity_item,null);
        }

        //GET VIEWS
        TextView nameTxt = (TextView) convertView.findViewById(R.id.nameItem);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageItem);

        //SET DATA

        nameTxt.setText(names[pos]);
        img.setImageResource(imageSmall[pos]);
        return convertView;
    }
}
