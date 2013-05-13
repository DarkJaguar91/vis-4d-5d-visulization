package GUI;

import heatMap.Gradient;
import heatMap.HeatMap;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

import Data.DataHolder;


/**
 * 
 * @author Brandon James Talbot, Benjamin Hugo, Heinrich Strauss
 *
 * This is the JFrame that contains all methods and components to draw the Graph for the Pulsar plotter
 */
public class HM2DPlot extends JFrame{

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -6544453672480127689L;

	// globals
	ChartPanel chartPanel;
	JPanel heatMapPanel;
	boolean gridOn = false;
	boolean showLine = true;
	HeatMap panel;

	int[] previousSelections;

	/**
	 * Constructor
	 * initialises the chart panel
	 * Initialises sizes and then creates initial graph
	 */
	public HM2DPlot(){
		super("Heat-Map Plotter");

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				((JFrame)arg0.getSource()).setVisible(false);
				DataHolder.refreshButtons();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});
		
		float[][] data;
		//	        int[] dl = data.length;
		//	        for int j = 0; j< data[0][data.length-1];

		this.setSize(692, 685); // 640x640 + padding; +30 for legend indices; +12 +15

		//data = HeatMap.generatePyramidData(100);
		boolean useGraphicsYAxis = true;
		data = new float[1][1]; 
		// you can use a pre-defined gradient: 
		Color[] gradientColors = new Color[]{Color.blue, Color.yellow, Color.red};
		Color[] customGradient = Gradient.createMultiGradient(gradientColors, 64); //was 320; TO DO: HM) Lo: Why is this limit hardcoded?
		panel = new HeatMap(data, useGraphicsYAxis, customGradient);

		// set miscelaneous settings
		panel.setDrawLegend(true);

		panel.setTitle("Temperature");
		panel.setDrawTitle(true);

		panel.setXAxisTitle("Dimension 1"); //DONE: get Dimension names from DataHolder
		panel.setDrawXAxisTitle(true);

		panel.setYAxisTitle("Dimension 2"); //DONE: get Dimension names from DataHolder
		panel.setDrawYAxisTitle(true);

		//panel.setCoordinateBounds(0.0, 1.0, 0.0, 1.0); // done in updateHMap()
		panel.setDrawXTicks(true);
		panel.setDrawYTicks(true);

		panel.setColorForeground(Color.black);
		panel.setColorBackground(Color.white);

		this.getContentPane().add(panel);
//		this.setJMenuBar(new JMenuBar.add(new JMenuItem("Exit")));
		JMenuBar Exit = new JMenuBar(); Exit.add(new JMenuItem("Exit")); //TO.DO.DEPR: HM) Lo: Add JMenu Handlers 
//		this.setJMenuBar(Exit);
		this.updateHMap();
		this.setVisible(true);
	}

	public void updateHMap(){
		int arraypositions [] = {-1, -1, -1, -1};
		
		arraypositions[DataHolder.getFixedDimension(0)] = DataHolder.getFixedDimensionStep(0);
		arraypositions[DataHolder.getFixedDimension(1)] = DataHolder.getFixedDimensionStep(1);
		
		int out1 = -1, out2 = -1;
		for (int i = 0; i < 4; ++i){
			if (i != DataHolder.getFixedDimension(0) && i != DataHolder.getFixedDimension(1)){
				if (out1 == -1){
					out1 = i;
					arraypositions[i] = -1;
				}
				else{
					out2 = i;
					arraypositions[i] = -2;
				}
			}
		}
		
		//create reduced array of data
		int size1 = ((int)DataHolder.getMaxFilter(out1) - (int)DataHolder.getMinFilter(out1));
		int size2 = ((int)DataHolder.getMaxFilter(out2) - (int)DataHolder.getMinFilter(out2));
		float[][] outData = new float[size2 + 1][size1 + 1];
		
		for (int i = (int)DataHolder.getMinFilter(out1); i <= (int)DataHolder.getMaxFilter(out1); ++i)
		{
			for (int y = (int)DataHolder.getMinFilter(out2); y <= (int)DataHolder.getMaxFilter(out2); ++y){
				outData[y - (int)DataHolder.getMinFilter(out2)][i - (int)DataHolder.getMinFilter(out1)] = (float)(DataHolder.data.getData()[(arraypositions[0] == -1) ? i : ((arraypositions[0] == -2) ? y : arraypositions[0])]
						[(arraypositions[1] == -1) ? i : ((arraypositions[1] == -2) ? y : arraypositions[1])]
								[(arraypositions[2] == -1) ? i : ((arraypositions[2] == -2) ? y : arraypositions[2])]
										[(arraypositions[3] == -1) ? i : ((arraypositions[3] == -2) ? y : arraypositions[3])]);
			}
		}

		float minx = DataHolder.data.getMinData(out1) + (DataHolder.data.getMaxData(out1) - DataHolder.data.getMinData(out1)) * (DataHolder.getMinFilter(out1) / (float)(DataHolder.data.getLength(out1)-1));
		float maxx = DataHolder.data.getMinData(out1) + (DataHolder.data.getMaxData(out1) - DataHolder.data.getMinData(out1)) * (DataHolder.getMaxFilter(out1) / (float)(DataHolder.data.getLength(out1)-1));
		float miny = DataHolder.data.getMinData(out2) + (DataHolder.data.getMaxData(out2) - DataHolder.data.getMinData(out2)) * (DataHolder.getMinFilter(out2) / (float)(DataHolder.data.getLength(out2)-1));
		float maxy = DataHolder.data.getMinData(out2) + (DataHolder.data.getMaxData(out2) - DataHolder.data.getMinData(out2)) * (DataHolder.getMaxFilter(out2) / (float)(DataHolder.data.getLength(out2)-1));
		
		panel.setCoordinateBounds(miny, maxy, minx, maxx);
	
		panel.setYAxisTitle(DataHolder.data.getDimensionName(out1));
		panel.setXAxisTitle(DataHolder.data.getDimensionName(out2));
		
		panel.updateData(outData, false);
	}

}