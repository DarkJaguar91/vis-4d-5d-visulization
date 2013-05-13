/**
 *
 * <p><strong>Title:</strong> HeatMap</p>
 *
 * <p>Description: HeatMap is a JPanel that displays a 2-dimensional array of
 * data using a selected color gradient scheme.</p>
 * <p>For specifying data, the first index into the float[][] array is the x-
 * coordinate, and the second index is the y-coordinate. In the constructor and
 * updateData method, the 'useGraphicsYAxis' parameter is used to control 
 * whether the row y=0 is displayed at the top or bottom. Since the usual
 * graphics coordinate system has y=0 at the top, setting this parameter to
 * true will draw the y=0 row at the top, and setting the parameter to false
 * will draw the y=0 row at the bottom, like in a regular, mathematical
 * coordinate system. This parameter was added as a solution to the problem of
 * "Which coordinate system should we use? Graphics, or mathematical?", and
 * allows the user to choose either coordinate system. Because the HeatMap will
 * be plotting the data in a graphical manner, using the Java Swing framework
 * that uses the standard computer graphics coordinate system, the user's data
 * is stored internally with the y=0 row at the top.</p>
 * <p>There are a number of defined gradient types (look at the static fields),
 * but you can create any gradient you like by using either of the following 
 * functions in the Gradient class:
 * <ul>
 *   <li>public static Color[] createMultiGradient(Color[] colors, int numSteps)</li>
 *   <li>public static Color[] createGradient(Color one, Color two, int numSteps)</li>
 * </ul>
 * You can then assign an arbitrary Color[] object to the HeatMap as follows:
 * <pre>myHeatMap.updateGradient(Gradient.createMultiGradient(new Color[] {Color.red, Color.white, Color.blue}, 256));</pre>
 * </p>
 *
 * <p>By default, the graph title, axis titles, and axis tick marks are not
 * displayed. Be sure to set the appropriate title before enabling them.</p>
 *
 * <hr />
 * <p><strong>Copyright:</strong> Copyright (c) 2007, 2008</p>
 *
 * <p>HeatMap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.</p>
 *
 * <p>HeatMap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.</p>
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with HeatMap; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA</p>
 *
 * @author Matthew Beckler (matthew@mbeckler.org)
 * @author Josh Hayes-Sheen (grey@grevian.org), Converted to use BufferedImage.
 * @author J. Keller (jpaulkeller@gmail.com), Added transparency (alpha) support, data ordering bug fix.
 * @version 1.6
 */

package heatMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Data.DataHolder;

//DONE: HM) Hover - display temperature
//TO.DO.DEPR: HM) Hi: add right-mouse context button
//DONE: HM) +Hi: scale temperature bar gradient
//TO DO: HM) allow for vertical axis orientation (and orient map accordingly)
//DONE: HM) +Hi: Coloured Axes with Correct Names
//DONE: Fix font Smoothing on heatMap Panel

public class HeatMap extends JPanel implements MouseMotionListener
{
	/**
	 * Generated Serial version
	 */
	private static final long serialVersionUID = 3021032568685232247L;
	private float[][] data;
	private int[][] dataColorIndices;

	// these four variables are used to print the axis labels
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;

	private static final int BORDER_L = 30;
	private static final int BORDER_R = 60;
	private static final int BORDER_T = 30;
	private static final int BORDER_B = 30;
	private static final int BORDER_X = BORDER_L + BORDER_R; 
	private static final int BORDER_Y = BORDER_T + BORDER_B; 

	int lastselectedX, lastselectedY = -1;

	private int tempMinObserved = Integer.MAX_VALUE;
	private int tempMaxObserved = 0;

	private DecimalFormat df = new DecimalFormat("##.##");

	private float tempSmallest = Float.MIN_VALUE;
	private float tempLargest = Float.MAX_VALUE;

	private boolean orientationX = true;

	private int drawPanelHeight, drawPanelWidth = 1;
	private float scaleX, scaleY = 1;

	private int[] fixedAxes = new int[]{-1,-1,-1,-1};

	private String title;
	private String xAxis;
	private String yAxis;

	private boolean drawTitle = false;
	private boolean drawXTitle = false;
	private boolean drawYTitle = false;
	private boolean drawLegend = false;
	private boolean drawXTicks = false;
	private boolean drawYTicks = false;

	private Color[] colors;
	private Color[] axesColors = {Color.RED,Color.GREEN,Color.BLUE, Color.MAGENTA};
	private Color bg = Color.white;
	private Color fg = Color.black;
	private Color pbg = this.getBackground();

	private BufferedImage bufferedImage;
	private Graphics2D bufferedGraphics;

	public static boolean debugOn = !true;
	public static void debug(String paramInput){
		if (debugOn)
			System.out.println(paramInput);
	}
	public static void debugn(String paramInput){
		if (debugOn)
			System.out.print(paramInput);
	}
	public static void debug(){
		if (debugOn)
			debug("");
	}
	public static void debugn(){
		if (debugOn)
			debugn("");
	}
	/**
	 * @param data The data to display, must be a complete array (non-ragged)
	 * @param useGraphicsYAxis If true, the data will be displayed with the y=0 row at the top of the screen. If false, the data will be displayed with they=0 row at the bottom of the screen.
	 * @param colors A variable of the type Color[]. See also {@link #createMultiGradient} and {@link #createGradient}.
	 */
	public HeatMap(float[][] paramData, boolean useGraphicsYAxis, Color[] colors)
	{
		super();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		updateGradient(colors);
		updateData(paramData, useGraphicsYAxis);

		this.setPreferredSize(new Dimension(60+paramData.length, 60+paramData[0].length));
		this.setDoubleBuffered(true);
		this.setFont(new Font("Tahoma",Font.PLAIN,12));

		this.bg = Color.white;
		this.fg = Color.black;


		this.addMouseListener(new MouseListener() {
			//			@Override
			//			public void mouseReleased(MouseEvent e) {
			@Override
			public void mouseReleased(MouseEvent e) {
				//        HeatMap.debug(":MOUSE_RELEASED_EVENT:");
			}
			@Override
			public void mousePressed(MouseEvent e) {
				//       HeatMap.debug("----------------------------------\n:MOUSE_PRESSED_EVENT:");
				mouseClicked(e);

			}
			@Override
			public void mouseExited(MouseEvent e) {
				//     HeatMap.debug(":MOUSE_EXITED_EVENT:");
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				//    HeatMap.debug(":MOUSE_ENTER_EVENT:");
				//				displayTemperature(e.getX(),e.getY(),1000);
				//								ToolTipManager.sharedInstance().setInitialDelay(500);
				//mouseClicked(e);
				HeatMap.debug("Mouse Entered Panel");
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				//                HeatMap.debug(":MOUSE_CLICK_EVENT:");
				JComponent component = (JComponent)e.getSource();

				int mX = e.getX();
				int mY = e.getY();
				int dX = getDPX(mX);
				int dY = getDPY(mY);
				float tValue =getValue(dX,dY); 
				//DONE-DEPR: HM) +Hi: indicate the axes-relative values
				float relX = xMin+ (float)((mX-BORDER_L) / (1.0*drawPanelWidth))*(xMax-xMin);
				float relY = yMin+ (float)((mY-BORDER_T) / (1.0*drawPanelHeight))*(yMax-yMin);
				//				HeatMap.debug


				MouseEvent phantom = new MouseEvent(
						component,
						MouseEvent.MOUSE_MOVED,
						System.currentTimeMillis(),
						0,
						e.getX(),
						e.getY(),
						0,
						false);

				//                HeatMap.debug();
				//				HeatMap.debug("Fixed Dimensions are: ("+fixedAxes[0]+","+fixedAxes[1]+","+fixedAxes[2]+"|"+fixedAxes[3]+")");
				if (tValue >= 0){
//					ToolTipManager.sharedInstance().mouseMoved(phantom);
					component.setToolTipText("("+dX+","+dY+"); ("+relX+","+relY+");\nTemperature: "+df.format(tValue)); // DONE: check if cropping axes returns correct value

//					displayTemperature(mX,mY,1000);
				}
				else{
					component.setToolTipText(null);
				}

				//				ToolTipManager.sharedInstance().setInitialDelay(0);

			}
	
		});
		/*            public void mouseOver(MouseEvent e){
//                HeatMap.debug("mouseOver");
            }*/
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e){
//				HeatMap.debug("mouseDragged");
			}
			public void mouseMoved(MouseEvent e){
				if ((e.getX()>BORDER_L)&&(e.getX()<getWidth()-BORDER_R)&&(e.getY()>BORDER_T)&&(e.getY()<getHeight()-BORDER_B)){
//					HeatMap.debug("mouse inside panel "+Math.random());
					JComponent component = (JComponent)e.getSource();

					int mX = e.getX();
					int mY = e.getY();
					int dX = getDPX(mX);
					int dY = getDPY(mY);
					float tValue =getValue(dX,dY); 

					/*
					MouseEvent phantom = new MouseEvent(
							component,
							MouseEvent.MOUSE_MOVED,
							System.currentTimeMillis(),
							0,
							e.getX(),
							e.getY(),
							0,
							false);

					//                HeatMap.debug();
					//				HeatMap.debug("Fixed Dimensions are: ("+fixedAxes[0]+","+fixedAxes[1]+","+fixedAxes[2]+"|"+fixedAxes[3]+")");
					 */					if (tValue >= 0){
						 //DONE-DEPR: HM) +Hi: indicate the axes-relative values
						 float relX = xMin+ (float)((mX-BORDER_L) / (1.0*drawPanelWidth))*(xMax-xMin);
						 float relY = yMin+ (float)((mY-BORDER_T) / (1.0*drawPanelHeight))*(yMax-yMin);
						 //				HeatMap.debug
//						 component.setToolTipText("("+dX+","+dY+"); ("+relX+","+relY+");\r\nTemperature: "+df.format(tValue)); // DONE: check if cropping axes returns correct value
					/*	 HeatMap.debug(""+DataHolder.data.getDimensionName(fixedAxes[0]).charAt(0)+"# "+DataHolder.getFixedDimensionStep(fixedAxes[0])+", "+
								 DataHolder.data.getDimensionName(fixedAxes[1]).charAt(0)+"# "+DataHolder.getFixedDimensionStep(fixedAxes[1])+", "+
								 DataHolder.data.getDimensionName(fixedAxes[2]).charAt(0)+": "+DataHolder.getFixedDimensionStep(fixedAxes[2])+", "+
								 DataHolder.data.getDimensionName(fixedAxes[3]).charAt(0)+": "+DataHolder.getFixedDimensionStep(fixedAxes[3])+", "+
								 ", Temperature: "+df.format(tValue));*/
						 float[] currentValues = getValues(mX,mY);
						 DecimalFormat df2 = new DecimalFormat("##.####");
						 component.setToolTipText(""+DataHolder.data.getDimensionName(fixedAxes[0]).charAt(0)+"# "+df2.format(currentValues[0])+", "+
								 DataHolder.data.getDimensionName(fixedAxes[1]).charAt(0)+"# "+df2.format(currentValues[1])+", "+
								 DataHolder.data.getDimensionName(fixedAxes[2]).charAt(0)+": "+df2.format(currentValues[2])+", "+
								 DataHolder.data.getDimensionName(fixedAxes[3]).charAt(0)+": "+df2.format(currentValues[3])+", "+
								 " Temperature: "+df.format(tValue));
						 //	ToolTipManager.sharedInstance().mouseMoved(phantom);
//						 displayTemperature(mX,mY,1000);
					 }
					 else{
						 component.setToolTipText(null);
					 }
				}
				else
				{

				}
			}
		});

		// this is the expensive function that draws the data plot into a 
		// BufferedImage. The data plot is then cheaply drawn to the screen when
		// needed, saving us a lot of time in the end.
		updateData(paramData, false);
		drawData();

	}
	public void mouseDragged(MouseEvent e){
//		HeatMap.debug("mouseDragged");
	}
	public void mouseMoved(MouseEvent e){
//		HeatMap.debug("mouseMoved");
	}
	/**
	 * Specify the coordinate bounds for the map. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param xMin The lower bound of x-values, used for axis labels
	 * @param xMax The upper bound of x-values, used for axis labels
	 */
	public void setCoordinateBounds(float xMin, float xMax, float yMin, float yMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;

		repaint();
	}

	public float[] getValues(int xParam, int yParam){
		/*""+DataHolder.data.getDimensionName(fixedAxes[0]).charAt(0)+"# "+DataHolder.getFixedDimensionStep(fixedAxes[0])+
		 DataHolder.data.getDimensionName(fixedAxes[1]).charAt(0)+"# "+DataHolder.getFixedDimensionStep(fixedAxes[1])+
		 DataHolder.data.getDimensionName(fixedAxes[2]).charAt(0)+": "+DataHolder.getFixedDimensionStep(fixedAxes[2])+
		 DataHolder.data.getDimensionName(fixedAxes[3]).charAt(0)+": "+DataHolder.getFixedDimensionStep(fixedAxes[3])+
		 " Temperature: "+df.format(tValue)*/
		
		//remove padding if not using DP[XY]
//		xParam -= BORDER_L;
//		yParam -= BORDER_T;
		
		float[] retArray = new float[5];
		for(int i = 0; i < 4; ++i){
//			HeatMap.debugn("\tReading "+DataHolder.data.getDimensionName(fixedAxes[i]));
			float rangeMin = DataHolder.data.getMinData(fixedAxes[i]); // DataHolder.getMinFilter(fixedAxes[i])
			float rangeMax = DataHolder.data.getMaxData(fixedAxes[i]); //DataHolder.getMaxFilter(fixedAxes[i])
//			HeatMap.debugn(" stepping "+DataHolder.getFixedDimensionStep(fixedAxes[i])+" (l:"+DataHolder.data.getLength(fixedAxes[i])+")");
			if (i < 2)
				retArray[i] = (float)((rangeMin+DataHolder.getFixedDimensionStep(fixedAxes[i])*1.0*(rangeMax-rangeMin))/(1.0*DataHolder.data.getLength(fixedAxes[i])));
			else if (i==2) // x-axis
				retArray[2] = (float)(1.0*getDPX(xParam)*((rangeMax-rangeMin)/(data.length)));
			else if (i==3) // y-axis
				retArray[3] = (float)(1.0*getDPY(yParam)*((rangeMax-rangeMin)/(data[0].length)));
			HeatMap.debug("max["+i+"]:"+rangeMax+"; min:"+rangeMin);
		}
//		HeatMap.debug();
/*		retArray[2] = getValue(getDPX(xParam),getDPY(yParam));
		retArray[3] = data[]*/
		
		retArray[4] = getValue(getDPX(xParam),getDPY(yParam));
//		for(int i = 0; i < 5; ++i)
//			HeatMap.debugn("\tretArray["+i+"]: "+retArray[i]);
//		HeatMap.debug();
		return retArray;
	}
	
	public void displayTemperature(int paramX, int paramY, int paramTime){
		//		HeatMap.debug("entering displayTemperature...");
		//		float tValue = getValue(paramX,paramY);
		/*try{
			Thread.sleep(paramTime);}
		catch (Exception e) {
		}*/
		//		HeatMap.debug("Leaving displayTemperature ("+tValue+")...");
		lastselectedX = paramX;
		lastselectedY = paramY;
	}

	public void setCoordinateBounds(double xMin, double xMax, double yMin, double yMax)
	{
		setCoordinateBounds((float)(xMin), (float)(xMax), (float)(yMin), (float)(yMax));
	}


	public void updateFixedAxes(){


		for (int i = 0; i < 3; ++i)
			fixedAxes[i] = DataHolder.getFixedDimension(i);

		fixedAxes[3] = -1;

		/*	int odd = -1;
		for (int i = 0; i < 4; ++i) {
			odd = (fixedAxes[i] == odd) ? i : -1;
			HeatMap.debugn("\t"+i);
			if (odd != -1)
				 fixedAxes[3] = i; // break;
		}*/
		//		fixedAxes[3] = odd; 
		fixedAxes[3] = 6;
		for (int i = 0; i < 3; ++i){
			fixedAxes[3] -= fixedAxes[i];	
		}


	}
	/**
	 * Get the data-point X-coordinate currently selected.
	 * @param xPos The current mouse x-value within the panel
	 */
	public int getDPX(int xPos){ // DONE: HM) fix getDPX
		if((xPos < BORDER_L) || (xPos > this.getWidth()-BORDER_R)){
			return -1;
		}
		xPos -= BORDER_L;
		int retValX = data.length -1 - (int)(data.length * ((drawPanelWidth-xPos)/(1.0*drawPanelWidth)));
		//		HeatMap.debugn("\nxMouse: (abs:"+xPos+") "+retValX);
		return retValX;
	}
	/**
	 * Get the data-point Y-coordinate currently selected.
	 * @param yPos The current mouse y-value within the panel
	 */
	public int getDPY(int yPos){ // DONE: HM) fix getDPY
		if((yPos < BORDER_T) || (yPos > this.getHeight()-BORDER_B)){
			return -1;
		}
		yPos -= BORDER_T;
		int retValY = (int)(data[0].length * ((drawPanelHeight-yPos)/(1.0*drawPanelHeight)));
		//		HeatMap.debug("; yMouse: (abs:"+yPos+") "+retValY);

		return retValY;
	}
	/**
	 * Specify the coordinate bounds for the X-range. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param xMin The lower bound of x-values, used for axis labels
	 * @param xMax The upper bound of x-values, used for axis labels
	 */
	public void setXCoordinateBounds(float xMin, float xMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;

		repaint();
	}

	/**
	 * Specify the coordinate bounds for the X Min. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param xMin The lower bound of x-values, used for axis labels
	 */
	public void setXMinCoordinateBounds(float xMin)
	{
		this.xMin = xMin;

		repaint();
	}

	/**
	 * Specify the coordinate bounds for the X Max. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param xMax The upper bound of x-values, used for axis labels
	 */
	public void setXMaxCoordinateBounds(float xMax)
	{
		this.xMax = xMax;

		repaint();
	}

	/**
	 * Specify the coordinate bounds for the Y-range. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param yMin The lower bound of y-values, used for axis labels
	 * @param yMax The upper bound of y-values, used for axis labels
	 */
	public void setYCoordinateBounds(float yMin, float yMax)
	{
		this.yMin = yMin;
		this.yMax = yMax;

		repaint();
	}

	/**
	 * Specify the coordinate bounds for the Y Min. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param yMin The lower bound of Y-values, used for axis labels
	 */
	public void setYMinCoordinateBounds(float yMin)
	{
		this.yMin = yMin;

		repaint();
	}

	/**
	 * Specify the coordinate bounds for the Y Max. Only used for the axis labels, which must be enabled seperately. Calls repaint() when finished.
	 * @param yMax The upper bound of y-values, used for axis labels
	 */
	public void setYMaxCoordinateBounds(float yMax)
	{
		this.yMax = yMax;
		repaint();
	}

	/**
	 * Updates the title. Calls repaint() when finished.
	 * @param title The new title
	 */
	public void setTitle(String title)
	{
		//DONE: HM) +Lo: Make Title Bold
		Font tF = bufferedGraphics.getFont();
		//		bufferedGraphics.setFont(font)(font)ont(bufferedGraphics.getFont().get)
		bufferedGraphics.setFont(new Font(tF.getName(),Font.BOLD,tF.getSize()+4));
		this.title = title;

		repaint();
		bufferedGraphics.setFont(tF);
	}

	/**
	 * Updates the state of the title. Calls repaint() when finished.
	 * @param drawTitle Specifies if the title should be drawn
	 */
	public void setDrawTitle(boolean drawTitle)
	{
		this.drawTitle = drawTitle;
		repaint();
	}
	/**
	 * Gets the color under the mouse cursor.
	 * @param x Horizontal offset
	 * @param y Vertical offset
	 */
	public Color getColor(int x, int y){
		if (x<0||y<0)
			return Color.BLACK;
		//DONE: HM) Get Color/value under cursor
		//		HeatMap.debug("getColor: ("+x+","+y+")");
		y = data[0].length - y -1;
//		HeatMap.debug("Value: (d"+data[x][y]+", dCI"+dataColorIndices[x][y]+") sizeof(di):["+dataColorIndices.length+","+dataColorIndices[0].length+"]");
		//		int minX, minY, maxX, maxY = 0;
		//		minX = this.getX();
		//		minY = this.getY();
		//		maxX = minX + drawPanelWidth;
		//		maxY = minY + drawPanelWidth;
		//		
		//		int dX = data.length-(int)(x / scaleX);
		//		int dY = (int)(y / scaleX);
		//		HeatMap.debug("Window Loc: ("+minX+","+minY+","+maxX+","+maxY+"); Scale Factor: "+scaleX+":"+scaleY);
		//		bufferedGraphics.drawRect(minX+50, minY+50, drawPa-100, maxX-minY-100);
		return (Color)colors[dataColorIndices[x][y]];
		//		return this.bufferedGraphics.getColor();
		// return colors[(int)(data[x][y])];
	}
	/**
	 * Gets the color under the mouse cursor.
	 * @param x Horizontal offset
	 * @param y Vertical offset
	 */
	public float getValue(int x, int y){

		if (x<0||y<1)
			return -1;
		//DONE: HM) Get value under cursor
//		HeatMap.debug("getValue: ("+x+","+y+")");
		y = data[0].length - y -1; // TODO: HM) !#Cnfm Ensure bottom-row displays tooltips when clicked
		//		HeatMap.debug("Value: ("+df.format(data[x][y])+", dCI"+dataColorIndices[x][y]+")\tsizeof(dCI[][]):["+dataColorIndices.length+","+dataColorIndices[0].length+"]");
		//		
		return data[x][y];
	}

	/**
	 * Updates the X-Axis title. Calls repaint() when finished.
	 * @param xAxisTitle The new X-Axis title
	 */
	public void setXAxisTitle(String xAxisTitle)
	{
		this.xAxis = xAxisTitle;

		repaint();
	}

	/**
	 * Updates the X-Axis title. Calls repaint() when finished.
	 * @param xAxisTitle The new X-Axis title
	 * @param xColor The new X-Axis title color
	 */
	public void setXAxisTitle(String xAxisTitle, Color xColor)
	{
//		HeatMap.debug("setXAxisTitle: "+xAxisTitle+" in "+xColor);
		Color tFG = this.getForeground();
		this.setForeground(xColor);
		this.setColorForeground(xColor);
		fg = xColor;
		this.xAxis = xAxisTitle;

		repaint();
		this.setForeground(tFG);
		this.setColorForeground(tFG);
		fg = tFG;
	}

	/**
	 * Updates the state of the X-Axis Title. Calls repaint() when finished.
	 * @param drawXAxisTitle Specifies if the X-Axis title should be drawn
	 */
	public void setDrawXAxisTitle(boolean drawXAxisTitle)
	{
		this.drawXTitle = drawXAxisTitle;

		repaint();
	}

	/**
	 * Updates the Y-Axis title. Calls repaint() when finished.
	 * @param yAxisTitle The new Y-Axis title
	 */
	public void setYAxisTitle(String yAxisTitle)
	{
		this.yAxis = yAxisTitle;
		repaint();
	}
	/**
	 * Updates the Y-Axis title. Calls repaint() when finished.
	 * @param yAxisTitle The new Y-Axis title
	 * @param yColor The new Y-Axis title color
	 */
	public void setYAxisTitle(String yAxisTitle, Color yColor)
	{	
//		HeatMap.debug("setYAxisTitle: "+yAxisTitle+" in "+yColor);
		Color tFG = this.getForeground();
		this.setForeground(yColor);
		this.setColorForeground(yColor);
		fg = yColor;
		this.yAxis = yAxisTitle;
		repaint();
		this.setForeground(tFG);
		this.setColorForeground(tFG);
		fg = tFG;
	}

	/**
	 * Updates the state of the Y-Axis Title. Calls repaint() when finished.
	 * @param drawYAxisTitle Specifies if the Y-Axis title should be drawn
	 */
	public void setDrawYAxisTitle(boolean drawYAxisTitle)
	{
		this.drawYTitle = drawYAxisTitle;

		repaint();
	}


	/**
	 * Updates the state of the legend. Calls repaint() when finished.
	 * @param drawLegend Specifies if the legend should be drawn
	 */
	public void setDrawLegend(boolean drawLegend)
	{
		this.drawLegend = drawLegend;

		repaint();
	}

	/**
	 * Updates the state of the X-Axis ticks. Calls repaint() when finished.
	 * @param drawXTicks Specifies if the X-Axis ticks should be drawn
	 */
	public void setDrawXTicks(boolean drawXTicks)
	{
		this.drawXTicks = drawXTicks;

		repaint();
	}

	/**
	 * Updates the state of the Y-Axis ticks. Calls repaint() when finished.
	 * @param drawYTicks Specifies if the Y-Axis ticks should be drawn
	 */
	public void setDrawYTicks(boolean drawYTicks)
	{
		this.drawYTicks = drawYTicks;

		repaint();
	}

	/**
	 * Updates the foreground color. Calls repaint() when finished.
	 * @param fg Specifies the desired foreground color
	 */
	public void setColorForeground(Color fg)
	{
		this.fg = fg;

		repaint();
	}

	/**
	 * Updates the background color. Calls repaint() when finished.
	 * @param bg Specifies the desired background color
	 */
	public void setColorBackground(Color bg)
	{
		this.bg = bg;

		repaint();
	}

	/**
	 * Updates the gradient used to display the data. Calls drawData() and 
	 * repaint() when finished.
	 * @param colors A variable of type Color[]
	 */
	public void updateGradient(Color[] colors)
	{
		this.colors = (Color[]) colors.clone();

		if (data != null)
		{
			updateDataColors();

			drawData();

			repaint();
		}
		else{
			HeatMap.debug("Data is null");
			//			HeatMap.debug("");
			//			HeatMap.debug("this.data size is: "+sizeOf(data));
		}
	}

	/**
	 * This uses the current array of colors that make up the gradient, and 
	 * assigns a color index to each data point, stored in the dataColorIndices
	 * array, which is used by the drawData() method to plot the points.
	 */
	private void updateDataColors()
	{
		//We need to find the range of the data values,
		// in order to assign proper colors.
		float largest = DataHolder.data.getMaxData(4);
		float smallest = DataHolder.data.getMinData(4);

		float range = largest - smallest;

		// dataColorIndices is the same size as the data array
		// It stores an int index into the color array
		dataColorIndices = new int[data.length][data[0].length]; //DONE-UNRP: HM) Hi: size dataColorIndices[][]  properly for read data    

		//assign a Color to each data point
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				float norm = (float)(Math.abs(data[x][y]) - smallest) / range; // 0 < norm < 1
				int colorIndex = (int) Math.floor(norm * (colors.length - 1));
				// if (data[x][y] < 0) HeatMap.debug(""+colorIndex+"; dataCI: "+dataColorIndices[x][y]);
				dataColorIndices[x][y] = colorIndex;
			}
		}
		tempSmallest = smallest;
		tempLargest = largest;
		tempMinObserved = (int)Math.min(smallest, tempMinObserved);
		tempMaxObserved = (int)Math.max(largest, tempMaxObserved);
	}


	/**
	 * Updates the data display, calls drawData() to do the expensive re-drawing
	 * of the data plot, and then calls repaint().
	 * @param paramData The data to display, must be a complete array (non-ragged)
	 * @param useGraphicsYAxis If true, the data will be displayed with the y=0 row at the top of the screen. If false, the data will be displayed with the y=0 row at the bottom of the screen.
	 */
	public void updateData(float[][] paramData, boolean useGraphicsYAxis)
	{ // TO.DO.DEPR: HM) Lo: Do NOT Copy Array!!

		if (data!=null){
			scaleX=(float)((drawPanelWidth)/(1.0*paramData.length));
			scaleY=(float)((drawPanelHeight)/(1.0*paramData[0].length));
		}
		HeatMap.debug("DrawPanel Size: ("+drawPanelWidth+","+drawPanelHeight+") of ("+getWidth()+","+getHeight()+")");

		if (data!=null)
			HeatMap.debug("Scales: ("+scaleX+","+scaleY+","+data.length+","+data[0].length+");");

		HeatMap.debug("dataSize: ["+DataHolder.data.getLength(0)+","+DataHolder.data.getLength(1)+","+DataHolder.data.getLength(2)+","+DataHolder.data.getLength(3)+"]");
		HeatMap.debug("Length of colors: "+colors.length);

		tempLargest = DataHolder.getMaxFilter(4);
		tempSmallest = DataHolder.getMinFilter(4);

		this.data = new float[paramData.length][paramData[0].length];
		for (int ix = 0; ix < paramData.length; ix++)
		{
			for (int iy = 0; iy < paramData[0].length; iy++)
			{
				//				// we use the graphics Y-axis internally
				//				if (useGraphicsYAxis)
				//				{
				//					this.data[ix][iy] = data[ix][iy];
				//				}
				//				else
				{
					float datapnt = paramData[ix][paramData[0].length - iy - 1];
					if (datapnt > tempLargest)
						datapnt = -1;
					if (datapnt < tempSmallest)
						datapnt = -1;
					this.data[ix][iy] = datapnt;
				}
			}
		}

		// DONE: HM) +Hi: Update titles
		// TODO: HM) #Cnfm Ensure Map Orientation (dup?)
		updateFixedAxes();
		setXAxisTitle(DataHolder.data.getDimensionName(fixedAxes[2]), axesColors[fixedAxes[2]]);
		setYAxisTitle(DataHolder.data.getDimensionName(fixedAxes[3]), axesColors[fixedAxes[3]]);
		updateDataColors();

		drawData();

		repaint();
		//		HeatMap.debug("Calling GC");
		//		System.gc(); // DONE: HM) +Lo: remove manual garbage collection
	}

	/**
	 * Creates a BufferedImage of the actual data plot.
	 *
	 * After doing some profiling, it was discovered that 90% of the drawing
	 * time was spend drawing the actual data (not on the axes or tick marks).
	 * Since the Graphics2D has a drawImage method that can do scaling, we are
	 * using that instead of scaling it ourselves. We only need to draw the 
	 * data into the bufferedImage on startup, or if the data or gradient
	 * changes. This saves us an enormous amount of time. Thanks to 
	 * Josh Hayes-Sheen (grey@grevian.org) for the suggestion and initial code
	 * to use the BufferedImage technique.
	 * 
	 * Since the scaling of the data plot will be handled by the drawImage in
	 * paintComponent, we take the easy way out and draw our bufferedImage with
	 * 1 pixel per data point. Too bad there isn't a setPixel method in the 
	 * Graphics2D class, it seems a bit silly to fill a rectangle just to set a
	 * single pixel...
	 *
	 * This function should be called whenever the data or the gradient changes.
	 */
	private void drawData()
	{
		bufferedImage = new BufferedImage(data.length,data[0].length, BufferedImage.TYPE_INT_ARGB);
		bufferedGraphics = bufferedImage.createGraphics();

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				//				HeatMap
				if (data[x][y] < 0)
					bufferedGraphics.setColor(pbg);
				else
					bufferedGraphics.setColor(colors[dataColorIndices[x][y]]);

				bufferedGraphics.fillRect(x, y, 1, 1);
				//				HeatMap.debugn("\t"+colors[dataColorIndices[x][y]]);
			}
			//			HeatMap.debug();
		}

		// Find the location on the dimension that is not plotted the 2D Graph and indicate it 
		//int graphFixed = DataHolder.getFixedDimension(DataHolder.plotterGraph.getFixedDimension());
		//HeatMap.debug("Graph Fixed: "+graphFixed);
	}

	/**
	 * The overridden painting method, now optimized to simply draw the data
	 * plot to the screen, letting the drawImage method do the resizing. This
	 * saves an extreme amount of time.
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		updateFixedAxes();

		Graphics2D g2d = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.setRenderingHints(rh);
		//		g2d.

		int width = this.getWidth();
		int height = this.getHeight();

		this.setOpaque(true);

		// clear the panel
		g2d.setColor(bg);
		g2d.fillRect(0, 0, width, height);

		// draw the heat map
		if (bufferedImage == null)
		{
			// Ideally, we only to call drawData in the constructor, or if we
			// change the data or gradients. We include this just to be safe.
			drawData();
		}

		// The data plot itself is drawn with 1 pixel per data point, and the
		// drawImage method scales that up to fit our current window size. This
		// is very fast, and is much faster than the previous version, which 
		// redrew the data plot each time we had to repaint the screen.
		g2d.drawImage(bufferedImage,
				31, 31,
				width - BORDER_R,
				height - BORDER_B,
				0, 0,
				bufferedImage.getWidth(), bufferedImage.getHeight(),
				null);

		drawPanelWidth = width - BORDER_X;
		drawPanelHeight = height - BORDER_Y;

		// border
		g2d.setColor(fg);
		g2d.drawRect(BORDER_L, BORDER_T, drawPanelWidth, drawPanelHeight);

		// title
		if (drawTitle && title != null)
		{
			g2d.setFont(new Font("Tahoma",Font.BOLD,16));
			g2d.drawString(title, (width / 2) - 4 * title.length(), 20);
			g2d.setFont(new Font("Tahoma",Font.PLAIN,12));
		}

		// axis ticks - ticks start even with the bottom left corner, end very close to end of line (might not be right on)
		int numXTicks = (width - 90) / 50;
		int numYTicks = (height - 60) / 50;

		String label = "";
		//		DecimalFormat df = new DecimalFormat("##.##");

		// Y-Axis ticks
		if (drawYTicks)
		{
			int selected = DataHolder.getFixedDimensionStep(DataHolder.getFixedDimension(2));
			int yDist = (int) ((height - 60) / (float) numYTicks); //distance between ticks
			for (int y = 0; y <= numYTicks; y++)
			{

				g2d.drawLine(26, height - 30 - y * yDist, 30, height - 30 - y * yDist);

				label = df.format(((y / (float) numYTicks) * (yMax - yMin)) + yMin);
				int labelY = height - 30 - y * yDist - 4 * label.length();
				//to get the text to fit nicely, we need to rotate the graphics
				g2d.rotate(Math.PI / 2);
				g2d.drawString(label, labelY, -14);
				g2d.rotate( -Math.PI / 2);
			}

			int gmf = (int)(DataHolder.getMinFilter(DataHolder.getFixedDimension(2)));
			int steps = (int)(DataHolder.getMaxFilter(DataHolder.getFixedDimension(2)) - DataHolder.getMinFilter(DataHolder.getFixedDimension(2)))+1;
			for (int y = 0; y < steps; y++) {

				//TODO: HM) #Cnfm: if x-axis is not lower in order to y-axis in fixed axes, orientationx = false.
				orientationX = (fixedAxes[2]<fixedAxes[3]);

				if ((y+gmf) == selected) // indicate the step over the bg (and fg)
				{
					HeatMap.debug("OrientationX: "+orientationX+"; ");
					if (orientationX){
						//TODO: HM) #Cnfm Ensure marker display once data is loaded
						//TODO: HM) #Cnfm Case Axes(230|1)+Data; Cannot find graph horiz line when stepping
						// Precision Underflow?
						int offsetY = height - BORDER_T - (int)((y+0.5) * (height-BORDER_Y)/(1.0*steps));
						// HeatMap.debug("height: "+height+"; Selected Y-Axis: "+selected+"; OffsetY: "+offsetY);
						g2d.setColor(bg);
						g2d.setColor(Color.black);
						g2d.fillRect(31, offsetY + 1 , width-BORDER_X,1);
						g2d.setColor(Color.red);
						g2d.fillRect(BORDER_L-4, offsetY , 5,3);
						g2d.fillRect(width-BORDER_R, offsetY , 5,3);
						g2d.setColor(fg);
					} else {		
						//DONE: HM) Fix this for vertical selection bar (HeatMap)
						//TODO: HM) #Cnfm Case Axes(031|2); Cannot find graph dataset

						int offsetY = /*width -*/ BORDER_L + (int)((y+0.5) * (width-BORDER_X)/(1.0*steps));
						HeatMap.debug("height: "+height+"; Selected Y-Axis: "+selected+"; OffsetY: "+offsetY);
						g2d.setColor(bg);
						g2d.setColor(Color.black);
						g2d.fillRect(offsetY + 1, 31 ,1, height-BORDER_Y);
						g2d.setColor(Color.red);
						g2d.fillRect(offsetY,26, 3, 5);
						g2d.fillRect(offsetY,height-BORDER_B ,3, 5);
						g2d.setColor(fg);
					}
				}
			}
		}

		// Y-Axis title
		if (drawYTitle && yAxis != null)
		{
			//to get the text to fit nicely, we need to rotate the graphics
			g2d.setColor(axesColors[fixedAxes[3]]);
			g2d.rotate(Math.PI / 2);
			g2d.drawString(yAxis, (height / 2) - 4 * yAxis.length(), -3);
			g2d.rotate(-Math.PI);
			g2d.setColor(Color.RED);
			g2d.setColor(axesColors[fixedAxes[3]]);
			g2d.drawString(yAxis, (height / 2) - 4 * yAxis.length(), -30);
			g2d.setColor(fg);
			g2d.rotate(Math.PI / 2);

		}

		// X-Axis ticks
		if (drawXTicks)
		{
			int xDist = (int) ((width - 90) / (float) numXTicks); //distance between ticks
			for (int x = 0; x <= numXTicks; x++)
			{
				g2d.drawLine(30 + x * xDist, height - 30, 30 + x * xDist, height - 26);
				label = df.format(((x / (float) numXTicks) * (xMax - xMin)) + xMin);
				int labelX = (31 + x * xDist) - 4 * label.length();
				g2d.drawString(label, labelX, height - 14);
			}
		}

		// X-Axis title
		if (drawXTitle && xAxis != null)
		{
			g2d.setColor(axesColors[fixedAxes[2]]);
			g2d.drawString(xAxis, (width / 2) - 4 * xAxis.length(), height - 3);
			g2d.setColor(fg);
		}

		// Legend
		if (drawLegend)
		{
			/*       g2d.drawRect(10, height-60, width - 20, height - 60);
            for (int x = 0; x < width - 21; x++)
            {
                int xStart = width - 31 - (int) Math.ceil(x * ((width - 60) / (colors.length * 1.0)));
                xStart = width - 31 - x;
                g2d.setColor(colors[(int) ((x / (float) (width - 60)) * (colors.length * 1.0))]);
                g2d.fillRect(xStart, height - 19, 1, 9);
            }*/
			g2d.drawRect(width - 50, 30, 10, height - 60);

			float tdiff = (float)Math.ceil(DataHolder.getMaxFilter(4)-DataHolder.getMinFilter(4));
			int toffset = (int)(DataHolder.getMinFilter(4)); 

			//			HeatMap.debug("tDiff: "+tdiff+"; tOffset: "+toffset+"; mO: "+tempMinObserved +"; MO: "+tempMaxObserved);
			int yStart; // = height - 31 - (int) Math.ceil(y * ((height - 60) / (colors.length * 1.0)));

			for (int y = 0; y < height - 61; y++)
			{
				yStart = height - 31 - y;
				g2d.setColor(colors[(int) ((y / (float) (height - 60)) * (colors.length * 1.0))]);
				//				g2d.setColor(colors[(int) ((y / (float) (height - 60)) * (colors.length * 1.0))+((int)((height - 61)/tdiff+toffset))]);
				g2d.fillRect(width - 49, yStart, 9, 1);
			}
			//			String labelMin = df.format(Math.abs((int)(DataHolder.getMinFilter(4))));  // was tempSmallest
			//			String labelMax = df.format(Math.abs((int)(DataHolder.getMaxFilter(4))));  // was tempLargest
			String labelMin = df.format(Math.abs((int)(tempSmallest)));  // was tempSmallest
			String labelMax = df.format(Math.abs((int)(tempLargest)));  // was tempLargest
			//DONE.DEPR: HM) Hi: draw min and max and last selected markers

			int labelMinY = height - 35; //(31 + x * xDist) - 4 * labelMin.length();
			int labelMaxY = 40; //(31 + x * xDist) - 4 * labelMin.length();
			g2d.setColor(fg);
			g2d.drawString(labelMin, width - 35, labelMinY);
			g2d.drawString(labelMax, width - 35, labelMaxY);

			//			g2d.setColor(Color.cyan);
			//			g2d.drawRect(width-BORDER_R+12, BORDER_T, 5, 1);
			//			g2d.drawRect(width-BORDER_R+15, BORDER_T, 1, 2);
			//			
			//			g2d.drawRect(width-BORDER_R+12, getHeight()-BORDER_B, 5, 1);
			//			g2d.drawRect(width-BORDER_R+15, getHeight()-BORDER_B, 1, -2);

			float tCurrDiff = DataHolder.getMaxFilter(4)- DataHolder.getMinFilter(4);
			float tMaxDiff = tempLargest-tempSmallest;

			//			int tMinY = (int) ((DataHolder.getMinFilter(4)/tCurrDiff)+(getHeight()-BORDER_B-((int)((drawPanelHeight)*(tCurrDiff))/(tMaxDiff))));//getHeight()-BORDER_B - Math.abs((int)(DataHolder.getMinFilter(4)*tMaxDiff*1.0));
			//			int tMaxY = (int) ((DataHolder.getMinFilter(4)/tCurrDiff)+((int) BORDER_T+((int)((drawPanelHeight)*(tCurrDiff))/(tMaxDiff))));//BORDER_T + Math.abs((int)(DataHolder.getMaxFilter(4)*tMaxDiff*1.0));

			int tMinY = (int)((DataHolder.getMinFilter(4) - tempSmallest)*(drawPanelHeight/(tMaxDiff*1.0)));
			int tMaxY = drawPanelHeight-(int)((tempLargest - DataHolder.getMaxFilter(4))*(drawPanelHeight/(tMaxDiff*1.0)));
//			HeatMap.debug("tCurrDiff: "+tCurrDiff);
//			HeatMap.debug("tMaxDiff: "+tMaxDiff);
//			HeatMap.debug("MinY: "+tMinY);
//			HeatMap.debug("MaxY: "+tMaxY);

			//			Blank Gradient Bar
			g2d.setColor(Color.black);
			g2d.fillRect(width - 49, BORDER_T, 9, drawPanelHeight-tMaxY);	
			g2d.fillRect(width - 49, drawPanelHeight+BORDER_B-tMinY, 9, tMinY);

/*			if ((lastselectedX >= 0)&&(lastselectedY >= 0)){
				//				float tMaxDiff = tempLargest-tempSmallest;
				float tValue = getValue(getDPX(lastselectedX),getDPY(lastselectedY));
				int tValY = (int)(getHeight()-(tValue/(tMaxDiff*1.0)*drawPanelHeight));
				HeatMap.debug("drawing ball at y="+tValY);
				g2d.setColor(Color.black);
				g2d.fillRect(getWidth()-45, tValY, 8, 8);
			}*/

			//			HeatMap.debug(this.getUI().toString());
			//			HeatMap.debug("xAxis: "+xAxis+"; yAxis: "+yAxis);

		}


		//TO.DO.DEPR: HM) move into a more dynamic method
/*		HeatMap.debug("lastselected at x:"+lastselectedX+"; y:"+lastselectedY);

		if ((lastselectedX >= 0)&&(lastselectedY >= 0)){
			float tMaxDiff = tempLargest-tempSmallest;
			float tValue = getValue(getDPX(lastselectedX),getDPY(lastselectedY));
			int tValY = (int)(getHeight()-(tValue/(tMaxDiff*1.0)*drawPanelHeight));
			HeatMap.debug("drawing ball at y="+tValY);
			g2d.setColor(Color.black);
			g2d.fillRect(getWidth()-45, tValY, 8, 8);
			g2d.fillOval(getWidth() - 39, tValY, 5, 5);
			g2d.setColor(Color.cyan);
			g2d.fillOval(getWidth() - 39, tValY, 3, 3);
			//			lastselectedX = lastselectedY = -1;
		}
*/	}

}

/*************************************************************************************************************************************************************************/
/**
 * This function generates data that is not vertically-symmetric, which
 * makes it very useful for testing which type of vertical axis is being
 * used to plot the data. If the graphics Y-axis is used, then the lowest
 * values should be displayed at the top of the frame. If the non-graphics
 * (mathematical coordinate-system) Y-axis is used, then the lowest values
 * should be displayed at the bottom of the frame.
 * @return float[][] data values of a simple vertical ramp
 */
/*	public static float[][] generateRampTestData()
{
	float[][] data = new float[10][10];
	for (int x = 0; x < 10; x++)
	{
		for (int y = 0; y < 10; y++)
		{
			data[x][y] = y;
		}
	}

	return data;
}*/

/**
 * This function generates an appropriate data array for display. It uses
 * the function: z = sin(x)*cos(y). The parameter specifies the number
 * of data points in each direction, producing a square matrix.
 * @param dimension Size of each side of the returned array
 * @return float[][] calculated values of z = sin(x)*cos(y)
 */
/*	public static float[][] generateSinCosData(int dimension)
{
	if (dimension % 2 == 0)
	{
		dimension++; //make it better
	}

	float[][] data = new float[dimension][dimension];
	float sX, sY; //s for 'Scaled'

	for (int x = 0; x < dimension; x++)
	{
		for (int y = 0; y < dimension; y++)
		{
			sX = (float)(20 * Math.PI * (x / (float) dimension)); // 0 < sX < 2 * Pi
			sY = (float)(20 * Math.PI * (y / (float) dimension)); // 0 < sY < 2 * Pi
			data[x][y] = (float)(Math.sin(sX) * Math.cos(sY));
		}
	}

	return data;
}
 */
/**
 * This function generates an appropriate data array for display. It uses
 * the function: z = Math.cos(Math.abs(sX) + Math.abs(sY)). The parameter 
 * specifies the number of data points in each direction, producing a 
 * square matrix.
 * @param dimension Size of each side of the returned array
 * @return float[][] calculated values of z = Math.cos(Math.abs(sX) + Math.abs(sY));
 */
/*	public static float[][] generatePyramidData(int dimension)
{
	if (dimension % 2 == 0)
	{
		dimension++; //make it better
	}

	float[][] data = new float[dimension][dimension];
	float sX, sY; //s for 'Scaled'

	for (int x = 0; x < dimension; x++)
	{
		for (int y = 0; y < dimension; y++)
		{
			sX = 6 * (x / (float) dimension); // 0 < sX < 6
			sY = 6 * (y / (float) dimension); // 0 < sY < 6
			sX = sX - 3; // -3 < sX < 3
			sY = sY - 3; // -3 < sY < 3
			data[x][y] = (float)(Math.cos(Math.abs(sX) + Math.abs(sY)));
		}
	}

	return data;
}
 */