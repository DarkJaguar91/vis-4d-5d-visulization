package Slider;

import java.awt.Color;


/**
 *
 */
public class JRangeSliderGradient extends JRangeSlider {
    /**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 7040743632076762163L;

	public JRangeSliderGradient() {
		super();
    }

    public JRangeSliderGradient(int orientation) {
        super(orientation);
    }

    public JRangeSliderGradient(int min, int max) {
        super(min, max);
    }

    public JRangeSliderGradient(int min, int max, int low, int high) {
        super(min, max, low, high, Color.black);
    }
	
	@Override
    public void updateUI() {
    	setUI(new JSliderUIGradient(this));
    }
}
