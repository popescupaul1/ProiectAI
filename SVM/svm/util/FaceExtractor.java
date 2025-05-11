package svm.util;

import svm.util.HOGFeatureExtractor;
import svm.util.FaceDetector;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;
import java.util.Comparator;
//Folosește FaceDetector pentru a extrage fața cu aria cea mai mare dintr-o imagine. 
//O scalează la 128x128 px.
public class FaceExtractor {

    private FaceDetector detector;

    public FaceExtractor(String modelPath) throws Exception {
        detector = new FaceDetector(modelPath);
    }

    public BufferedImage extractLargestFace(BufferedImage image) {
        List<Rectangle> detections = detector.detectFaces(image);
        if (detections == null || detections.isEmpty()) {
            System.out.println("Nicio fata detectata in imagine.");
            return null;
        }

        // Selectează patratul cu aria maxima
        Rectangle max = detections.stream()
                .max(Comparator.comparingInt(r -> r.width * r.height))
                .orElse(null);

        if (max == null || max.width <= 0 || max.height <= 0) {
			System.out.println("Nicio față validă detectată.");
			return null;
		}


        System.out.println("Fata detectata la: x=" + max.x + ", y=" + max.y + 
                           ", width=" + max.width + ", height=" + max.height);

        
        BufferedImage face = image.getSubimage(max.x, max.y, max.width, max.height);

        // Scaleaza la 128x128
        BufferedImage scaled = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(face, 0, 0, 128, 128, null);
        g.dispose();

        return scaled;
    }

    public static void main(String[] args) throws Exception {
        FaceExtractor extractor = new FaceExtractor("data/models/face_detector.model");

        File testImg = new File("data/test/frame.jpg");
        if (!testImg.exists()) {
            System.out.println("Imaginea nu exista!");
            return;
        }

        BufferedImage img = ImageIO.read(testImg);
        BufferedImage face = extractor.extractLargestFace(img);

        if (face != null) {
            ImageIO.write(face, "jpg", new File("data/test/test_face.jpg"));
            System.out.println("Fata a fost extrasa cu succes.");
        } else {
            System.out.println("Nicio fata detectata.");
        }
    }
}