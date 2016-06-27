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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Timer;
import java.util.TimerTask;


public class FirstPicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{


    private JavaCameraView mOpenCvCameraView;
    String file;
    Mat mImage;
    Mat mImageF;
    Mat markers;
    Mat threeChannel;
    Mat fg;
    Mat erodeMask;
    Point erodePoint;
    Mat dilateMask;
    Point dilatePoint;
    Mat bg;
    WatershedSegmenter segmenter;
    static int count;
    boolean booli;


    public class WatershedSegmenter{

        public Mat process(Mat image){
            markers.convertTo(markers,CvType.CV_32S);
            Imgproc.watershed(image, markers);
            markers.convertTo(markers,CvType.CV_32SC1);
            return markers;
        }
    }

    public Mat Segmentation(Mat image){
        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_BGR2GRAY );
        Imgproc.threshold(threeChannel, threeChannel, 40, 255, Imgproc.THRESH_BINARY);
//        Log.i("OpenCv EVENT", String.valueOf(threeChannel.depth()));
//        Log.i("OpenCv EVENT", String.valueOf(threeChannel.channels()));
//        Log.i("OpenCv EVENT", String.valueOf(fg.depth()));
//        Log.i("OpenCv EVENT", String.valueOf(fg.channels()));
        Imgproc.erode(threeChannel,fg,erodeMask,erodePoint,5);
        Imgproc.dilate(fg,bg,dilateMask,dilatePoint,5);
        Imgproc.threshold(bg,bg,40, 255,Imgproc.THRESH_BINARY);
        return bg;
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

        Button segmentImage = (Button) findViewById(R.id.buttonSegment);
        segmentImage.setOnClickListener(this);

        Button startExperiment = (Button) findViewById(R.id.buttonBurstPicture);
        startExperiment.setOnClickListener(this);



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
        mImage = new Mat(height, width, CvType.CV_8U);
        mImageF = new Mat(height, width, CvType.CV_8U);
        threeChannel = new Mat();

        fg = new Mat(mImage.size(),CvType.CV_8U);
        erodeMask = new Mat();
        erodePoint = new Point(-1,-1);


        bg = new Mat(mImage.size(),CvType.CV_8U);
        dilateMask = new Mat();
        dilatePoint = new Point(-1,-1);


        markers = new Mat(mImage.size(),CvType.CV_8U,new Scalar(0));

        segmenter = new WatershedSegmenter();
//
    }

    @Override
    public void onCameraViewStopped() {
        mImage.release();
        mImageF.release();
        threeChannel.release();

        fg.release();
        erodeMask.release();

        bg.release();
        dilateMask.release();

        markers.release();

//        bg.release();
//




    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mImage = inputFrame.rgba();

//        Imgproc.Canny(mImage, mImageF,0,150);
        return mImage; // This function must return
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";

        Boolean bool = Imgcodecs.imwrite(file,mImageF);

        if (bool) {
            Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
        }
            return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonSaveImage:

                file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";

                Boolean bool = Imgcodecs.imwrite(file,mImage);

                if (bool) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                }

                break;

            case R.id.buttonSegment:

                bg = Segmentation(mImage);

                file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";

                bool = Imgcodecs.imwrite(file,bg);

                if (bool) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                }

                break;
//
            case R.id.buttonBurstPicture:


                Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        bg = Segmentation(mImage);
                        file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test"+ String.valueOf(count) + ".png";
                        count++;

                        booli=Imgcodecs.imwrite(file,bg);

                        if (booli) {
                            Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                        }

                    }
                }, 0, 1000);



                break;


        }
    }


}


