package slaker.sydneyuni.au.com.slaker.utils;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

import java.util.ArrayList;
import java.util.Collection;

import slaker.sydneyuni.au.com.slaker.activities.ExperimentActivity;

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
             * @return an array of doubles which contain the partial derivatives of the gompertz function parameters.
             */

            double a = parameters[0];
            double b = parameters[1];
            double c = parameters[2];

            return new double[]{

                    //function
                    /*a * (Math.exp(-b * Math.exp(-c * Math.log(t)))),*/

                    //Calculate the Jacobian
                    //Partial derivative to coef a
                    Math.exp(-b*Math.pow(t,-c)),
                    //Partial derivative to coef b,
                    -a*Math.exp(-b*Math.pow(t,-c))*(Math.pow(t,-c)),
                    //Partial derivative to coef c
                    a*Math.exp(-b*Math.pow(t,-c))*(b*Math.pow(t,-c))*Math.log(t)
            };
        }
    }

    class GompertzFitter extends AbstractCurveFitter {
        protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {

            /**
             * @author Mario Fajardo
             * @param points a collection of observations obtained by createWeightedPoint method
             * @return a LeastSquaresProblem class for solving a GompertzFunction class.
             */

            final int len = points.size();
            final double[] target = new double[len];
            final double[] weights = new double[len];
            final double[] initialGuess = {ExperimentActivity.initialCoefA, 10, 1.5};

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

        return new WeightedObservedPoint(1, time, area);
    }

    public double[] fitCurve(ArrayList<WeightedObservedPoint> observations) {

        /**
         * @author Mario Fajardo
         * @param observations an array of WeightedObservations created by createWeightedPoint method.
         * @return a String value of the resulting coefficients fitted to the Gompertz function.
         */
        GompertzFitter fitter = new GompertzFitter();
//        ArrayList<WeightedObservedPoint> observations = new ArrayList<>();

       /* WeightedObservedPoint point = new WeightedObservedPoint(1,0,0);
//
        observations.add(point);*/

        return fitter.fit(observations);
    }


}




