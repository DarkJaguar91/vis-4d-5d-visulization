package Data;

import GUI.SliderMenu;

public class DataHolder {
	
	public static int[] fixedDimensions;
	public static DataArray data;
	
	public static SliderMenu sliderMenu;
	
	public DataHolder (){
		fixedDimensions = new int[3];
		fixedDimensions[0] = 0;
		fixedDimensions[1] = 1;
		fixedDimensions[2] = 2;
		
		data = new DataArray(10, 10, 10, 10, new String [] {"first", "second", "third", "forth"});
		
		sliderMenu = new SliderMenu();
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
			float step = (data.getMaxData(0) - data.getMinData(0)) / (data.getLength(0));
			return sliderMenu.range1.getLowValue() * step;
		}
		else if (index == 1){
			float step = (data.getMaxData(1) - data.getMinData(1)) / (data.getLength(1));
			return sliderMenu.range2.getLowValue() * step;
		}
		else if (index == 2){
			float step = (data.getMaxData(2) - data.getMinData(2)) / (data.getLength(2));
			return sliderMenu.range3.getLowValue() * step;
		}
		else if (index == 3){
			float step = (data.getMaxData(3) - data.getMinData(3)) / (data.getLength(3));
			return sliderMenu.range4.getLowValue() * step;
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
			float step = (data.getMaxData(0) - data.getMinData(0)) / (data.getLength(0));
			return sliderMenu.range1.getHighValue() * step;
		}
		else if (index == 1){
			float step = (data.getMaxData(1) - data.getMinData(1)) / (data.getLength(1));
			return sliderMenu.range2.getHighValue() * step;
		}
		else if (index == 2){
			float step = (data.getMaxData(2) - data.getMinData(2)) / (data.getLength(2));
			return sliderMenu.range3.getHighValue() * step;
		}
		else if (index == 3){
			float step = (data.getMaxData(3) - data.getMinData(3)) / (data.getLength(3));
			return sliderMenu.range4.getHighValue() * step;
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
