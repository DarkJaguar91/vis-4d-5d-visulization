package GUI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

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
	
	public GraphScreen(){
		super("Graph Plotter");
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.setSize(800, 600);
		
		chartPanel = new ChartPanel(null);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(chartPanel);
		
		this.plotGraph();
		
		this.setVisible(true);
	}
	
	public void plotGraph(){
		XYSeriesCollection set = new XYSeriesCollection();

		// System.out.println(maps.size());

		XYSeries series = new XYSeries("2D Plot");
		
		int[] arrayIndexes = new int[4];
		for (int i = 0; i < 4; ++i)
			arrayIndexes[i] = -1;
		
		arrayIndexes[DataHolder.getFixedDimension(0)] = DataHolder.getFixedDimensionStep(0);
		arrayIndexes[DataHolder.getFixedDimension(1)] = DataHolder.getFixedDimensionStep(1);
		arrayIndexes[DataHolder.getFixedDimension(2)] = DataHolder.getFixedDimensionStep(2);
		
		int odd = -1;
		for (int i = 0; i < 4; ++i){
			odd = (arrayIndexes[i] == -1) ? i : -1;
			if (odd != -1)
				break;
		}
		
		float step = (DataHolder.data.getMaxData(odd) - DataHolder.data.getMinData(odd)) / DataHolder.data.getLength(odd);
		
		step = (float)Math.floor(step * 100f) / 100f;
		
		for (int i = (int)DataHolder.getMinFilter(odd); i <= (int)DataHolder.getMaxFilter(odd); ++i){
			float temp = DataHolder.data.getData()[arrayIndexes[0] == -1 ? i : arrayIndexes[0]][arrayIndexes[1] == -1 ? i : arrayIndexes[1]][arrayIndexes[2] == -1 ? i : arrayIndexes[2]][arrayIndexes[3] == -1 ? i : arrayIndexes[3]];
			if (temp >= DataHolder.getMinFilter(4) && temp <= DataHolder.getMaxFilter(4))
				series.add((i+1) * step, temp);
		}
		
		set.addSeries(series);

		final JFreeChart chart = ChartFactory.createXYLineChart("2D Plot", // chart title
				"" + DataHolder.data.getDimensionName(odd), // x axis label
				"Temperature", // y axis label
				set, // data
				PlotOrientation.VERTICAL, false, // include legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(Color.white);

		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);

		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		plot.setRenderer(renderer);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		chartPanel.setChart(chart);

		repaint();
	}
	
}
