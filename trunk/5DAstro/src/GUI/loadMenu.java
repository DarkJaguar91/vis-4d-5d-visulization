package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class loadMenu extends JDialog{
	/**
	 * generated serial ID
	 */
	private static final long serialVersionUID = 4484316667280328406L;

	Image img = null;
	
	public loadMenu(){
		super();
		setVisible(true);
		img = new ImageIcon("Image/loading.png").getImage();
		JPanel l = new JPanel(){

			/**
			 * generated serial ID
			 */
			private static final long serialVersionUID = 585698405898269144L;
			
			@Override
			public void paint (Graphics g){
				if (img != null){
					g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
				}
			}
		};
		l.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
		this.setLayout(new BorderLayout());
		this.add(l, BorderLayout.CENTER);
		this.pack();
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		
		
		this.setLocationRelativeTo(null);
	}
}
