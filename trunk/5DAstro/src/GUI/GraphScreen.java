package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Data.DataHolder;

/**
 * 
 * @author Brandon James Talbot
 *
 * This is the JFrame that contains all methods and components to draw the Graph for the Pulsar plotter
 */
public class GraphScreen extends JFrame{
	
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -6544453672480127689L;

	// globals
	ChartPanel chartPanel;
	boolean gridOn = false;
	boolean showLine = true;
	
	/**
	 * Constructor
	 * initialises the chart panel
	 * Initialises sizes and then creates initial graph
	 */
	public GraphScreen(){
		super("Graph Plotter");
		
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
		
		this.setSize(800, 600);
		
		chartPanel = new ChartPanel(null);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(chartPanel);
		
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
		
		this.setJMenuBar(bar);
		
		this.setVisible(true);
	}
	
	/**
	 * Plots a graph for the given data in the static class DataHolder
	 */
	public void plotGraph(){
		// create a set
		XYSeriesCollection set = new XYSeriesCollection();

		// create a series for the set
		XYSeries series = new XYSeries("2D Plot");
		
		// create array of indexes (allows for fixed dimension checks)
		int[] arrayIndexes = new int[4];
		for (int i = 0; i < 4; ++i)
			arrayIndexes[i] = -1;
		
		// set the indexes array for each "Fixed" dimension to its specified step
		arrayIndexes[DataHolder.getFixedDimension(0)] = DataHolder.getFixedDimensionStep(0);
		arrayIndexes[DataHolder.getFixedDimension(1)] = DataHolder.getFixedDimensionStep(1);
		arrayIndexes[DataHolder.getFixedDimension(2)] = DataHolder.getFixedDimensionStep(2);
		
		// find the dimension number that isnt fixed
		int odd = -1;
		for (int i = 0; i < 4; ++i){
			odd = (arrayIndexes[i] == odd) ? i : -1;
			if (odd != -1)
				break;
		}
		
		// calculate the step in non-fixed dimension
		float step = (DataHolder.data.getMaxData(odd) - DataHolder.data.getMinData(odd)) / DataHolder.data.getLength(odd);
		step = (float)Math.floor(step * 100f) / 100f;
		
		// add each point into the series
		for (int i = (int)DataHolder.getMinFilter(odd); i <= (int)DataHolder.getMaxFilter(odd); ++i){
			// get the data from the array at the point
			float temp = DataHolder.data.getData()[arrayIndexes[0] == -1 ? i : arrayIndexes[0]][arrayIndexes[1] == -1 ? i : arrayIndexes[1]][arrayIndexes[2] == -1 ? i : arrayIndexes[2]][arrayIndexes[3] == -1 ? i : arrayIndexes[3]];
			if (temp >= DataHolder.getMinFilter(4) && temp <= DataHolder.getMaxFilter(4)) // if within specified temp range
				series.add((i+1) * step, temp);
		}
		
		set.addSeries(series); // add the series to the set

		// create a chart from the series
		final JFreeChart chart = ChartFactory.createXYLineChart("2D Plot", // chart title
				"" + DataHolder.data.getDimensionName(odd), // x axis label
				"Temperature", // y axis label
				set, // data
				PlotOrientation.VERTICAL, false, // include legend
				true, // tooltips
				false // urls
				);

		// set the charts background
		chart.setBackgroundPaint(Color.white);

		// set the plotters background
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);

		plot.setDomainGridlinePaint(gridOn ? Color.gray : Color.white);// makes it invis
		plot.setRangeGridlinePaint(gridOn ? Color.gray : Color.white); // makes it invis

		// create renderer
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, showLine);
		renderer.setSeriesShapesVisible(1, true);
		plot.setRenderer(renderer);

		// draw the ticks
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// set the chart panes chart
		chartPanel.setChart(chart);

		// repaint the frame
		repaint();
	}
	
}
