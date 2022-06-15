package schisch_visualizer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class VisualizerThread extends Thread {
	SchischVisualizer sv;
	int mode;
	/*
	 * mode 1: step back
	 * mode 2: step forward
	 * mode 3: auto slow
	 * mode 4: auto medium
	 * mode 5: auto fast
	 * mode 6: auto really fast
	 * mode 7: auto gotta go fast
	 */
	
	VisualizerThread(SchischVisualizer sv, int mode) {
		this.sv = sv;
		this.mode = mode;
		if (mode == 8) {
			sv.isWorking = false;
		}
	}
	
	public void start() {
		
		if (!sv.isWorking) {
			sv.isWorking = true;
			if (mode == 1 || mode == 2) {
				this.draw(sv.canvas);
			} else if (mode >= 3 && mode <= 7) {
				int time = switch (mode) {
					case 3 -> 1000;
					case 4 -> 600;
					case 5 -> 100;
					case 6 -> 33;
					case 7 -> 12;
					default -> 0;
				};

				for ( ;sv.steps < sv.zustaende.size(); sv.steps++) {

					if (!sv.isWorking) {
						break;
					}
					sv.up = true;
					//Do this with Thread(?)
					this.draw(sv.canvas);
					if (!sv.isFirst) {
						sv.update();
					}
					try {
						TimeUnit.MILLISECONDS.sleep(time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				sv.steps--;
			} else if (mode == 9) { //Reset
				sv.steps = 0;
				this.draw(sv.canvas);
				
			} else if (mode == 10) { //Screenshot
				try {
					this.drawImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (mode == 11) { //Gif
				try {
					this.drawAllImages();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			sv.isWorking = false;
			if (!sv.isFirst) {
				sv.update();
			}
		} else {
			System.out.println("program is already working.");
		}
		
	}
	
	
	
	
	private void draw(Canvas canvas) {
		Graphics g = sv.canvas.getGraphics();
		
		int he = sv.canvas.getHeight();
		int wi = sv.canvas.getWidth();

		int ar;
		
		//Drawing and Stuff
		int cols = sv.zustaende.get(sv.steps)[0].length;
		int rows = sv.zustaende.get(sv.steps).length;


		ar = Math.min((he - 20) / rows, (wi - 20) / cols);
		
		
		int w = ar;
		int h = ar;
		//L�nge: Anzahl < 40 
		//H�he: Anzahl < 32
		
		if (!sv.isFirst) {
			
			if (sv.steps - 1 >= 0) {
				if (sv.up && (cols != sv.zustaende.get(sv.steps-1)[0].length || rows != sv.zustaende.get(sv.steps-1).length)) {
					sv.isFirst = true;
				}
			}
			if (sv.steps + 1 < sv.zustaende.size()) {
				if (!sv.up && (cols != sv.zustaende.get(sv.steps+1)[0].length || rows != sv.zustaende.get(sv.steps+1).length)) {
					sv.isFirst = true;
				}
			}
			
		}
		
		
		
		if (sv.isFirst) {
			g.setColor(Color.white);
			g.clearRect(0, 0, canvas.getWidth(),canvas.getHeight());
			
			g.setColor(Color.black);
			g.drawRect(0, 0, canvas.getWidth()-1, canvas.getHeight()-1);
			
			drawGrid(g,cols,rows,w,h);

			//insertChars(g,w,h);
			//sv.drawInfos(g);
			sv.isFirst = false;
		} else {
			//FindChange and only change that stuff, duh.
			//ArrayList<Punkt> punkte = this.findChange(old, not_old);
			
			ArrayList<Punkt> punkte;
			if (sv.up) {
				punkte = findChange(sv.steps-1, sv.steps);
			} else {
				punkte = findChange(sv.steps+1, sv.steps);
			}
			
			eraseCells(g, w, h, punkte);
			drawCells(g, w, h, punkte);
			//insertCharsCells(g,w,h, punkte);
			//sv.eraseInfos(g);
			//sv.drawInfos(g);

		}
		g.dispose();
		
	}
	
	/*
	 * Neues Update-Grafik-Modul. Fuellt nur Zellen aus, die sich veraendert haben.
	 * @param g Grafik
	 * fuer grosse oder kleine Zellen.
	 * @param punkte Veraenderte Punkte.
	 */
	/*
	private void insertCharsCells(Graphics g, int w, int h, ArrayList<Punkt> punkte) {
		for (Punkt p : punkte) {
			String s;
			if (sv.zustaende.get(sv.steps)[p.x][p.y] >= '0' && sv.zustaende.get(sv.steps)[p.x][p.y] <= '9') {
				s = " ";
			} else {
				s = "" + sv.zustaende.get(sv.steps)[p.x][p.y];
			}
			
			g.drawString(s, 19+p.y*w, 25+p.x*h);
			
		}
	}
	*/
	/**
	 * Neues Update-Grafik-Modul. Zeichnet Zellen neu, die sich veraendert haben.
	 * @param g Grafik
	 * @param w Breite der Zellen
	 * @param h Hoehe der Zellen
	 * @param punkte Veraenderte Punkte
	 */
	private void drawCells(Graphics g, int w, int h, ArrayList<Punkt> punkte) {
		for (Punkt p: punkte) {
			int j = p.x;
			int i = p.y;
			Color c;
			switch(sv.zustaende.get(sv.steps)[j][i]) {
				case '0':
					c = new Color(255, 255, 255);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '1':
					c = new Color(0, 0, 0);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '2':
					c = new Color(46, 39, 157);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '3':
					c = new Color(0, 68, 69);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '4':
					c = new Color(200, 19, 54);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '5':
					c = new Color(241, 250, 60);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '6':
					c = new Color(63, 197, 240);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '7':
					c = new Color(243, 12, 212);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '8':
					c = new Color(212, 215, 221);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case '9':
					c = new Color(25, 182, 0);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'A':
				case 'a':
					c = new Color(170, 113, 57);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'B':
				case 'b':
					c = new Color(64, 142, 47);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'C':
				case 'c':
					c = new Color(57, 50, 118);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'D':
				case 'd':
					c = new Color(108, 37, 111);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'E':
				case 'e':
					c = new Color(2, 104, 138);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'F':
				case 'f':
					c = new Color(213, 0, 20);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					break;
				case 'P':
				case 'p':
					c = new Color(255, 255, 255);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					if (w > 8) {
						g.fillOval(14+i*w, 14+j*h, w-8, h-8);
					} else {
						g.fillOval(11+i*w, 11+j*h, w-2, h-2);
					}
					break;
				case 'O':
				case 'o':
					c = new Color(255, 255, 255);
					g.setColor(c);
					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					if (w > 8) {
						g.drawOval(14+i*w, 14+j*h, w-8, h-8);
					} else {
						g.drawOval(11+i*w, 11+j*h, w-2, h-2);
					}
					break;

				case 'X':
				case 'x':
					c = new Color(255, 255, 255);
					g.setColor(c);

					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					((Graphics2D) g).setStroke(new BasicStroke(2));
					//Get Ecke:
					int x = 12+i*w;
					int y = 12+j*h;
					int x2 = 8+(i+1)*w;
					int y2 = 8+(j+1)*w;

					g.drawLine(x,y,x2,y2);
					g.drawLine(x,y2,x2,y);
					((Graphics2D) g).setStroke(new BasicStroke(1));

					break;
				case '>':
					c = new Color(255, 255, 255);
					g.setColor(c);

					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					((Graphics2D) g).setStroke(new BasicStroke(2));
					//Get Ecke:
					x = 14+i*w;
					y = 14+j*h;
					x2 = 6+(i+1)*w;
					y2 = 6+(j+1)*h;
					int y3 = (y+y2)/2;
					g.drawLine(x,y,x2,y3);
					g.drawLine(x,y2,x2,y3);
					((Graphics2D) g).setStroke(new BasicStroke(1));

					break;
				case '<':
					c = new Color(255, 255, 255);
					g.setColor(c);

					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					((Graphics2D) g).setStroke(new BasicStroke(2));
					//Get Ecke:
					x = 14+i*w;
					y = 14+j*h;
					x2 = 6+(i+1)*w;
					y2 = 6+(j+1)*h;
					y3 = (y+y2)/2;
					g.drawLine(x2,y,x,y3);
					g.drawLine(x2,y2,x,y3);
					((Graphics2D) g).setStroke(new BasicStroke(1));

					break;
				case '^':
					c = new Color(255, 255, 255);
					g.setColor(c);

					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					((Graphics2D) g).setStroke(new BasicStroke(2));
					//Get Ecke:
					x = 14+i*w;
					y = 14+j*h;
					x2 = 6+(i+1)*w;
					int x3 = (x+x2)/2;
					y2 = 6+(j+1)*h;
					g.drawLine(x,y2,x3,y);
					g.drawLine(x2,y2,x3,y);
					((Graphics2D) g).setStroke(new BasicStroke(1));

					break;
				case 'V':
				case 'v':
					c = new Color(255, 255, 255);
					g.setColor(c);

					g.fillRect(10+i*w, 10+j*h, w, h);
					g.setColor(new Color(0, 0, 0));

					((Graphics2D) g).setStroke(new BasicStroke(2));
					//Get Ecke:
					x = 14+i*w;
					y = 14+j*h;
					x2 = 6+(i+1)*w;
					x3 = (x+x2)/2;
					y2 = 6+(j+1)*h;
					g.drawLine(x,y,x3,y2);
					g.drawLine(x2,y,x3,y2);
					((Graphics2D) g).setStroke(new BasicStroke(1));

					break;

			}
			g.setColor(Color.black);
			g.drawRect(10+i*w, 10+j*h, w, h);
		}
	}
	
	/**
	 * Neues Update-Grafik-Modul. Loescht nur die Zellen, die sich veraendert haben.
	 * @param g Grafik
	 * @param w Breite der Zellen
	 * @param h Hoehe der Zellen
	 * @param punkte Veraenderte Punkte
	 */
	private void eraseCells(Graphics g, int w, int h, ArrayList<Punkt> punkte) {
		for (Punkt p : punkte) {
			g.clearRect(10+p.y*w, 10+p.x*h, w, h);
		}
	}
	
	/**
	 * Altes Grafik-Modul. Zeichnet das komplette Grid. Wird bei Initialisierung verwendet.
	 * @param g Grafik
	 * @param cols Zahl der Spalten.
	 * @param rows Anzahl der Reihen.
	 * @param w Breite der Zellen.
	 * @param h Hoehe der Zellen.
	 */
	private void drawGrid (Graphics g, int cols, int rows, int w, int h) {
		//System.out.println("H: " + h + " W: " + w);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				Color c;
				switch(sv.zustaende.get(sv.steps)[j][i]) {
					case '0':
						c = new Color(255, 255, 255);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '1':
						c = new Color(0, 0, 0);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '2':
						c = new Color(46, 39, 157);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '3':
						c = new Color(0, 68, 69);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '4':
						c = new Color(200, 19, 54);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '5':
						c = new Color(241, 250, 60);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '6':
						c = new Color(63, 197, 240);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '7':
						c = new Color(243, 12, 212);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '8':
						c = new Color(212, 215, 221);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case '9':
						c = new Color(25, 182, 0);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'A':
					case 'a':
						c = new Color(170, 113, 57);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'B':
					case 'b':
						c = new Color(64, 142, 47);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'C':
					case 'c':
						c = new Color(57, 50, 118);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'D':
					case 'd':
						c = new Color(108, 37, 111);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'E':
					case 'e':
						c = new Color(2, 104, 138);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'F':
					case 'f':
						c = new Color(213, 0, 20);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						break;
					case 'P':
					case 'p':
						c = new Color(255, 255, 255);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						if (w > 8) {
							g.fillOval(14+i*w, 14+j*h, w-8, h-8);
						} else {
							g.fillOval(11+i*w, 11+j*h, w-2, h-2);
						}
						break;
					case 'O':
					case 'o':
						c = new Color(255, 255, 255);
						g.setColor(c);
						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						if (w > 8) {
							g.drawOval(14+i*w, 14+j*h, w-8, h-8);
						} else {
							g.drawOval(11+i*w, 11+j*h, w-2, h-2);
						}
						break;

					case 'X':
					case 'x':
						c = new Color(255, 255, 255);
						g.setColor(c);

						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						((Graphics2D) g).setStroke(new BasicStroke(2));
						//Get Ecke:
						int x = 12+i*w;
						int y = 12+j*h;
						int x2 = 8+(i+1)*w;
						int y2 = 8+(j+1)*w;

						g.drawLine(x,y,x2,y2);
						g.drawLine(x,y2,x2,y);
						((Graphics2D) g).setStroke(new BasicStroke(1));

						break;
					case '>':
						c = new Color(255, 255, 255);
						g.setColor(c);

						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						((Graphics2D) g).setStroke(new BasicStroke(2));
						//Get Ecke:
						x = 14+i*w;
						y = 14+j*h;
						x2 = 6+(i+1)*w;
						y2 = 6+(j+1)*h;
						int y3 = (y+y2)/2;
						g.drawLine(x,y,x2,y3);
						g.drawLine(x,y2,x2,y3);
						((Graphics2D) g).setStroke(new BasicStroke(1));

						break;
					case '<':
						c = new Color(255, 255, 255);
						g.setColor(c);

						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						((Graphics2D) g).setStroke(new BasicStroke(2));
						//Get Ecke:
						x = 14+i*w;
						y = 14+j*h;
						x2 = 6+(i+1)*w;
						y2 = 6+(j+1)*h;
						y3 = (y+y2)/2;
						g.drawLine(x2,y,x,y3);
						g.drawLine(x2,y2,x,y3);
						((Graphics2D) g).setStroke(new BasicStroke(1));

						break;
					case '^':
						c = new Color(255, 255, 255);
						g.setColor(c);

						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						((Graphics2D) g).setStroke(new BasicStroke(2));
						//Get Ecke:
						x = 14+i*w;
						y = 14+j*h;
						x2 = 6+(i+1)*w;
						int x3 = (x+x2)/2;
						y2 = 6+(j+1)*h;
						g.drawLine(x,y2,x3,y);
						g.drawLine(x2,y2,x3,y);
						((Graphics2D) g).setStroke(new BasicStroke(1));

						break;
					case 'V':
					case 'v':
						c = new Color(255, 255, 255);
						g.setColor(c);

						g.fillRect(10+i*w, 10+j*h, w, h);
						g.setColor(new Color(0, 0, 0));

						((Graphics2D) g).setStroke(new BasicStroke(2));
						//Get Ecke:
						x = 14+i*w;
						y = 14+j*h;
						x2 = 6+(i+1)*w;
						x3 = (x+x2)/2;
						y2 = 6+(j+1)*h;
						g.drawLine(x,y,x3,y2);
						g.drawLine(x2,y,x3,y2);
						((Graphics2D) g).setStroke(new BasicStroke(1));

						break;
				}



			}
		}
		//DRAW Just Lines.

		g.setColor(new Color(0,0,0));
		for (int i = 0; i <= rows; i++) {
			g.drawLine(10,10+i*h,10+cols*w,10+i*h);
		}
		for (int j = 0; j <= cols; j++) {
			g.drawLine(10+j*w,10,10+j*w,10+rows*h);
		}
	}
	
	/**
	 * Neues Update-Grafik-Modul. Findet alle Zellen, die sich zum Vorzustand veraendert haben.
	 * @param old Voriger Zustand.
	 * @param not_old Neuer Zustand.
	 * @return Liste der Koordinaten aller Zellen, die sich veraendert haben.
	 */
	private ArrayList<Punkt> findChange(int old, int not_old) {
		ArrayList<Punkt> punkte = new ArrayList<Punkt>();
		if (old == -1) {
			//ALL THE POINTS of not_old
			for (int i = 0; i < sv.zustaende.get(not_old).length; i++) {
				for (int j = 0; j < sv.zustaende.get(not_old)[i].length; j++) {
					punkte.add(new Punkt(i,j));
				}
			}
			return punkte;
			
		}
		
		for (int i = 0; i < sv.zustaende.get(old).length; i++) {
			for (int j = 0; j < sv.zustaende.get(old)[i].length; j++) {
				if (sv.zustaende.get(old)[i][j] != sv.zustaende.get(not_old)[i][j]) {	
					punkte.add(new Punkt(i,j));
				}
			}
		}
		return punkte;
	}
	
	/*
	 * Altes Grafik-Modul. Fuellt das komplette Grid mit Zeichen. Wird bei Initialisierung verwendet.
	 * @param g Grafik
	 *  isBig fuer grosse oder kleine Zellen.
	 */
	/*
	private void insertChars(Graphics g, int w, int h) {
		//Einf�gen der Array-Werte in das Gitter
		for (int i = 0; i < sv.zustaende.get(sv.steps).length; i++) {
			for (int j = 0; j < sv.zustaende.get(sv.steps)[i].length; j++) {
				String s;
				if (sv.zustaende.get(sv.steps)[i][j] >= '0' && sv.zustaende.get(sv.steps)[i][j] <= '9') {
					s = " ";
				} else {
					s = "" + sv.zustaende.get(sv.steps)[i][j];
				}
				
				g.drawString(s, 19+j*w, 25+i*h);
				
				
			}
		}
	}
	*/

	private void drawAllImages() throws IOException {
		sv.isWorking = true;
		sv.update();


		//ArrayList<BufferedImage> files = new ArrayList<BufferedImage>();

		int oldSteps = sv.steps;


		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateOnly = new SimpleDateFormat("MM-dd-yyyy");
		SimpleDateFormat timeOnly = new SimpleDateFormat("HH-mm-ss");
		Date d = cal.getTime();
		String ts = dateOnly.format(d) + "-" + timeOnly.format(d);

		ImageOutputStream output = new FileImageOutputStream(new File("gif"+ts+".gif"));

		BufferedImage firstImage = this.drawImageOnRAM(20);



		GifSequenceWriter writer = new GifSequenceWriter(output, firstImage.getType(), 1, false);

		writer.writeToSequence(firstImage);
		BufferedImage nextImage = null;
		boolean firstOne = true;
		for (sv.steps = 1; sv.steps < sv.zustaende.size(); sv.steps++) {
			sv.up = true;

			if (firstOne) {
				nextImage = this.drawImageOnRAM(firstImage, 20); //BETTER... With "Draw Only Changes"
				firstOne = false;
			} else {
				this.drawImageOnRAM(nextImage, 20);//BETTER... With "Draw Only Changes"
			}
			writer.writeToSequence(nextImage);
			sv.updateGif((int)((double)sv.steps/(double)(sv.zustaende.size())*100));

		}
		sv.steps = oldSteps;


		writer.close();
		output.close();


		System.out.println("Gif is complete");

		//deleteDirectory(f);

		System.out.println("Deletion of TMP is complete");


		sv.isWorking = false;
		sv.update();



	}

	private void drawImage() throws IOException {
		
		//TODO
		//By not creating a new Graphics object each time,
		//and finding the changed parts to redraw
		//This part can be sped up quite a bit.
		int width = sv.canvas.getWidth();
		int height = sv.canvas.getHeight();
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		
		
		int he = sv.canvas.getHeight();
		int wi = sv.canvas.getWidth();
		
		int ar;
		
		//Drawing and Stuff
		
		int cols = sv.zustaende.get(sv.steps)[0].length;
		int rows = sv.zustaende.get(sv.steps).length;
		
		int w;
		int h;

		ar = Math.min((he - 20) / rows, (wi - 20) / cols);
		
		
		w = ar;
		h = ar;
		
		if (!sv.isFirst) {
			if (sv.steps - 1 >= 0) {
				if (sv.up && (cols != sv.zustaende.get(sv.steps-1)[0].length || rows != sv.zustaende.get(sv.steps-1).length)) {
					sv.isFirst = true;
				}
			}
			if (sv.steps + 1 < sv.zustaende.size()) {
				if (!sv.up && (cols != sv.zustaende.get(sv.steps+1)[0].length || rows != sv.zustaende.get(sv.steps+1).length)) {
					sv.isFirst = true;
				}
			}
		}
		
		
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.black);
		g.drawRect(0, 0, width-1, height-1);
		
		drawGrid(g,cols,rows,w,h);
		//sv.insertChars(g,w,h);
		
		g.dispose();


		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateOnly = new SimpleDateFormat("MM-dd-yyyy");
		SimpleDateFormat timeOnly = new SimpleDateFormat("HH-mm-ss");
		Date d = cal.getTime();
		String ts = dateOnly.format(d) + "-" + timeOnly.format(d);
		
		File f = new File("sv"+ts+".jpg");
		ImageIO.write(bi,"jpg",f);
	}

	private BufferedImage drawImageOnRAM(int size) {
		if (size < 1) {
			size = 20;
		}



		//Drawing and Stuff

		int cols = sv.zustaende.get(sv.steps)[0].length;
		int rows = sv.zustaende.get(sv.steps).length;

		int w ;
		int h;

		//Feste Auflösung (Es gibt weiße Flächen) ODER wie hier unten: 20 Pixel Seitenlänge je Box (Das Bild wird angepasst)?
		int width = cols*size + 20;
		int height = rows*size + 20;
		w = size;
		h = size;

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();

		/*
		g.clearRect(0, 0, width, height);
		g.setColor(Color.black);
		g.drawRect(0, 0, width-80, height);


		*/

		if (!sv.isFirst) {

			if (sv.steps - 1 >= 0) {
				if (sv.up && (cols != sv.zustaende.get(sv.steps-1)[0].length || rows != sv.zustaende.get(sv.steps-1).length)) {

					sv.isFirst = true;
				}
			}
			if (sv.steps + 1 < sv.zustaende.size()) {
				if (!sv.up && (cols != sv.zustaende.get(sv.steps+1)[0].length || rows != sv.zustaende.get(sv.steps+1).length)) {

					sv.isFirst = true;
				}
			}

		}

		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.black);
		g.drawRect(0, 0, width-1, height-1);

		drawGrid(g,cols,rows,w,h);
		//sv.insertChars(g,isBig);


		//ONLY CHANGES? -> Return Array?
		/*
		g.setColor(Color.white);
		g.fillOval(0,0,width,height);
		*/


		g.dispose();


		return bi;


	}

	private BufferedImage drawImageOnRAM(BufferedImage old, int size) {
		if (size < 1) {
			size = 20;
		}

		Graphics2D g = old.createGraphics();


		int w;
		int h;

		//Feste Auflösung (Es gibt weiße Flächen) ODER wie hier unten: 20 Pixel Seitenlänge je Box (Das Bild wird angepasst)?
		w = size;
		h = size;


		ArrayList<Punkt> punkte;
		if (sv.up) {
			punkte = findChange(sv.steps-1, sv.steps);
		} else {
			punkte = findChange(sv.steps+1, sv.steps);
		}

		eraseCells(g, w, h, punkte);
		drawCells(g, w, h, punkte);


		g.dispose();


		return old;


	}
}
