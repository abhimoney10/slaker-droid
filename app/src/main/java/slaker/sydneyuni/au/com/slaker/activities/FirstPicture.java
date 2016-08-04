package slaker.sydneyuni.au.com.slaker.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import slaker.sydneyuni.au.com.slaker.CurveFitter;
import slaker.sydneyuni.au.com.slaker.R;
import slaker.sydneyuni.au.com.slaker.utils.DataExporter;
import slaker.sydneyuni.au.com.slaker.utils.Segmenter;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FirstPicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{


    private Segmenter binary;
    private CurveFitter fitter;

    private DataExporter exporter = new DataExporter();

    private JavaCameraView mOpenCvCameraView;

    private Mat mImage;
    private Mat mImageB;

    public static int count;
    public boolean onClickbool;
    public boolean firstPicBool=true;
    public boolean onTouchBoolean = true;


    public List<String> areasArray;
    public ArrayList<WeightedObservedPoint> observations;

    ArrayList<Double> initialArea;
    double slakingIndex;
    ArrayList<Double> areaAggregates;

    private List<MatOfPoint> contours;




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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_picture);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.FirstPictureCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);



        Button saveImage = (Button) findViewById(R.id.buttonSaveImage);
        saveImage.setOnClickListener(this);

        Button startExperiment = (Button) findViewById(R.id.buttonBurstPicture);
        startExperiment.setOnClickListener(this);

        Button buttonWatershed = (Button) findViewById(R.id.buttonExportData);
        buttonWatershed.setOnClickListener(this);

        binary = new Segmenter();



    }

    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallBack);

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
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
            mImageB = binary.drawContours(binary.contourDetection(mImage), mImage);
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

            case R.id.buttonSaveImage:
               String location = Environment.getExternalStorageDirectory() + "/Slaker/test.png";

                 File file = new File(location);

                onClickbool = Imgcodecs.imwrite(location,mImageB);

                if (file.mkdirs()) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage" );
                } else{
                    onClickbool = Imgcodecs.imwrite(location,mImageB);
                    if(onClickbool) {
                        Log.i("OpenCv EVENT", "SUCCESS writing image to external storage" );
                    } else{
                        Log.i("OpenCv EVENT", "FAILED writing image to external storage" );

                    }

                }

                break;

            case R.id.buttonBurstPicture:

                if(!firstPicBool){
                    break;
                }

                if(onTouchBoolean){
                    onTouchBoolean = false;
                }

                    count = 1;
                    areasArray = new ArrayList<>();
                    fitter = new CurveFitter();
                    contours = new List<MatOfPoint>() {
                        @Override
                        public int size() {
                            return 0;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public boolean contains(Object o) {
                            return false;
                        }

                        @Override
                        public Iterator<MatOfPoint> iterator() {
                            return null;
                        }

                        @Override
                        public Object[] toArray() {
                            return new Object[0];
                        }

                        @Override
                        public <T> T[] toArray(T[] ts) {
                            return null;
                        }

                        @Override
                        public boolean add(MatOfPoint matOfPoint) {
                            return false;
                        }

                        @Override
                        public boolean remove(Object o) {
                            return false;
                        }

                        @Override
                        public boolean containsAll(Collection<?> collection) {
                            return false;
                        }

                        @Override
                        public boolean addAll(Collection<? extends MatOfPoint> collection) {
                            return false;
                        }

                        @Override
                        public boolean addAll(int i, Collection<? extends MatOfPoint> collection) {
                            return false;
                        }

                        @Override
                        public boolean removeAll(Collection<?> collection) {
                            return false;
                        }

                        @Override
                        public boolean retainAll(Collection<?> collection) {
                            return false;
                        }

                        @Override
                        public void clear() {

                        }

                        @Override
                        public MatOfPoint get(int i) {
                            return null;
                        }

                        @Override
                        public MatOfPoint set(int i, MatOfPoint matOfPoint) {
                            return null;
                        }

                        @Override
                        public void add(int i, MatOfPoint matOfPoint) {

                        }

                        @Override
                        public MatOfPoint remove(int i) {
                            return null;
                        }

                        @Override
                        public int indexOf(Object o) {
                            return 0;
                        }

                        @Override
                        public int lastIndexOf(Object o) {
                            return 0;
                        }

                        @Override
                        public ListIterator<MatOfPoint> listIterator() {
                            return null;
                        }

                        @Override
                        public ListIterator<MatOfPoint> listIterator(int i) {
                            return null;
                        }

                        @Override
                        public List<MatOfPoint> subList(int i, int i1) {
                            return null;
                        }
                    };
                    observations = new ArrayList<>();


                 class BeeperControl {
                    private final ScheduledExecutorService scheduler =
                            Executors.newScheduledThreadPool(1);

                    public void beepForAnHour() {
                        final Runnable beeper = new Runnable() {
                            public void run() {

                                if(count<360){
                                    slakingIndex=0;
                                    contours = binary.contourDetection(mImage);
                                    areaAggregates = binary.measureArea(contours);

                                    areasArray.add(String.valueOf(areaAggregates));


                                    if(count==1){
                                        initialArea = areaAggregates;
                                    }
                                    if(count>1){
                                        for (int aggregateId = 0; aggregateId < contours.size(); aggregateId++) {
                                        Log.d("EVENT", "run:  area for aggregate "+ aggregateId + "is : " + areaAggregates.get(aggregateId));
                                        slakingIndex+=(areaAggregates.get(aggregateId)-initialArea.get(aggregateId))/initialArea.get(aggregateId);
                                        }

                                        slakingIndex =slakingIndex/contours.size();
                                        Log.d("EVENT", "run:  Slaking index is " + slakingIndex);
                                        observations.add(fitter.createWeightedPoint(count, slakingIndex));
                                        areasArray.add(String.valueOf(slakingIndex));
                                    }
                                    count+=1;


                                }else{
                                    exporter.exportCsv(areasArray);
                                    Log.d("event", "run: Gompertz coefficients are " + fitter.fitCurve(observations));

                                }
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


                BeeperControl beep = new BeeperControl();
                beep.beepForAnHour();

                break;

            case R.id.buttonExportData:
                exporter.exportCsv(areasArray);
                Log.i("OpenCv EVENT", "SUCCESS writing image to external storage" );
                break;

        }
    }





}


