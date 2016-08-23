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
                    "About this app",
                    "Preparing your samples",
                    "Setting your experiment details",
                    "Image recognition",
                    "Time of the experiment",
                    "Results"



            };
    public String[] details=
            {
                    "Slakes is the mobile version of the algorithm written by Fajardo et.al",
                    "Make sure the soil samples are air dried for at least 24 hours",
                    "Select the number of soil aggregates to be used as well as the name of your project. The app will save the results of the slaking process on a csv file with that name",
                    "The image recognition algorithm is implemented with the Java library OpenCV, and it measures independently each of the soil samples every second, storing them for later fitting of the slaking model",
                    "The experiment need a minimum of 10 minutes to finalize. Since the results are directly related with the image segmentation, make sure that your phone position and the light conditions are stable",
                    "The results are expressed as a Slaking index coefficient, where …"



            };
    public int[] imageSmall ={R.drawable.inst1,R.drawable.inst2,R.drawable.inst3,R.drawable.inst4,R.drawable.inst5,R.drawable.inst6};
    public int[] imageBig ={R.drawable.inst3,R.drawable.inst3,R.drawable.inst3,R.drawable.inst3,R.drawable.inst3,R.drawable.inst3};


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
