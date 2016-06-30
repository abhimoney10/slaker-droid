package slaker.sydneyuni.au.com.slaker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class FirstPicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{


    private JavaCameraView mOpenCvCameraView;
    String file;
    Mat mImage;
    Mat mImageB;
    Mat mImageW;


    Mat threeChannel;
    Mat oneChannel;
    Mat fg;
    Mat erodeMask;
    Point erodePoint;
    Mat dilateMask;
    Point dilatePoint;
    Mat bg;

    Mat markers;
    int threshold = 10;

    Mat hierarchy;

    int meanArea;


    static int count;
    boolean onClickbool;
    boolean firstPicBool;
    boolean onTouchBoolean = true;


    double[] times ={1.0,1.1,1.2,1.4,1.6,
            1.9,2.1,2.4,2.8,3.2,3.6,4.2,4.7,
            5.4, 6.2,7.0,8.0,9.2,10.4,11.9,
            13.6,15.5,17.6,20.1,22.9,26.1,29.7,
            33.9,38.6,44.0,50.2,57.2,65.2,74.3,
            84.6,96.4,109.9,125.2,142.7,162.6,
            185.3,211.1,240.5,274.1,312.3,355.9,405.5,
            462.1,526.5,600};


    public Mat watershedSegmenter(Mat image, Mat mark){
        Imgproc.cvtColor(image,mImageW,Imgproc.COLOR_BGRA2BGR,4);
        Log.d("EVENT", "watershedSegmenter: image Channels  :  " + mImageW.channels());
        mark.convertTo(mark,CvType.CV_32SC4);
        Log.d("EVENT", "watershedSegmenter: markers Channels  :  " + mark.channels());
        Imgproc.watershed(mImageW, mark);
        mark.convertTo(mark,CvType.CV_8U);
        return mark;
    }


    public Mat binarize(Mat image){

/*        initialize the contours object
        and the sum of the areas result*/
        List<MatOfPoint> contours  = new ArrayList<>();
        int sum =0;

//        Convert to gray and cut values by thresholding
        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 80, 255, Imgproc.THRESH_BINARY_INV);

//        Create a distance matrix to select the foreground and convert to 8 bit grayscale
        Imgproc.distanceTransform(threeChannel,threeChannel,Imgproc.CV_DIST_L1, Imgproc.CV_DIST_MASK_3);
        threeChannel.convertTo(oneChannel,CvType.CV_8U);
//        second thresholding
        Imgproc.threshold(oneChannel, oneChannel, threshold, 255, Imgproc.THRESH_BINARY);
//          find contours and store them in contours object, the size should be equal to the number of soil aggregates
        Imgproc.findContours(oneChannel, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

//          Draw contours over the input image and also store the contours in the markers to measure the area (I may erase this part)
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(image, contours, contourIdx,new Scalar(255,255,255),5);
            Imgproc.drawContours(markers, contours, contourIdx,new Scalar(255,255,255),5);
//            get the mean areas by contour
            Mat contour = contours.get(contourIdx);
            sum+=Imgproc.contourArea(contour);
        }

//        workaround if no contours are found
        if(contours.size()!= 0){
            meanArea = sum/contours.size();
        }

//        Log.d("EVENT", "binarize: Mean Area =  " + meanArea);

        Log.d("Event", "binarize: Contours size =  " + contours.size());
//        Log.d("EVENT", "binarize: "+threeChannel.channels());
//        Imgproc.erode(threeChannel,fg,erodeMask,erodePoint,2);
//
//        Imgproc.dilate(threeChannel,bg,dilateMask,dilatePoint,3);
//        Imgproc.threshold(bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV);
//
//        Core.addWeighted(fg,1,bg,1,1,markers);

        return image;
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

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.FirstPictureCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);


        Button saveImage = (Button) findViewById(R.id.buttonSaveImage);
        saveImage.setOnClickListener(this);

        Button startExperiment = (Button) findViewById(R.id.buttonBurstPicture);
        startExperiment.setOnClickListener(this);

        Button buttonWatershed = (Button) findViewById(R.id.buttonWatershed);
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
        oneChannel = new Mat();

        fg = new Mat(mImage.size(),CvType.CV_32SC1);
        erodeMask = new Mat();
        erodePoint = new Point(-1,-1);


        bg = new Mat(mImage.size(),CvType.CV_32SC1);
        dilateMask = new Mat();
        dilatePoint = new Point(-1,-1);

        markers = new Mat(mImage.size(), CvType.CV_32SC1,new Scalar(0));

        hierarchy = new Mat();

//
    }

    @Override
    public void onCameraViewStopped() {
        mImage.release();
        mImageB.release();
        mImageW.release();


        threeChannel.release();
        fg.release();
        erodeMask.release();

        bg.release();
        dilateMask.release();

        markers.release();

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
            mImageB = binarize(mImage);
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

//              Here i need to implement to run a method over a pre-specified amount of times (in seconds)

                break;

            case R.id.buttonWatershed:

                file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";
                onClickbool = Imgcodecs.imwrite(file,binarize(mImage));

                if (onClickbool) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                }

                break;

        }
    }





}


