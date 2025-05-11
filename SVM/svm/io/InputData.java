package svm.io;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import svm.SVM;

public class InputData extends Dialog {
    SVM svm;
    public String input_file = null;

    public InputData(SVM svm){
        super(svm, "Input Data", false);
        this.svm = svm;
        setResizable(false);
        setBackground(Color.darkGray);
        setLayout(null);
    }

    public void loadInputData() {
        FileDialog fd = new FileDialog(this, "Load Input Data", FileDialog.LOAD);
        fd.setDirectory(".");
        fd.setVisible(true);
        String file = fd.getFile();
        if(file != null){
            input_file = fd.getDirectory() + file;
            System.out.println("Loaded input file: " + input_file);
        }
    }
}
