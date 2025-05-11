package svm.gui;

import java.awt.*;
import java.awt.event.*;
import svm.SVM;

public class SimulationControl extends Dialog {
    SVM svm;
    public TextArea ta;
    public Button close;

    public SimulationControl(SVM svm, int width, int height) {
        super(svm, "Simulation Info", false);
        this.svm = svm;
        setBackground(svm.settings.background_color_default);
        setResizable(false);
        setLayout(null);

        Label title = new Label("Simulation log");
        title.setBounds(20, 30, 200, 30);
        title.setForeground(Color.WHITE);
        add(title);

        ta = new TextArea("Logs or messages will appear here...");
        ta.setBounds(20, 70, width - 40, height - 130);
        ta.setBackground(svm.settings.button_color_default);
        ta.setForeground(svm.settings.string_color_default);
        add(ta);

        close = new Button("Close");
        close.setBounds((width - 100) / 2, height - 50, 100, 30);
        close.setBackground(svm.settings.button_color_default);
        close.setForeground(svm.settings.button_label_default);
        close.addActionListener(e -> this.setVisible(false));
        add(close);
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            this.dispose();
            return true;
        }
        return super.handleEvent(e);
    }
}
