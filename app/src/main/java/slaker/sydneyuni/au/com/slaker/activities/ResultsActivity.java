package slaker.sydneyuni.au.com.slaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import slaker.sydneyuni.au.com.slaker.R;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        String resultCoefA = intent.getStringExtra(ExperimentActivity.COEF_A);
        String resultCoefB = intent.getStringExtra(ExperimentActivity.COEF_B);
        String resultCoefC = intent.getStringExtra(ExperimentActivity.COEF_C);

        TextView resultCoefAtextView = new TextView(this);
        resultCoefAtextView.setTextSize(40);
        resultCoefAtextView.setText(resultCoefA);

        TextView resultCoefBtextView = new TextView(this);
        resultCoefBtextView.setTextSize(40);
        resultCoefBtextView.setText(resultCoefB);

        TextView resultCoefCtextView = new TextView(this);
        resultCoefCtextView.setTextSize(40);
        resultCoefCtextView.setText(resultCoefC);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_results_messagges);
        layout.addView(resultCoefAtextView);
    }

}
