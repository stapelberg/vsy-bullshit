package hsma.ss2011.vsy;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The Java based Client for Bullshit Bingo
 * VSY lecture in Summer Semester 2011 at HSMA.
 * @since 20110320
 */
public class JavaBullshitBingo extends JFrame {
	private LoginPanel loginPanel;
	private GameManagement manager;
	
	public JavaBullshitBingo() {
		this.setName("BullshitBingo");
		this.setTitle("BullshitBingo");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.add(new MenuBar(), BorderLayout.NORTH);
		
		// TODO Draw the panel
		this.loginPanel = new LoginPanel();
		this.add(this.loginPanel, BorderLayout.CENTER);
		
		this.pack();
	}
	
	public static void main(String[] args) { 
		new JavaBullshitBingo().setVisible(true);
	}

}
