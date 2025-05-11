package svm;

import java.io.*;
import java.util.List;
import svm.util.ImageSample;

public class SMOSVM implements Serializable {
    private double[][] X;
    private int[] Y;
    private double[] alphas;
    private double b;
    private double C = 1.0;
    private double tol = 1e-3;
    private double eps = 1e-3;
    private int maxPasses = 5;

    private double gamma = 0.01;
    private double r = 0.0;

    private double kernel(double[] x1, double[] x2) {
        double dot = 0;
        for (int i = 0; i < x1.length; i++) {
            dot += x1[i] * x2[i];
        }
        return Math.tanh(gamma * dot + r);
    }

    public void train(double[][] X, int[] Y) {
        this.X = X;
        this.Y = Y;
        int m = X.length;
        alphas = new double[m];
        b = 0;
        int passes = 0;

        while (passes < maxPasses) {
            int changed = 0;

            for (int i = 0; i < m; i++) {
                double Ei = predictRaw(X[i]) - Y[i];

                if ((Y[i] * Ei < -tol && alphas[i] < C) || (Y[i] * Ei > tol && alphas[i] > 0)) {
                    int j = (i + 1) % m;
                    double Ej = predictRaw(X[j]) - Y[j];

                    double alpha_i_old = alphas[i];
                    double alpha_j_old = alphas[j];

                    double L, H;
                    if (Y[i] != Y[j]) {
                        L = Math.max(0, alphas[j] - alphas[i]);
                        H = Math.min(C, C + alphas[j] - alphas[i]);
                    } else {
                        L = Math.max(0, alphas[i] + alphas[j] - C);
                        H = Math.min(C, alphas[i] + alphas[j]);
                    }

                    if (L == H) continue;

                    double eta = 2 * kernel(X[i], X[j]) - kernel(X[i], X[i]) - kernel(X[j], X[j]);
                    if (eta >= 0) continue;

                    alphas[j] -= Y[j] * (Ei - Ej) / eta;
                    alphas[j] = clip(alphas[j], L, H);

                    if (Math.abs(alphas[j] - alpha_j_old) < eps) continue;

                    alphas[i] += Y[i] * Y[j] * (alpha_j_old - alphas[j]);

                    double b1 = b - Ei
                        - Y[i] * (alphas[i] - alpha_i_old) * kernel(X[i], X[i])
                        - Y[j] * (alphas[j] - alpha_j_old) * kernel(X[i], X[j]);
                    double b2 = b - Ej
                        - Y[i] * (alphas[i] - alpha_i_old) * kernel(X[i], X[j])
                        - Y[j] * (alphas[j] - alpha_j_old) * kernel(X[j], X[j]);

                    b = (b1 + b2) / 2.0;
                    changed++;
                }
            }

            if (changed == 0) passes++;
            else passes = 0;
        }
    }

    public void train(List<ImageSample> samples) {
        int m = samples.size();
        double[][] X = new double[m][];
        int[] Y = new int[m];

        for (int i = 0; i < m; i++) {
            X[i] = samples.get(i).features;
            Y[i] = samples.get(i).label;
        }

        train(X, Y);
    }

    public void saveModel(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this);
        }
    }

    public static SMOSVM loadModel(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (SMOSVM) in.readObject();
        }
    }

    private double predictRaw(double[] x) {
        double sum = 0;
        for (int i = 0; i < X.length; i++) {
            if (alphas[i] > 0)
                sum += alphas[i] * Y[i] * kernel(X[i], x);
        }
        return sum + b;
    }

    public int predict(double[] x) {
        return predictRaw(x) >= 0 ? 1 : -1;
    }

    private double clip(double value, double low, double high) {
        return Math.max(low, Math.min(high, value));
    }
}
