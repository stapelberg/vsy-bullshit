package hsma.ss2011.vsy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class BullshitButton extends JButton implements ActionListener {
	private GameManagement manager;
	private GamePanel parentPanel;
	private int field;
	
	public BullshitButton(GamePanel parentPanel, GameManagement manager, String word, int field) {
		this.setSize(100,50);
		this.setText(word);
		this.manager = manager;
		this.field = field;
		this.parentPanel = parentPanel;
		this.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String winner = null;
		try {
			this.manager.makeMove(this.field);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		// check if someonse has won
		try {
			winner = manager.checkWinner();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Fehler beim aktualisieren der Spielerliste: "
					+ e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		this.setEnabled(false);
		this.parentPanel.renewPlayerList(winner);
	}

}
