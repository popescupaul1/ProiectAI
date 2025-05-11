package svm;

import svm.util.ImageSample;

import java.io.*;
import java.util.List;

public class LinearSVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private double[] weights;
    private double bias;

    // Training parameters
    private double learningRate = 0.001;
    private double regularization = 0.01; // L2 regularization strength
    private int epochs = 100;

    public LinearSVM() {}

    public LinearSVM(double learningRate, double regularization, int epochs) {
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.epochs = epochs;
    }

    public void train(List<ImageSample> samples) {
        int featureSize = samples.get(0).features.length;
        weights = new double[featureSize];
        bias = 0;

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (ImageSample sample : samples) {
                double[] x = sample.features;
                int y = sample.label;

                double prediction = dot(weights, x) + bias;

                // Apply hinge loss update rule
                if (y * prediction < 1) {
                    // Misclassified or on margin
                    for (int i = 0; i < featureSize; i++) {
                        weights[i] += learningRate * (y * x[i] - 2 * regularization * weights[i]);
                    }
                    bias += learningRate * y;
                } else {
                    // Only regularization
                    for (int i = 0; i < featureSize; i++) {
                        weights[i] += learningRate * (-2 * regularization * weights[i]);
                    }
                }
            }
        }
    }

    public int predict(double[] features) {
        if (features.length != weights.length) {
            System.err.println("Invalid input length: model=" + weights.length + ", input=" + features.length);
            return -1;
        }
        double result = dot(weights, features) + bias;
        return result >= 0 ? 1 : -1;
    }

    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public void saveModel(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this);
        }
    }

    public static LinearSVM loadModel(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (LinearSVM) in.readObject();
        }
    }

    // Optional: for model inspection
    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }
}
