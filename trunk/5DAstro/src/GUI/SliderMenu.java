package GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Data.DataHolder;

import com.jidesoft.swing.RangeSlider;

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
	public JSlider chooser3D, chooser2D, chooserGraph, step3D, step2D, stepGraph;
	public RangeSlider range1, range2, range3, range4, rangeHeat;
	private JMenuItem loadFile;
	
	// data holder global (for method calls)

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
		JLabel RangeandFilter = new JLabel("Filter and Range sliders");
		JLabel heatSelector = new JLabel("Filter down Temperature");
		JLabel step3DL = new JLabel("Step for 3D Display");
		JLabel step2DL = new JLabel("Step for 2D Display");
		JLabel stepGraphL = new JLabel("Step for Graph");

		// Slider details
		{
			// initialise
			chooser3D = new JSlider(0, 3, 3 - DataHolder.fixedDimensions[0]);
			chooser2D = new JSlider(0, 3, 3 - DataHolder.fixedDimensions[1]);
			chooserGraph = new JSlider(0, 3, 3 - DataHolder.fixedDimensions[2]);
			step3D = new JSlider(0, DataHolder.data.getLength(0) - 1, 0);
			step2D = new JSlider(0, DataHolder.data.getLength(1) - 1, 0);
			stepGraph = new JSlider(0, DataHolder.data.getLength(2) - 1, 0);
			
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
			range1 = new RangeSlider(0, DataHolder.data.getLength(0)-1, 0, DataHolder.data.getLength(0)-1);
			range2 = new RangeSlider(0, DataHolder.data.getLength(1)-1, 0, DataHolder.data.getLength(1)-1);
			range3 = new RangeSlider(0, DataHolder.data.getLength(2)-1, 0, DataHolder.data.getLength(2)-1);
			range4 = new RangeSlider(0, DataHolder.data.getLength(3)-1, 0, DataHolder.data.getLength(3)-1);
			rangeHeat = new RangeSlider(0, 100, 0, 100);
			
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
		bar.add(loadFile);
		
		loadFile.addMouseListener(this);
		
		this.setJMenuBar(bar);		
		
		// placement (Layout)
		setSize(800, 720);
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
		float step = 0;
		if (dim != 4)
			step = (max - min) / (DataHolder.data.getLength(dim));
		else
			step = (max - min) / 100;
		
		step = (float)Math.floor(step * 100f) / 100f;
		
		if (slider instanceof RangeSlider){
			RangeSlider s = (RangeSlider)slider;
			Hashtable<Object, Object> labels = new Hashtable<>();
			labels.put(new Integer(s.getLowValue()), new JLabel("" + (s.getLowValue() + 1) *  step));
			labels.put(new Integer(s.getHighValue()), new JLabel("" + (s.getHighValue() + 1) * step));
			labels.put(new Integer(s.getMinimum()), new JLabel("" + (s.getMinimum()) * step));
			labels.put(new Integer(s.getMaximum()), new JLabel("" + (s.getMaximum())* step));
			s.setLabelTable(labels);
		}
		else{
			Hashtable<Object, Object> labels = new Hashtable<>();
			labels.put(new Integer(slider.getValue()), new JLabel("" + (slider.getValue() + 1) * step));
			labels.put(new Integer(slider.getMinimum()), new JLabel("" + (slider.getMinimum()) * step));
			labels.put(new Integer(slider.getMaximum()), new JLabel("" + (slider.getMaximum()) * step));
			slider.setLabelTable(labels);
		}
	}
	
	private void checkSlider(int num, RangeSlider slider){
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
	}
	
}
