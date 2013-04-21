package heatMap;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;

import Data.DataArray;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HM2DGraph {
	private final Color loColor = new Color(0,0,255);
	private final Color mdColor = new Color(0,255,0);
	private final Color hiColor = new Color(255,0,0);

	private BufferedImage internalMap;
	private Color[] scale = new Color[256];
	
//	private float[][][][] dataCopy = DataArray.getData();

/*	public HM2DGraph() {
		initialiseColours();
		initialiseImage("/home/hstrauss/640x640.png");
		System.out.println("initialised HM2DGraph");
	}*/

	private void initialiseColours()
	{
		int i;
		// initialise an color scale from loColor to mdColor and up to hiColor
		for (i = 0; i < 128; i++){
			scale[i] = new Color((int)((loColor.getRed()*2.0*(128-i)/256)+(mdColor.getRed()*2.0*(i)/256)),
					(int)((loColor.getGreen()*2.0*(128-i)/256)+(mdColor.getGreen()*2.0*(i)/256)),
					(int)((loColor.getBlue()*2.0*(128-i)/256)+(mdColor.getBlue()*2.0*(i)/256)));
			scale[i+128] = new Color((int)((mdColor.getRed()*2.0*(128-i)/256)+(hiColor.getRed()*2.0*(i)/256)),
					(int)((mdColor.getGreen()*2.0*(128-i)/256)+(hiColor.getGreen()*2.0*(i)/256)),
					(int)((mdColor.getBlue()*2.0*(128-i)/256)+(hiColor.getBlue()*2.0*(i)/256)));				 
		}
		// final element excluded from for loop above
		scale[255] = new Color(hiColor.getRed(),hiColor.getGreen(),hiColor.getBlue());
	}
	private void initialiseImage(String path){
		try {
			internalMap = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("Cannot load Image File: " +path);
			return;
		}
		for (int x = 0; x < 640; x++){
			for (int y = 0; y<640; y++){
				internalMap.setRGB(x, y, scale[(int)(Math.random()*256)].getRGB());
			}
		}
	}
	public BufferedImage getImage(){
		return internalMap;
	}
	public Color[] getScale(){
		return scale;
	}

	public Color getColor(int index){
		if (index < 0) index = 0;
		if (index > 255) index = 255;
		// return null.equals(scale[index])? null : scale[index];
		assert scale[index] != null; // this is initialised in the constructor()
		return scale[index];
	}
	
}
