package Data;

import java.util.Vector;

public class Loader {
	private static int[] fixedDimensions = {3,2,1};
	private static int[] fixedStep = {0,0,0};
	private static Vector<DataChangeListener> listeners = new Vector<DataChangeListener>();
	private static int size[] = {15,15,15,15};
	private static float[][][][] data;
	private static float[] vMax = new float[5];
	/**
	 * Notifies all registered change listeners
	 */
	private static void notifyAllListeners(){
		for (DataChangeListener ear: listeners)
			ear.reloadEvent();
	}
	public static void setFixedDimension(int index, int val, int step){
		assert index >= 0 && index < 4 && val >= 0 && val <= 4 && step >= 0 && step < size[index];
		fixedDimensions[index] = val;
		fixedStep[index] = step;
	}
	public static int getFixedDimension(int index){
		assert index >= 0 && index < 4;
		return fixedDimensions[index];
	}
	public static int getFixedDimensionStep(int index){
		assert index >= 0 && index < 4;
		return fixedStep[index];
	}
	/**
	 * Registers a change listener
	 * @param ear
	 */
	public static void registerListener(DataChangeListener ear){
		listeners.add(ear);
	}
	/**
	 * gets minimum filter value 
	 * @param index (0 <= index < 4)
	 * @return dimension minimum ( 0 <= min < discrete size of dimension ) 
	 */
	public static int getMinFilter(int index) {
		assert index >= 0 && index < 4;
		return minFilter[index];
	}
	/**
	 * Sets minimum filter value
	 * @param index (0 <= index < 4)
	 * @param val ( 0 <= min < discrete size of dimension )
	 */
	public static void setMinFilter(int index, int val) {
		assert index >= 0 && index < 4 && val >= 0 && val < size[index];
		Loader.minFilter[index] = val;
		notifyAllListeners();
	}
	/**
	 * gets maximum filter value 
	 * @param index (0 <= index < 4)
	 * @return dimension maximum ( 0 <= max < discrete size of dimension ) 
	 */
	public static int getMaxFilter(int index) {
		assert index >= 0 && index < 4;
		return maxFilter[index];
	}
	/**
	 * Sets maximum filter value
	 * @param index (0 <= index < 4)
	 * @param val ( 0 <= max < discrete size of dimension )
	 */
	public static void setMaxFilter(int index, int val) {
		assert index >= 0 && index < 4 && val >= 0 && val < size[index];
		Loader.maxFilter[index] = val;
		notifyAllListeners();
	}
	/**
	 * gets minimum heat
	 * @return min heat
	 */
	public static float getHeatMinFilter() {
		return heatMinFilter;
	}
	/**
	 * sets minimum heat
	 * @param heatMinFilter
	 */
	public static void setHeatMinFilter(float heatMinFilter) {
		Loader.heatMinFilter = heatMinFilter;
	}
	/**
	 * get maximum heat
	 * @return max heat
	 */	
	public static float getHeatMaxFilter() {
		return heatMaxFilter;
	}
	/**
	 * set maximum heat
	 * @param heatMaxFilter
	 */
	public static void setHeatMaxFilter(float heatMaxFilter) {
		Loader.heatMaxFilter = heatMaxFilter;
		notifyAllListeners();
	}
	/**
	 * gets array dimension size
	 * @param index (0 <= index < 4)
	 * @return dimension size
	 */
	public static int getSize(int index) {
		assert index >= 0 && index < 4;
		return size[index];
	}
	/**
	 * gets a shallow copy of the array
	 * @return 4D array of heat values
	 */
	public static final float[][][][] getData() {
		return data;
	}
	/**
	 * gets the max value of each dimension (not the number of steps as given by getSize)
	 * @param index (0 <= index < 5)
	 * @return max
	 */
	public static float getvMax(int index) {
		assert index >= 0 && index <= 4;
		return vMax[index];
	}
	/**
	 * gets the min value of each dimension (not the number of steps as given by getSize)
	 * @param index (0 <= index < 5)
	 * @return min
	 */
	public static float getvMin(int index) {
		assert index >= 0 && index <= 4;
		return vMin[index];
	}
	/**
	 * gets the labels for each dimension
	 * @param index (0 <= index < 5)
	 * @return label
	 */
	public static String getLabels(int index) {
		assert index >= 0 && index <= 4;
		return labels[index];
	}
	private static float[] vMin = new float[5];
	private static int[] minFilter = new int[4];
	private static int[] maxFilter = new int[4];
	private static float heatMinFilter = 0;
	private static float heatMaxFilter = 0;
	private static String[] labels = new String[5]; 
	public static void loadData(String filename){
		//TODO: set data size
		data = new float[size[0]][size[1]][size[2]][size[3]];
		//TODO: read the min of dimensions
		vMin[0] = 0;
		vMin[1] = 0;
		vMin[2] = 0;
		vMin[3] = 0;
		vMin[4] = 100;
		vMax[0] = 100;
		vMax[1] = 100;
		vMax[2] = 100;
		vMax[3] = 100;
		vMax[4] = 0;
		//reset filters
		for (int i = 0; i < 4; ++i){
			minFilter[i] = 0;
			maxFilter[i] = size[i];
		}
		//TODO: read labels:
		labels[0] = "Period";
		labels[1] = "Amplitude";
		labels[2] = "Offset";
		labels[3] = "Frequency";
		labels[4] = "?";
		//TODO: read data:
		for (int i = 0; i < size[0]; ++i)
			for (int j = 0; j < size[1]; ++j)
				for (int k = 0; k < size[2]; ++k)
					for (int l = 0; l < size[3]; ++l){
						data[i][j][k][l] = i/(float)size[0]*100;
						vMax[4] = Math.max(vMax[4], data[i][j][k][l]);
						vMin[4] = Math.min(vMin[4], data[i][j][k][l]);
					}
		heatMaxFilter = vMax[4];
		heatMinFilter = vMin[4];
		
		//finally notify all listeners of the change
		notifyAllListeners();
	}
}
