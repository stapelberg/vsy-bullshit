package hsma.ss2011.vsy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class BullshitButton extends JButton implements ActionListener {
	
	public BullshitButton(String word) {
		this.setText(word);
		this.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO JSON Call, that the button was hit.
		
		this.setEnabled(false);
	}

}
