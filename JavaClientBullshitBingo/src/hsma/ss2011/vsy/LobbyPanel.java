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
	private JavaBullshitBingo parent;
	private JButton createButton;
	private JButton joinButton;
	private JButton refreshButton;
	private JList gameList;
	private JList playerList;
	private JLabel wordlistLabel;
	private JLabel sizeLabel;
	private JPanel leftPanel; // refresh, create, gamelist
	private JPanel rightPanel; // join, playerlist, Labels
	
	public LobbyPanel(GameManagement manager, JavaBullshitBingo parent) {
		this.setVisible(false);
		this.manager = manager;
		this.parent = parent;
		try {
			this.sessions = this.manager.currentGames();
		} catch (JSONException e) {
			JOptionPane.showMessageDialog(null, this.manager.getError(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "LobbyPanel(): currentGames Call scheitert.",
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}

		this.setLayout(new BorderLayout(10, 3));
		
		this.drawLeftPanel();
		this.drawRightPanel();
		
		this.setVisible(true);
	}
	
	/**
	 * Draw the left part of the Panel
	 */
	private void drawLeftPanel() {
		this.leftPanel = new JPanel(new BorderLayout());

		this.createButton = new JButton("Spiel erstellen");
		this.createButton.addActionListener(this);
		this.refreshButton = new JButton("Aktualisieren");
		this.refreshButton.addActionListener(this);
		
		
		this.leftPanel.add(this.refreshButton, BorderLayout.NORTH);
		this.leftPanel.add(this.createButton, BorderLayout.SOUTH);
		
		// add the current gameList
		try {
			this.refreshGameList();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		this.add(this.leftPanel, BorderLayout.WEST);
	}
	
	/**
	 * Draw the Panel containing the list of players, joinButton and information labels
	 */
	private void drawRightPanel() {
		this.rightPanel = new JPanel(new BorderLayout());
		
		// the panel containing the information labels
		JPanel sub = new JPanel(new GridLayout(2, 2));
		this.wordlistLabel = new JLabel("");
		this.sizeLabel = new JLabel("");
		sub.add(new JLabel("Wordlist:"));
		sub.add(this.wordlistLabel);
		sub.add(new JLabel("Size:"));
		sub.add(this.sizeLabel);
		
		String[] fill = {""};
		this.playerList = new JList(fill);
		
		this.joinButton = new JButton("Spiel beitreten");
		this.joinButton.addActionListener(this);
		
		this.rightPanel.add(this.playerList, BorderLayout.NORTH );
		this.rightPanel.add(sub, BorderLayout.CENTER);
		this.rightPanel.add(this.joinButton, BorderLayout.SOUTH);
		
		this.add(this.rightPanel, BorderLayout.EAST);
	}
	
	/**
	 * Refresh the list of games - replaces the list object with a new one.
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @throws IOException
	 */
	private void refreshGameList() throws ClientProtocolException, JSONException, IOException {
		// if there's a list, remove that one
		if (this.gameList != null)
			this.leftPanel.remove(this.gameList);
		
		if (this.sessions != null) {
			String[] names = new String[this.sessions.length];
			for (int i=0; i<names.length; i++)
				names[i] = this.sessions[i].getName();
			this.gameList = new JList(names);
		} else {
			String[] fill = {""};
			this.gameList = new JList(fill);
		}
		
		this.gameList.addListSelectionListener(this);
		this.leftPanel.add(new JScrollPane(this.gameList), BorderLayout.CENTER);
	}
	
	private void refreshPlayerList(int index) {
		// if there's a list, remove that one
		if (this.playerList != null)
			this.rightPanel.remove(this.playerList);
		
		if (this.sessions != null) {
			String[] players = this.sessions[index].getParticipants();
			this.playerList = new JList(players);
		} else {
			String[] fill = {""};
			this.playerList = new JList(fill);
		}
		this.playerList.setEnabled(false);
		this.rightPanel.add(new JScrollPane(this.playerList), BorderLayout.NORTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.joinButton) {
			try {
				this.parent.setCurrentPanel(new GamePanel(this.parent, this.manager));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getSource() == this.refreshButton) {
			try {
				this.sessions = this.manager.currentGames();
				this.refreshGameList();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getSource() == this.createButton) {
			parent.setCurrentPanel(new CreateGamePanel(parent, manager));
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.gameList) { // just update the gameID in the manager
			int index = this.gameList.getSelectedIndex();
			this.manager.setGameID(this.sessions[index].getId());
			this.refreshPlayerList(index);
			this.wordlistLabel.setText(this.sessions[index].getWordlist());
			this.sizeLabel.setText(String.valueOf(this.sessions[index].getSize()));
		}
	}

}
