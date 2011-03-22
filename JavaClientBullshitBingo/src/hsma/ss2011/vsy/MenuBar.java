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
	private JMenuItem fileDisconnect;
	private JMenuItem fileQuit;
	
	public MenuBar() {
		this.generateFileMenu();
		this.add(file);
	}
	
	/**
	 * Create the file menu
	 */
	private void generateFileMenu() {
		file = new JMenu("Datei");

		fileDisconnect = new JMenuItem("Verbindung trennen");
		fileDisconnect.addActionListener(this);

		fileQuit = new JMenuItem("Beenden");
		fileQuit.addActionListener(this);

		// Add items to the menu
		file.add(fileDisconnect);
		file.add(fileQuit);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.fileDisconnect) {
			// TODO disconnect and clean the settings.
		} else if (e.getSource() == this.fileQuit) {
			System.exit(0);
		} 
	}

}
