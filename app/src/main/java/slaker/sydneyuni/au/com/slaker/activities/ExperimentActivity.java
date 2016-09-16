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
import android.widget.TextView;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.util.Precision;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import slaker.sydneyuni.au.com.slaker.R;
import slaker.sydneyuni.au.com.slaker.utils.CurveFitter;
import slaker.sydneyuni.au.com.slaker.utils.DataExporter;
import slaker.sydneyuni.au.com.slaker.utils.Segmenter;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ExperimentActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{

    String projectName;
    String numAggregates;

    private Segmenter binary;
    private CurveFitter fitter;
    private DataExporter exporter;

    private JavaCameraView mOpenCvCameraView;

    private Mat mImage;
    private Mat mImageB;

    public static int count;
    public boolean firstPicBool;
    public boolean onTouchBoolean = true;


    public List<String> areasArray;
    public ArrayList<WeightedObservedPoint> observations;

    ArrayList<Double> initialArea;
    double slakingIndex;
    ArrayList<Double> areaAggregates;

    private List<MatOfPoint> contours;
    private double[] SLAKING_RESULT;
    private String coefA;
    private String coefB;
    private String coefC;
    public static double initialCoefA;


    private Integer[] logSeq = new Integer[]{
            1,2,3,4,5,6,7,8,9,10,
            11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,30,
            32,34,36,38,40,42,44,46,48,50,
            54,58,62,66,70,78,86,110,150,
            210,280,380,480,600
    };
    BeeperControl beep;
    TextView timeLeft;
    String timeString;




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
    class BeeperControl {

        /////
        final Runnable timeText = new Runnable(){
            public void run() {

                timeString = String.valueOf((602 - count) / 60) + " Minutes left";
                timeLeft.setText(timeString);

            }
        };

        /////
        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        public void beepForAnHour() {
            final Runnable beeper = new Runnable() {
                public void run() {

                    if(count<601){


                        slakingIndex=0;
                        areaAggregates = binary.measureArea(contours);

                        for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                            Log.d("EVENT", "run:  area for aggregate "+ aggregateId + "is : " + areaAggregates.get(aggregateId));
                            slakingIndex+=(areaAggregates.get(aggregateId)-initialArea.get(aggregateId))/initialArea.get(aggregateId);
                        }


                        slakingIndex =slakingIndex/contours.size();
                        Log.d("EVENT", "run:  contour size is "+ contours.size());
                        Log.d("EVENT", "run:  Slaking index is " + slakingIndex);
                        if(Arrays.asList(logSeq).contains(count)) {
                            observations.add(fitter.createWeightedPoint(count, slakingIndex));
                            areasArray.add(String.valueOf(slakingIndex));
                            initialCoefA=slakingIndex;
                        }


//                        timeLeft.setText("hola");
                        count+=1;



                    }else{

                        exporter = new DataExporter();
                        exporter.exportCsv(areasArray,projectName);
                        SLAKING_RESULT = fitter.fitCurve(observations);

                        coefA=String.valueOf(Precision.round((double)Array.get(SLAKING_RESULT,0),1));
                        coefB=String.valueOf(Precision.round((double)Array.get(SLAKING_RESULT,1),1));
                        coefC=String.valueOf(Precision.round((double)Array.get(SLAKING_RESULT,2),1));

                        Log.d("event", "run: Gompertz coefficient A is: " + coefA);
                        Log.d("event", "run: Gompertz coefficient B is: " + coefB);
                        Log.d("event", "run: Gompertz coefficient C is: " + coefC);
                        sendResult();

                    }
/////
                    ExperimentActivity.this.runOnUiThread(timeText);
/////
                }

            };
            final ScheduledFuture<?> beeperHandle =
                    scheduler.scheduleAtFixedRate(beeper, 1,1, SECONDS);
            scheduler.schedule(new Runnable() {
                public void run() { beeperHandle.cancel(true); }
            }, 60 * 10, SECONDS);

            firstPicBool=beeperHandle.isDone();

        }

    }



    public final static String COEF_A = "com.slaker.utils.COEF_A";
    public final static String COEF_B = "com.slaker.utils.COEF_B";
    public final static String COEF_C = "com.slaker.utils.COEF_C";

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

    public void sendResult() {
        Intent intentResultActivity = new Intent(this, ResultsActivity.class);
        intentResultActivity.putExtra(COEF_A, coefA);
        intentResultActivity.putExtra(COEF_B, coefB);
        intentResultActivity.putExtra(COEF_C, coefC);
        startActivity(intentResultActivity);
    }


    public void backToMain() {
        Intent intentBack = new Intent(this, MainActivity.class);
        startActivity(intentBack);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.FirstPictureCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);


        Intent intent = getIntent();
        numAggregates = intent.getStringExtra(UserActivity.messageAggregates);
        projectName = intent.getStringExtra(UserActivity.messageprojectName);


        Button startExperiment = (Button) findViewById(R.id.buttonBurstPicture);
        startExperiment.setOnClickListener(this);

        Button firstPicture = (Button) findViewById(R.id.buttonFirstPicture);
        firstPicture.setOnClickListener(this);

        Button backInstructions = (Button) findViewById(R.id.buttonBackInstructions);
        backInstructions.setOnClickListener(this);

        timeLeft = (TextView) findViewById(R.id.timeLeft);

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
            backToMain();
        }

        if(beep!=null) {
            beep.scheduler.shutdown();
            backToMain();
        }

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
            //sort areas of objects

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

            case R.id.buttonFirstPicture:

                if (onTouchBoolean) {
                    onTouchBoolean = false;
                }

                areasArray = new ArrayList<>();
                fitter = new CurveFitter();
                observations = new ArrayList<>();


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

                    Button startExperiment= (Button) findViewById(R.id.buttonBurstPicture);
                    startExperiment.setVisibility(View.VISIBLE);

                }

                break;


            case R.id.buttonBurstPicture:

                if(!firstPicBool){
                    break;
                }

                ///need to put some message here

                if(onTouchBoolean){
                    onTouchBoolean = false;
                }

                    observations.add(fitter.createWeightedPoint(1, 0));
                    count = 2;


                beep = new BeeperControl();
                beep.beepForAnHour();


                break;

            case R.id.buttonBackInstructions:

                if(beep!=null) {
                    beep.scheduler.shutdown();
                    backToMain();
                }else{
                    backToMain();
                }


        }
    }





}


