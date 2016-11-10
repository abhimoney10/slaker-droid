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
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
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

    private String projectName;
    private String numAggregates;

    private Segmenter binary;
    private CurveFitter fitter;
    private DataExporter exporter;

    private JavaCameraView mOpenCvCameraView;

    private Mat mImage;
    private Mat mImageB;

    public static int count;
    public boolean firstPicBool;
    public boolean onTouchBoolean = true;


    public ArrayList<ArrayList<String>> areasArray;
    public ArrayList<ArrayList<WeightedObservedPoint>> observations;

    private ArrayList<Double> initialArea;
    private double slakingIndex;
    private ArrayList<Double> areaAggregates;

    private List<MatOfPoint> contours;
    private String coefA = "";
    private String coefB = "";
    private String coefC = "";
    private String sdFinal;
    public static double initialCoefA=0d;


    private Integer[] logSeq = new Integer[]{
            1,2,3,4,5,6,7,8,9,10,
            11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,30,
            32,34,36,38,40,42,44,46,48,50,
            54,58,62,66,70,78,86,110,150,
            210,280,380,480,600
    };

    private TenMinutesTest beep;
    private TextView timeLeft;
    private Double[][] slakingIndexArray;



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
    private class TenMinutesTest {
        final Runnable timeText = new Runnable(){
            public void run() {

                String timeString;
                if((601 - count) / 60 < 1){
                    timeString = String.valueOf(601 - count) + " Seconds left";
                }else {
                    timeString = String.valueOf((601 - count) / 60) + " Minutes left";
                }
                timeLeft.setText(timeString);

            }
        };

        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        public void MultipleFit() {
            final Runnable beeper = new Runnable() {
                public void run() {

                    if(count<601) {


                        slakingIndex = 0;
                        areaAggregates = binary.measureArea(contours);

                        if(count==1){
                            for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                                slakingIndexArray[aggregateId][count - 1]=slakingIndex;
                            }
                        }else {

                            for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                                Log.d("EVENT", "run:  area for aggregate " + aggregateId + " is : " + areaAggregates.get(aggregateId));
                                slakingIndex += (areaAggregates.get(aggregateId) - initialArea.get(aggregateId)) / initialArea.get(aggregateId);
                                Log.d("EVENT", "run:  contour size is " + contours.size());
                                slakingIndexArray[aggregateId][count - 1] = slakingIndex;
                                Log.d("EVENT", "run:  Slaking index for aggregate " + aggregateId + " is " + slakingIndexArray[aggregateId][count - 1]);
                            }
                        }
//
//
                        Log.d("EVENT", "Count is " + count);



                        if(Arrays.asList(logSeq).contains(count)) {
                            for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                                observations.add(new ArrayList<WeightedObservedPoint>());
                                areasArray.add(new ArrayList<String>());
                                observations.get(aggregateId).add(fitter.createWeightedPoint(count, slakingIndexArray[aggregateId][count-1]));
                                areasArray.get(aggregateId).add(String.valueOf(slakingIndexArray[aggregateId][count-1]));

                                Log.d("EVENT", "run: Slaking index values are "  + areasArray.get(aggregateId).size());

                            }
                        }

                        count+=1;

                        Log.d("EVENT", "Success");
                        Log.d("EVENT", "Count is " + count);

                    } else {

                        Log.d("event","starting analysis ...");
                        Log.d("event","Initial guess for Gompertz fitting is: " + slakingIndexArray[0][count - 2]);

                        Double tmpA = 0d;
                        Double tmpB = 0d;
                        Double tmpC = 0d;
                        double[]sd = new double[contours.size()];
                        StandardDeviation standardDeviation = new StandardDeviation(false);
                        exporter = new DataExporter();

                        for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                            initialCoefA = slakingIndexArray[aggregateId][count - 2];
                            Log.d("event",fitter.fitCurve(observations.get(aggregateId)).length + "Coefficients found");
                            if(fitter.fitCurve(observations.get(aggregateId)).length<3) {
                                Log.d("event","It was impossible to fit a curve to soil aggregate : " + aggregateId + "Using less observations");
                            }

                            Log.d("event", "run: Gompertz coefficients for "+ aggregateId + "is: " + Arrays.toString(fitter.fitCurve(observations.get(aggregateId))));
                            Log.d("event", "run: Coef A for sample " + aggregateId + " is " + fitter.fitCurve(observations.get(aggregateId))[0]);


                            tmpA += fitter.fitCurve(observations.get(aggregateId))[0];
                            tmpB += fitter.fitCurve(observations.get(aggregateId))[1];
                            tmpC += fitter.fitCurve(observations.get(aggregateId))[2];

                            sd[aggregateId]= Precision.round(fitter.fitCurve(observations.get(aggregateId))[0],1);
                        }

                        tmpA=Precision.round(tmpA/contours.size(), 1);
                        tmpB=Precision.round(tmpB/contours.size(), 1);
                        tmpC=Precision.round(tmpC/contours.size(), 1);

                        if(tmpA<0){
                            tmpA=0.0;
                        }

                        coefA = String.valueOf(tmpA);
                        coefB = String.valueOf(tmpB);
                        coefC = String.valueOf(tmpC);


                        Log.d("event", "run: Gompertz coefficient A is: " + coefA);
                        Log.d("event", "run: Gompertz coefficient B is: " + coefB);
                        Log.d("event", "run: Gompertz coefficient C is: " + coefC);

                        sdFinal= String.valueOf(Precision.round(standardDeviation.evaluate(sd),1));

                        Log.d("event", "run: Standard deviation of the result is : " + sdFinal);
                        Log.d("event", "Exporting data ...  ");

                        exporter.exportCsv(areasArray, projectName,coefA,coefB,coefC,sdFinal);
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
            }, (60 * 10)+1, SECONDS);

            firstPicBool=beeperHandle.isDone();

        }

    }


    public final static String COEF_A = "com.slaker.utils.COEF_A";
    public final static String COEF_B = "com.slaker.utils.COEF_B";
    public final static String COEF_C = "com.slaker.utils.COEF_C";
    public final static String SDFINAL = "com.slaker.utils.sdFinal";

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
        intentResultActivity.putExtra(SDFINAL, sdFinal);
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
        onTouchBoolean = !onTouchBoolean;
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
                slakingIndexArray = new Double[Integer.valueOf(numAggregates)][600];


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
                observations.add(new ArrayList<WeightedObservedPoint>());
                for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                    observations.get(count).add(fitter.createWeightedPoint(1, 0));
                }
                Log.d("EVENT", "SUCCESS");
                    count = 1;


                beep = new TenMinutesTest();
                beep.MultipleFit();


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


