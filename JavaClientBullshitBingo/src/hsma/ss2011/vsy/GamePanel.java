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
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class GamePanel extends JPanel implements ActionListener {
	private GameManagement manager;
	private JavaBullshitBingo parent;
	private JButton leaveButton;
	private JList playerList;
	private JPanel infoPanel;
	private Timer updateTimer;
	public int updateCounter;
	
	
	public GamePanel(JavaBullshitBingo parent, GameManagement manager)
		throws ClientProtocolException, IOException, JSONException {
		this.manager = manager;
		this.parent = parent;
		this.updateTimer = new Timer(100, this);
		this.updateCounter = 0;
		
		this.setLayout(new BorderLayout());
		this.manager.joinGame(this.manager.getGameID());
		
		this.drawFieldPanel();
		this.drawInformationPanel();
		updateTimer.start();
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
		this.infoPanel = new JPanel(new BorderLayout());
		
		this.leaveButton = new JButton("Spiel verlassen");
		this.leaveButton.addActionListener(this);
		
		this.infoPanel.add(this.leaveButton, BorderLayout.SOUTH);
		this.renewPlayerList();
		this.add(this.infoPanel, BorderLayout.EAST);
	}
	
	
	/**
	 * Refresh the list at the right side of the Panel
	 */
	private void renewPlayerList() {
		// we don't want a NPE
		if (this.playerList != null)
			this.infoPanel.remove(this.playerList);
		
		try {
			this.playerList = new JList(this.manager.getPlayers(manager.getGameID()));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Aktualisieren der Spielerliste fehlgeschlagen:\n"
					+ e.getMessage(), "Fehler" , JOptionPane.ERROR_MESSAGE);
		}
		this.infoPanel.add(new JScrollPane(playerList), BorderLayout.CENTER);
		this.parent.pack();
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
		} else { // Timer
			String winner = null;
			try {
				winner = manager.checkWinner();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "GamePanel: Aktualisieren schlägt fehl:\n"
						+ e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
			
			if (winner != null) {
				// someone won the game so finish it
				JOptionPane.showMessageDialog(null, "Spieler " + winner + " hat gewonnen!",
						"Sieger!", JOptionPane.INFORMATION_MESSAGE);
				updateTimer.stop();
				this.parent.setCurrentPanel(new LobbyPanel(manager, parent));
			}
			
			// refresh the playerList every second
			if (updateCounter%10 == 0) {
				this.renewPlayerList();
				updateCounter = 0; // reset
			}
			
			updateCounter++;
		}
	}
}
