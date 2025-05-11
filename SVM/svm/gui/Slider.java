package svm.gui;

import java.awt.*;
import svm.SVM;

public class Slider extends Panel implements Runnable {
    About about;
    Thread t;
    Image im;
    Graphics img;
    int w1, h1;
    int delay = 50;
    String[] students;
    Font fnt = new Font("Arial", 0, 12);
    int y, w = 10;
    boolean init = true, control;
    int ww, hh;

    public Slider(About about, int ww, int hh) {
    	this.about = about;
		this.ww = ww;
		this.hh = hh;    		
    	setStudents();
		setFont(fnt); 
		start();
    }

	public void setStudents(){
		String[] student = new String[57];
		student[0] = "ALBU  LAUREN\u021AIU";
		student[1] = "ALEXANDRU  CLAUDIU";
		student[2] = "AMANOLESEI  ANDREI";
		student[3] = "ANTON  BIANCA";
		student[4] = "BI\u015EOC  ELENA";
		student[5] = "BULGAC  ROMAN";
		student[6] = "CAPLEA  MARIAN";
		student[7] = "CAZAMIR  GABRIEL";
		student[8] = "CHELE  MAXIM";
		student[9] = "CHIORESCU  IOANA";
		student[10] = "CORFU  PAULA";
		student[11] = "CO\u015EULEANU  MARIA";
		student[12] = "CRE\u021AU  ANDREI";
		student[13] = "CROITORIU  DAN";
		student[14] = "DIMA  SABINA";
		student[15] = "DRACEA  IULIA";
		student[16] = "DUMITRA\u015E  GABRIEL";
		student[17] = "FARCA\u015EANU  IUSTINA";
		student[18] = "FLOREA  ROBERT";
		student[19] = "FLUTUR  MIHAELA";
		student[20] = "GHERASIM  ANA";
		student[21] = "GRAMAD\u0102  FLORIN";
		student[22] = "HOLCA  ROBERT";
		student[23] = "IASCHIV  NICOLETA";
		student[24] = "ICHIM  MARIA";
		student[25] = "ILIE  EDUARD";
		student[26] = "ION  ANDREI";
		student[27] = "LEFTER  VLAD";
		student[28] = "LUPU  LIVIU";
		student[29] = "MAGADAN  ALEXANDRU";
		student[30] = "MATAL\u0102  ALEXANDRU";
		student[31] = "MIH\u0102IL\u0102  R\u0102ZVAN";
		student[32] = "MORARU  ROBERT";
		student[33] = "MOSOR  DENIS";
		student[34] = "MUNTEANU  SEBASTIAN";
		student[35] = "MUTU  ANDREEA";
		student[36] = "NICHITA  BOGDAN";
		student[37] = "NIC\u015EAN  ALEXANDRA";
		student[38] = "OCHIAN  MIHAI";
		student[39] = "PANAINTESCU FLORENTINA";
		student[40] = "PARMAC  MIRCEA";
		student[41] = "PASCAL  OTILIA";
		student[42] = "PA\u015EAPARUG\u0102  RADU";
		student[43] = "PINTESCU  SEBASTIAN";
		student[44] = "PLETEA  MARIAN";
		student[45] = "POPESCU  PAUL";
		student[46] = "ROTARU  ANDREEA";
		student[47] = "RUSU-COZMA  ANDREI";
		student[48] = "SANDU  IOAN";
		student[49] = "SANDU  IOANA";
		student[50] = "SC\u00CERLATACHE ANDREEA";
		student[51] = "\u015EELET  COSMIN";
		student[52] = "\u015ETEFU  ALEXANDRU";
		student[53] = "TIULIULIUC  GEORGIANA";
		student[54] = "TUMURUG  PETRU";
		student[55] = "\u021AUCHEL  ŞTEFAN";
		student[56] = "UNGUREANU  IOANA";

		students = new String[student.length*100];
		for(int i = 0; i < students.length; i++)
			students[i] = student[i % student.length];
	}
	
    public void start() {
    	if(t == null){
    		t = new Thread(this); 
    		t.start();
	        try{Thread.sleep(1000);}
			catch(InterruptedException e) { }    			
    	}
    }
    	
    public void stop() {if(t != null){ t = null;}}	
	
    public void run() {
	    do {
	        repaint();
			try {Thread.sleep(delay);}
			catch(InterruptedException e) {return;}
	    } while(true);
    }  	
	
	public void reset(){
		y = hh + 10;	
		repaint();
		stop();
	}

    public final void paint(Graphics g) {
    	if(init){
			im = createImage(ww, hh);
			img = im.getGraphics();	
			for(int i = 0; i < students.length; i++) {
				FontMetrics fm = img.getFontMetrics(fnt);
				h1 += fm.getHeight();
				if(fm.stringWidth(students[i]) > w1) w1 = fm.stringWidth(students[i]);
			}
			y = hh + 10; 	
			init = false;
		}
		Color color = null;
		for(int l = 0; l < students.length; l++) {
			float f = (float)hh / 4.0F;
			float f1 = 1.0F, f2 = 1.0F, f3 = 1.0F;
			int i1 = y + (int)(1.5 * getFont().getSize() * l);
			if(i1 >= 0 && i1 <= hh) {
				float ff = 0;
				if((float)i1 <= f)
					ff = (float)i1 / f;
				else if((float)i1 >= (float)hh - f)
					ff = ((float)hh - (float)i1) / f;
				else
					ff = 1.0F;
				color = new Color((int)((float)255 * ff), (int)((float)255 * ff), (int)((float)255 * ff));
			}else color = new Color(0, 0, 0);
			img.setColor(color);
			img.setFont(fnt);
			img.drawString(students[l], w, i1);
	    }
	    g.drawImage(im, 0, 0, this);
    }

    public final void update(Graphics g) {   
    	if(img!=null){	
			img.setColor(Color.black);
			img.fillRect(0,0,ww,hh);		
		}
		if(y < -(int)((float)h1*0.75f))
			y = hh + 10;
		else
			y --;
		paint(g);
	}		

}
