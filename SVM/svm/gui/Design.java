package svm.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import svm.SVM;

public class Design extends Panel implements MouseListener, MouseMotionListener{
	SVM svm;
	Image im;
	Graphics img;
	int ww, hh;
	int Ox, Oy, cx, cy, ccx, ccy;
	boolean init = true;
	String coords = "";
	public boolean show_coords = false;
	public boolean show_line = false;
	public boolean show_lines = false;
	public boolean calculates = false;
	public int x1, y1, x2, y2;
	public int x01, y01, x02, y02;
	public int x11, y11, x12, y12;
	
	public void paint(Graphics g) {
        update(g);
    }
	public Design(SVM svm){
		this.svm = svm;
		addMouseListener(this);
		addMouseMotionListener(this);		
	}
	
	public void initO(){
		Ox = ww/2; Oy = hh/2;
		cx = 0; cy = 0;
		repaint();
	}
	
	public void update(Graphics g) {
        if (init) {
            ww = getSize().width;
            hh = getSize().height;
            im = createImage(ww, hh);
            img = im.getGraphics();
            initO();
            init = false;
        }

        img.setColor(svm.settings.background_color);
        img.fillRect(0, 0, ww, hh);

        drawAxis(img);

        if (show_line) {
            img.setColor(Color.GREEN);
            img.drawLine(Ox + x1, Oy - y1, Ox + x2, Oy - y2);
        }

        if (show_coords) {
            img.setColor(Color.WHITE);
            img.drawString(coords, ccx + 15, ccy + 30);
        }

        if (calculates && svm.calculates != null) {
            img.drawImage(svm.calculates, (ww - svm.calculates.getWidth(this)) / 2,
                    (hh - svm.calculates.getHeight(this)) / 2, this);
        }

        g.drawImage(im, 0, 0, this);
    }


	public void drawAxis(Graphics g){
		//deseneaza gridul
		if(svm.settings.grid){
			g.setColor(svm.settings.grid_color);	
			for(int i=Ox+svm.settings.axis_min; i<=Ox+svm.settings.axis_max; i+=svm.settings.grid_size) g.drawLine(i, Oy+svm.settings.axis_min, i, Oy+svm.settings.axis_max);
			for(int j=Oy+svm.settings.axis_min; j<=Oy+svm.settings.axis_max; j+=svm.settings.grid_size) g.drawLine(Ox+svm.settings.axis_min, j, Ox+svm.settings.axis_max, j);
		}
		//deseneaza axele
		if(svm.settings.axis){
			g.setColor(svm.settings.axis_color);		
			g.drawLine(Ox+svm.settings.axis_min, Oy, Ox+svm.settings.axis_max, Oy);
			g.drawLine(Ox, Oy+svm.settings.axis_min, Ox, Oy+svm.settings.axis_max);	
			//deseneaza gradatiile
			if(svm.settings.gradations){
				for(int i=Ox; i<=Ox+svm.settings.axis_max; i+=svm.settings.axis_gradations) g.drawLine(i, Oy-2, i, Oy+2);
				for(int i=Ox; i>=Ox+svm.settings.axis_min; i-=svm.settings.axis_gradations) g.drawLine(i, Oy-2, i, Oy+2);
				for(int j=Oy; j<=Oy+svm.settings.axis_max; j+=svm.settings.axis_gradations) g.drawLine(Ox-2, j, Ox+2, j);
				for(int j=Oy; j>=Oy+svm.settings.axis_min; j-=svm.settings.axis_gradations) g.drawLine(Ox-2, j, Ox+2, j);
			}
		}
	}

	public void setPointsOfLine(float[] w){
		show_line = true;
		if(Math.abs(w[0]) < Math.abs(w[1])){
			x1 = svm.settings.axis_min;
			y1 = (int)((-w[2]-w[0]*x1)/w[1]+0.5);
			x2 = svm.settings.axis_max;
			y2 = (int)((-w[2]-w[0]*x2)/w[1]+0.5);
		}else{
			y1 = svm.settings.axis_min;
			x1 = (int)((-w[2]-w[1]*y1)/w[0]+0.5);
			y2 = svm.settings.axis_max;
			x2 = (int)((-w[2]-w[1]*y2)/w[0]+0.5);			
		}
		repaint();
	}	
	

	
	public void mouseClicked(MouseEvent me) {initO();}

	public void mouseEntered(MouseEvent me) {}

	public void mouseExited(MouseEvent me) {}

	public void mouseMoved(MouseEvent me) {
		ccx = me.getX(); ccy = me.getY();
		coords = "(" + (ccx-Ox) + "," + (Oy-ccy) + ")";
		if(ccx<=2 || ccx >= ww-5 || ccy<=5 || ccy>=hh-5) coords = "";
		repaint();
	} 
		
	public void mousePressed(MouseEvent me) { 
		cx = me.getX(); cy = me.getY();  
		coords = "";		
	}
			
	public void mouseDragged(MouseEvent me) {
		int x = me.getX(), y = me.getY();
		/*if(svm.ind.V != null && svm.ind.V[0].getDimension() == 2){
			cx = x - cx; cy = y - cy;  
			Ox += cx; Oy += cy;
			cx = x; cy = y;
			coords = "";
			repaint();	
		*/
	}

	public void mouseReleased(MouseEvent me) {
		int x = me.getX(), y = me.getY(); 
	}	
	
}