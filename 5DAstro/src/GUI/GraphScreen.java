package GUI;

import heatMap.Gradient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
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
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Data.DataHolder;

/**
 * 
 * @author Brandon James Talbot
 * 
 *         This is the JFrame that contains all methods and components to draw
 *         the Graph for the Pulsar plotter
 */
public class GraphScreen extends JFrame {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -6544453672480127689L;

	// globals
	ChartPanel chartPanel;
	boolean gridOn = true;
	boolean showLine = true;

	/**
	 * Constructor initialises the chart panel Initialises sizes and then
	 * creates initial graph
	 */
	public GraphScreen() {
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
				((JFrame) arg0.getSource()).setVisible(false);
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
		final JMenuItem grid = new JMenuItem("Hide Grid");
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
//		bar.add(line);
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
	public void plotGraph() {
		// create a set
		final XYSeriesCollection set = new XYSeriesCollection();

		// create a series for the set
		final XYSeries series = new XYSeries("2D Plot");

		// create array of indexes (allows for fixed dimension checks)
		int[] arrayIndexes = new int[4];
		for (int i = 0; i < 4; ++i)
			arrayIndexes[i] = -1;

		// set the indexes array for each "Fixed" dimension to its specified
		// step
		arrayIndexes[DataHolder.getFixedDimension(0)] = DataHolder
				.getFixedDimensionStep(0);
		arrayIndexes[DataHolder.getFixedDimension(1)] = DataHolder
				.getFixedDimensionStep(1);
		arrayIndexes[DataHolder.getFixedDimension(2)] = DataHolder
				.getFixedDimensionStep(2);

		// find the dimension number that isnt fixed
		int odd = -1;
		for (int i = 0; i < 4; ++i) {
			odd = (arrayIndexes[i] == odd) ? i : -1;
			if (odd != -1)
				break;
		}

		// calculate the step in non-fixed dimension
		float max = DataHolder.data.getMaxData(odd);
		float min = DataHolder.data.getMinData(odd);

		float dataMax = DataHolder.data.getLength(odd) - 1;

		// add each point into the series
		for (int i = (int) DataHolder.getMinFilter(odd); i <= (int) DataHolder
				.getMaxFilter(odd); ++i) {
			// get the data from the array at the point
			float temp = DataHolder.data.getData()[arrayIndexes[0] == -1 ? i
					: arrayIndexes[0]][arrayIndexes[1] == -1 ? i
					: arrayIndexes[1]][arrayIndexes[2] == -1 ? i
					: arrayIndexes[2]][arrayIndexes[3] == -1 ? i
					: arrayIndexes[3]];
			if (temp >= DataHolder.getMinFilter(4)
					&& temp <= DataHolder.getMaxFilter(4)) // if within
															// specified temp
															// range
				series.add(min + (max - min)
						* (float) (((i)) / (float) (dataMax)), temp);
		}

		set.addSeries(series); // add the series to the set

		// create a chart from the series
		final JFreeChart chart = ChartFactory.createXYLineChart("2D Plot", // chart
																			// title
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

		plot.setDomainGridlinePaint(gridOn ? Color.gray : Color.white);// makes
																		// it
																		// invis
		plot.setRangeGridlinePaint(gridOn ? Color.gray : Color.white); // makes
																		// it
																		// invis

		// create renderer
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() {

			private static final long serialVersionUID = 7198588657058620063L;

			@Override
			public Paint getItemPaint(int row, int col) {
				Paint cpaint = getItemColor(row, col);
				if (cpaint == null) {
					cpaint = super.getItemPaint(row, col);
				}
				return cpaint;
			}

			public Color getItemColor(int row, int col) {
				double y = set.getYValue(row, col);

				float val = (float) y;
				float min = DataHolder.data.getMinData(4);
				float max = DataHolder.data.getMaxData(4);

				int colVal = (int) ((319) * (float) ((val - min) / (float) (max - min)));
				Color[] gradientColors = new Color[] { Color.blue,
						Color.yellow, Color.red };
				Color[] customGradient = Gradient.createMultiGradient(
						gradientColors, 320);

				return customGradient[colVal];
			}
		};
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesLinesVisible(0, false);

		// SET TOOLTIP FOR RENDERER
		renderer.setBaseToolTipGenerator(new XYToolTipGenerator() {
			public String generateToolTip(XYDataset dataset, int series,
					int item) {
				StringBuffer sb = new StringBuffer();
				Number x = dataset.getX(series, item);
				Number y = dataset.getY(series, item);
				sb.append("(" + x + "," + y + ")");
				return sb.toString();
			}
		});
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
