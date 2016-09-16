package slaker.sydneyuni.au.com.slaker.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import slaker.sydneyuni.au.com.slaker.R;

public class UserActivity extends AppCompatActivity {


    public final static String messageAggregates= "1";
    public final static String messageprojectName= "EmptyProject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "The app will close since depends on the user permissions", Toast.LENGTH_SHORT).show();
                }
            }

            case 2: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "The app will close since depends on the user permissions", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
