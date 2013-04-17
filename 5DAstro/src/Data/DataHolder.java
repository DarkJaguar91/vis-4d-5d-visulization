package Data;

import GUI.SliderMenu;

public class DataHolder {
	
	public int chosen3D = 0;
	public int chosen2D = 1;
	public int chosenGraph = 2;
	
	public DataHolder (){
		new SliderMenu(this);
	}
}
