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
		fixedDimensions[0] = 0;
		fixedDimensions[1] = 1;
		fixedDimensions[2] = 2;
		
		data = new DataArray(50, 50, 50, 100, new String [] {"first", "second", "third", "forth"});
		
		sliderMenu = new SliderMenu();
		plotter = new frmPlot();
		plotter.setLocation(sliderMenu.getWidth(), plotter.getLocation().y);
	}
	
	public static void updatePlotter(){
		plotter.reload();
	}
	
	public static int getFixedDimension(int index){
		return fixedDimensions[index];
	}
	
	public static int getFixedDimensionStep(int index){
		if (index == 0){
			return sliderMenu.step3D.getValue();
		}
		else if (index == 1){
			return sliderMenu.step2D.getValue();
		}
		else if (index == 2){
			return sliderMenu.stepGraph.getValue();
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
			float step = (data.getMaxData(4) - data.getMinData(4)) / 100f;
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
			float step = (data.getMaxData(4) - data.getMinData(4)) / 100f;
			return sliderMenu.rangeHeat.getHighValue() * step;
		}
		else {
			return Float.MIN_VALUE;
		}
	}
}
