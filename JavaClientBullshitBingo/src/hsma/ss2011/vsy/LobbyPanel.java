package hsma.ss2011.vsy;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class LobbyPanel extends JPanel implements ActionListener {
	private GameManagement manager; // Communication with the server
	private GameSession[] sessions;
	private JavaBullshitBingo parent;
	private JButton createButton;
	private JButton joinButton;
	private JButton refreshButton;
	private JList gameList;
	private JList playerList;
	private JLabel wordlistLabel;
	private JLabel sizeLabel;
	
	public LobbyPanel(GameManagement manager, JavaBullshitBingo parent) {
		this.setVisible(false);
		
		this.manager = manager;
		this.parent = parent;

		this.setLayout(new BorderLayout());
		
		this.drawLeftPanel();
		
		this.setVisible(true);
	}
	
	private void drawLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		this.createButton = new JButton("Spiel erstellen");
		this.createButton.addActionListener(this);
		this.refreshButton = new JButton("Aktualisieren");
		this.refreshButton.addActionListener(this);
		
		
		panel.add(this.refreshButton, BorderLayout.NORTH);
		//panel.add(this.gameList, BorderLayout.CENTER);
		panel.add(this.createButton, BorderLayout.SOUTH);
		
		this.add(panel, BorderLayout.WEST);
	}
	
	private void refreshGameList() {
		this.gameList = new JList();
		
		this.gameList.setSize(150, 300);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
