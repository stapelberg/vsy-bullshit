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
	private JPanel infoPanel;
	
	public GamePanel(JavaBullshitBingo parent, GameManagement manager)
		throws ClientProtocolException, IOException, JSONException {
		this.manager = manager;
		this.parent = parent;
		
		this.setLayout(new BorderLayout());
		this.manager.joinGame(this.manager.getGameID());
		
		this.drawFieldPanel();
		this.drawInformationPanel();
	}
	
	/**
	 * Draw the Panel that contains the Buttons
	 */
	private void drawFieldPanel() {
		// create Panel with size*size
		JPanel fieldPanel = new JPanel(new GridLayout(this.manager.getSize(), this.manager.getSize()));
		String[] words = this.manager.getBuzzwords();
		
		for (int i=0; i<words.length; i++) {
			fieldPanel.add(new BullshitButton(this, manager, words[i], i));
		}
		this.add(fieldPanel, BorderLayout.WEST);
	}
	
	/**
	 * Draw the Panel containing the playerList and leaveButton
	 */
	private void drawInformationPanel() {
		this.infoPanel = new JPanel(new BorderLayout());
		
		this.leaveButton = new JButton("Spiel verlassen");
		this.leaveButton.addActionListener(this);
		
		this.infoPanel.add(this.leaveButton, BorderLayout.SOUTH);
		this.renewPlayerList(null);
		this.add(this.infoPanel, BorderLayout.EAST);
	}
	
	
	/**
	 * Refresh the list at the right side of the Panel
	 * @param playerList
	 */
	private void renewPlayerList(String winner) {
		if (winner != null) {
			// someone won the game so finish it
			JOptionPane.showMessageDialog(null, "Spieler " + winner + " hat gewonnen!",
					"Sieger!", JOptionPane.INFORMATION_MESSAGE);
			this.parent.setCurrentPanel(new LobbyPanel(manager, parent));
		} else {
			// we don't want a NPE
			if (this.playerList != null)
				this.infoPanel.remove(this.playerList);
			
			try {
				this.playerList = new JList(this.manager.getPlayers(manager.getGameID()));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Aktualisierung der Spielerliste schlug fehl(GamePanel)", "Fehler" , JOptionPane.ERROR_MESSAGE);
			}
			this.infoPanel.add(this.playerList, BorderLayout.CENTER);
			this.parent.pack();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.leaveButton) {
			try {
				this.manager.leaveGame();
				this.parent.setCurrentPanel(new LobbyPanel(this.manager, this.parent));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public GameManagement getManager() {
		return this.manager;
	}
}
