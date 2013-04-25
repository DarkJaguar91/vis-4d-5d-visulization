package Slider;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;

/**
 *
 */
public class JRangeSlider extends JSlider {

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 9164340019538138990L;
	public static final String CLIENT_PROPERTY_MOUSE_POSITION = "RangeSlider.mousePosition";
    public static final String CLIENT_PROPERTY_ADJUST_ACTION = "RangeSlider.adjustAction";
    public static final String PROPERTY_LOW_VALUE = "lowValue";
    public static final String PROPERTY_HIGH_VALUE = "highValue";
	

    public JRangeSlider() {
    }

    public JRangeSlider(int orientation) {
        super(orientation);
    }

    public JRangeSlider(int min, int max) {
        super(min, max);
    }

    public JRangeSlider(int min, int max, int low, int high) {
        super(new DefaultBoundedRangeModel(low, high - low,
                min, max));
        getModel().removeChangeListener(changeListener);
        getModel().removeChangeListener(changeListener); // work around a JSlider bug which registers two change listeners with this constructor.
        getModel().addChangeListener(changeListener);
    }
    
    @Override
    public void updateUI() {
    	setUI(new JSliderUI(this));
    }

    public int getLowValue() {
        return getModel().getValue();
    }


    public int getHighValue() {
        return getModel().getValue() + getModel().getExtent();
    }

    public boolean contains(int value) {
        return (value >= getLowValue() && value <= getHighValue());
    }

    @Override
    public void setValue(int value) {
        Object clientProperty = getClientProperty(CLIENT_PROPERTY_MOUSE_POSITION);
        if (clientProperty != null) {
            if (Boolean.TRUE.equals(clientProperty)) {
                setLowValue(value);
            }
            else {
                setHighValue(value);
            }
        }
        else {
            setLowValue(value);
        }
    }


    public void setLowValue(int lowValue) {
        int old = getLowValue();
        int high;
        if ((lowValue + getModel().getExtent()) > getMaximum()) {
            high = getMaximum();
        }
        else {
            high = getHighValue();
        }
        int extent = high - lowValue;

        Object property = getClientProperty(CLIENT_PROPERTY_ADJUST_ACTION);
        getModel().setRangeProperties(lowValue, extent,
                getMinimum(), getMaximum(), property == null || (!property.equals("scrollByBlock") && !property.equals("scrollByUnit")));
        firePropertyChange(PROPERTY_LOW_VALUE, old, getLowValue());

    }

    public void setHighValue(int highValue) {
        int old = getHighValue();
        getModel().setExtent(highValue - getLowValue());
        firePropertyChange(PROPERTY_HIGH_VALUE, old, getHighValue());
    }
}
