package Data;

import GUI.SliderMenu;

public class DataHolder {
	
	public int[] fixedDimensions;
	public DataArray data;
	
	public SliderMenu sliderMenu;
	
	public DataHolder (){
		fixedDimensions = new int[3];
		fixedDimensions[0] = 0;
		fixedDimensions[1] = 1;
		fixedDimensions[2] = 2;
		
		data = new DataArray(100, 100, 100, 100, new String [] {"first", "second", "third", "forth"});
		
		sliderMenu = new SliderMenu(this);
	}
	
	public int getFixedDimension(int index){
		return fixedDimensions[index];
	}
	
	public int getFixedDimensionStep(int index){
		
	}
	
	public 
}
