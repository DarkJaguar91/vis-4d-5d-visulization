package Slider;
import heatMap.Gradient;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * 
 */
class JSliderUIGradient extends BasicSliderUI {

	Color[] customGradient;
	
	public JSliderUIGradient(JSlider slider) {
		super(slider);
		JRangeSliderGradient s = (JRangeSliderGradient)slider;
		int numTicks = s.getMaximum() - s.getMinimum();
		Color[] gradientColors = new Color[]{Color.blue, Color.yellow, Color.red};
		customGradient = Gradient.createMultiGradient(gradientColors, numTicks);
	}
	
	public static ComponentUI createUI(JComponent slider) {
		return new JSliderUIGradient((JSlider) slider);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
	}	

	@Override
	public void paintTrack(Graphics g) {
		Rectangle trackBounds = (Rectangle)trackRect.clone();
		
		JRangeSliderGradient s = (JRangeSliderGradient)slider;
		
		int numTicks = s.getMaximum() - s.getMinimum();
		// check if colours have changed
		if (numTicks != customGradient.length){
			Color[] gradientColors = new Color[]{Color.blue, Color.yellow, Color.red};
			customGradient = Gradient.createMultiGradient(gradientColors, numTicks);
		}
		int cy = (trackBounds.height / 2) - 2;
        int cw = trackBounds.width;
        
        float uw = cw / (float)numTicks;
        
        g.translate(trackBounds.x, trackBounds.y + cy);
		
        for (int i = 0; i < numTicks; ++i){
        	g.setColor(customGradient[i]);
        	g.fillRect((int)(i * uw), 0, (int)Math.ceil(uw), 3);
        }
        
        g.translate(-trackBounds.x, -(trackBounds.y + cy));
	}

	int knob = 0;
	
	@Override
	public void paintThumb(Graphics g) {
		knob = 0;
		paintActualThumb(g);
		Point p = adjustThumbForHighValue();
		knob = 1;
		paintActualThumb(g);
		restoreThumbForLowValue(p);
	}
	
	public void paintActualThumb(Graphics g){
		Rectangle knobBounds = thumbRect;
        int w = knobBounds.width;
        int h = knobBounds.height;

        g.translate(knobBounds.x, knobBounds.y);

        JRangeSliderGradient s = (JRangeSliderGradient)slider;
        float val = knob == 0 ? s.getLowValue() : s.getHighValue();
        float min = s.getMinimum();
        float max = s.getMaximum();
        
        int colVal = (int)((customGradient.length-1) * (float)((val- min) / (float)(max - min)));
        
        if ( slider.isEnabled() ) {
            g.setColor(customGradient[colVal]);
        }
        else {
            g.setColor(slider.getBackground().darker());
        }

        Color highlightColor = Color.gray, shadowColor = Color.black;
        
        Boolean paintThumbArrowShape =
            (Boolean)slider.getClientProperty("Slider.paintThumbArrowShape");

        if ((!slider.getPaintTicks() && paintThumbArrowShape == null) ||
            paintThumbArrowShape == Boolean.FALSE) {

            // "plain" version
            g.fillRect(0, 0, w, h);

            g.setColor(Color.black);
            g.drawLine(0, h-1, w-1, h-1);
            g.drawLine(w-1, 0, w-1, h-1);

            g.setColor(highlightColor);
            g.drawLine(0, 0, 0, h-2);
            g.drawLine(1, 0, w-2, 0);

            g.drawLine(1, h-2, w-2, h-2);
            g.drawLine(w-2, 1, w-2, h-3);
        }
        else if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            int cw = w / 2;
            g.fillRect(1, 1, w-3, h-1-cw);
            Polygon p = new Polygon();
            p.addPoint(1, h-cw);
            p.addPoint(cw-1, h-1);
            p.addPoint(w-2, h-1-cw);
            g.fillPolygon(p);

            g.setColor(highlightColor);
            g.drawLine(0, 0, w-2, 0);
            g.drawLine(0, 1, 0, h-1-cw);
            g.drawLine(0, h-cw, cw-1, h-1);

            g.setColor(Color.black);
            g.drawLine(w-1, 0, w-1, h-2-cw);
            g.drawLine(w-1, h-1-cw, w-1-cw, h-1);

            g.setColor(shadowColor);
            g.drawLine(w-2, 1, w-2, h-2-cw);
            g.drawLine(w-2, h-1-cw, w-1-cw, h-2);
        }
        else {  // vertical
            int cw = h / 2;
            if(slider.getComponentOrientation().isLeftToRight()) {
                  g.fillRect(1, 1, w-1-cw, h-3);
                  Polygon p = new Polygon();
                  p.addPoint(w-cw-1, 0);
                  p.addPoint(w-1, cw);
                  p.addPoint(w-1-cw, h-2);
                  g.fillPolygon(p);

                  g.setColor(highlightColor);
                  g.drawLine(0, 0, 0, h - 2);                  // left
                  g.drawLine(1, 0, w-1-cw, 0);                 // top
                  g.drawLine(w-cw-1, 0, w-1, cw);              // top slant

                  g.setColor(Color.black);
                  g.drawLine(0, h-1, w-2-cw, h-1);             // bottom
                  g.drawLine(w-1-cw, h-1, w-1, h-1-cw);        // bottom slant

                  g.setColor(shadowColor);
                  g.drawLine(1, h-2, w-2-cw,  h-2 );         // bottom
                  g.drawLine(w-1-cw, h-2, w-2, h-cw-1 );     // bottom slant
            }
            else {
                  g.fillRect(5, 1, w-1-cw, h-3);
                  Polygon p = new Polygon();
                  p.addPoint(cw, 0);
                  p.addPoint(0, cw);
                  p.addPoint(cw, h-2);
                  g.fillPolygon(p);

                  g.setColor(highlightColor);
                  g.drawLine(cw-1, 0, w-2, 0);             // top
                  g.drawLine(0, cw, cw, 0);                // top slant

                  g.setColor(Color.black);
                  g.drawLine(0, h-1-cw, cw, h-1 );         // bottom slant
                  g.drawLine(cw, h-1, w-1, h-1);           // bottom

                  g.setColor(shadowColor);
                  g.drawLine(cw, h-2, w-2,  h-2 );         // bottom
                  g.drawLine(w-1, 1, w-1,  h-2 );          // right
            }
        }

        g.translate(-knobBounds.x, -knobBounds.y);
	}

	protected void restoreThumbForLowValue(Point p) {
		thumbRect.x = p.x;
		thumbRect.y = p.y;
	}

	protected Point adjustThumbForHighValue() {
		Point p = thumbRect.getLocation();
		if (slider.getOrientation() == JSlider.HORIZONTAL) {
			int valuePosition = xPositionForValue(((JRangeSliderGradient) slider)
					.getHighValue());
			thumbRect.x = valuePosition - (thumbRect.width / 2);
		} else {
			int valuePosition = yPositionForValue(((JRangeSliderGradient) slider)
					.getHighValue());
			thumbRect.y = valuePosition - (thumbRect.height / 2);
		}
		return p;
	}

	protected void adjustSnapHighValue() {
		int sliderValue = ((JRangeSliderGradient) slider).getHighValue();
		int snappedValue = sliderValue;
		int majorTickSpacing = slider.getMajorTickSpacing();
		int minorTickSpacing = slider.getMinorTickSpacing();
		int tickSpacing = 0;

		if (minorTickSpacing > 0) {
			tickSpacing = minorTickSpacing;
		} else if (majorTickSpacing > 0) {
			tickSpacing = majorTickSpacing;
		}

		if (tickSpacing != 0) {
			if ((sliderValue - slider.getMinimum()) % tickSpacing != 0) {
				float temp = (float) (sliderValue - slider.getMinimum())
						/ (float) tickSpacing;
				int whichTick = Math.round(temp);
				snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
			}

			if (snappedValue != sliderValue) {
				((JRangeSliderGradient) slider).setHighValue(snappedValue);
			}
		}
	}

	@Override
	protected void calculateThumbLocation() {
		if (slider.getSnapToTicks()) {
			adjustSnapHighValue();
		}
		super.calculateThumbLocation();
	}

	@Override
	protected TrackListener createTrackListener(JSlider slider) {
		return new RangeTrackListener(super.createTrackListener(slider));
	}

	protected class RangeTrackListener extends TrackListener {
		int handle;
		int handleOffset;
		int mouseStartLocation;
		TrackListener _listener;

		public RangeTrackListener(TrackListener listener) {
			_listener = listener;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!slider.isEnabled()) {
				return;
			}

			if (slider.isRequestFocusEnabled()) {
				slider.requestFocus();
			}

			handle = getMouseHandle(e.getX(), e.getY());
			setMousePressed(handle);

			if (handle == MOUSE_HANDLE_MAX || handle == MOUSE_HANDLE_MIN
					|| handle == MOUSE_HANDLE_MIDDLE
					|| handle == MOUSE_HANDLE_BOTH) {
				handleOffset = (slider.getOrientation() == JSlider.VERTICAL) ? e
						.getY()
						- yPositionForValue(((JRangeSliderGradient) slider)
								.getLowValue()) : e.getX()
						- xPositionForValue(((JRangeSliderGradient) slider)
								.getLowValue());

				mouseStartLocation = (slider.getOrientation() == JSlider.VERTICAL) ? e
						.getY() : e.getX();

				slider.getModel().setValueIsAdjusting(true);
			} else if (handle == MOUSE_HANDLE_LOWER
					|| handle == MOUSE_HANDLE_UPPER) {
				_listener.mousePressed(e);
				slider.putClientProperty(
						JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION, null);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!slider.isEnabled()) {
				return;
			}

			int newLocation = (slider.getOrientation() == JSlider.VERTICAL) ? e
					.getY() : e.getX();

			int newValue = (slider.getOrientation() == JSlider.VERTICAL) ? valueForYPosition(newLocation)
					: valueForXPosition(newLocation);

			if (newValue < slider.getModel().getMinimum()) {
				newValue = slider.getModel().getMinimum();
			}

			if (newValue > slider.getModel().getMaximum()) {
				newValue = slider.getModel().getMaximum();
			}

			if (handle == MOUSE_HANDLE_BOTH) {
				if ((newLocation - mouseStartLocation) >= 1) {
					handle = MOUSE_HANDLE_MAX;
				} else if ((newLocation - mouseStartLocation) <= -1) {
					handle = MOUSE_HANDLE_MIN;
				} else {
					return;
				}
			}

			JRangeSliderGradient JRangeSlider = (JRangeSliderGradient) slider;
			switch (handle) {
			case MOUSE_HANDLE_MIN:
				JRangeSlider.setLowValue(Math.min(newValue,
						JRangeSlider.getHighValue()));
				break;
			case MOUSE_HANDLE_MAX:
				JRangeSlider.setHighValue(Math.max(JRangeSlider.getLowValue(),
						newValue));
				break;
			case MOUSE_HANDLE_MIDDLE:
				if (true) {
					int delta = (slider.getOrientation() == JSlider.VERTICAL) ? valueForYPosition(newLocation
							- handleOffset)
							- JRangeSlider.getLowValue()
							: valueForXPosition(newLocation - handleOffset)
									- JRangeSlider.getLowValue();
					if ((delta < 0)
							&& ((JRangeSlider.getLowValue() + delta) < JRangeSlider
									.getMinimum())) {
						delta = JRangeSlider.getMinimum()
								- JRangeSlider.getLowValue();
					}

					if ((delta > 0)
							&& ((JRangeSlider.getHighValue() + delta) > JRangeSlider
									.getMaximum())) {
						delta = JRangeSlider.getMaximum()
								- JRangeSlider.getHighValue();
					}

					if (delta != 0) {
						JRangeSlider.setLowValue(JRangeSlider.getLowValue()
								+ delta);
						JRangeSlider.setHighValue(JRangeSlider.getHighValue()
								+ delta);
					}
				}
				break;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			slider.getModel().setValueIsAdjusting(false);
			setMouseReleased(handle);
			_listener.mouseReleased(e);
		}

		private void setCursor(int c) {
			Cursor cursor = Cursor.getPredefinedCursor(c);

			if (slider.getCursor() != cursor) {
				slider.setCursor(cursor);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!slider.isEnabled()) {
				return;
			}

			int handle = getMouseHandle(e.getX(), e.getY());
			setMouseRollover(handle);
			switch (handle) {
			case MOUSE_HANDLE_MIN:
				setCursor(Cursor.DEFAULT_CURSOR);
				break;
			case MOUSE_HANDLE_MAX:
				setCursor(Cursor.DEFAULT_CURSOR);
				break;
			case MOUSE_HANDLE_MIDDLE:
			case MOUSE_HANDLE_NONE:
			default:
				setCursor(Cursor.DEFAULT_CURSOR);
				break;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				slider.getModel().setValue(slider.getModel().getMinimum());
				slider.getModel().setExtent(
						slider.getModel().getMaximum()
								- slider.getModel().getMinimum());
				slider.repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			hover = true;
			 slider.repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			hover = false;
			 slider.repaint();
			 setCursor(Cursor.DEFAULT_CURSOR);
		}
	}

	protected static final int MOUSE_HANDLE_NONE = 0;

	protected static final int MOUSE_HANDLE_MIN = 1;

	protected static final int MOUSE_HANDLE_MAX = 2;

	protected static final int MOUSE_HANDLE_MIDDLE = 4;

	protected static final int MOUSE_HANDLE_LOWER = 5;

	protected static final int MOUSE_HANDLE_UPPER = 6;

	protected static final int MOUSE_HANDLE_BOTH = 7;

	protected int getMouseHandle(int x, int y) {
		Rectangle rect = trackRect;

		slider.putClientProperty(JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION,
				null);

		boolean inMin = false;
		boolean inMax = false;
		if (thumbRect.contains(x, y)) {
			inMin = true;
		}
		Point p = adjustThumbForHighValue();
		if (thumbRect.contains(x, y)) {
			inMax = true;
		}
		restoreThumbForLowValue(p);
		if (inMin && inMax) {
			return MOUSE_HANDLE_BOTH;
		} else if (inMin) {
			return MOUSE_HANDLE_MIN;
		} else if (inMax) {
			return MOUSE_HANDLE_MAX;
		}

		if (slider.getOrientation() == JSlider.VERTICAL) {
			int minY = yPositionForValue(((JRangeSliderGradient) slider).getLowValue());
			int maxY = yPositionForValue(((JRangeSliderGradient) slider).getHighValue());
			Rectangle midRect = new Rectangle(rect.x, Math.min(minY, maxY)
					+ thumbRect.height / 2, rect.width, Math.abs(maxY - minY)
					- thumbRect.height);
			if (midRect.contains(x, y)) {
				return MOUSE_HANDLE_MIDDLE;
			}
			int sy = rect.y + Math.max(minY, maxY) + thumbRect.height / 2;
			Rectangle lowerRect = new Rectangle(rect.x, sy, rect.width, rect.y
					+ rect.height - sy);
			if (lowerRect.contains(x, y)) {
				slider.putClientProperty(
						JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION, true);
				return MOUSE_HANDLE_LOWER;
			}
			Rectangle upperRect = new Rectangle(rect.x, rect.y, rect.width,
					Math.min(maxY, minY) - thumbRect.height / 2);
			if (upperRect.contains(x, y)) {
				slider.putClientProperty(
						JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION, false);
				return MOUSE_HANDLE_UPPER;
			}

			return MOUSE_HANDLE_NONE;
		} else {
			int minX = xPositionForValue(((JRangeSliderGradient) slider).getLowValue());
			int maxX = xPositionForValue(((JRangeSliderGradient) slider).getHighValue());

			Rectangle midRect = new Rectangle(Math.min(minX, maxX)
					+ thumbRect.width / 2, rect.y, Math.abs(maxX - minX)
					- thumbRect.width, rect.height);
			if (midRect.contains(x, y)) {
				return MOUSE_HANDLE_MIDDLE;
			}
			Rectangle lowerRect = new Rectangle(rect.x, rect.y, Math.min(minX,
					maxX) - thumbRect.width / 2 - rect.x, rect.height);
			if (lowerRect.contains(x, y)) {
				slider.putClientProperty(
						JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION, true);
				return MOUSE_HANDLE_LOWER;
			}
			int sx = rect.x + Math.abs(maxX - minX) + thumbRect.width / 2;
			Rectangle upperRect = new Rectangle(sx, rect.y, rect.width - sx,
					rect.height);
			if (upperRect.contains(x, y)) {
				slider.putClientProperty(
						JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION, false);
				return MOUSE_HANDLE_UPPER;
			}
			return MOUSE_HANDLE_NONE;
		}
	}

	protected boolean hover;
	protected boolean second;
	protected boolean rollover1;
	protected boolean pressed1;
	protected boolean rollover2;
	protected boolean pressed2;

	protected void setMouseRollover(int handle) {
		switch (handle) {
		case MOUSE_HANDLE_MIN: {
			rollover1 = true;
			rollover2 = false;
		}
			break;
		case MOUSE_HANDLE_MAX: {
			rollover2 = true;
			rollover1 = false;
		}
			break;
		case MOUSE_HANDLE_MIDDLE:
		case MOUSE_HANDLE_BOTH: {
			rollover1 = true;
			rollover2 = true;
		}
			break;
		case MOUSE_HANDLE_NONE:
			rollover1 = false;
			rollover2 = false;
			break;
		}
		slider.repaint(thumbRect);
		Point p = adjustThumbForHighValue();
		slider.repaint(thumbRect);
		restoreThumbForLowValue(p);
	}

	protected void setMousePressed(int handle) {
		switch (handle) {
		case MOUSE_HANDLE_MIN: {
			pressed1 = true;
			pressed2 = false;
		}
			break;
		case MOUSE_HANDLE_MAX: {
			pressed2 = true;
			pressed1 = false;
		}
			break;
		case MOUSE_HANDLE_MIDDLE:
		case MOUSE_HANDLE_BOTH: {
			pressed1 = true;
			pressed2 = true;
		}
			break;
		case MOUSE_HANDLE_NONE:
			pressed1 = false;
			pressed2 = false;
			break;
		}
		slider.repaint(thumbRect);
		Point p = adjustThumbForHighValue();
		slider.repaint(thumbRect);
		restoreThumbForLowValue(p);
	}

	protected void setMouseReleased(int handle) {
		pressed1 = false;
		pressed2 = false;
		slider.repaint(thumbRect);
		Point p = adjustThumbForHighValue();
		slider.repaint(thumbRect);
		restoreThumbForLowValue(p);
	}

	@Override
	public void scrollByBlock(int direction) {
		synchronized (slider) {

			int oldValue;
			Object clientProperty = slider
					.getClientProperty(JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION);
			if (clientProperty == null) {
				oldValue = slider.getValue();
			} else if (Boolean.TRUE.equals(clientProperty)) {
				oldValue = ((JRangeSliderGradient) slider).getLowValue();
			} else {
				oldValue = ((JRangeSliderGradient) slider).getHighValue();
			}
			int blockIncrement = (slider.getMaximum() - slider.getMinimum()) / 10;
			if (blockIncrement <= 0
					&& slider.getMaximum() > slider.getMinimum()) {

				blockIncrement = 1;
			}

			int delta = blockIncrement
					* ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);
			slider.putClientProperty(
					JRangeSliderGradient.CLIENT_PROPERTY_ADJUST_ACTION, "scrollByBlock");
			if (clientProperty == null) {
				slider.setValue(Math.max(
						Math.min(oldValue + delta, slider.getMaximum()),
						slider.getMinimum()));
			} else if (Boolean.TRUE.equals(clientProperty)) {
				((JRangeSliderGradient) slider).setLowValue(Math.max(
						Math.min(oldValue + delta, slider.getMaximum()),
						slider.getMinimum()));
			} else {
				((JRangeSliderGradient) slider).setHighValue(Math.max(
						Math.min(oldValue + delta, slider.getMaximum()),
						slider.getMinimum()));
			}
			slider.putClientProperty(
					JRangeSliderGradient.CLIENT_PROPERTY_ADJUST_ACTION, null);
		}
	}

	@Override
	public void scrollByUnit(int direction) {
		synchronized (slider) {

			int oldValue;
			Object clientProperty = slider
					.getClientProperty(JRangeSliderGradient.CLIENT_PROPERTY_MOUSE_POSITION);
			if (clientProperty == null) {
				oldValue = slider.getValue();
			} else if (Boolean.TRUE.equals(clientProperty)) {
				oldValue = ((JRangeSliderGradient) slider).getLowValue();
			} else {
				oldValue = ((JRangeSliderGradient) slider).getHighValue();
			}
			int delta = 1 * ((direction > 0) ? POSITIVE_SCROLL
					: NEGATIVE_SCROLL);

			slider.putClientProperty(
					JRangeSliderGradient.CLIENT_PROPERTY_ADJUST_ACTION, "scrollByUnit");
			if (clientProperty == null) {
				slider.setValue(Math.max(
						Math.min(oldValue + delta, slider.getMaximum()),
						slider.getMinimum()));
			} else if (Boolean.TRUE.equals(clientProperty)) {
				((JRangeSliderGradient) slider).setLowValue(Math.max(
						Math.min(oldValue + delta, slider.getMaximum()),
						slider.getMinimum()));
			} else {
				((JRangeSliderGradient) slider).setHighValue(Math.max(
						Math.min(oldValue + delta, slider.getMaximum()),
						slider.getMinimum()));
			}
			slider.putClientProperty(
					JRangeSliderGradient.CLIENT_PROPERTY_ADJUST_ACTION, null);
		}
	}
}
