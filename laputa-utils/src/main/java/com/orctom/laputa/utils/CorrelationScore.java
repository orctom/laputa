package com.orctom.laputa.utils;

import java.util.Arrays;
import java.util.stream.IntStream;

public class CorrelationScore {

  public static double euclideanDistanceScore(double x1, double y1, double x2, double y2) {
    return 1 / (Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) + 1);
  }

  public static double pearsonScore(double[] sample1, double[] sample2) {
    if (sample1.length != sample2.length) {
      throw new IllegalArgumentException("Size is not equal!");
    }
    int size = sample1.length;

    double sum1 = Arrays.stream(sample1).sum();
    double sum2 = Arrays.stream(sample2).sum();

    double squareSam1 = Arrays.stream(sample1).map(i -> Math.pow(i, 2)).sum();
    double squareSam2 = Arrays.stream(sample2).map(i -> Math.pow(i, 2)).sum();

    double productSum = IntStream.range(0, size).mapToDouble(i -> sample1[i] * sample2[i]).sum();

    double num = productSum - sum1 * sum2 / size;

    double den = Math.sqrt((squareSam1 - Math.pow(sum1, 2) / size) * (squareSam2 - Math.pow(sum2, 2) / size));

    return 0 == den ? 0 : num / den;
  }
}
