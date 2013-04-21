package GUI;

import heatMap.Gradient;
import heatMap.HeatMap;

//import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Component;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
//import java.io.File;

import javax.swing.JFrame;
// import javax.swing.JInternalFrame;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
import javax.swing.JPanel;

//import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;

import Data.DataHolder;
//import Data.HeightMap;
//import Data.ActiveMap;
//import HM2DStuff.HM2DGraph;
//import HM2DStuff.DrawingPane;
//import HM2DStuff.ImageLoader;

// import Forms.DrawingPane;

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

	//
	//	private HeightMap dataHMap = ImageLoader.loadHeightMap(new File("res/heightmapdata.pmf"));
	//	private ActiveMap activeHMap = new ActiveMap(dataHMap);
	//	
	//	private DrawingPane label;

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
		/*
		this.setSize(650, 670); // 640x640 + padding

		chartPanel = new ChartPanel(null);
		heatMapPanel = new JPanel();
	//JFrame heatMapFrame = new JFrame(new HM2DGraph().getImage());
		JFrame heatMapFrame = new JFrame("");
//		heatMapPanel = new JPanel(this);
//		label = new DrawingPane(new JFrame(), this);

	//	heatMapFrame.add((Component)(new HM2DGraph().getImage()));

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(chartPanel);
		//this.getContentPane().add(heatMapFrame);

		this.plotGraph();

		// menu bar
		JMenuBar bar = new JMenuBar();
		final JMenuItem grid = new JMenuItem("Show Grid");
		bar.add(grid);
		grid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gridOn = !gridOn;
				grid.setText(gridOn ? "Hide Grid" : "Show Grid");
				plotGraph();
			}
		});
		final JMenuItem line = new JMenuItem("Scatter Plot");
		bar.add(line);
		line.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showLine = !showLine;
				line.setText(showLine ? "Scatter Plot" : "Line Graph");
				plotGraph();
			}
		});

		//	this.setJMenuBar(bar);

		HM2DGraph newGraph = new HM2DGraph();
		this.setVisible(true);
		 */
		//		 super("Heat Map Frame");
		float[][] data;
		//	        int[] dl = data.length;
		//	        for int j = 0; j< data[0][data.length-1];

		this.setSize(650, 670); // 640x640 + padding

		data = HeatMap.generatePyramidData(100);
		boolean useGraphicsYAxis = true;

		// you can use a pre-defined gradient:
		panel = new HeatMap(data, useGraphicsYAxis, Gradient.GRADIENT_BLUE_TO_RED);

		// or you can also make a custom gradient:
		Color[] gradientColors = new Color[]{Color.blue, Color.yellow, Color.red};
		Color[] customGradient = Gradient.createMultiGradient(gradientColors, 320);
		panel.updateGradient(customGradient);

		// set miscelaneous settings
		panel.setDrawLegend(true);

		panel.setTitle("Height (m)");
		panel.setDrawTitle(true);

		panel.setXAxisTitle("Dimension 1"); //TODO: get Dimension names from DataHolder
		panel.setDrawXAxisTitle(true);

		panel.setYAxisTitle("Dimension 2"); //TODO: get Dimension names from DataHolder
		panel.setDrawYAxisTitle(true);

		//panel.setCoordinateBounds(0.0, 1.0, 0.0, 1.0); // done in updateHMap()
		panel.setDrawXTicks(true);
		panel.setDrawYTicks(true);

		panel.setColorForeground(Color.black);
		panel.setColorBackground(Color.white);

		this.previousSelections = new int[6]; // last values for fast fail redraw; have fixed dimensions or limits changed?
		this.getContentPane().add(panel);
		this.updateHMap();
		this.setVisible(true);
	}

	public void updateHMap(){
		updateHMap(null,null);

	}
	public void updateHMap(double[][] newdata, double[] values)
	{

		boolean fastfailredraw = false; // don't redraw unless the limits have changed

		float incdata[][][][] = DataHolder.data.getData();
		// int[] fixedDims = DataHolder.fixedDimensions.clone();

		//Fixed Dimensions' dimensions: indices of fixed dimensions, low and high bounds
		int fd0 = DataHolder.getFixedDimension(0);
		int fd1 = DataHolder.getFixedDimension(1); 
		float fd0l = DataHolder.getMinFilter(fd0);
		float fd0h = DataHolder.getMaxFilter(fd0);
		float fd1l = DataHolder.getMinFilter(fd1);
		float fd1h = DataHolder.getMaxFilter(fd1);
		int ifd0l = (int)fd0l;
		int ifd0h = (int)fd0h;
		int ifd1l = (int)fd1l;
		int ifd1h = (int)fd1h;

		// Get current selections to fast-fail redraw if unnecessary
		int currentSelections[] = new int[6]; 
		currentSelections[0] = fd0;
		currentSelections[1] = fd0;
		currentSelections[2] = ifd0l;
		currentSelections[3] = ifd0h;
		currentSelections[4] = ifd1l;
		currentSelections[5] = ifd1h;

		for (int i = 0; i< previousSelections.length;i++){
			if (previousSelections[i] != currentSelections[i])
				fastfailredraw = true;
		}		
		if(!fastfailredraw) 
		{			
			return;
		}

		//update previousselections for redraw

		previousSelections = currentSelections;

		//create reduced array of data
		float[][] outData = new float[ifd1h-ifd1l][ifd0h-ifd0l]; //check if data in column [size] are lost
		//		System.out.println("Setting coords: ("+ifd0l+","+ifd0h+","+ifd1l+","+ifd1h+")");

		//		for (int arrj;arrj<;
		//TODO: optimize HMap to use global data

		//		System.out.println("Looping fd0 from "+ifd0l+":"+ifd0h+"; d0 length: "+outData[0].length);
		//		System.out.println("Looping fd1 from "+ifd1l+":"+ifd1h+"; d1 length: "+outData.length);
		for (int arrj=ifd1l;arrj<ifd1h;arrj++)
		{
			for (int arri=ifd0l;arri<ifd0h;arri++){
				outData[arrj-ifd1l][arri-ifd0l] = (float)(incdata[fd0][fd1][arrj-ifd1l][arri-ifd0l] * (Math.random()+0.5)); //TODO: remove random once draw is proven
			}
		}

		//		System.out.println("Setting coords: ("+fd0l+","+fd0h+","+fd1l+","+fd1h+")");
		panel.setCoordinateBounds(fd0l,fd0h,fd1l,fd1h);

		//panel.updateData(incdata[fd0][fd1], false);
		panel.updateData(outData, false); 
		

	}

}
