import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class Game extends Thread {
	public static String version = "1.0.0";

	public static Game game;
	
	public static int sizex = 1150;
	public static int sizey = 850;
	public static int posx = 50;
	public static int posy = 50;
	public static int bsize = 8;
	
	BufferedImage background;
			
	Random rnd = new Random();
	int bx; //Position of the ball
	int by;
	int speedx; //Speed of the ball
	int speedy;
	int addSpeed = 1; //Speed added every touch
	
	//Position of the bar
	int barSizex = 100;
	int barSizey = 10;
	int barx = posx; 
	int bary = posy+sizey+barSizey-1;
	
	boolean go; //ball is rolling
	boolean exitEasterEgg = false;
	
	int score = 0;
	
	boolean test; //if true you can not lose
	
	JFrame frame = new JFrame();
	

	
	public static void main(String[] args) {
		game = new Game();
	}
	
	Game(){
		super();
		frame.setSize(sizex+(posx*2), sizey+(posy*2));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Small Game by Steffen Beschta v."+ version);
		List <Image> imgs = new ArrayList<Image>();
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_128x128.png")).getImage());
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_64x64.png")).getImage());
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_32x32.png")).getImage());
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_16x16.png")).getImage());
		frame.setIconImages(imgs);
		frame.addMouseMotionListener(new MyMouseMotionAdapter());
		frame.addKeyListener((new MyKeyListener()));
		
		background = (BufferedImage) frame.getContentPane().createImage(frame.getWidth(), frame.getHeight());
		
		start();
	}
	
	Game(JFrame f){
		super();
		frame = f;
		
		//save Components
		Container cPane = f.getContentPane();
		ArrayList<Component> comp = new ArrayList<Component>();
		for(int i = 0; i < cPane.getComponentCount(); i++) {
			comp.add(cPane.getComponent(i));
		}
		
		//save attributes
		Dimension size = f.getSize();
		Point location = f.getLocation();
		boolean resizable = f.isResizable();
		boolean visible = f.isVisible();
		String title = f.getTitle();
		boolean focusable = f.isFocusable();
		
		//get image for background
		Robot robby = null;
		try {
		robby = new Robot();
		} catch (AWTException e) {}
		background = robby.createScreenCapture(new Rectangle((int)location.getX(),(int)location.getY(),(int)size.getWidth(),(int)size.getHeight()));
		/*BufferedImage image = new BufferedImage((int)size.getWidth(),(int)size.getHeight(),BufferedImage.TYPE_INT_RGB);
		f.printAll(image.createGraphics());*/
		
		
		//clear Components
		cPane.removeAll();
		
		//edit attributes
		//f.setSize(sizex+(posx*2), sizey+(posy*2));
		sizex = size.width-(posx*2);
		sizey = size.height-(posy*2);
		bary = posy+sizey+barSizey-1;
		//f.setResizable(false); //--> if not resizable the size is bigger?!
		f.setVisible(true);
		f.setTitle(title + " with a small Game by Steffen Beschta v."+ version);
		f.setFocusable(true);
		f.requestFocusInWindow();
		
		//add Listeners
		MyMouseMotionAdapter mouseAdapter = new MyMouseMotionAdapter();
		f.addMouseMotionListener(mouseAdapter);
		MyKeyListener keyListener = new MyKeyListener();
		f.addKeyListener(keyListener);
		
		start(); //start the game in the JFrame f
		
		//reset Components
		cPane.removeAll();
		for(int i = 0; i < comp.size(); i++) {
			cPane.add(comp.get(i));
		}
		cPane.repaint();
		
		//reset attributes
		f.setResizable(resizable);
		f.setVisible(visible);
		f.setTitle(title);
		f.setFocusable(focusable);
		
		//remove Listeners
		f.removeMouseMotionListener(mouseAdapter);
		f.removeKeyListener(keyListener);
		
		f.repaint();
	}
	
	@Override
	public void run(){		
		try {
			sleep(40); // ohne Pause wird g.drawRect nicht richtig ausgeführt
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//starting values
		/*test = false;
		speedx = 7;
		speedy = 7;
		bx = posx;
		by = posy;
		go = true;
		g.drawRect(posx-1, posy-1, sizex+bsize+1, sizey+bsize+1);
		g.fillOval(bx, by, bsize, bsize);*/
		
		restart();
		
		Graphics g = frame.getGraphics();
			
		while (!exitEasterEgg) {
			while(go && !exitEasterEgg){
				move(g, speedx, speedy);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			g.setColor(Color.black);
		}
	}
	
	void move(Graphics g, int speedx, int speedy){
		clear(g);
		bx += speedx;
		by += speedy;
		
		g.fillRect(barx, bary, barSizex, barSizey);
		
		if(bx < posx){ 			//left side
			this.speedx *= -1;
			bx = posx;
		}
		
		if(bx > sizex+posx){ 	//right side
			this.speedx *= -1;
			bx = sizex+posx;
		}
		
		if(by < posy){			//upper side
			this.speedy *= -1;
			by = posy;
		}
		
		if(by > sizey+posy){	//bottom side -> check if bar is there
			by = sizey+posy;	
			if((bx>=barx && bx<=barx+barSizex)||test) { // bar is there
				this.speedy *= -1;						
				if(rnd.nextBoolean()) {				
					if(this.speedx>0) {
						this.speedx += addSpeed;
					}else {
						this.speedx -= addSpeed;
					}
				}else {
					if(this.speedy>0) {
						this.speedy += addSpeed;
					}else {
						this.speedy -= addSpeed;
					}
				}				
				System.out.println("speedx:"+ this.speedx +"speedy:" + this.speedy);
				score++;
			}else {
				g.setColor(Color.red);
				g.drawString("GAME OVER!", posx+(sizex/2), posy+(sizey/2));
				g.drawString("SCORE: " + score, posx+(sizex/2)+7, posy+(sizey/2)+20);
				g.drawString("press 'r' to restart", posx+(sizex/2)-10, posy+(sizey/2)+40);
				System.out.println("GAME OVER! \n SCORE: "+ score + "\n Your final speed was: \n x:"+ speedx+ "\n y:"+ speedy);
				go = false;
				System.out.println("Your score is: " + score);
			}
		}
		
		g.fillOval(bx, by, bsize, bsize);
	}
	
	void clear(Graphics g){
		//g.clearRect(posx, posy, sizex+bsize, sizey+bsize);
		//g.clearRect(0, bary, sizex+(posx*2), barSizey);
		Image subImg = background.getSubimage(0, bary, sizex+(posx*2), barSizey);
		g.drawImage(subImg, 0, bary, sizex+(posx*2), barSizey, null); //clear bar
		
		subImg = background.getSubimage(bx, by, bsize, bsize);
		g.drawImage(subImg, bx, by, bsize, bsize, null); //clear ball
		g.drawRect(posx-1, posy-1, sizex+bsize+1, sizey+bsize+1);
		
	}
	
	 void restart() {
		System.out.println("restart");
		go = false;
		frame.getGraphics().drawImage(background, 0, 0, frame.getSize().width, frame.getSize().height, null); //clear all
		
		//starting values
		test = false;
		speedx = 7;
		speedy = 7;
		bx = posx;
		by = posy;
		score = 0;
		go = true;
		
	}
	
	void test() {
		System.out.println("test");
		test = true;
	}
	
	void bug() {
		System.out.println("bug");
		test = false;
	}
	
	
		
	public class MyMouseMotionAdapter extends MouseMotionAdapter{
		@Override
		public void mouseMoved(MouseEvent arg0) {
			super.mouseDragged(arg0);
			int x = arg0.getPoint().x;
			/*if((x>posx && x<posx+sizex)) {
				barx = x;
			}*/
			barx = x;
			
		}
	}
	
	public class MyKeyListener implements KeyListener{
		
		int test = 0;
		int bug = 0;

		@Override
		public void keyPressed(KeyEvent ev) {}

		@Override
		public void keyReleased(KeyEvent ev) {
			if(ev.getKeyCode() == KeyEvent.VK_ESCAPE) { 
				exitEasterEgg = true;
			}
		}

		@Override
		public void keyTyped(KeyEvent ev) {
			switch(ev.getKeyChar()) {
			
			case 'r': 
				restart();
				test = 0;
				bug = 0;
				break;
				
			case 't': 
				if(test == 0) test=1;
				if(test == 3) { test(); test = 0;}
				bug = 0;
				break;
				
			case 'e': 
				if(test == 1) test=2;
				bug = 0;
				break;
				
			case 's': 
				if(test == 2) test=3;
				bug = 0;
				break;
				
			case 'b': 
				bug=1;
				test = 0;
				break;	
				
			case 'u': 
				if(bug == 1) bug=2;
				test = 0;
				break;	
				
			case 'g': 
				if(bug == 2) bug();
				test = 0;
				bug = 0;
				break;	
			
			default:
				test = 0;
				bug = 0;
			}
		}	
	}
}
