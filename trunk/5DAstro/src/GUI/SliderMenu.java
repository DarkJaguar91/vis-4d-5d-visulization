package GUI;

import java.awt.Component;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;

import Data.DataHolder;

import com.jidesoft.swing.RangeSlider;

public class SliderMenu extends JFrame {

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 5316995727166463675L;

	// globals
	protected JSlider chooser3D, chooser2D, chooserGraph;
	protected RangeSlider range1, range2, range3, range4, rangeHeat;

	protected DataHolder dataHolder;

	public SliderMenu(DataHolder holder) {
		dataHolder = holder;

		InitializeVariables();
	}

	private void InitializeVariables() {
		JLabel RangeandFilter = new JLabel("Filter and Range sliders");
		JLabel heatSelector = new JLabel("Filter down Temperature");

		// Slider details
		{
			// initialise
			chooser3D = new JSlider(0, 4, 0);
			chooser2D = new JSlider(0, 4, 1);
			chooserGraph = new JSlider(0, 4, 2);

			// set labels
			chooser3D.setToolTipText("First filter (4D -> 3D)");
			chooser2D.setToolTipText("First filter (3D -> 2D)");
			chooserGraph.setToolTipText("Third Filer (2D -> Graph");

			// set Points
			chooserGraph.setMajorTickSpacing(1);
			chooserGraph.setPaintTicks(true);

			// add labels
			Hashtable<Object, Object> labels = new Hashtable<>();
			labels.put(new Integer(0), new JLabel("data 1"));
			labels.put(new Integer(1), new JLabel("data 2"));
			labels.put(new Integer(2), new JLabel("data 3"));
			labels.put(new Integer(3), new JLabel("data 4"));
			chooserGraph.setLabelTable(labels);
		}

		// Range selector setup
		{
			
		}
	}

}
