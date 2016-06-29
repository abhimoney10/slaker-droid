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
    Mat mImageB;
    Mat mImageW;


    Mat threeChannel;
    Mat fg;
    Mat erodeMask;
    Point erodePoint;
    Mat dilateMask;
    Point dilatePoint;
    Mat bg;

    Mat markers;


    static int count;
    boolean onClickbool;
    boolean firstPicBool;
    boolean onTouchBoolean = true;



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
        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.distanceTransform(threeChannel,image,Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE);
        image.convertTo(image,CvType.CV_8U);
        Imgproc.threshold(image, image, 10, 255, Imgproc.THRESH_BINARY);

        Imgproc.Canny(image,image,3,3);
        Log.d("EVENT", "binarize: "+image.channels());
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

        fg = new Mat(mImage.size(),CvType.CV_32SC1);
        erodeMask = new Mat();
        erodePoint = new Point(-1,-1);


        bg = new Mat(mImage.size(),CvType.CV_32SC1);
        dilateMask = new Mat();
        dilatePoint = new Point(-1,-1);

        markers = new Mat(mImage.size(), CvType.CV_32SC1,new Scalar(0));


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

                Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        bg = binarize(mImage);
                        file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test"+ String.valueOf(count) + ".png";
                        count++;

                        firstPicBool=Imgcodecs.imwrite(file,bg);

                        if (firstPicBool) {
                            Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                        }

                    }
                }, 0, 1000);

                break;

            case R.id.buttonWatershed:
                Mat watershed_mat;
                watershed_mat=watershedSegmenter(mImage, binarize(mImage));

                file = Environment.getExternalStorageDirectory() + "/Images_Slaker/test.png";
                onClickbool = Imgcodecs.imwrite(file,watershed_mat);

                if (onClickbool) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                }

                break;

        }
    }





}


