import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import Data.Loader;
import Drawing3D.Plotter3D;
import Forms.Plot3D;


public class SKAVisualization{
	public static void main (String [] args){
		Loader.loadData("w/e");
		new Plot3D();
		
	}
}
