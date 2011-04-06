package hsma.ss2011.vsy;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class LobbyPanel extends JPanel implements ActionListener, ListSelectionListener {
	private GameManagement manager; // Communication with the server
	private GameSession[] sessions;
	private JavaBullshitBingo parentFrame;
	private JButton createButton;
	private JButton joinButton;
	private JButton refreshButton;
	private JList gameList;
	private JLabel playerLabel;
	private JLabel wordlistLabel;
	private JLabel sizeLabel;
	
	
	/**
	 * The Panel showing current games and let the user decide wether to join one
	 * or create one with another configuration.
	 * @param manager
	 * @param parentFrame
	 */
	public LobbyPanel(GameManagement manager, JavaBullshitBingo parentFrame) {
		this.setVisible(false);
		this.manager = manager;
		this.parentFrame = parentFrame;
		
		try {
			this.sessions = this.manager.currentGames();
		} catch (JSONException e) {
			JOptionPane.showMessageDialog(null, this.manager.getError(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "LobbyPanel(): currentGames Call scheitert.",
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}

		this.setSize(320, 350);
		this.setLayout(new BorderLayout(10, 3));
		
		this.drawPanelContent();
		this.setVisible(true);
	}
	
	
	/**
	 * Draw the left part of the Panel
	 */
	private void drawPanelContent() {
		// the panel containing the information labels
		JPanel subInfoPanel = new JPanel(new GridLayout(3, 2));
		this.playerLabel = new JLabel("");
		this.wordlistLabel = new JLabel("");
		this.sizeLabel = new JLabel("");
		subInfoPanel.add(new JLabel("Players:"));
		subInfoPanel.add(this.playerLabel);
		subInfoPanel.add(new JLabel("Wordlist:"));
		subInfoPanel.add(this.wordlistLabel);
		subInfoPanel.add(new JLabel("Size:"));
		subInfoPanel.add(this.sizeLabel);
		
		// Buttons
		this.createButton = new JButton("Spiel erstellen");
		this.createButton.addActionListener(this);
		this.refreshButton = new JButton("Aktualisieren");
		this.refreshButton.addActionListener(this);
		this.joinButton = new JButton("Spiel beitreten");
		this.joinButton.addActionListener(this);
		
		// add the current gameList
		try {
			this.refreshGameList();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		// Place the elements
		this.add(this.refreshButton, BorderLayout.NORTH);
		this.add(gameList, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new GridLayout(3, 1));
		southPanel.add(subInfoPanel);
		southPanel.add(joinButton);
		southPanel.add(createButton);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	
	
	/**
	 * Refresh the list of games - replaces the list object with a new one.
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @throws IOException
	 */
	private void refreshGameList() throws ClientProtocolException, JSONException, IOException {
		this.sessions = manager.currentGames();
		
		// if there's a list, remove that one
		if (this.gameList != null)
			this.remove(this.gameList);
		
		if (this.sessions != null) {
			String[] names = new String[this.sessions.length];
			for (int i=0; i<names.length; i++)
				names[i] = this.sessions[i].getName();
			this.gameList = new JList(names);
		} else {
			String[] fill = {" "};
			this.gameList = new JList(fill);
		}
		
		this.gameList.addListSelectionListener(this);
		this.gameList.setSize(150, 300);
		this.add(new JScrollPane(this.gameList), BorderLayout.CENTER);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Join the selected Game
		if (e.getSource() == this.joinButton) {
			try {
				parentFrame.setCurrentPanel(new GamePanel(this.parentFrame, this.manager));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "LobbyPanel - kann Spiel nicht beitreten: "
						+ e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		// Refresh the shown panel - easiest way: draw a new one.
		} else if (e.getSource() == this.refreshButton) {
			try {
				parentFrame.setCurrentPanel(new LobbyPanel(manager, parentFrame));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Lobby: Aktualisieren der Spieleliste fehlgeschlagen.",
						"Fehler", JOptionPane.ERROR_MESSAGE);
			}
		// Create a new Game
		} else if (e.getSource() == this.createButton) {
			parentFrame.setCurrentPanel(new CreateGamePanel(parentFrame, manager));
		}
	}
	
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.gameList) {
			int index = this.gameList.getSelectedIndex();
			this.manager.setGameID(this.sessions[index].getId());
			this.playerLabel.setText(String.valueOf(sessions[index].getParticipants().length));
			this.wordlistLabel.setText(this.sessions[index].getWordlist());
			this.sizeLabel.setText(String.valueOf(this.sessions[index].getSize()));
		}
	}

}
