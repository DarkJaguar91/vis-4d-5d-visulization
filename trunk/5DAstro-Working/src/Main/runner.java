package Main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Data.DataHolder;

public class runner {
	public static void main (String [] args){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new DataHolder();
	}
}
