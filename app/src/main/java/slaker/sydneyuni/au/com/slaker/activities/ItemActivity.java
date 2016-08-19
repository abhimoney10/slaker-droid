package slaker.sydneyuni.au.com.slaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import slaker.sydneyuni.au.com.slaker.R;
import slaker.sydneyuni.au.com.slaker.utils.CustomAdapter;

public class ItemActivity extends AppCompatActivity {
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_item);

        //GET PASSED DATA

        Intent i=getIntent();
        pos=i.getExtras().getInt("Position");

        final CustomAdapter adapter = new CustomAdapter(this);

        final ImageView img = (ImageView) findViewById(R.id.InstImage);
        final TextView details= (TextView) findViewById(R.id.InstDetails);
        final TextView name = (TextView) findViewById(R.id.InstText);


        //SET DATA

        img.setImageResource(adapter.images[pos]);
        details.setText(adapter.details[pos]);
        name.setText(adapter.names[pos]);

        Button nextBtn = (Button) findViewById(R.id.nextInstructions);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=pos+1;

                if(position>adapter.getCount()-1) {
                    position = pos;
                }

                img.setImageResource(adapter.images[position]);
                details.setText("Name: " + adapter.details[position]);
                name.setText(adapter.names[position]);

                if(!(position>=adapter.getCount()-1)){
                    pos+=1;
                }else{
                    pos= -1;
                }
            }
        });



    }
}
