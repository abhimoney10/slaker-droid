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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;




public class FirstPicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnClickListener{

    private JavaCameraView mOpenCvCameraView;
    String file;
    Mat mImage;
    Mat mImageF;


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
        mImage = new Mat(height, width, CvType.CV_8UC4);
        mImageF = new Mat(height, width, CvType.CV_8UC4);

    }

    @Override
    public void onCameraViewStopped() {
        mImage.release();
        mImageF.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mImage = inputFrame.rgba();
        Imgproc.Canny(mImage, mImageF,0,150);
        return mImageF; // This function must return
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

                Boolean bool = Imgcodecs.imwrite(file,mImageF);

                if (bool) {
                    Log.i("OpenCv EVENT", "SUCCESS writing image to external storage");
                }

                break;

//            case R.id.twoButton:
//                // do your code
//                break;
//
//            case R.id.threeButton:
//                // do your code
//                break;
//
//            default:
//                break;
//          }

        }
    }

}


