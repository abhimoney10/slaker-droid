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
    int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_item);

        //GET PASSED DATA

        final Intent i=getIntent();
        currentPos =i.getExtras().getInt("Position");

        final CustomAdapter adapter = new CustomAdapter(this);
        final ImageView img = (ImageView) findViewById(R.id.InstImage);
        final TextView details= (TextView) findViewById(R.id.InstDetails);



        //SET DATA

        img.setImageResource(adapter.imageBig[currentPos]);
        details.setText(adapter.details[currentPos]);

        final Button nextBtn = (Button) findViewById(R.id.nextInstructions);
        final Button goExperiment = (Button) findViewById(R.id.goExperimentButton);
        final Button exampleButton = (Button) findViewById(R.id.buttonExample);

        if(currentPos==2){
            exampleButton.setVisibility(View.VISIBLE);

        }else{
            exampleButton.setVisibility(View.INVISIBLE);
        }

        if(currentPos == adapter.getCount()-1) {
            nextBtn.setText("Back to instructions");
            goExperiment.setVisibility(View.VISIBLE);
        }else{
            goExperiment.setVisibility(View.INVISIBLE);
            nextBtn.setText(getString(R.string.nextInstructions));
        }


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentPos==2){
                    exampleButton.setVisibility(View.VISIBLE);
                }else{
                    exampleButton.setVisibility(View.INVISIBLE);
                }

                int nextPosition= currentPos +1;



                if(nextPosition<adapter.getCount()-1) {
                    Button goExperiment = (Button) findViewById(R.id.goExperimentButton);
                    goExperiment.setVisibility(View.INVISIBLE);
                    img.setImageResource(adapter.imageBig[nextPosition]);
                    details.setText(adapter.details[nextPosition]);
                    currentPos += 1;
                }
                if(nextPosition==adapter.getCount()-1){
                    nextBtn.setText("Back to instructions");
                    img.setImageResource(adapter.imageBig[nextPosition]);
                    details.setText(adapter.details[nextPosition]);
                    goExperiment.setVisibility(View.VISIBLE);
                    exampleButton.setVisibility(View.INVISIBLE);
                    currentPos += 1;

                }

                if(nextPosition > adapter.getCount()-1) {
                    Intent intentFinalButton = new Intent(getApplicationContext(), InstructionsActivity.class);
                    startActivity(intentFinalButton);
                }

            }
        });






    }

    public void startUserActivity(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    public void startExampleInstructions(View view){
        Intent i = new Intent(this, ExampleInstructions.class);
        startActivity(i);

    }
}
