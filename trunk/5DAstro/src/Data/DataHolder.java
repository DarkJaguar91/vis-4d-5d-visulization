package Data;

import GUI.GraphScreen;
import GUI.SliderMenu;
import GUI.frmPlot;

/**
 * 
 * @author Brandon James Talbot
 * 
 *  Static class that contains all required variables for slider and plotting frames
 */
public class DataHolder {
	
	//Global variables
	public static int[] fixedDimensions; // the 3 fixed dimension states (first is 3d, 2d then graph)
	public static DataArray data; // the data class being used
	
	public static SliderMenu sliderMenu; // the slider menu frame
	public static frmPlot plotter; // the 3d jframe
	public static GraphScreen plotterGraph; // the graph Jframe
	
	/**
	 * Constructor
	 * initialises
	 * Reads in data
	 * starts all frames
	 */
	public DataHolder (){
		fixedDimensions = new int[3];
		fixedDimensions[0] = 0;
		fixedDimensions[1] = 1;
		fixedDimensions[2] = 2;
		
		data = new DataArray(10, 10, 10, 10, new String [] {"first", "second", "third", "forth"});
		
		sliderMenu = new SliderMenu();
		plotter = new frmPlot();
		plotter.setLocation(sliderMenu.getWidth(), plotter.getLocation().y);
		plotterGraph = new GraphScreen();
		plotterGraph.setLocation(sliderMenu.getWidth(), plotter.getHeight());
	}
	
	/**
	 * Method that updates all plotters when something has changed
	 */
	public static void updatePlotter(){
		plotter.reload();
		plotterGraph.plotGraph();
	}
	
	/**
	 * Gets the fixed dimension for the specified plot (3d, 2d then graph) 
	 * @param index The index (0 - 2)
	 * @return The fixed dimension (0 - 3)
	 */
	public static int getFixedDimension(int index){
		return fixedDimensions[index];
	}
	
	/**
	 * Gets the step for the fixed dimension (3d, 2d, graph)
	 * @param index The index for the dimension (0 - 2)
	 * @return The currently selected step value (as array index)
	 */
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
	
	/**
	 * Gets the minimum value for the Specified dimension
	 * Note: for index 0 - 3 (returns array index)
	 * Note: for index 4 (returns float value of heat)
	 * @param index The index of the dimension
	 * @return float value for minimum "value" of dimension
	 */
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
	
	/**
	 * Gets the maximum value for the Specified dimension
	 * Note: for index 0 - 3 (returns array index)
	 * Note: for index 4 (returns float value of heat)
	 * @param index The index of the dimension
	 * @return float value for maximum "value" of dimension
	 */
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
