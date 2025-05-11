package svm;

import svm.util.ImageSample;
import java.io.*;
import java.util.*;

// Antrenează câte un SVM pentru fiecare persoană (pozitiv pe sine, negativ pe restul),
// folosind vectorii HOG salvați separat pentru fiecare persoană în .csv
public class TrainMultiSVM {

    public static void main(String[] args) throws Exception {
        File hogDir = new File("svm/data/hog_dataset/");
        File[] hogFiles = hogDir.listFiles((dir, name) -> name.endsWith(".csv") && !name.equals("out.csv"));

        if (hogFiles == null || hogFiles.length == 0) {
            System.out.println("Nu exista vectori HOG în svm/data/hog_dataset/");
            return;
        }

        // Încarcă toți vectorii în memorie
        Map<String, List<ImageSample>> allData = new HashMap<>();
        for (File f : hogFiles) {
            String name = f.getName().replace(".csv", "");
            List<ImageSample> vectors = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    double[] feat = new double[parts.length]; // toate valorile sunt parte din vector
                    for (int i = 0; i < parts.length; i++) {
                        feat[i] = Double.parseDouble(parts[i]);
                    }
                    vectors.add(new ImageSample(feat, 0)); // Eticheta o setăm mai jos
                }
            }
            allData.put(name, vectors);
        }

        File modelDir = new File("svm/data/models/");
        if (!modelDir.exists()) modelDir.mkdirs();

        // Antrenează un model per persoană
        for (String targetName : allData.keySet()) {
            System.out.println("Antrenam model pentru: " + targetName);
            List<ImageSample> trainingSet = new ArrayList<>();

            for (Map.Entry<String, List<ImageSample>> entry : allData.entrySet()) {
                int label = entry.getKey().equals(targetName) ? 1 : -1;
                for (ImageSample s : entry.getValue()) {
                    trainingSet.add(new ImageSample(s.features, label));
                }
            }

            SMOSVM svm = new SMOSVM();
            svm.train(trainingSet);
            svm.saveModel("svm/data/models/" + targetName + ".model");
            System.out.println("Model salvat: " + targetName + ".model");
        }

        System.out.println("Toate modelele au fost antrenate cu succes!");
    }
}
