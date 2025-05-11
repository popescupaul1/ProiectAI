package svm.util;
import svm.LinearSVM;
import svm.util.HOGFeatureExtractor;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
//Detectează fete în imagini folosind un clasificator SVM antrenat. 
//Returnează patrate pentru zonele detectate.
public class FaceDetector {

    private LinearSVM svm;

    public FaceDetector(String modelPath) throws Exception {
        svm = LinearSVM.loadModel(modelPath);
    }

    public List<Rectangle> detectFaces(BufferedImage image) {
        List<Rectangle> detections = new ArrayList<>();

        int windowSize = 128;
        int step = 16;

        for (int y = 0; y <= image.getHeight() - windowSize; y += step) {
            for (int x = 0; x <= image.getWidth() - windowSize; x += step) {
                BufferedImage sub = image.getSubimage(x, y, windowSize, windowSize);
                double[] features = HOGFeatureExtractor.extract(sub);
                int label = svm.predict(features);
                if (label == 1) {
                    detections.add(new Rectangle(x, y, windowSize, windowSize));
                }
            }
        }

        return applyNonMaxSuppression(detections, 0.3);
    }

    private List<Rectangle> applyNonMaxSuppression(List<Rectangle> boxes, double threshold) {
        List<Rectangle> result = new ArrayList<>();
        boxes.sort(Comparator.comparingInt(r -> -r.width * r.height));

        while (!boxes.isEmpty()) {
            Rectangle current = boxes.remove(0);
            result.add(current);
            boxes.removeIf(r -> computeIoU(current, r) > threshold);
        }

        return result;
    }

    private double computeIoU(Rectangle a, Rectangle b) {
        int x1 = Math.max(a.x, b.x);
        int y1 = Math.max(a.y, b.y);
        int x2 = Math.min(a.x + a.width, b.x + b.width);
        int y2 = Math.min(a.y + a.height, b.y + b.height);

        int w = Math.max(0, x2 - x1);
        int h = Math.max(0, y2 - y1);
        int interArea = w * h;
        int unionArea = a.width * a.height + b.width * b.height - interArea;

        return (double) interArea / unionArea;
    }
}
