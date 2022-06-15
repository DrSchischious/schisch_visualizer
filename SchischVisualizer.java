package schisch_visualizer;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class SchischVisualizer extends JFrame {

	JPanel contentPane;
	ArrayList<char[][]> zustaende;
	int steps = -1;
	boolean charMode = false;
	boolean isFirst = true;
	boolean up = true;
	Canvas canvas = null;
	boolean isWorking = false;
	
	Label statusSteps = null;
	Label statusRows = null;
	Label statusCols = null;
	Label statusWork = null;

	Button saveGif;
	Button savePic;

	//TODO: Zur�ck soll im ersten Fall auch zur�ckf�hren
	
	/**
	 * Fuegt einen Zustand des zweidimensionalen Arrays der Liste aller Zustaende hinzu.
	 * @param charr hinzuzufuegender Zustand (char-Modus).
	 */
	public void step(char[][] charr) {
		this.charMode = true;
		/*
		char[][] charr2 = new char[charr.length][charr[0].length];
		for (int i = 0; i < charr.length; i++) {
			for (int j = 0; j < charr[i].length; j++) {
				charr2[i][j] = charr[i][j];
			}
		}
		*/
		char[][] charr2 = new char[charr[0].length][charr.length];
		for (int i = 0; i < charr2.length; i++) {
			for (int j = 0; j < charr2[i].length; j++) {
				charr2[i][j] = charr[j][i];
			}
		}
		
		this.zustaende.add(charr2);


		//Save Difference...
		
	}
	
	/**
	 * Startet die Darstellung von 2D-Array-Zustaenden mittels SV.
	 */
	public void start() { //�bergebe diesem viele Zust�nde von char[][]'s
		
		ArrayList<char[][]> harr;
		
		if (this.charMode == true) {
			harr = this.zustaende;
		
			EventQueue.invokeLater(new Runnable() {
				
				public void run() {
					try {
						SchischVisualizer frame = new SchischVisualizer(harr);
						frame.steps = -1;
						frame.setVisible(true);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} else {
			System.out.println("Fehler! Keine Steps gefunden!");
			System.exit(0);
			
		}
		
		
	}

	/**
	 * Einfacher Konstruktor. Kreiiert eine Schein-Version des SV.
	 */
	public SchischVisualizer() {
		this.zustaende = new ArrayList<char[][]>();
		
	}
	
	
	
	
	
	
	
	
	
	private void draw() {
		if (isFirst != true) {
			if (steps == -1) {
				steps = 0;
			}
			SchischVisualizer s = this;
			new Thread() {
				public void run() {
					if (isWorking == true) {
						
						VisualizerThread vt = new VisualizerThread(s,1);
						vt.start();
					}
					
				}
			}.start();
		}
		
	}
	
	
	
	/**
	 * Realer Konstruktor, der nach start mit den geeigneten Informationen gefuellt wird.
	 * @param charr Alle Zustaende
	 */
	public SchischVisualizer(ArrayList<char[][]> charr) {
		this.zustaende = charr;
		setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.8),(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.8));
		this.setResizable(true);
		setTitle("Schisch Visualizer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(25, 25, (int)(this.getSize().getWidth()-25), (int)(this.getSize().getHeight()-25));
		
		contentPane = new JPanel();
		
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		SchischVisualizer sv = this;
		contentPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (sv.isFirst == false) {
					sv.isFirst = true;
					VisualizerThread vt = new VisualizerThread(sv, 1);
					vt.start();
				}
				
				
			}
		});
		setContentPane(contentPane);
		contentPane.setLayout(new GridBagLayout());

		
		
		
		//ALL THE CONSTRAINTS!!
		GridBagConstraints c01 = new GridBagConstraints();
		c01.gridx = 0;
		c01.gridy = 0;
		c01.gridwidth = 4;
		c01.gridheight = 10;
		c01.weightx = 1;
		c01.weighty = 1;
		c01.insets = new Insets(10,10,10,10);
		c01.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c011 = new GridBagConstraints();
		c011.gridx = 0;
		c011.gridy = 10;
		c011.gridwidth = 1;
		c011.gridheight = 1;
		c011.weightx = 1;
		c011.weighty = 1/11;
		c011.insets = new Insets(10,10,10,10);
		c011.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c012 = new GridBagConstraints();
		c012.gridx = 1;
		c012.gridy = 10;
		c012.gridwidth = 1;
		c012.gridheight = 1;
		c012.weightx = 1;
		c012.weighty = 1/11;
		c012.insets = new Insets(10,10,10,10);
		c012.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c013 = new GridBagConstraints();
		c013.gridx = 2;
		c013.gridy = 10;
		c013.gridwidth = 1;
		c013.gridheight = 1;
		c013.weightx = 1;
		c013.weighty = 1/11;
		c013.insets = new Insets(10,10,10,10);
		c013.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c014 = new GridBagConstraints();
		c014.gridx = 3;
		c014.gridy = 10;
		c014.gridwidth = 1;
		c014.gridheight = 1;
		c014.weightx = 1;
		c014.weighty = 1/11;
		c014.insets = new Insets(10,10,10,10);
		c014.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c02 = new GridBagConstraints();
		c02.gridx = 4;
		c02.gridy = 0;
		c02.gridwidth = 1;
		c02.gridheight = 1;
		c02.weighty = 1;
		c02.insets = new Insets(10,0,10,0);
		c02.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c03 = new GridBagConstraints();
		c03.gridx = 4;
		c03.gridy = 1;
		c03.gridwidth = 1;
		c03.gridheight = 1;
		c03.weighty = 1;
		c03.insets = new Insets(10,0,10,0);
		c03.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c04 = new GridBagConstraints();
		c04.gridx = 4;
		c04.gridy = 2;
		c04.gridwidth = 1;
		c04.gridheight = 1;
		c04.weighty = 1;
		c04.insets = new Insets(10,0,10,0);
		c04.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c05 = new GridBagConstraints();
		c05.gridx = 4;
		c05.gridy = 3;
		c05.gridwidth = 1;
		c05.gridheight = 1;
		c05.weighty = 1;
		c05.insets = new Insets(10,0,10,0);
		c05.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c06 = new GridBagConstraints();
		c06.gridx = 4;
		c06.gridy = 4;
		c06.gridwidth = 1;
		c06.gridheight = 1;
		c06.weighty = 1;
		c06.insets = new Insets(10,0,10,0);
		c06.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c07 = new GridBagConstraints();
		c07.gridx = 4;
		c07.gridy = 5;
		c07.gridwidth = 1;
		c07.gridheight = 1;
		c07.weighty = 1;
		c07.insets = new Insets(10,0,10,0);
		c07.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c08 = new GridBagConstraints();
		c08.gridx = 4;
		c08.gridy = 6;
		c08.gridwidth = 1;
		c08.gridheight = 1;
		c08.weighty = 1;
		c08.insets = new Insets(10,0,10,0);
		c08.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c09 = new GridBagConstraints();
		c09.gridx = 4;
		c09.gridy = 7;
		c09.gridwidth = 1;
		c09.gridheight = 1;
		c09.weighty = 1;
		c09.insets = new Insets(10,0,10,0);
		c09.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c10 = new GridBagConstraints();
		c10.gridx = 4;
		c10.gridy = 8;
		c10.gridwidth = 1;
		c10.gridheight = 1;
		c10.weighty = 1;
		c10.insets = new Insets(10,0,10,0);
		c10.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c11 = new GridBagConstraints();
		c11.gridx = 4;
		c11.gridy = 9;
		c11.gridwidth = 1;
		c11.gridheight = 1;
		c11.weighty = 1;
		c11.insets = new Insets(10,0,10,0);
		c11.fill= GridBagConstraints.BOTH;
		
		GridBagConstraints c12 = new GridBagConstraints();
		c12.gridx = 4;
		c12.gridy = 10;
		c12.gridwidth = 1;
		c12.gridheight = 1;
		c12.weighty = 1;
		c12.insets = new Insets(10,0,10,0);
		c12.fill= GridBagConstraints.BOTH;
		
		
		
		
		
		Canvas canvas = new Canvas();
		canvas.setBackground(Color.WHITE);
		canvas.setIgnoreRepaint(true);
		
		
		//canvas.setBounds(25, 25, 875, 775);
		contentPane.add(canvas,c01);
		
		
		this.canvas = canvas;
		SchischVisualizer s = this;
	
		Button butt02 = new Button("Zurück");
		butt02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps < 0) {
								steps = 1;
							}
							if (steps-1 >= 0) {
								steps--;
								up = false;
								VisualizerThread vt = new VisualizerThread(s, 1);
								vt.start();
								//draw(canvas); //Can Happen without Thread
							}
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
				
				
			}
		});
		butt02.setBackground(SystemColor.activeCaption);
		butt02.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt02.setBounds(25, 825, 70, 22);
		//butt02.setLocation(25,825);
		//butt02.setSize(70,22);
		//butt02.setMaximumSize(new Dimension(160,200));
		
		
		
		contentPane.add(butt02,c02);
		
		Button butt03 = new Button("Schritt");
		butt03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps+1 < zustaende.size()) {
								steps++;
								up = true;
								VisualizerThread vt = new VisualizerThread(s, 2);
								vt.start();
								//draw(canvas); //Can Happen without Thread
								
							}
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
				
				
			}
		});
		butt03.setBackground(SystemColor.activeCaption);
		butt03.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt03.setBounds(125, 825, 70, 22);
		contentPane.add(butt03,c03);
		
		Button butt04 = new Button("Auto (Slow)");
		butt04.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps == -1) {
								steps = 0;
							}
							
							VisualizerThread vt = new VisualizerThread(s, 3);
							vt.start();
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
				
				
				
			}
		});
		
		butt04.setBackground(SystemColor.activeCaption);
		butt04.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt04.setBounds(225, 825, 100, 22);
		contentPane.add(butt04,c04);
		

		Button butt05 = new Button("Auto (Medium)");
		butt05.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps == -1) {
								steps = 0;
							}
							
							VisualizerThread vt = new VisualizerThread(s, 4);
							vt.start();
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
				
			}
		});
		butt05.setBackground(SystemColor.activeCaption);
		butt05.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt05.setBounds(355, 825, 100, 22);
		contentPane.add(butt05,c05);
		

		Button butt06 = new Button("Auto (Fast)");
		butt06.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps == -1) {
								steps = 0;
							}
							
							VisualizerThread vt = new VisualizerThread(s, 5);
							vt.start();
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
			}
		});
		butt06.setBackground(SystemColor.activeCaption);
		butt06.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt06.setBounds(485, 825, 100, 22);
		contentPane.add(butt06,c06);
		
		Button butt07 = new Button("Gotta Go 30 FPS");
		butt07.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps == -1) {
								steps = 0;
							}
							
							VisualizerThread vt = new VisualizerThread(s, 6);
							vt.start();
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
			}
		});
		butt07.setBackground(SystemColor.activeCaption);
		butt07.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt07.setBounds(615, 825, 100, 22);
		contentPane.add(butt07,c07);
		
		Button butt08 = new Button("Gotta Go FAST");
		butt08.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							if (steps == -1) {
								steps = 0;
							}
							
							VisualizerThread vt = new VisualizerThread(s, 7);
							vt.start();
						} else {
							System.out.println("Is already in Work.");
						}
						
					}
				}.start();
			}
		});
		
		butt08.setBackground(SystemColor.activeCaption);
		butt08.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt08.setBounds(745, 825, 100, 22);
		contentPane.add(butt08,c08);
		
		Button butt09 = new Button("Stop!");
		butt09.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						
							
							VisualizerThread vt = new VisualizerThread(s, 8);
							
						
						
					}
				}.start();
			}
		});
		butt09.setBackground(SystemColor.activeCaption);
		butt09.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt09.setBounds(800, 825, 100, 22);
		contentPane.add(butt09,c09);
		
		Button butt10 = new Button("Reset!");
		butt10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						savePic.setEnabled(true);
						saveGif.setEnabled(true);
						if (isWorking == false) {
							
							VisualizerThread vt = new VisualizerThread(s, 9);
							vt.start();
						}
						
					}
				}.start();
			}
		});
		butt10.setBackground(SystemColor.activeCaption);
		butt10.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt10.setBounds(800, 825, 100, 22);
		contentPane.add(butt10,c10);
		
		Button butt11 = new Button("Save Image!");
		butt11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						if (isWorking == false) {
							
							VisualizerThread vt = new VisualizerThread(s, 10);
							vt.start();
						}
						
					}
				}.start();
			}
		});
		butt11.setEnabled(false);
		butt11.setBackground(SystemColor.activeCaption);
		butt11.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt11.setBounds(800, 825, 100, 22);
		savePic = butt11;
		contentPane.add(butt11,c11);
		
		Button butt12 = new Button("Save GIF!");
		butt12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						if (isWorking == false) {
							
							VisualizerThread vt = new VisualizerThread(s, 11);
							vt.start();
						}
						
					}
				}.start();
			}
		});
		butt12.setEnabled(false);
		butt12.setBackground(SystemColor.activeCaption);
		butt12.setFont(new Font("Segoe UI", Font.BOLD, 12));
		//butt12.setBounds(800, 825, 100, 22);
		saveGif = butt12;
		contentPane.add(butt12,c12);
		
		Label l13 = new Label("Schritt: X");
		l13.setFont(new Font("Segoe UI", Font.BOLD, 12));
		contentPane.add(l13,c011);
		
		Label l14 = new Label("Reihen: ");
		l14.setFont(new Font("Segoe UI", Font.BOLD, 12));
		contentPane.add(l14,c012);
		
		Label l15 = new Label("Spalten: ");
		l15.setFont(new Font("Segoe UI", Font.BOLD, 12));
		contentPane.add(l15,c013);
		
		Label l16 = new Label("Leerlauf");
		l16.setFont(new Font("Segoe UI", Font.BOLD, 12));
		contentPane.add(l16,c014);
		
		this.statusSteps = l13;
		this.statusRows = l14;
		this.statusCols = l15;
		this.statusWork = l16;
		
	}
	
	protected void update() {
		this.updateSteps();
		this.updateRows();
		this.updateCols();
		this.updateWork();
	}
	
	
	private void updateSteps() {
		this.statusSteps.setText("Schritt: "+(steps+1)+"/"+zustaende.size());
	}
	
	private void updateRows() {
		this.statusRows.setText("Reihen: "+zustaende.get(steps).length);
	}
	
	private void updateCols() {
		this.statusCols.setText("Spalten: "+zustaende.get(steps)[0].length);
	}
	
	private void updateWork() {
		if (this.isWorking == true) {
			this.statusWork.setText("In Arbeit");
		} else {
			this.statusWork.setText("Leerlauf");
		}
	}
	
	public void updateGif(int i) {
		if (i < 100) {
			this.statusWork.setText("Gif: " + i + " % fertig");
		} else {
			this.statusWork.setText("Leerlauf");
		}
		
	}
	
	
}
