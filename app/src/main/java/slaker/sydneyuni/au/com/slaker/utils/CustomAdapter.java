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

    private Context c;

    public String[] names={"1","2"};
    public String[] details={"inst 1"," inst 2"};
    public int[]    images={R.drawable.inst1,R.drawable.inst2};

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
        TextView detailsTxt = (TextView) convertView.findViewById(R.id.detailsItem);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageItem);

        //SET DATA

        nameTxt.setText(names[pos]);
        detailsTxt.setText(details[pos]);
        img.setImageResource(images[pos]);
        return convertView;
    }
}
