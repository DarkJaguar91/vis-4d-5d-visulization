package GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Data.DataHolder;
import Slider.JColorSlider;
import Slider.JRangeSlider;
import Slider.JRangeSliderGradient;

/**
 * 
 * @author Brandon James Talbot
 *	Slider menu JFrame
 */
public class SliderMenu extends JFrame implements ChangeListener, MouseListener{

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 5316995727166463675L;

	// globals
	public JColorSlider chooser3D, chooser2D, chooserGraph, step3D, step2D, stepGraph;
	public JRangeSlider range1, range2, range3, range4;
	public JRangeSliderGradient rangeHeat;
	JMenuItem loadFile, show3D, showGraph, Help, showHMap, hm2dGraph;
	JPanel heatColourRange;
	JFileChooser chooser;
	
	/**
	 * Constructor
	 */
	public SliderMenu() {
		super("Slider Menu");

		InitializeVariables();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Initialises the JFrame components and layout 
	 */
	private void InitializeVariables() {
		JLabel RangeandFilter = new JLabel("Fixate Dimension and Dimension Range sliders");
		JLabel heatSelector = new JLabel("Set Temperature Range");
		JLabel step3DL = new JLabel("Step for 3D Display");
		JLabel step2DL = new JLabel("Step for 2D Display");
		JLabel stepGraphL = new JLabel("Step for Graph");
		
		// init file chooser
		chooser = new JFileChooser();

		// Slider details
		{
			// initialise
			chooser3D = new JColorSlider(0, 3, 3 - DataHolder.fixedDimensions[0], Color.red);
			chooser2D = new JColorSlider(0, 3, 3 - DataHolder.fixedDimensions[1], Color.green);
			chooserGraph = new JColorSlider(0, 3, 3 - DataHolder.fixedDimensions[2], Color.blue);
			step3D = new JColorSlider(0, DataHolder.data.getLength(0)-1, 0, Color.red);
			step2D = new JColorSlider(0, DataHolder.data.getLength(1)-1, 0, Color.green);
			stepGraph = new JColorSlider(0, DataHolder.data.getLength(2)-1, 0, Color.blue);
			
			// add change listener
			chooser2D.addMouseListener(this);
			chooser3D.addMouseListener(this);
			chooserGraph.addMouseListener(this);
			step2D.addChangeListener(this);
			step3D.addChangeListener(this);
			stepGraph.addChangeListener(this);
			
			// set vertical (if needed)
			chooser3D.setOrientation(JSlider.VERTICAL);
			chooser2D.setOrientation(JSlider.VERTICAL);
			chooserGraph.setOrientation(JSlider.VERTICAL);
			
			// set labels
			chooser3D.setToolTipText("First filter (4D -> 3D)");
			chooser2D.setToolTipText("First filter (3D -> 2D)");
			chooserGraph.setToolTipText("Third Filer (2D -> Graph");
			step3D.setToolTipText("Set the current step to look at for the 3D Filter");
			step2D.setToolTipText("Set the current step to look at for the 2D Filter");
			stepGraph.setToolTipText("Set the current step to look at for the Graph Filter");

			// set Points
			chooser3D.setMajorTickSpacing(1);
			chooser3D.setPaintTicks(true);
			chooser2D.setMajorTickSpacing(1);
			chooser2D.setPaintTicks(true);
			chooserGraph.setMajorTickSpacing(1);
			chooserGraph.setPaintTicks(true);
			step3D.setMajorTickSpacing(1);
			step3D.setPaintTicks(true);
			step2D.setMajorTickSpacing(1);
			step2D.setPaintTicks(true);
			stepGraph.setMajorTickSpacing(1);
			stepGraph.setPaintTicks(true);
			
			// add labels
			Hashtable<Object, Object> labels = new Hashtable<>();
			labels.put(new Integer(3), new JLabel(DataHolder.data.getDimensionName(0)));
			labels.put(new Integer(2), new JLabel(DataHolder.data.getDimensionName(1)));
			labels.put(new Integer(1), new JLabel(DataHolder.data.getDimensionName(2)));
			labels.put(new Integer(0), new JLabel(DataHolder.data.getDimensionName(3)));
			chooserGraph.setLabelTable(labels);
			chooserGraph.setPaintLabels(true);
			step3D.setPaintLabels(true);
			setText(step3D);
			step2D.setPaintLabels(true);
			setText(step2D);
			stepGraph.setPaintLabels(true);
			setText(stepGraph);
		}

		// Range selector setup
		{
			// initialize
			range1 = new JRangeSlider(0, DataHolder.data.getLength(0)-1, 0, DataHolder.data.getLength(0)-1, Color.red);
			range2 = new JRangeSlider(0, DataHolder.data.getLength(1)-1, 0, DataHolder.data.getLength(1)-1, Color.green);
			range3 = new JRangeSlider(0, DataHolder.data.getLength(2)-1, 0, DataHolder.data.getLength(2)-1, Color.blue);
			range4 = new JRangeSlider(0, DataHolder.data.getLength(3)-1, 0, DataHolder.data.getLength(3)-1, Color.magenta);
			rangeHeat = new JRangeSliderGradient(0, 100, 0, 100);
			
			// set Labels
			range1.setToolTipText("Select the Range for Dimension: " + DataHolder.data.getDimensionName(0));
			range2.setToolTipText("Select the Range for Dimension: " + DataHolder.data.getDimensionName(1));
			range3.setToolTipText("Select the Range for Dimension: " + DataHolder.data.getDimensionName(2));
			range4.setToolTipText("Select the Range for Dimension: " + DataHolder.data.getDimensionName(3));
			rangeHeat.setToolTipText("Select the Range for the \"Temperature\"");
			
			// add change listener
			range1.addChangeListener(this);
			range2.addChangeListener(this);
			range3.addChangeListener(this);
			range4.addChangeListener(this);
			rangeHeat.addChangeListener(this);
			
			// set ticks
			range1.setMajorTickSpacing(1);
			range1.setPaintTicks(true);
			range2.setMajorTickSpacing(1);
			range2.setPaintTicks(true);
			range3.setMajorTickSpacing(1);
			range3.setPaintTicks(true);
			range4.setMajorTickSpacing(1);
			range4.setPaintTicks(true);
			rangeHeat.setMajorTickSpacing(1);
			rangeHeat.setPaintTicks(true);
			
			// setLabels
			range1.setPaintLabels(true);
			setText(range1);
			range2.setPaintLabels(true);
			setText(range2);
			range3.setPaintLabels(true);
			setText(range3);
			range4.setPaintLabels(true);
			setText(range4);
			rangeHeat.setPaintLabels(true);
			setText(rangeHeat);
		}
		
		// adding the Menu
		JMenuBar bar = new JMenuBar();
		loadFile = new JMenuItem("Load new File");
		show3D = new JMenuItem("Hide 3D Plotter");
		showGraph = new JMenuItem("Hide Graph Plotter");
		showHMap = new JMenuItem("Hide Heat Map");
		Help = new JMenuItem("Help");
		bar.add(loadFile);
		bar.add(show3D);
		bar.add(showHMap);
		bar.add(showGraph);
		bar.add(Help);
		
		loadFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION){
					DataHolder.data.loadFile(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		show3D.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DataHolder.plotter.setVisible(!DataHolder.plotter.isVisible());		
				refreshButtons();
			}
		});
		showGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DataHolder.plotterGraph.setVisible(!DataHolder.plotterGraph.isVisible());
				refreshButtons();		
			}
		});
		showHMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DataHolder.hm2dGraph.setVisible(!DataHolder.hm2dGraph.isVisible());
				refreshButtons();		
			}
		});
		Help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(DataHolder.sliderMenu, "Slider tooltips explain each sliders propertis.\n\n\nAbout:\nVisualizer for 4 Dimensional Array Data\n\nAuthors:\nBrandon James Talbot\nBenjamin Hugo\nHeinrich Strauss", "Help", JOptionPane.INFORMATION_MESSAGE);				
			}
		});
		
		this.setJMenuBar(bar);		
		
		// placement (Layout)
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(RangeandFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup() // top filters
								.addComponent(chooser3D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(chooser2D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(chooserGraph, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(layout.createParallelGroup()
										.addComponent(range1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(range2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(range3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(range4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										)
								)
								.addComponent(heatSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(rangeHeat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(step3DL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(step3D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(step2DL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(step2D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(stepGraphL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(stepGraph, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
						.addContainerGap()
				);

		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(RangeandFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(chooser3D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(chooser2D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(chooserGraph, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGroup(layout.createSequentialGroup()
									.addComponent(range1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(range2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(range3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(range4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									)
							)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(heatSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(rangeHeat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(step3DL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(step3D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(step2DL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(step2D, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(stepGraphL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(stepGraph, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap()
				);
		this.pack();
//		this.setSize(this.getWidth() * 2, this.getHeight());
	}

	/**
	 * refreshes the slider to the new data
	 */
	public void refreshSlidersWithNewData(){
		// set range choosers maximums incase its changed
		range1.setMaximum(DataHolder.data.getLength(0) - 1);
		range2.setMaximum(DataHolder.data.getLength(1) - 1);
		range3.setMaximum(DataHolder.data.getLength(2) - 1);
		range4.setMaximum(DataHolder.data.getLength(3) - 1);
		
		// set the max and min values for range sliders
		range1.setHighValue(range1.getMaximum());
		range2.setHighValue(range2.getMaximum());
		range3.setHighValue(range3.getMaximum());
		range4.setHighValue(range4.getMaximum());
		range1.setLowValue(0);
		range2.setLowValue(0);
		range3.setLowValue(0);
		range4.setLowValue(0);
		
		// set and check the sliders texts and the step sliders
		setText(range1);
		setText(range2);
		setText(range3);
		setText(range4);
		checkSlider(0, range1);
		checkSlider(1, range2);
		checkSlider(2, range3);
		checkSlider(3, range4);
		
		// check the ehat sliders text
		setText(rangeHeat);		
		
		// change axis names
		Hashtable<Object, Object> labels = new Hashtable<>();
		labels.put(new Integer(3), new JLabel(DataHolder.data.getDimensionName(0)));
		labels.put(new Integer(2), new JLabel(DataHolder.data.getDimensionName(1)));
		labels.put(new Integer(1), new JLabel(DataHolder.data.getDimensionName(2)));
		labels.put(new Integer(0), new JLabel(DataHolder.data.getDimensionName(3)));
		chooserGraph.setLabelTable(labels);
	}
	
	/**
	 * Tells the buttons to show the correct data (hide or show)
	 */
	public void refreshButtons(){
		// 3d
		show3D.setText(DataHolder.plotter.isVisible() ? "Hide 3D Plotter" : "Show 3D Plotter");
		// graph		
		showGraph.setText(DataHolder.plotterGraph.isVisible() ? "Hide Graph Plotter" : "Show Graph Plotter");
		// heatmap
		//showHMap.setText(DataHolder.hm2dGraph.isVisible() ? "Hide Heat Map" : "Show Heat Map");
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// Range 1
		if (e.getSource().equals(range1)){
			checkSlider(0, range1);
			setText(range1);
		}
		// Range 2
		else if (e.getSource().equals(range2)){
			checkSlider(1, range2);
			setText(range2);
		}
		// Range 3
		else if (e.getSource().equals(range3)){
			checkSlider(2, range3);
			setText(range3);
		}
		// Range 4
		else if (e.getSource().equals(range4)){
			checkSlider(3, range4);
			setText(range4);
		}
		// heat
		else if (e.getSource().equals(rangeHeat)){
			setText(rangeHeat);
		}
		// step 3D
		else if (e.getSource().equals(step3D)){
			setText(step3D);
		}
		// step2D
		else if (e.getSource().equals(step2D)){
			setText(step2D);
		}
		// stepGraph
		else if (e.getSource().equals(stepGraph)){
			setText(stepGraph);
		}
		
		DataHolder.updatePlotter();
	}
	
	/**
	 * Sets the text for the specific slider
	 * @param slider The slider to set the text
	 */
	private void setText(JSlider slider){		
		int dim = 0;
		if (slider.equals(range1))
			dim = 0;
		else if (slider.equals(range2))
			dim = 1;
		else if (slider.equals(range3))
			dim = 2;
		else if (slider.equals(range4))
			dim = 3;
		else if (slider.equals(rangeHeat))
			dim = 4;
		else {
			if (slider.equals(chooser3D)){
				dim = DataHolder.fixedDimensions[0];
			}
			else if (slider.equals(chooser2D)){
				dim = DataHolder.fixedDimensions[1];
			}
			else if(slider.equals(chooserGraph)){
				dim = DataHolder.fixedDimensions[2];
			}
		}
		
		float min = DataHolder.data.getMinData(dim);
		float max = DataHolder.data.getMaxData(dim);
		
		if (slider instanceof JRangeSlider || slider instanceof JRangeSliderGradient){
			JRangeSlider s = (JRangeSlider)slider;
			Hashtable<Object, Object> labels = new Hashtable<>();
			float lowVal = min + (max - min) * (float)((s.getLowValue() - s.getMinimum()) / (float)(s.getMaximum() - (float)s.getMinimum()));
			float highVal = min + (max - min) * (float)( (s.getHighValue() - s.getMinimum()) / (float)(s.getMaximum() - (float)s.getMinimum()));
			labels.put(new Integer(s.getLowValue()), new JLabel("" + lowVal));
			labels.put(new Integer(s.getHighValue()), new JLabel("" + highVal));			
			labels.put(new Integer(s.getMinimum()), new JLabel("" + min));
			labels.put(new Integer(s.getMaximum()), new JLabel("" + max));
			s.setLabelTable(labels);
		}
		else{
			Hashtable<Object, Object> labels = new Hashtable<>();

			float newmin = min + (max - min) * (float)( (slider.getMinimum() - 0) / (float)(DataHolder.data.getLength(dim)-1));
			float newmax = min + (max - min) * (float)( (slider.getMaximum() - 0) / (float)(DataHolder.data.getLength(dim)-1));

			float highVal = newmin + (newmax - newmin) * (float)( (slider.getValue() - slider.getMinimum()) / (float)(slider.getMaximum() - (float)slider.getMinimum()));
			labels.put(new Integer(slider.getValue()), new JLabel("" + highVal));
			labels.put(new Integer(slider.getMinimum()), new JLabel("" + newmin));
			labels.put(new Integer(slider.getMaximum()), new JLabel("" + newmax));
			slider.setLabelTable(labels);
		}
	}
	
	/**
	 * Checks the slider to tell the steps what their minimum or maximum value should be
	 * @param num The number of the dimension to use (0 - 3)
	 * @param slider the slider that determines the range it should have
	 */
	private void checkSlider(int num, JRangeSlider slider){
		if (DataHolder.fixedDimensions[0] == num){
			step3D.setMinimum(slider.getLowValue());
			step3D.setMaximum(slider.getHighValue());
		
			step3D.setValue(slider.getLowValue());
			
			setText(step3D);
		}
		else if (DataHolder.fixedDimensions[1] == num){
			step2D.setMinimum(slider.getLowValue());
			step2D.setMaximum(slider.getHighValue());
			
			step2D.setValue(slider.getLowValue());
			
			// set labels
			setText(step2D);
		}
		else if (DataHolder.fixedDimensions[2] == num){
			stepGraph.setMinimum(slider.getLowValue());
			stepGraph.setMaximum(slider.getHighValue());
			
			stepGraph.setValue(slider.getLowValue());
			
			setText(stepGraph);
		}
	}

	/**
	 * Checks the colour for the dimension chooser and the step for the chooser
	 */
	private void checkColor(){
		int dim;
		// 3D
		dim = 3 - chooser3D.getValue();
		switch (dim){
		case 0 :
			chooser3D.colour = range1.colour;
			break;
		case 1 :
			chooser3D.colour = range2.colour;
			break;
		case 2 :
			chooser3D.colour = range3.colour;
			break;
		case 3 :
			chooser3D.colour = range4.colour;
			break;
		}
		step3D.colour = chooser3D.colour;

		// 2D
		dim = 3 - chooser2D.getValue();
		switch (dim){
		case 0 :
			chooser2D.colour = range1.colour;
			break;
		case 1 :
			chooser2D.colour = range2.colour;
			break;
		case 2 :
			chooser2D.colour = range3.colour;
			break;
		case 3 :
			chooser2D.colour = range4.colour;
			break;
		}
		step2D.colour = chooser2D.colour;

		// Graph
		dim = 3 - chooserGraph.getValue();
		switch (dim){
		case 0 :
			chooserGraph.colour = range1.colour;
			break;
		case 1 :
			chooserGraph.colour = range2.colour;
			break;
		case 2 :
			chooserGraph.colour = range3.colour;
			break;
		case 3 :
			chooserGraph.colour = range4.colour;
			break;
		}
		stepGraph.colour = chooserGraph.colour;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// chooser 3D
		if (e.getSource().equals(chooser3D)){
			DataHolder.fixedDimensions[0] = 3 - chooser3D.getValue();
		}
		// chooser 2D
		if (e.getSource().equals(chooser2D)){
			if ((chooser2D.getValue()) == chooser3D.getValue()){
				chooser2D.setValue(3 - DataHolder.fixedDimensions[1]);
			}
			else {
				DataHolder.fixedDimensions[1] = (3 - chooser2D.getValue());
			}
		}
		// chooser graph
		if (e.getSource().equals(chooserGraph)){
			if (chooser3D.getValue() == chooserGraph.getValue() || chooser2D.getValue() == chooserGraph.getValue()) {
				chooserGraph.setValue(3 - DataHolder.fixedDimensions[2]);
			}else {
				DataHolder.fixedDimensions[2] = 3 - chooserGraph.getValue();
			}
		}
		
		// array for checking
		ArrayList<Integer> temp = new ArrayList<>();
		for (int i = 0; i < 4; ++i)
			if (i != DataHolder.fixedDimensions[0])
				temp.add(i);
		if (!temp.contains(DataHolder.fixedDimensions[1])){
			DataHolder.fixedDimensions[1] = temp.get(0);
			chooser2D.setValue(3 - DataHolder.fixedDimensions[1]);
		}
		
		temp.clear();
		for (int i = 0; i < 4; ++i)
			if (i != DataHolder.fixedDimensions[0] && i != DataHolder.fixedDimensions[1])
				temp.add(i);
		if (!temp.contains(DataHolder.fixedDimensions[2])){
			DataHolder.fixedDimensions[2] = temp.get(0);
			chooserGraph.setValue(3 - DataHolder.fixedDimensions[2]);
		}

		// check all steps
		checkSlider(0, range1);
		checkSlider(1, range2);
		checkSlider(2, range3);
		checkSlider(3, range4);
		
		DataHolder.updatePlotter();
		
		checkColor();
	}
	
}
