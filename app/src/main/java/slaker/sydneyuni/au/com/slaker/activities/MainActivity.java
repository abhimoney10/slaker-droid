package slaker.sydneyuni.au.com.slaker.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.opencv.android.OpenCVLoader;

import slaker.sydneyuni.au.com.slaker.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    static{
        if(OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV loaded succesfully");
        }else{
            Log.d(TAG,"OpenCV not loaded caca");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,1);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar activity_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startInstructions(View view) {
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void startUserActivity(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }


    public void watchYoutubeVideo(View view){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/GP7xiPzK_AM"));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://youtu.be/GP7xiPzK_AM"));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Quit if back is pressed
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(true);
            return true;
        }
        System.gc();
        System.exit(0);

        return super.onKeyDown(keyCode, event);

    }

}

