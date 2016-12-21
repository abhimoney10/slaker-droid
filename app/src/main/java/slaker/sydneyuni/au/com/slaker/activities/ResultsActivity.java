package slaker.sydneyuni.au.com.slaker.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import slaker.sydneyuni.au.com.slaker.R;

public class ResultsActivity extends Activity {
    TextView resultCoefAtextView;
    TextView resultExplained;
    Drawable circle;
    int color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_results);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String coefA =intent.getStringExtra(ExperimentActivity.COEF_A);
        resultCoefAtextView = (TextView) findViewById(R.id.result);
        resultCoefAtextView.setText(coefA);

        resultExplained = (TextView) findViewById(R.id.resultExplained);
        circle= resultCoefAtextView.getBackground();

        if(Double.parseDouble(coefA)<=3){
            resultExplained.setText("Good job, you have a stable soil");
            color=Color.parseColor("#FF99CC00");
            circle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        }
        if(Double.parseDouble(coefA)>3&Double.parseDouble(coefA)<=7){
            resultExplained.setText("The stability of your soil is average");
            color=Color.parseColor("#FFFF00");
            circle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        if(Double.parseDouble(coefA)>7){
            resultExplained.setText("Your soil seems to be very unstable");
            color=Color.parseColor("#FFFF4444");
            circle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }



    }

}
