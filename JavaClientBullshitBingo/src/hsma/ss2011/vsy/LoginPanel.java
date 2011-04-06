package hsma.ss2011.vsy;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Frame to connect to a server and get a list of currently
 * available game sessions and to create new ones.
 */
public class LoginPanel extends JPanel implements ActionListener {
	private JavaBullshitBingo parent;
	private JButton connect;
	private JTextField server;
	private JTextField port;
	private JTextField nick;
	
	public LoginPanel(JavaBullshitBingo parent) {
		this.setVisible(false);
		this.setLayout(new GridLayout(2, 1));
		
		this.parent = parent;
		
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
		this.connect.addActionListener(this);
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
		if (e.getSource() == this.connect) {
			try {
				GameManagement manager = new GameManagement(this.server.getText(),
						Integer.parseInt(this.port.getText()), this.nick.getText());
				manager.registerPlayer();
				this.parent.setCurrentPanel(new LobbyPanel(manager, this.parent));
			} catch (Exception error_1) { // we just want to show the error
				JOptionPane.showMessageDialog(null, "Login nicht möglich: Fehlerhafte Angaben."
						+ error_1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Throwable e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}

}
