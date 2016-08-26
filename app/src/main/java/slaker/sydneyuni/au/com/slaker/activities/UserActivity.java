package slaker.sydneyuni.au.com.slaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import slaker.sydneyuni.au.com.slaker.R;

public class UserActivity extends AppCompatActivity {


    public final static String messageAggregates= "1";
    public final static String messageprojectName= "EmptyProject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

    }

    public void startFirstPicture(View view) {
        Intent intent = new Intent(this, ExperimentActivity.class);

        EditText editText1 = (EditText) findViewById(R.id.numAggregates);
        String numAggregates = editText1.getText().toString();
        intent.putExtra(messageAggregates, numAggregates);



        EditText editText2 = (EditText) findViewById(R.id.projectName);
        String projectName = editText2.getText().toString();
        intent.putExtra(messageprojectName, projectName);



        startActivity(intent);
    }


}
