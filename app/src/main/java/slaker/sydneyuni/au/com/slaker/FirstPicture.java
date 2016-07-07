package slaker.sydneyuni.au.com.slaker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.opencsv.CSVWriter;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FirstPicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{


    private JavaCameraView mOpenCvCameraView;
    String file;

    Mat mImage;
    Mat mImageB;
    Mat mImageW;


    Mat threeChannel;
    Mat threeChannelWatershed;
    Mat oneChannel;
    Mat fg;

    int threshold = 10;

    Mat hierarchy;

    double meanArea;
    List<String> areasArray;


    static int count;
    boolean onClickbool;
    boolean firstPicBool=true;
    boolean onTouchBoolean = true;


    WeightedObservedPoint singleObservation;
    ArrayList<WeightedObservedPoint> observations;



    public Mat segment(Mat image){

/*        initialize the contours object
        and the sum of the areas result*/
        List<MatOfPoint> contoursFg  = new ArrayList<>();
        int sum =0;

//      Convert to gray and cut values by thresholding
        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(image, threeChannelWatershed, Imgproc.COLOR_BGR2RGB);
        Imgproc.threshold(threeChannel, threeChannel, 80, 255, Imgproc.THRESH_BINARY_INV);

//      Create a distance matrix to select the foreground and convert to 8 bit grayscale
        Imgproc.distanceTransform(threeChannel,threeChannel,Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_5);
        threeChannel.convertTo(fg,CvType.CV_8U);

//        second thresholding foreground
        Imgproc.threshold(fg, fg, threshold, 255, Imgproc.THRESH_BINARY);

// find contours and store them in contours object, the size should be equal to the number of soil aggregates
        Imgproc.findContours(fg, contoursFg, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);


//
// Draw contours over the input image and also store the contours in the markers to measure the area (I may erase this part)
        for (int contourIdx = 0; contourIdx < contoursFg.size(); contourIdx++) {
//            foreground markers
            Imgproc.drawContours(image, contoursFg, contourIdx,new Scalar(255,255/(contourIdx+3),255/(contourIdx+3)),2);
//            get the mean areas by contour
            Mat contour = contoursFg.get(contourIdx);
            sum+=Imgproc.contourArea(contour);
        }


        image.convertTo(image,CvType.CV_32SC1);

//        workaround if no contours are found
        if(contoursFg.size()!= 0){
            meanArea = sum/contoursFg.size();
        }


//        Log.d("EVENT", "segment: Mean Area =  " + meanArea);
//        Log.d("Event", "segment: Number of soil aggregates =  " + contoursFg.size());

        image.convertTo(image,CvType.CV_8UC1);


        return image;
    }

    class GompertzFunction implements ParametricUnivariateFunction {
        public double value(double t, double[] parameters) {
//            return parameters[0] * Math.pow(t, parameters[1]) * Math.exp(-parameters[2] * t);
              return parameters[0] * (Math.exp(-parameters[1] * Math.exp(-parameters[2] * Math.log(t))));
        }

        @Override
        public double[] gradient(double t, double[] parameters) {
            double a  = parameters[0];
            double b = parameters[1];
            double c = parameters[2];

            return new double[]{
                    a * (Math.exp(-b * Math.exp(-c * Math.log(t)))),
                    a*b*c*Math.exp(-b * Math.pow(t,-c))*Math.pow(t,-c-1),
                    a*b*c*(Math.exp(-b*Math.pow(t,-c))*Math.pow(t,-c-2)*(-c-1)+(b*c*Math.exp(-b*Math.pow(t,-c)))*Math.pow(t,-2*c-2))

//                    Math.exp(-c*t) * Math.pow(t, b),
//                    a * Math.exp(-c*t) * Math.pow(t, b) * Math.log(t),
//                    a * (-Math.exp(-c*t)) * Math.pow(t, b+1)

            };
        }
    }

    public class GompertzFitter extends AbstractCurveFitter {
        protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {
            final int len = points.size();
            final double[] target = new double[len];
            final double[] weights = new double[len];
            final double[] initialGuess = {1.0, 1.0, 1.0};

            int i = 0;
            for (WeightedObservedPoint point : points) {
                target[i] = point.getY();
                weights[i] = point.getWeight();
                i += 1;
            }

            final AbstractCurveFitter.TheoreticalValuesFunction model = new
                    AbstractCurveFitter.TheoreticalValuesFunction(new GompertzFunction(), points);

            return new LeastSquaresBuilder().
                    maxEvaluations(Integer.MAX_VALUE).
                    maxIterations(Integer.MAX_VALUE).
                    start(initialGuess).
                    target(target).
                    weight(new DiagonalMatrix(weights)).
                    model(model.getModelFunction(), model.getModelFunctionJacobian()).
                    build();
        }
    }

    public WeightedObservedPoint createWeightedPoint(double time,double area){
        singleObservation = new WeightedObservedPoint(1,time,area);

        return singleObservation;
    }

    public String fitCurve(ArrayList<WeightedObservedPoint> observations){
        GompertzFitter fitter = new GompertzFitter();
//        ArrayList<WeightedObservedPoint> observations = new ArrayList<>();

        WeightedObservedPoint point = new WeightedObservedPoint(1,0,0);
//
        observations.add(point);

        final double coeffs[] = fitter.fit(observations);

        return Arrays.toString(coeffs);
    }

    public void exportCsv(List<String> areas) {

        File location = new File(Environment.getExternalStorageDirectory() + "/Slaker/");
         if(!location.exists()){
              location.mkdir();
         }

        String csv = Environment.getExternalStorageDirectory() + "/Slaker/data.csv";

        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv), ',');
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] stringArray = areas.toArray(new String[0]);
        writer.writeNext(stringArray);
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        mImageW = new Mat(height, width, CvType.CV_8UC3);

        threeChannel = new Mat();
        threeChannelWatershed = new Mat(mImage.size(),CvType.CV_8UC2);
        oneChannel = new Mat();

        fg = new Mat();

        hierarchy = new Mat();

//
    }
    @Override
    public void onCameraViewStopped() {
        mImage.release();
        mImageB.release();
        mImageW.release();


        threeChannel.release();
        threeChannelWatershed.release();
        fg.release();


        hierarchy.release();

//        bg.release();
//




    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mImage = inputFrame.rgba();

        if(onTouchBoolean) {
            mImageB = mImage;
        } else {
            mImageB = segment(mImage);
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

                    count=1;
                    areasArray= new ArrayList<>();

                 class BeeperControl {
                    private final ScheduledExecutorService scheduler =
                            Executors.newScheduledThreadPool(1);

                    public void beepForAnHour() {
                        final Runnable beeper = new Runnable() {
                            public void run() {

//
                                if(count<60){
                                    Log.d("EVENT", "run:  area is "+ meanArea);

                                    createWeightedPoint(count,meanArea);
                                    areasArray.add(String.valueOf(meanArea));
                                    count+=1;
                                }else{
                                    exportCsv(areasArray);
                                }


                            }
                        };
                         final ScheduledFuture<?> beeperHandle =
                                scheduler.scheduleAtFixedRate(beeper, 1,1, SECONDS);
                        scheduler.schedule(new Runnable() {
                            public void run() { beeperHandle.cancel(true); }
                        }, 60 * 1, SECONDS);

                        firstPicBool=beeperHandle.isDone();

                    }

                }


                BeeperControl beep = new BeeperControl();
                beep.beepForAnHour();

//                if(!firstPicBool){
//                    Log.d("Event", "onClick Check array: " + String.valueOf(fitGompertz(areasArray)));
//                }

                break;

            case R.id.buttonExportData:


                exportCsv(areasArray);
                Log.i("OpenCv EVENT", "SUCCESS writing image to external storage" );


                break;

        }
    }





}


