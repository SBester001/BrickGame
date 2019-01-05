import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class Game extends JFrame {
	public static String version = "1.0.0";

	public static Game game;
	
	public static final int sizex = 1150;
	public static final int sizey = 850;
	public static final int posx = 50;
	public static final int posy = 50;
	public static final int bsize = 8;
			
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
	
	int score = 0;
	
	boolean test; //if true you can not lose
	

	
	public static void main(String[] args) {
		game = new Game();
		game.start();
	}
	
	Game(){
		super();
		setSize(sizex+(posx*2), sizey+(posy*2));
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Small Game by Steffen Beschta v."+ version);
		List <Image> imgs = new ArrayList<Image>();
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_128x128.png")).getImage());
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_64x64.png")).getImage());
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_32x32.png")).getImage());
		imgs.add(new ImageIcon(getClass().getResource("resources/BrickGame_img_16x16.png")).getImage());
		setIconImages(imgs);
		addMouseMotionListener(new MyMouseMotionAdapter());
		addKeyListener((new MyKeyListener()));
	}
	
	void start(){		
		try {
			Thread.sleep(40); // ohne Pause wird g.drawRect nicht richtig ausgeführt
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//starting values
		test = false;
		speedx = 7;
		speedy = 7;
		bx = posx;
		by = posy;
		go = true;
		
		Graphics g = getGraphics();	
		g.drawRect(posx-1, posy-1, sizex+bsize+1, sizey+bsize+1);
		g.fillOval(bx, by, bsize, bsize);
		
		while (true) {
			while(go){
				move(g);
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
	
	void move(Graphics g){
		clear(g);
		bx += speedx;
		by += speedy;
		
		g.fillRect(barx, bary, barSizex, barSizey);
		
		if(bx < posx){ 			//left side
			speedx *= -1;
			bx = posx;
		}
		
		if(bx > sizex+posx){ 	//right side
			speedx *= -1;
			bx = sizex+posx;
		}
		
		if(by < posy){			//upper side
			speedy *= -1;
			by = posy;
		}
		
		if(by > sizey+posy){	//bottom side -> check if bar is there
			by = sizey+posy;	
			if((bx>=barx && bx<=barx+barSizex)||test) { // bar is there
				speedy *= -1;						
				if(rnd.nextBoolean()) {				
					if(speedx>0) {
						speedx += addSpeed;
					}else {
						speedx -= addSpeed;
					}
				}else {
					if(speedy>0) {
						speedy += addSpeed;
					}else {
						speedy -= addSpeed;
					}
				}				
				System.out.println("speedx:"+ speedx +"speedy:" + speedy);
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
		g.clearRect(posx, posy, sizex+bsize, sizey+bsize);
		g.clearRect(0, bary, sizex+(posx*2), barSizey);
	}
	
	 void restart() {
		System.out.println("restart");
		go = false;
		
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
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {
			switch(arg0.getKeyChar()) {
			
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
