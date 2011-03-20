package hsma.ss2011.vsy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * The menu used by the main frame.
 */
public class MenuBar extends JMenuBar implements ActionListener {
	private JMenu file;
	private JMenuItem fileGameManagement;
	private JMenuItem fileQuit;
	
	private GameManagementFrame gameManagement;
	
	public MenuBar() {
		this.generateFileMenu();
		this.add(file);
		
		// Create the Frame handling the connection/game settings.
		this.gameManagement = new GameManagementFrame();
	}
	
	/**
	 * Create the file menu
	 */
	private void generateFileMenu() {
		file = new JMenu("Datei");

		fileGameManagement = new JMenuItem("Spielverwaltung");
		fileGameManagement.addActionListener(this);

		fileQuit = new JMenuItem("Beenden");
		fileQuit.addActionListener(this);

		// Add items to the menu
		file.add(fileGameManagement);
		file.add(fileQuit);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.fileGameManagement) {
			this.gameManagement.setVisible(true);
		} else if (e.getSource() == this.fileQuit) {
			System.exit(0);
		} 
	}

}
