package slaker.sydneyuni.au.com.slaker.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Segmenter {


    Mat threeChannel;
    Mat threeChannelWatershed;
    Mat fg;

    int numAggregates = 2;
    int threshold = 10;

    Mat hierarchy;


    public List<MatOfPoint> contourDetection(Mat image) {

/*      Initialize the contours object
        and the sum of the areas result
        Convert to gray and cut values by thresholding
        Create a distance matrix to select the foreground and convert to 8 bit grayscale
        second thresholding foreground
        find contours and store them in contours object, the size should be equal to the number of soil aggregates
        Filter contours in order to select only the number of selected aggregates at the
        beggining (Assuming that they are the two biggest)
        Draw contours over the input image and also store the contours in the markers to measure the area (I may erase this part)
        */

        class matSorter implements Comparator<MatOfPoint>{
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

        threeChannel = new Mat();
        threeChannelWatershed = new Mat(image.size(), CvType.CV_8UC2);
        fg = new Mat();

        hierarchy = new Mat();

        List<MatOfPoint> contoursFg = new ArrayList<>();


        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(image, threeChannelWatershed, Imgproc.COLOR_BGR2RGB);
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY_INV);


        Imgproc.distanceTransform(threeChannel, threeChannel, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_5);
        threeChannel.convertTo(fg, CvType.CV_8U);

        Imgproc.threshold(fg, fg, threshold, 255, Imgproc.THRESH_BINARY);


        Imgproc.findContours(fg, contoursFg, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        if(contoursFg.size()==0){
            return contoursFg;
        }else {
            if (contoursFg.size() > numAggregates) {
                Collections.sort((ArrayList) contoursFg, new matSorter()); // Sort the arraylist
                contoursFg = contoursFg.subList(0, numAggregates);
            }
            return contoursFg;
        }
    }

    public double measureArea(List<MatOfPoint> contours) {

        int sum = 0;

        if (!contours.isEmpty()){
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                Mat contour = contours.get(contourIdx);
                sum += Imgproc.contourArea(contour);
            }

            if (contours.size() != 0) {
                return sum / contours.size();
            } else {
                return 0;
            }
        }else {
            return 0;
        }
    }

    public Mat drawContours(List<MatOfPoint> contours, Mat image){


        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            foreground markers
            Imgproc.drawContours(image, contours, contourIdx, new Scalar(255, 255 / (contourIdx + 3), 255 / (contourIdx + 3)), 2);
        }

        image.convertTo(image, CvType.CV_8UC1);

        return image;
    }

}