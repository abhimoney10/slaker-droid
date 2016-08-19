package slaker.sydneyuni.au.com.slaker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import slaker.sydneyuni.au.com.slaker.R;
import slaker.sydneyuni.au.com.slaker.utils.CustomAdapter;

public class InstructionsActivity extends Activity {

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        lv= (ListView) findViewById(R.id.instructions);

        //ADAPTER

        CustomAdapter adapter = new CustomAdapter(this);
        lv.setAdapter(adapter);

        //EVENTS
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {

                Intent i = new Intent(getApplicationContext(),ItemActivity.class);

                //PASS INDEX or pos

                i.putExtra("Position",pos);
                startActivity(i);


            }
        });
        }
}
