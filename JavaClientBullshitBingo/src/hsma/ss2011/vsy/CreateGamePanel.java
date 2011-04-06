package hsma.ss2011.vsy;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class CreateGamePanel extends JPanel implements ActionListener {
	private JavaBullshitBingo parent;
	private GameManagement manager;
	private JButton cancelButton;
	private JButton createButton;
	private JComboBox sizeBox;
	private JComboBox wordlistBox;
	private JTextField nameField;
	
	public CreateGamePanel(JavaBullshitBingo parent, GameManagement manager) {
		this.parent = parent;
		this.manager = manager;

		this.setLayout(new GridLayout(4,2));
		
		// first row: name
		this.nameField = new JTextField();
		this.add(new JLabel("Spielname:"));
		this.add(this.nameField);
		
		// second row: wordlist
		try {
			this.wordlistBox = new JComboBox(this.manager.getWordlists());
		} catch (ClientProtocolException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (JSONException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		this.add(new JLabel("Wordlist:"));
		this.add(this.wordlistBox);
		
		// third row: size:
		String[] possibleFieldSize = {"3", "4", "5"};
		this.sizeBox = new JComboBox(possibleFieldSize);
		this.add(new JLabel("Größe:"));
		this.add(this.sizeBox);
		
		// fourth row: Buttons
		this.createButton = new JButton("Spiel Erstellen");
		this.createButton.addActionListener(this);
		this.add(this.createButton);
		
		this.cancelButton = new JButton("Abbrechen");
		this.cancelButton.addActionListener(this);
		this.add(this.cancelButton);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.createButton) {
			try {
				String wordlist = this.wordlistBox.getSelectedItem().toString();
				int size = Integer.parseInt(this.sizeBox.getSelectedItem().toString());
				
				this.manager.createGame(this.nameField.getText(), size, wordlist);
				this.parent.setCurrentPanel(new GamePanel(parent, manager));
				
			} catch (ClientProtocolException error) {
				JOptionPane.showMessageDialog(null, error.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			} catch (IOException error) {
				JOptionPane.showMessageDialog(null, error.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			} catch (JSONException error) {
				JOptionPane.showMessageDialog(null, this.manager.getError(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
			
		} else if ( e.getSource() == this.cancelButton) {
			this.parent.setCurrentPanel(new LobbyPanel(manager, parent));
		}
	}

}
