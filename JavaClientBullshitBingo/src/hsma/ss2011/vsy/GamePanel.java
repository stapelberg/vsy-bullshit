package hsma.ss2011.vsy;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class GamePanel extends JPanel implements ActionListener {
	private GameManagement manager;
	private JavaBullshitBingo parent;
	private JButton leaveButton;
	private JList playerList;
	private String gameID;
	
	public GamePanel(JavaBullshitBingo parent, GameManagement manager)
		throws ClientProtocolException, IOException, JSONException {
		
		this.gameID = manager.getGameID();
		this.manager = manager;
		this.parent = parent;
		
		this.playerList = new JList(manager.getPlayers(this.gameID));
		this.setLayout(new BorderLayout(10, 10));
		
		this.drawFieldPanel();
		this.drawInformationPanel();
		
		this.parent.pack();
	}
	
	/**
	 * Draw the Panel that contains the Buttons
	 */
	private void drawFieldPanel() {
		// create Panel with size*size
		JPanel fieldPanel = new JPanel(new GridLayout(this.manager.getSize(), this.manager.getSize()));
		String[] words = this.manager.getBuzzwords();
		
		for (int i=0; i<words.length; i++) {
			fieldPanel.add(new BullshitButton(manager, words[i], i));
		}
		
		this.add(fieldPanel, BorderLayout.WEST);
	}
	
	/**
	 * Draw the Panel containing the playerList and leaveButton
	 */
	private void drawInformationPanel() {
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		this.playerList.setSize(120, 386);
		this.leaveButton = new JButton();
		this.leaveButton.addActionListener(this);
		this.leaveButton.setName("Spiel verlassen");
		
		infoPanel.add(this.playerList, BorderLayout.NORTH);
		infoPanel.add(this.leaveButton, BorderLayout.SOUTH);
		
	}
	
	public void renewPlayerList(JList playerList) {
		this.playerList = playerList;
		this.playerList.setSize(120, 386);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.leaveButton) {
			try {
				this.manager.leaveGame();
				//this.parent.setCurrentPanel(new Lobby(this.parent, this.manager));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
