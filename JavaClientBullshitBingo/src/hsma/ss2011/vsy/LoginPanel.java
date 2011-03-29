package hsma.ss2011.vsy;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Frame to connect to a server and get a list of currently
 * available game sessions and to create new ones.
 */
public class LoginPanel extends JPanel implements ActionListener {
	private JButton connect;
	private JTextField server;
	private JTextField port;
	private JTextField nick;
	
	public LoginPanel() {
		this.setVisible(false);
		this.setLayout(new GridLayout(2, 1));
		
		this.createGameManagementPanel();
		this.setVisible(true);
	}
	
	/**
	 * Place everything and set the panel up
	 */
	private void createGameManagementPanel() {
		// reference to place the objects in the sub panels
		JPanel line;
		
		// create the objects
		this.connect = new JButton("Verbinden");
		this.server = new JTextField(20);
		this.port = new JTextField(5);
		this.nick = new JTextField(10);
		
		// place the first line, keep NORTH open for a Logo
		line = new JPanel();
		line.add(new JLabel("Server:"));
		line.add(this.server);
		line.add(new JLabel("\tPort:"));
		line.add(this.port);
		this.add(line, BorderLayout.CENTER);
		
		//place the second line
		line = new JPanel();
		line.add(new JLabel("Nick:"));
		line.add(this.nick);
		line.add(this.connect);
		this.add(line);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
