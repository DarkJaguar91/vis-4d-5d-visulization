package Data;

import java.util.Random;

public class DataArray {
	private float[][][][] data;
	private String[] DimensionNames;
	private float[] minData;
	private float[] maxData;
	/**
	 * Generate a random data set
	 * 
	 * @param dim1
	 *            length of first dimension
	 * @param dim2
	 *            length of second dimension
	 * @param dim3
	 *            length of third dimension
	 * @param dim4
	 *            length of forth dimension
	 * @param names
	 *            The names of the dimensions [4];
	 */
	public DataArray(int dim1, int dim2, int dim3, int dim4, String[] names) {
		data = new float[dim1][dim2][dim3][dim4];

		DimensionNames = names;

		Random r = new Random(System.currentTimeMillis());

		for (int i = 0; i < dim1; ++i)
			for (int x = 0; x < dim2; ++x)
				for (int y = 0; y < dim3; ++y)
					for (int v = 0; v < dim4; ++v)
						data[i][x][y][v] = r.nextFloat() * 100;
		calculateData();
	}

	/**
	 * Sets the data to the given array
	 * 
	 * @param data
	 *            4D float array
	 */
	public DataArray(float[][][][] data, String[] names) {
		this.data = data;
		DimensionNames = names;
		calculateData();
	}

	private void calculateData() {
		minData = new float[5];
		maxData = new float[5];
		for (int i = 0; i < 4; ++i){
			minData[i] = 0;
			maxData[i] = 99;
		}
		
		minData[4] = Float.MAX_VALUE;
		maxData[4] = Float.MIN_VALUE;
		
		for (int i = 0; i < data.length; ++i)
			for (int x = 0; x < data[i].length; ++x)
				for (int y = 0; y < data[i][x].length; ++y)
					for (int v = 0; v < data[i][x][y].length; ++v) {						
						minData[4] = Math.min(minData[4], data[i][x][y][v]);
						maxData[4] = Math.max(maxData[4], data[i][x][y][v]);
					}
	}

	public float getMinData(int index) {
		return minData[index];
	}

	public float getMaxData(int index) {
		return maxData[index];
	}

	public String getDimensionName(int index){
		return DimensionNames[index];
	}
	
	public int getLength(int index) {
		switch (index) {
		case 0:
			return data.length;
		case 1:
			return data[0].length;
		case 2:
			return data[0][0].length;
		case 3:
			return data[0][0][0].length;
		default:
			return 0;
		}
	}
}