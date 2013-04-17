package Data;

import GUI.SliderMenu;
import GUI.frmPlot;

public class DataHolder {
	
	public static int[] fixedDimensions;
	public static DataArray data;
	
	public static SliderMenu sliderMenu;
	public static frmPlot plotter;
	public DataHolder (){
		fixedDimensions = new int[3];
		fixedDimensions[0] = 2;
		fixedDimensions[1] = 1;
		fixedDimensions[2] = 0;
		
		data = new DataArray(10, 10, 10, 10, new String [] {"first", "second", "third", "forth"});
		
		sliderMenu = new SliderMenu();
		plotter = new frmPlot();
	}
	
	public static int getFixedDimension(int index){
		return fixedDimensions[index];
	}
	
	public static int getFixedDimensionStep(int index){
		if (index == 0){
			return sliderMenu.chooser3D.getValue();
		}
		else if (index == 1){
			return sliderMenu.chooser2D.getValue();
		}
		else if (index == 2){
			return sliderMenu.chooserGraph.getValue();
		}
		else {
			return -1;
		}
	}
	
	public static float getMinFilter(int index){
		if (index == 0){
			return sliderMenu.range1.getLowValue();
		}
		else if (index == 1){
			return sliderMenu.range2.getLowValue();
		}
		else if (index == 2){
			return sliderMenu.range3.getLowValue();
		}
		else if (index == 3){
			return sliderMenu.range4.getLowValue();
		}
		else if (index == 4){
			float step = (data.getMaxData(4) - data.getMinData(4)) / (data.getLength(4));
			return sliderMenu.rangeHeat.getLowValue() * step;
		}
		else {
			return Float.MIN_VALUE;
		}
	}
	
	public static float getMaxFilter(int index){
		if (index == 0){
			return sliderMenu.range1.getHighValue();
		}
		else if (index == 1){
			return sliderMenu.range2.getHighValue();
		}
		else if (index == 2){
			return sliderMenu.range3.getHighValue() ;
		}
		else if (index == 3){
			return sliderMenu.range4.getHighValue();
		}
		else if (index == 4){
			float step = (data.getMaxData(4) - data.getMinData(4)) / (data.getLength(4));
			return sliderMenu.rangeHeat.getHighValue() * step;
		}
		else {
			return Float.MIN_VALUE;
		}
	}
}
