package svm.util;

import svm.util.ImageSample;
import svm.LinearSVM;

import java.io.*;
import java.util.*;

// Antrenează un model SVM pentru detectarea fețelor,
// folosind direct vectorii HOG salvați în svm/data/hog_dataset/out.csv.

public class FaceDetectorTrainer {

    public static void main(String[] args) throws Exception {
        File hogFile = new File("svm/data/hog_dataset/out.csv");

        if (!hogFile.exists()) {
            System.out.println("Nu exista svm/data/hog_dataset/out.csv. Rulează mai întai GenerateHOGForDetector!");
            return;
        }

        List<ImageSample> samples = new ArrayList<>();
        int expectedLength = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(hogFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                if (expectedLength == -1) {
                    expectedLength = parts.length - 1;
                }

                if (parts.length - 1 != expectedLength) {
                    System.out.println("Linie ignorată (dimensiune incorectă): " + Arrays.toString(parts));
                    continue;
                }

                double[] features = new double[expectedLength];
                for (int i = 0; i < expectedLength; i++) {
                    features[i] = Double.parseDouble(parts[i]);
                }
                int label = Integer.parseInt(parts[parts.length - 1]);
                samples.add(new ImageSample(features, label));
            }
        }

        System.out.println("Antrenare SVM pe " + samples.size() + " exemple...");

        LinearSVM svm = new LinearSVM();
		svm.train(samples);

        File outDir = new File("svm/data/models/");
        if (!outDir.exists()) outDir.mkdirs();

        svm.saveModel("svm/data/models/face_detector.model");
        System.out.println("Modelul face_detector.model a fost salvat cu succes!");
    }
}
