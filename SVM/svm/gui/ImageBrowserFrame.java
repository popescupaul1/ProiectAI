package svm.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class ImageBrowserFrame extends JFrame {
    private JLabel imageLabel;
    private JLabel counterLabel;
    private List<File> images;
    private int currentIndex = 0;
    private String pseudonim;

    public ImageBrowserFrame(Frame parent, String pseudonim) {
        this.pseudonim = pseudonim.toLowerCase().trim();
        setTitle("Vizualizare imagini - " + this.pseudonim);
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        imageLabel = new JLabel("", JLabel.CENTER);
        counterLabel = new JLabel("0 / 0", JLabel.CENTER);

        add(imageLabel, BorderLayout.CENTER);
        add(counterLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevBtn = new JButton("<<");
        JButton nextBtn = new JButton(">>");
        JButton deleteBtn = new JButton("Șterge");

        buttonPanel.add(prevBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(nextBtn);
        add(buttonPanel, BorderLayout.NORTH);

        File dir = new File("svm/data/images_raw/" + this.pseudonim);
        if (dir.exists() && dir.isDirectory()) {
            images = new ArrayList<>(Arrays.asList(
                dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jpg"))
            ));
            images.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            if (!images.isEmpty()) {
                showImage();
            } else {
                imageLabel.setText("Nicio imagine găsită.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Director inexistent: " + dir.getPath());
        }

        prevBtn.addActionListener((ActionEvent e) -> {
            if (currentIndex > 0) {
                currentIndex--;
                showImage();
            }
        });

        nextBtn.addActionListener((ActionEvent e) -> {
            if (currentIndex < images.size() - 1) {
                currentIndex++;
                showImage();
            }
        });

        deleteBtn.addActionListener((ActionEvent e) -> {
			if (!images.isEmpty()) {
				File currentFile = images.get(currentIndex);

				imageLabel.setIcon(null);
				imageLabel.repaint();

				if (currentFile.delete()) {
					images.remove(currentIndex);
					if (currentIndex >= images.size()) {
						currentIndex = Math.max(0, images.size() - 1);
					}
					showImage();
				} else {
					JOptionPane.showMessageDialog(this,
							"Nu s-a putut șterge imaginea.");
				}
			}
		});



        setVisible(true);
    }

    private void showImage() {
        if (images.isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText("Nu mai sunt imagini.");
            counterLabel.setText("0 / 0");
            return;
        }

        File imgFile = images.get(currentIndex);
        try {
            BufferedImage img = ImageIO.read(imgFile);
            Image scaled = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.setText("");
            counterLabel.setText((currentIndex + 1) + " / " + images.size());
        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Imagine coruptă.");
        }
    }
}
