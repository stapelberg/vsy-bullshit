package hsma.ss2011.vsy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class BullshitButton extends JButton implements ActionListener {
	private GameManagement manager;
	private int field;
	
	public BullshitButton(GameManagement manager, String word, int field) {
		this.setSize(100,50);
		this.setText(word);
		this.manager = manager;
		this.field = field;
		this.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			this.manager.makeMove(this.field);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		this.setEnabled(false);
	}

}
