package Data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;


public class LoadData {
	private float[][][][] data; // array of data
	private String[] DimensionNames; // names for each dimension
	private float[] minData; // min data value
	private float[] maxData; // max data value 
	
	public LoadData(String filename){
		//Other variables from Chris:
		byte[]      type = new byte[40]; 
		int       maxDims; 
		int       tmpi1; 
		int       tmpi2; 
		int       tmpi3; 
		long       rLog; 
		boolean    changed; 
		long       elems; 
		long       dimSizes; 
		long       dims; 
		int       noDims; 
		long       buffSize; 
		long       noEls;
		int       elSize; 
		int       minVal;
		int       maxVal; 
		double       sumVal; 
		double       meanVal; 
		double       stdDevVal; 
		double       varVal; 
		int       min; 
		int       max; 
		double       spread; 
		byte[]	fileName_chris = new byte[8];
		int       precision;
		//read in the file
		try {
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename));
			byte[] bytes = new byte[24];
			ByteBuffer converter = ByteBuffer.wrap(bytes);
			
			is.read(bytes, 0, 24);
			int[] sizeInfo = new int[6];
			for (int i = 0; i < 6; ++i)
				sizeInfo[i] = converter.getInt(i*4);
			
			assert sizeInfo[4] == 4; // we can only handle floats at this point 
			
			bytes = new byte[sizeInfo[2]];
			is.read(bytes, 24, sizeInfo[2]);
			converter = ByteBuffer.wrap(bytes);
			converter.get(type, 0, 39);
			maxDims = converter.getInt(40);
			
			assert maxDims == 4;   //we can only handle 4D arrays of floats!
			
			tmpi1 = converter.getInt(44);
			tmpi2 = converter.getInt(48);
			tmpi3 = converter.getInt(52);
			rLog = converter.getLong(56);
			changed = (bytes[64] == 1);
			elems = converter.getLong(65);
			dimSizes = converter.getLong(73);
			dims = converter.getLong(81);
			noDims = converter.getInt(89);
			buffSize = converter.getLong(93);
			noEls = converter.getLong(101);
			elSize = converter.getInt(109);
			minVal = converter.getInt(113);
			maxVal = converter.getInt(117);
			sumVal = converter.getDouble(121);
			meanVal = converter.getDouble(129);
			stdDevVal = converter.getDouble(137);
			varVal = converter.getDouble(145);
			minData[4] = min = converter.getInt(153);
			maxData[4] = max = converter.getInt(157);
			spread = converter.getDouble(161);			
			is.read(fileName_chris, 169, 8);
			precision = converter.getInt(177);
			//read in dimSizes:
			bytes = new byte[maxDims];
			is.read(bytes,24+sizeInfo[2],4*maxDims);
			//lets assume we're only reading four dimensions
			converter = ByteBuffer.wrap(bytes);
			int dim1Size,dim2Size,dim3Size,dim4Size;
			data = new float[dim1Size=converter.getInt(0)][dim2Size=converter.getInt(4)]
					[dim3Size=converter.getInt(8)][dim4Size=converter.getInt(12)]; //construct the dataset
			bytes = new byte[noDims*(sizeInfo[3]+sizeInfo[5])];
			is.read(bytes,24+sizeInfo[2]+4*maxDims,noDims*(sizeInfo[3]+sizeInfo[5]));
			converter = ByteBuffer.wrap(bytes);
			for (int i = 0; i < 4; ++i){
				minData[i] = converter.getFloat(4 + i*(sizeInfo[3]+sizeInfo[5]));
				maxData[i] = converter.getFloat(8 + i*(sizeInfo[3]+sizeInfo[5]));
				byte[] axisName = new byte[8];
				converter.get(axisName, 16 + i*(sizeInfo[3]+sizeInfo[5]), 8);
				DimensionNames[i] = new String(axisName);
			}
			//now read the elements (assume they are floats):
			for (int i = 0; i < dim1Size; ++i)
				for (int j = 0; j < dim2Size; ++j)
					for (int k = 0; k < dim3Size; ++k){
						int flatIndexOf3D = i*dim1Size + j*dim2Size + k*dim3Size;
						bytes = new byte[sizeInfo[4]*dim4Size];
						is.read(bytes,
								24+sizeInfo[2]+4*maxDims+noDims*(sizeInfo[3]+sizeInfo[5])+flatIndexOf3D,
								sizeInfo[4]*dim4Size);
						converter = ByteBuffer.wrap(bytes);
						for (int f = 0; f < dim4Size; ++f)
							data[i][j][k][f] = converter.getFloat(f*dim4Size);
					}
			//done... now close
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
