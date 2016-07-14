//package slaker.sydneyuni.au.com.slaker.utils;
//
//import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
//import org.apache.commons.math3.fitting.AbstractCurveFitter;
//import org.apache.commons.math3.fitting.WeightedObservedPoint;
//import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
//import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
//import org.apache.commons.math3.linear.DiagonalMatrix;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//
///**
// * Created by mfaj1435 on 7/12/2016.
// *
// *
// *
// */
//
//
//
//class GompertzFunction implements ParametricUnivariateFunction {
//    public double value(double t, double[] parameters) {
////            return parameters[0] * Math.pow(t, parameters[1]) * Math.exp(-parameters[2] * t);
//        return parameters[0] * (Math.exp(-parameters[1] * Math.exp(-parameters[2] * Math.log(t))));
//    }
//
//    @Override
//    public double[] gradient(double t, double[] parameters) {
//        double a  = parameters[0];
//        double b = parameters[1];
//        double c = parameters[2];
//
//        return new double[]{
//                a * (Math.exp(-b * Math.exp(-c * Math.log(t)))),
//                a*b*c*Math.exp(-b * Math.pow(t,-c))*Math.pow(t,-c-1),
//                a*b*c*(Math.exp(-b*Math.pow(t,-c))*Math.pow(t,-c-2)*(-c-1)+(b*c*Math.exp(-b*Math.pow(t,-c)))*Math.pow(t,-2*c-2))
//
////                    Math.exp(-c*t) * Math.pow(t, b),
////                    a * Math.exp(-c*t) * Math.pow(t, b) * Math.log(t),
////                    a * (-Math.exp(-c*t)) * Math.pow(t, b+1)
//
//        };
//    }
//}
//public class CurveFitter {
//
//    public class GompertzFitter extends AbstractCurveFitter {
//        protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {
//            final int len = points.size();
//            final double[] target = new double[len];
//            final double[] weights = new double[len];
//            final double[] initialGuess = {1.0, 1.0, 1.0};
//
//            int i = 0;
//            for (WeightedObservedPoint point : points) {
//                target[i] = point.getY();
//                weights[i] = point.getWeight();
//                i += 1;
//            }
//
//            final AbstractCurveFitter.TheoreticalValuesFunction model = new
//                    AbstractCurveFitter.TheoreticalValuesFunction(new GompertzFunction(), points);
//
//            return new LeastSquaresBuilder().
//                    maxEvaluations(Integer.MAX_VALUE).
//                    maxIterations(Integer.MAX_VALUE).
//                    start(initialGuess).
//                    target(target).
//                    weight(new DiagonalMatrix(weights)).
//                    model(model.getModelFunction(), model.getModelFunctionJacobian()).
//                    build();
//        }
//    }
//
//    public WeightedObservedPoint createWeightedPoint(double time,double area){
//        singleObservation = new WeightedObservedPoint(1,time,area);
//
//        return singleObservation;
//    }
//
//    public String fitCurve(ArrayList<WeightedObservedPoint> observations){
//        GompertzFitter fitter = new GompertzFitter();
////        ArrayList<WeightedObservedPoint> observations = new ArrayList<>();
//
//        WeightedObservedPoint point = new WeightedObservedPoint(1,0,0);
////
//        observations.add(point);
//
//        final double coeffs[] = fitter.fit(observations);
//
//        return Arrays.toString(coeffs);
//    }
//
//}
