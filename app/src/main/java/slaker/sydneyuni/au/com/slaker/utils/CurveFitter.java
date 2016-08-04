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
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class CurveFitter {


    class GompertzFunction implements ParametricUnivariateFunction {
        public double value(double t, double[] parameters) {
            /**
             * @author Mario Fajardo
             * @param t the independent variable, in this case the time.
             * @param parameters the coefficients to be optimized for the gompertz function.
             * @return the resulting values of the function.
             */

//            return parameters[0] * Math.pow(t, parameters[1]) * Math.exp(-parameters[2] * t);
            return parameters[0] * (Math.exp(-parameters[1] * Math.exp(-parameters[2] * Math.log(t))));
        }

        @Override
        public double[] gradient(double t, double[] parameters) {

            /**
             * @author Mario Fajardo
             * @param t the independent variable, in this case the time.
             * @param parameters the coefficients to be optimized for the gompertz function.
             * @return an array of doubles which contain the first, second and third derivatives of the gompertz function.
             */

            double a = parameters[0];
            double b = parameters[1];
            double c = parameters[2];

            return new double[]{

                    //function
                    /*a * (Math.exp(-b * Math.exp(-c * Math.log(t)))),*/

                    //first derivative
                    a * b * c * Math.exp(-b * Math.pow(t, -c)) * Math.pow(t, -c - 1),

                    // second derivative
                    a * b * c * (Math.exp(-b * Math.pow(t, -c)) * Math.pow(t, -c - 2) * (-c - 1) + (b * c * Math.exp(-b * Math.pow(t, -c))) * Math.pow(t, -2 * c - 2))
                    ,

                    // third derivative
                    -a * b * c * (Math.pow(b, 2) * Math.pow(c, 2) * Math.exp(-b * Math.pow(t, c)) * Math.pow(t, (3 * c) - 3) +
                            3 * b * c * Math.exp(-b * Math.pow(t, c)) * Math.pow(t, (2 * c) - 3) +
                            Math.pow(c, 2) * Math.exp(-b * Math.pow(t, c)) * Math.pow(t, c - 3) +
                            2 * Math.exp(-b * Math.pow(t, c)) * Math.pow(t, c - 3) -
                            3 * c * Math.exp(-b * Math.pow(t, c)) * Math.pow(t, c - 3) -
                            3 * b * Math.pow(c, 2) * Math.exp(-b * Math.pow(t, c)) * Math.pow(t, (2 * c) - 3))
//
//
//
//                    Math.exp(-c*t) * Math.pow(t, b),
//                    a * Math.exp(-c*t) * Math.pow(t, b) * Math.log(t),
//                    a * (-Math.exp(-c*t)) * Math.pow(t, b+1)

            };
        }
    }

    class GompertzFitter extends AbstractCurveFitter {
        protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {

            /**
             * @author Mario Fajardo
             * @param points a collection ob observations obtained by createWeightedPoint method
             * @return a LeastSquaresProblem class for solving a GompertzFunction class.
             */

            final int len = points.size();
            final double[] target = new double[len];
            final double[] weights = new double[len];
            final double[] initialGuess = {1, 1.15, 1.0};

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

    public WeightedObservedPoint createWeightedPoint(double time, double area) {

        /**Simple wraper for creating a weighted point to be used by GompertzFitter.getProblem.
         * @author Mario Fajardo
         * @param time a double specifying the time of the observation.
         * @param area a double specifying the area of the soil aggregate at time t.
         * @return a weighted point to be used by GompertzFitter.getProblem
         */
        WeightedObservedPoint singleObservation = new WeightedObservedPoint(1, time, area);

        return singleObservation;
    }

    public String fitCurve(ArrayList<WeightedObservedPoint> observations) {
        /**
         * @author Mario Fajardo
         * @param observations an array of WeightedObservations created by createWeightedPoint method.
         * @return a String value of the resulting coefficients fitted to the G`ompertz function.
         */
        GompertzFitter fitter = new GompertzFitter();
//        ArrayList<WeightedObservedPoint> observations = new ArrayList<>();

       /* WeightedObservedPoint point = new WeightedObservedPoint(1,0,0);
//
        observations.add(point);*/

        final double coeffs[] = fitter.fit(observations);

        return Arrays.toString(coeffs);
    }

}




