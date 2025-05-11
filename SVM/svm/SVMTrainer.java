package svm;

import svm.util.ImageSample;
import java.util.List;
import svm.SMOSVM; 
public class SVMTrainer {

    public void train(List<ImageSample> samples, String modelOutputPath) throws Exception {
        SMOSVM svm = new SMOSVM();
        svm.train(samples);
        svm.saveModel(modelOutputPath);
    }

    public static void main(String[] args) throws Exception {
        
    }
}
