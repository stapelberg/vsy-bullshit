package hsma.ss2011.vsy;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Frame to connect to a server and get a list of currently
 * available game sessions and to create new ones.
 */
public class GameManagementFrame extends JFrame implements ActionListener {
	
	public GameManagementFrame() {
		this.setName("Spielverwaltung");
		this.setTitle("Spielverwaltung");
		this.setLayout(new BorderLayout());
		// in order to keep everything the way it is, just hide the window.
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.add(new JLabel("Bitte weitergehen."));
		
		this.pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
