package Slider;

import java.awt.Color;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

public class JColorSlider extends JSlider {

	public Color colour;
	
	/**
	 * generated serial ID
	 */
	private static final long serialVersionUID = -6992325961672899138L;
	
	public JColorSlider(Color c) {
		super();
		colour = c;
	}



	public JColorSlider(BoundedRangeModel brm, Color c) {
		super(brm);

		colour = c;
	}



	public JColorSlider(int orientation, int min, int max, int value, Color c) {
		super(orientation, min, max, value);

		colour = c;
	}



	public JColorSlider(int min, int max, int value, Color c) {
		super(min, max, value);

		colour = c;
	}



	public JColorSlider(int min, int max, Color c) {
		super(min, max);

		colour = c;
	}



	public JColorSlider(int orientation, Color c) {
		super(orientation);
		colour = c;
	}



	@Override
    public void updateUI() {
    	setUI(new JColorSliderUI(this));
    }
	
}
