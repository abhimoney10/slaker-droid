package slaker.sydneyuni.au.com.slaker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import slaker.sydneyuni.au.com.slaker.R;
import slaker.sydneyuni.au.com.slaker.utils.Segmenter;

public class ExampleInstructions extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{

    String numAggregates="1";

    private Segmenter binary;


    private JavaCameraView mOpenCvCameraView;

    private Mat mImage;
    private Mat mImageB;


    public boolean firstPicBool;
    public boolean onTouchBoolean = true;


    ArrayList<Double> initialArea;

    ArrayList<Double> areaAggregates;

    private List<MatOfPoint> contours;



    class matSorter implements Comparator<MatOfPoint> {
        @Override
        public int compare(MatOfPoint a, MatOfPoint b) {

            if (Imgproc.contourArea(a) < Imgproc.contourArea(b)) {
                return 1;
            } else if (Imgproc.contourArea(a) > Imgproc.contourArea(b)) {
                return -1;
            } else {
                return 0;
            }
        }
    }


    private BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.d("Mario EVENT", "OpenCv loaded succesfully");
                    mOpenCvCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };


    public void backToInstructions() {
        Intent intentBack = new Intent(this, InstructionsActivity.class);
        startActivity(intentBack);
    }

    public void backToMain() {
        Intent intentBack = new Intent(this, MainActivity.class);
        startActivity(intentBack);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_instructions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.FirstPictureCameraViewExample);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);


        Button firstPicture = (Button) findViewById(R.id.buttonFirstPictureExample);
        firstPicture.setOnClickListener(this);

        Button backInstructions = (Button) findViewById(R.id.buttonBackInstructionsExample);
        backInstructions.setOnClickListener(this);



        binary = new Segmenter();
        areaAggregates = new ArrayList<>();
        contours = new ArrayList<>();


    }

    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallBack);

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
            backToInstructions();
        }
        backToInstructions();
    }
    @Override
    public void onCameraViewStarted(int width, int height) {


        mImage = new Mat(height, width, CvType.CV_32S);
        mImageB = new Mat(height, width, CvType.CV_32S);

    }
    @Override
    public void onCameraViewStopped() {
        mImage.release();
        mImageB.release();
    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mImage = inputFrame.rgba();

        if(onTouchBoolean) {
            mImageB = mImage;
        } else{
            contours = binary.contourDetection(mImage,Integer.valueOf(numAggregates));

            Collections.sort(contours, new matSorter());
            mImageB = binary.drawContours(contours, mImage);
        }
        return mImageB;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(onTouchBoolean){
            onTouchBoolean = false;
        }else {
            onTouchBoolean = true;
        }
        return false;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonFirstPictureExample:

                if (onTouchBoolean) {
                    onTouchBoolean = false;
                }


                initialArea = binary.measureArea(contours);

                if (contours.size() < Integer.valueOf(numAggregates)){
                    Log.d("EVENT", "no contours found");
                    firstPicBool = false;
                }else{
                    Log.d("EVENT", "run:  contour size is "+ contours.size());

                    areaAggregates = binary.measureArea(contours);
                    for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                        Log.d("EVENT", "run:  area for aggregate "+ aggregateId + "is : " + areaAggregates.get(aggregateId));
                    }

                    firstPicBool = true;
               }

                break;



            case R.id.buttonBackInstructionsExample:

                backToInstructions();

        }
    }





}


