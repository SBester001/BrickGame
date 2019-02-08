import javax.swing.JFrame;

public class EasterEgg extends Thread{
	
	JFrame parent;
	
	public EasterEgg(JFrame parent) {
		this.parent = parent;
	}
	
	@Override
	public void run() {		
		new Game(parent);
		
		super.run();
	}
}
