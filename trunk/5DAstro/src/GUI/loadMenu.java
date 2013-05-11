package GUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class loadMenu extends JFrame{
	/**
	 * generated serial ID
	 */
	private static final long serialVersionUID = 4484316667280328406L;

	public loadMenu(){
		super("Loading");
		setVisible(true);
		JLabel l = new JLabel("Loading...");
		l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		this.setLayout(new BorderLayout());
		
		this.add(l, BorderLayout.CENTER);
		
		this.pack();
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}
}
