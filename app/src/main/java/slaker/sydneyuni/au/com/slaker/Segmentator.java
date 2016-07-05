
//    public class Segmentator{
//
//        markers = new Mat(mImage.size(), CvType.CV_32SC1,new Scalar(0));
//
///*        initialize the contours object
//        and the sum of the areas result*/
//        List<MatOfPoint> contoursFg  = new ArrayList<>();
//        List<MatOfPoint> contoursBg  = new ArrayList<>();
//        int sum =0;
//
////        Convert to gray and cut values by thresholding
//        Imgproc.cvtColor(image, threeChannel, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.cvtColor(image, threeChannelWatershed, Imgproc.COLOR_BGR2RGB);
//
//        Imgproc.threshold(threeChannel, threeChannel, 80, 255, Imgproc.THRESH_BINARY_INV);
////define backgorund for later use
//        Imgproc.dilate(threeChannel, bg,dilateMask);
//        Imgproc.threshold(bg, bg, 80, 255, Imgproc.THRESH_BINARY_INV);
//
////        Create a distance matrix to select the foreground and convert to 8 bit grayscale
//        Imgproc.distanceTransform(threeChannel,threeChannel,Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_5);
//        threeChannel.convertTo(fg,CvType.CV_8U);
//        bg.convertTo(bg,CvType.CV_8U);
////
////        second thresholding foreground
//        Imgproc.threshold(fg, fg, threshold, 255, Imgproc.THRESH_BINARY);
//
//// find contours and store them in contours object, the size should be equal to the number of soil aggregates
////        Imgproc.findContours(fg, contoursFg, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
//        Imgproc.findContours(bg, contoursBg, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
//
//        fg.convertTo(markers,CvType.CV_32SC1);
//
////
//// Draw contours over the input image and also store the contours in the markers to measure the area (I may erase this part)
////        for (int contourIdx = 0; contourIdx < contoursFg.size(); contourIdx++) {
////            Imgproc.drawContours(image, contours, contourIdx,new Scalar(255,0,0),5);
////            foregrond markers
////            Imgproc.drawContours(markers, contoursFg, contourIdx,new Scalar(255,0,0),1);
////            background markers
////            get the mean areas by contour
////            Mat contour = contoursFg.get(contourIdx);
////            sum+=Imgproc.contourArea(contour);
////        }
//
//        Imgproc.drawContours(markers, contoursBg,1,new Scalar(255,0,0),1);
////        Core.addWeighted(fg,1,bg,1,1,markers);
//
////        markers.convertTo(markers,CvType.CV_32SC1);
////        Imgproc.watershed(threeChannelWatershed,markers);
//
////        workaround if no contours are found
////        if(contoursFg.size()!= 0){
////            meanArea = sum/contoursFg.size();
////        }
//
//
////        Log.d("EVENT", "segment: Mean Area =  " + meanArea);
//
////        Log.d("Event", "segment: Contours size =  " + contours.size());
//        Log.d("EVENT", "segment: Channels image  "+fg.channels());
////        Log.d("EVENT", "segment: Channels markers  "+markers.channels());
////        Imgproc.erode(threeChannel,fg,erodeMask,erodePoint,2);
////
////        Imgproc.dilate(threeChannel,bg,dilateMask,dilatePoint,3);
////        Imgproc.threshold(bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV);
////
//        markers.convertTo(markers,CvType.CV_8UC1);
//
//        return markers;
//    }