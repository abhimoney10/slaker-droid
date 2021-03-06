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
    Mat fg;
    Mat hierarchy;
    int threshold = 5;



    ArrayList<Double> aggregateAreas;


    public List<MatOfPoint> contourDetection(Mat image,Integer numAggregates) {

/*      Initialize the contours object
        and the sum of the areas result
        Convert to gray and cut values by thresholding
        Create a distance matrix to select the foreground and convert to 8 bit grayscale
        second thresholding foreground
        find contours and store them in contours object, the size should be equal to the number of soil aggregates
        Filter contours in order to select only the number of selected aggregates at the
        beginning (Assuming that they are the two biggest)
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
        fg = new Mat();
        hierarchy = new Mat();

        List<MatOfPoint> contoursFg = new ArrayList<>();


        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 80, 255, Imgproc.THRESH_BINARY_INV);


        Imgproc.distanceTransform(threeChannel, threeChannel, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_5);
        threeChannel.convertTo(fg, CvType.CV_8U);

        Imgproc.threshold(fg, fg, threshold, 255, Imgproc.THRESH_BINARY);


        Imgproc.findContours(fg, contoursFg, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        if(contoursFg.size()==0){

            threeChannel.release();
            fg.release();
            hierarchy.release();

            return contoursFg;
        }else {
            if (contoursFg.size() > numAggregates) {
                Collections.sort(contoursFg, new matSorter()); // Sort the arraylist
                contoursFg = contoursFg.subList(0, numAggregates);
            }

            threeChannel.release();
            fg.release();
            hierarchy.release();

            return contoursFg;
        }
    }

    public ArrayList<Double> measureArea(List<MatOfPoint> contours) {

        aggregateAreas = new ArrayList<>();

        if (!contours.isEmpty()) {
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                Mat contour = contours.get(contourIdx);
                aggregateAreas.add(Imgproc.contourArea(contour));
            }
            return aggregateAreas;
        } else{
            return new ArrayList<>(0);
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