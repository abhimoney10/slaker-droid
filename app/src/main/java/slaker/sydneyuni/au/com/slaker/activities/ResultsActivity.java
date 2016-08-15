package slaker.sydneyuni.au.com.slaker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import slaker.sydneyuni.au.com.slaker.R;

public class ResultsActivity extends Activity {
    TextView resultCoefAtextView;
    TextView resultCoefBtextView;
    TextView resultCoefCtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_results);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String coefA =intent.getStringExtra(ExperimentActivity.COEF_A);
        resultCoefAtextView = (TextView) findViewById(R.id.activity_message_CoefA);
        resultCoefAtextView.setTextSize(40);
        resultCoefAtextView.setText("Coefficient A is " + coefA);


        String coefB =intent.getStringExtra(ExperimentActivity.COEF_B);
        resultCoefBtextView = (TextView) findViewById(R.id.activity_message_CoefB);
        resultCoefBtextView.setTextSize(40);
        resultCoefBtextView.setText("Coefficient B is " + coefB);

        String coefC =intent.getStringExtra(ExperimentActivity.COEF_C);
        resultCoefCtextView = (TextView) findViewById(R.id.activity_message_CoefC);
        resultCoefCtextView.setTextSize(40);
        resultCoefCtextView.setText("Coefficient C is " + coefC);


    }

}
