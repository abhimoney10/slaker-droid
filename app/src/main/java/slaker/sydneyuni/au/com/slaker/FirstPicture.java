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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FirstPicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{


    private JavaCameraView mOpenCvCameraView;
    String file;
    String filecsv;

    Mat mImage;
    Mat mImageB;
    Mat mImageW;


    Mat threeChannel;
    Mat threeChannelWatershed;
    Mat oneChannel;
    Mat fg;

//    int dilation_size = 5;
//    Mat dilateMask;
//    Point dilatePoint;
//    Mat bg;


    int threshold = 10;

    Mat hierarchy;

    int meanArea;
    List<String> areasArray;


    static int count;
    boolean onClickbool;
    boolean firstPicBool=true;
    boolean onTouchBoolean = true;

    int[] times ={1,2,3,4};




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

//    public int[] fitGompertz(ArrayList areas) {
//        int[]areasLog = new int[0];
//        for (int t: this.times) {
//            Array.set(areasLog,t,areas.get(t));
//        }
//        return areasLog;
//    }

    public void exportCsv(List<String> areas) {

        String csv = Environment.getExternalStorageDirectory() + "/Images_Slaker/data.csv";

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

                file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";
                onClickbool = Imgcodecs.imwrite(file,mImageB);

                if (onClickbool) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
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
                                if(count<600){
                                    Log.d("EVENT", "run:  area is "+ meanArea);
                                    areasArray.add(String.valueOf(meanArea));
                                    count+=1;
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

//                if(!firstPicBool){
//                    Log.d("Event", "onClick Check array: " + String.valueOf(fitGompertz(areasArray)));
//                }

                break;

            case R.id.buttonExportData:

//                file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";
//                onClickbool = Imgcodecs.imwrite(file, segment(mImage));
//
//                if (onClickbool) {

                    exportCsv(areasArray);

                Log.i("OpenCv EVENT", "SUCCESS writing data to external storage");

//                }

                break;

        }
    }





}


