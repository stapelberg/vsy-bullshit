package hsma.ss2011.vsy;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The Java based Client for Bullshit Bingo
 * VSY lecture in Summer Semester 2011 at HSMA.
 * @since 20110320
 */
public class JavaBullshitBingo extends JFrame {
	private JPanel currentPanel;
	
	public JavaBullshitBingo() {
		this.setName("BullshitBingo");
		this.setTitle("BullshitBingo");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.add(new MenuBar(this), BorderLayout.NORTH);
		
		// TODO Draw the panel
		this.currentPanel = new LoginPanel(this);
		this.add(this.currentPanel, BorderLayout.CENTER);
		
		this.setResizable(false);
		this.pack();
	}
	
	/**
	 * Set the panel object that shall be shown next.
	 * @param panel The new JPanel
	 */
	public void setCurrentPanel(JPanel panel) {
		this.currentPanel.removeAll(); //  get rid of the current Panel
		this.remove(this.currentPanel);
		this.currentPanel = panel;
		this.add(this.currentPanel, BorderLayout.CENTER);
		this.currentPanel.setVisible(true);
		this.pack(); // repack the Frame to get the correct size.
	}
	
	public static void main(String[] args) { 
		new JavaBullshitBingo().setVisible(true);
	}

}
