package Drawing3D;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_POINTS;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2.GL_COMPILE;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import Data.DataHolder;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
/**
 * 3+1 D scatter plotter with options for keeping a 4th dimension fixed
 * implements basic point selection and value filters
 * @author Benjamin
 */
public class Plotter3D extends GLJPanel implements GLEventListener,
				MouseMotionListener,MouseListener,MouseWheelListener{
	/**
	 * serial
	 */
	private static final long serialVersionUID = -7011190352085848963L;
	static final int FPS = 30;
	static final FPSAnimator animator = new FPSAnimator(FPS, true); 
	//Camera Variables
	float zoomFactor = 500;
	float angleX = 130;
	float angleY = -45;
	float focusX,focusY,focusZ;
	float zNear = 0.1f;
	float zFar = 10000;
	float heightScale = 1f;
	int wantTicksX = 5;
	int wantTicksH = 5;
	int wantTicksY = 5;
	int wantMinorTicksX = 5;
	int wantMinorTicksH = 5;
	int wantMinorTicksY = 5;
	double majorTickIntervalX;
	double majorTickIntervalH;
	double majorTickIntervalY;
	double scaleX = 1, scaleY = 1, scaleZ = 1;
	float[] axisColour = {1,1,1};
	int minorTickLength = 2;
	int majorTickLength = 6;
	int textOffsetFromAxis = 25;
	double textScale = 0.035;
	double percentOfTextScale = 1;
	//control variables,ChangeListener
	boolean isMouseRightDown = false;
	boolean isMouseLeftDown = false;
	int prevX, prevY;
	int currentX, currentY;
	//Axis variables:
	double globalMinX = 0;
	double globalMinY = 0;
	double globalMaxX = 100;
	double globalMaxY = 100;
	double globalMaxH = 100;
	double globalMinH = 0;
	double globalMaxC = 100;
	double globalMinC = 0;
	double axisOffset = 0;
	//Unproject setup:
	double[] projectionMatrix = new double[16];
	double[] viewMatrix = new double[16];
	int[] viewport = new int[4];
	double XI,YI,ZI;
	double[] worldspaceCoordNear = new double[4];
	double[] worldspaceCoordFar = new double[4];
	int axisList, pointsList;
	boolean firstRun = true;
	boolean shouldRecompileLists = true;
	boolean shouldRecompileAxis = false;
	
	String label1 = "X", label2 = "Y", label3 = "Z";
	/**
	 * Default constructor for 3D plotter
	 */
	public Plotter3D(){
		
		this.addGLEventListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		
		final GLJPanel me = this;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				animator.add(me);
				animator.start();
			}
		});
	}
	/**
	 * Computes a new set of axis and saves them to a display list
	 * @param gl
	 */
	private void createAxis(GL2 gl){
		gl.glNewList(axisList, GL_COMPILE);
		//select mins and maxs
		scaleX = 100/(globalMaxX - globalMinX);
		scaleY = 100/(globalMaxY - globalMinY);
		scaleZ = 100/(globalMaxH - globalMinH);
		
		double offSetX = -globalMinX*scaleX;
		double offSetY = -globalMinY*scaleY;
		double offSetH = -globalMinH*scaleZ;
		
		axisOffset = ((globalMaxX*scaleX - globalMinX*scaleX)*0.1f + (globalMaxY*scaleY - globalMinY*scaleY)*0.1f)*0.5f;
		this.majorTickIntervalX = (globalMaxX*scaleX - globalMinX*scaleX + axisOffset)/((float)this.wantTicksX-1);
		this.majorTickIntervalH = (globalMaxH*scaleZ - globalMinH*scaleZ)/((float)this.wantTicksH-1);
		this.majorTickIntervalY = (globalMaxY*scaleY - globalMinY*scaleY + axisOffset)/((float)this.wantTicksY-1);
		double minorTickIntervalX = this.majorTickIntervalX / (wantMinorTicksX+1);
		double minorTickIntervalH = this.majorTickIntervalH / (wantMinorTicksH+1);
		double minorTickIntervalY = this.majorTickIntervalY / (wantMinorTicksY+1);
		//draw axis
		gl.glColor3f(axisColour[0],axisColour[1],axisColour[2]);
		gl.glBegin(GL.GL_LINES);
		//X:
		gl.glVertex3d(globalMaxX*scaleX+offSetX,0,globalMinY*scaleY-axisOffset+offSetY);
		gl.glVertex3d(globalMinX*scaleX-axisOffset+offSetX,0,globalMinY*scaleY-axisOffset+offSetY);
		//Height:
		gl.glVertex3d(globalMaxX*scaleX+offSetX,globalMaxH*scaleZ + offSetH,globalMinY*scaleY-axisOffset+offSetY);
		gl.glVertex3d(globalMaxX*scaleX+offSetX,globalMinH*scaleZ + offSetH,globalMinY*scaleY-axisOffset+offSetY);
		//Z:
		gl.glVertex3d(globalMinX*scaleX-axisOffset+offSetX,0,globalMaxY*scaleY+offSetY);
		gl.glVertex3d(globalMinX*scaleX-axisOffset+offSetX,0,globalMinY*scaleY-axisOffset+offSetY);
		//X ruler:
		for(float i = 0; i < globalMaxX*scaleX - globalMinX*scaleX + axisOffset; i+=minorTickIntervalX){
			gl.glVertex3d(globalMinX*scaleX-axisOffset + i+offSetX,0,globalMinY*scaleY-axisOffset+offSetY);
			gl.glVertex3d(globalMinX*scaleX-axisOffset + i+offSetX,0,globalMinY*scaleY-axisOffset - minorTickLength+offSetY);
		}
		//Height ruler:
		for(float i = 0; i <= globalMaxH*scaleZ - globalMinH*scaleZ; i+=minorTickIntervalH){
			gl.glVertex3d(globalMaxX*scaleX+offSetX,globalMinH*scaleZ + i + offSetH ,globalMinY*scaleY-axisOffset+offSetY);
			gl.glVertex3d(globalMaxX*scaleX+offSetX,globalMinH*scaleZ + i + offSetH ,globalMinY*scaleY-axisOffset - minorTickLength+offSetY);
		}
		//Z ruler:
		for(float i = 0; i <= globalMaxY*scaleY - globalMinY*scaleY + axisOffset; i+=minorTickIntervalY){
			gl.glVertex3d(globalMinX*scaleX-axisOffset +offSetX,0,globalMinY*scaleY-axisOffset + i+offSetY);
			gl.glVertex3d(globalMinX*scaleX-axisOffset - minorTickLength +offSetX,0,globalMinY*scaleY-axisOffset + i+offSetY);
		}
		//Major ticks
		//X ruler:
		for(float i = 0; i <= globalMaxX*scaleX - globalMinX*scaleX + axisOffset; i+=majorTickIntervalX){
			gl.glVertex3d(globalMinX*scaleX-axisOffset + i+offSetX,0,globalMinY*scaleY-axisOffset+offSetY);
			gl.glVertex3d(globalMinX*scaleX-axisOffset + i+offSetX,0,globalMinY*scaleY-axisOffset - majorTickLength+offSetY);
		}
		//Height ruler:
		for(float i = 0; i <= globalMaxH*scaleZ - globalMinH*scaleZ; i+=majorTickIntervalH){
			gl.glVertex3d(globalMaxX*scaleX+offSetX,globalMinH*scaleZ + i + offSetH,globalMinY*scaleY-axisOffset+offSetY);
			gl.glVertex3d(globalMaxX*scaleX+offSetX,globalMinH*scaleZ + i + offSetH,globalMinY*scaleY-axisOffset - majorTickLength+offSetY);
		}
		//Z ruler:
		for(float i = 0; i <= globalMaxY*scaleY - globalMinY*scaleY + axisOffset; i+=majorTickIntervalY){
			gl.glVertex3d(globalMinX*scaleX-axisOffset+offSetX,0,globalMinY*scaleY-axisOffset + i+offSetY);
			gl.glVertex3d(globalMinX*scaleX-axisOffset - majorTickLength+offSetX,0,globalMinY*scaleY-axisOffset + i+offSetY);
		}
		gl.glEnd();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
		double stepX = 0, stepY = 0, stepH = 0;
		//Draws cutting plane and cutting line indicators onto the axis (depending on the fixed dimensions)
		switch(DataHolder.getFixedDimension(0)){
		case 0:
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(1)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(2)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			switch(DataHolder.getFixedDimension(1)){
			case 1:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 2:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				case 3:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 2:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 1:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 3:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 3:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 1:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 2:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			}
			break;
		case 1:
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(2)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			switch(DataHolder.getFixedDimension(1)){
			case 0:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 2:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				case 3:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 2:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 0:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 3:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 3:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 0:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 2:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			}
			break;
		case 2:
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(1)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			switch(DataHolder.getFixedDimension(1)){
			case 0:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 1:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				case 3:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 1:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 0:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 3:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 3:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 0:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 1:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			}
			break;
		case 3:
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(1)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(2)-1);
			switch(DataHolder.getFixedDimension(1)){
			case 0:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMinY*scaleY-axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(DataHolder.getFixedDimensionStep(1)*stepX,globalMaxY*scaleY+axisOffset+offSetY,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 1:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,globalMinH*scaleZ-axisOffset-minorTickLength+offSetY);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,globalMinH*scaleZ-axisOffset+minorTickLength+offSetY);
					break;
				case 2:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 1:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMinH*scaleZ-axisOffset+offSetH);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,DataHolder.getFixedDimensionStep(1)*stepH,globalMaxH*scaleZ+axisOffset+offSetH);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 0:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 2:
					gl.glVertex3d(-axisOffset-minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					gl.glVertex3d(-axisOffset+minorTickLength,0,DataHolder.getFixedDimensionStep(2)*stepY);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			case 2:
				gl.glColor4f(0, 0.5f, 0,0.2f);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMinY*scaleY-axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMinX*scaleX-axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glVertex3d(offSetX+globalMaxX*scaleX+axisOffset,globalMaxY*scaleY+axisOffset+offSetY,DataHolder.getFixedDimensionStep(1)*stepY);
				gl.glEnd();
				gl.glColor3f(1, 0, 1);
				gl.glBegin(GL.GL_LINES);
				switch(DataHolder.getFixedDimension(2)){
				case 0:
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset-minorTickLength);
					gl.glVertex3d(DataHolder.getFixedDimensionStep(2)*stepX,0,-axisOffset+minorTickLength);
					break;
				case 1:
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset-minorTickLength);
					gl.glVertex3d(offSetX+globalMaxX*scaleX,DataHolder.getFixedDimensionStep(2)*stepH,-axisOffset+minorTickLength);
					break;
				}
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
				break;
			}
			break;
		}
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		gl.glEnd();
		gl.glEndList();
	}
	/**
	 * Creates a display list for the points
	 * @param gl
	 */
	private void createPointsDisplayList(GL2 gl){
		gl.glNewList(pointsList, GL_COMPILE);
		gl.glBegin(GL_POINTS);		
		scaleX = 100/(globalMaxX - globalMinX);
		scaleY = 100/(globalMaxY - globalMinY);
		scaleZ = 100/(globalMaxH - globalMinH);
		double offSetX = 0;//globalMinX*scaleX;
		double offSetY = 0;
		double offSetH = 0;//globalMinH*scaleZ;
		switch(DataHolder.getFixedDimension(0)){
		case 0: 
			double stepX = (globalMaxX - globalMinX )*scaleX/(DataHolder.data.getLength(1)-1);
			double stepY = (globalMaxY - globalMinY )*scaleY/(DataHolder.data.getLength(2)-1);
			double stepH = (globalMaxH - globalMinH )*scaleZ/(DataHolder.data.getLength(3)-1);
			
			for (int i = (int)DataHolder.getMinFilter(1); i <= Math.min(DataHolder.data.getLength(1)-1,(int)DataHolder.getMaxFilter(1)); ++i)
				for (int j = (int)DataHolder.getMinFilter(2); j <= Math.min(DataHolder.data.getLength(2)-1,(int)DataHolder.getMaxFilter(2)); ++j)
					for (int k = (int)DataHolder.getMinFilter(3); k <= Math.min(DataHolder.data.getLength(3)-1,(int)DataHolder.getMaxFilter(3)); ++k)
					{
						if (DataHolder.data.getData()[DataHolder.getFixedDimensionStep(0)][i][j][k] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[DataHolder.getFixedDimensionStep(0)][i][j][k] > DataHolder.getMaxFilter(4))
							continue;
						
						float colVal = (DataHolder.data.getData()[DataHolder.getFixedDimensionStep(0)][i][j][k] - DataHolder.data.getMinData(4))/(DataHolder.data.getMaxData(4)-DataHolder.data.getMinData(4));
						if (colVal <= 0.5)
							gl.glColor3f((colVal)*2,
								(colVal),
								(0.5f-colVal));
						else
							gl.glColor3f(1,
									(1-colVal),
									0);
						gl.glVertex3d(i*stepX-offSetX,j*stepY-offSetY,k*stepH-offSetH);
					}		
			break;
		case 1: 
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(2)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			
			for (int i = (int)DataHolder.getMinFilter(0); i <= Math.min(DataHolder.data.getLength(0)-1,(int)DataHolder.getMaxFilter(0)); ++i)
				for (int j = (int)DataHolder.getMinFilter(2); j <= Math.min(DataHolder.data.getLength(2)-1,(int)DataHolder.getMaxFilter(2)); ++j)
					for (int k = (int)DataHolder.getMinFilter(3); k <= Math.min(DataHolder.data.getLength(3)-1,(int)DataHolder.getMaxFilter(3)); ++k)
					{
						if (DataHolder.data.getData()[i][DataHolder.getFixedDimensionStep(0)][j][k] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[i][DataHolder.getFixedDimensionStep(0)][j][k] > DataHolder.getMaxFilter(4))
							continue;
						float colVal = (DataHolder.data.getData()[i][DataHolder.getFixedDimensionStep(0)][j][k] - DataHolder.data.getMinData(4))/(DataHolder.data.getMaxData(4)-DataHolder.data.getMinData(4));
						if (colVal <= 0.5)
							gl.glColor3f((colVal)*2,
								(colVal),
								(0.5f-colVal));
						else
							gl.glColor3f(1,
									(1-colVal),
									0);
						gl.glVertex3d(i*stepX-offSetX,j*stepY-offSetY,k*stepH-offSetH);
					}
			break;
		case 2: 
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(1)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			
			for (int i = (int)DataHolder.getMinFilter(0); i <= Math.min(DataHolder.data.getLength(0)-1,(int)DataHolder.getMaxFilter(0)); ++i)
				for (int j = (int)DataHolder.getMinFilter(1); j <= Math.min(DataHolder.data.getLength(1)-1,(int)DataHolder.getMaxFilter(1)); ++j)
					for (int k = (int)DataHolder.getMinFilter(3); k <= Math.min(DataHolder.data.getLength(3)-1,(int)DataHolder.getMaxFilter(3)); ++k)
					{
						if (DataHolder.data.getData()[i][j][DataHolder.getFixedDimensionStep(0)][k] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[i][j][DataHolder.getFixedDimensionStep(0)][k] > DataHolder.getMaxFilter(4))
							continue;
						float colVal = (DataHolder.data.getData()[i][j][DataHolder.getFixedDimensionStep(0)][k] - DataHolder.data.getMinData(4))/(DataHolder.data.getMaxData(4)-DataHolder.data.getMinData(4));
						if (colVal <= 0.5)
							gl.glColor3f((colVal)*2,
								(colVal),
								(0.5f-colVal));
						else
							gl.glColor3f(1,
									(1-colVal),
									0);
						gl.glVertex3d(i*stepX-offSetX,j*stepY-offSetY,k*stepH-offSetH);
					}
			break;
		case 3:
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(1)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(2)-1);
			
			for (int i = (int)DataHolder.getMinFilter(0); i <= Math.min(DataHolder.data.getLength(0)-1,(int)DataHolder.getMaxFilter(0)); ++i)
				for (int j = (int)DataHolder.getMinFilter(1); j <= Math.min(DataHolder.data.getLength(1)-1,(int)DataHolder.getMaxFilter(1)); ++j)
					for (int k = (int)DataHolder.getMinFilter(2); k <= Math.min(DataHolder.data.getLength(2)-1,(int)DataHolder.getMaxFilter(2)); ++k)
					{
						if (DataHolder.data.getData()[i][j][k][DataHolder.getFixedDimensionStep(0)] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[i][j][k][DataHolder.getFixedDimensionStep(0)] > DataHolder.getMaxFilter(4))
							continue;
						float colVal = (DataHolder.data.getData()[i][j][k][DataHolder.getFixedDimensionStep(0)] - DataHolder.data.getMinData(4))/(DataHolder.data.getMaxData(4)-DataHolder.data.getMinData(4));
						if (colVal <= 0.5)
							gl.glColor3f((colVal)*2,
								(colVal),
								(0.5f-colVal));
						else
							gl.glColor3f(1,
									(1-colVal),
									0);
						gl.glVertex3d(i*stepX-offSetX,j*stepY-offSetY,k*stepH-offSetH);
					}
			break;
		}
		gl.glEnd();
		gl.glEndList();
	}
	/**
	 * Sets up the display lists according to what dimension is fixed
	 * @param gl
	 */
	private void loadDisplayLists(GL2 gl){
		//first set the global maxs and mins
		switch(DataHolder.getFixedDimension(0)){
		case 0: 
			globalMaxX = DataHolder.data.getMaxData(1);
			globalMaxY = DataHolder.data.getMaxData(2);
			globalMaxH = DataHolder.data.getMaxData(3);
			globalMaxC = DataHolder.data.getMaxData(4);
			globalMinX = DataHolder.data.getMinData(1);
			globalMinY = DataHolder.data.getMinData(2);
			globalMinH = DataHolder.data.getMinData(3);
			globalMinC = DataHolder.data.getMinData(4);
			label1 = DataHolder.data.getDimensionName(1);
			label2 = DataHolder.data.getDimensionName(2);
			label3 = DataHolder.data.getDimensionName(3);
			break;
		case 1: 
			globalMaxX = DataHolder.data.getMaxData(0);
			globalMaxY = DataHolder.data.getMaxData(2);
			globalMaxH = DataHolder.data.getMaxData(3);
			globalMaxC = DataHolder.data.getMaxData(4);
			globalMinX = DataHolder.data.getMinData(0);
			globalMinY = DataHolder.data.getMinData(2);
			globalMinH = DataHolder.data.getMinData(3);
			globalMinC = DataHolder.data.getMinData(4);
			label1 = DataHolder.data.getDimensionName(0);
			label2 = DataHolder.data.getDimensionName(2);
			label3 = DataHolder.data.getDimensionName(3);
			break;
		case 2: 
			globalMaxX = DataHolder.data.getMaxData(0);
			globalMaxY = DataHolder.data.getMaxData(1);
			globalMaxH = DataHolder.data.getMaxData(3);
			globalMaxC = DataHolder.data.getMaxData(4);
			globalMinX = DataHolder.data.getMinData(0);
			globalMinY = DataHolder.data.getMinData(1);
			globalMinH = DataHolder.data.getMinData(3);
			globalMinC = DataHolder.data.getMinData(4);
			label1 = DataHolder.data.getDimensionName(0);
			label2 = DataHolder.data.getDimensionName(1);
			label3 = DataHolder.data.getDimensionName(3);
			break;
		case 3:
			globalMaxX = DataHolder.data.getMaxData(0);
			globalMaxY = DataHolder.data.getMaxData(1);
			globalMaxH = DataHolder.data.getMaxData(2);
			globalMaxC = DataHolder.data.getMaxData(4);
			globalMinX = DataHolder.data.getMinData(0);
			globalMinY = DataHolder.data.getMinData(1);
			globalMinH = DataHolder.data.getMinData(2);
			globalMinC = DataHolder.data.getMinData(4);
			label1 = DataHolder.data.getDimensionName(0);
			label2 = DataHolder.data.getDimensionName(1);
			label3 = DataHolder.data.getDimensionName(2);
			break;
		}
		//delete display lists if necessary
		if (!firstRun){
			gl.glDeleteLists(pointsList, 1);
			gl.glDeleteLists(axisList, 1);
		} else firstRun = false;
		pointsList = gl.glGenLists(1);
		axisList = gl.glGenLists(1);
		
		createAxis(gl);
		createPointsDisplayList(gl);
	}
	/**
	 * Draws the text on the axis
	 * @param gl
	 */
	private void drawRullerText(GL2 gl){
		double offSetX = -globalMinX*scaleX;
		double offSetY = -globalMinY*scaleY;
		double offSetH = -globalMinH*scaleZ;
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0.00000");
		gl.glScaled(1, 1/this.heightScale, 1);
		gl.glColor3f(axisColour[0],axisColour[1],axisColour[2]);
		//Height ruler:
		for(float i = 0; i <= globalMaxH*scaleZ - globalMinH*scaleZ; i += majorTickIntervalH){
			gl.glPushMatrix();

			gl.glTranslated(globalMaxX*scaleX+offSetX,
					(i + globalMinH*scaleZ +offSetH)*this.heightScale,
					globalMinY*scaleY - axisOffset - majorTickLength-textOffsetFromAxis+offSetY);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
			
			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i/scaleZ+globalMinH));
			gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslated(globalMaxX*scaleX+offSetX,
				(globalMaxH*scaleZ+globalMinH*scaleZ)*0.5*heightScale+offSetH,
				globalMinY*scaleY - majorTickLength - textOffsetFromAxis*3+offSetY);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		gl.glColor3f(1, 0, 1);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, ""+label2);
		gl.glColor3f(axisColour[0],axisColour[1],axisColour[2]);
		gl.glPopMatrix();
		//X ruler:
		for(float i = 0; i <= globalMaxX*scaleX - globalMinX*scaleX + axisOffset; i += majorTickIntervalX){
			gl.glPushMatrix();

			gl.glTranslated(globalMinX*scaleX-axisOffset + i+offSetX,
					0,
					globalMinY*scaleY - axisOffset -majorTickLength - majorTickLength*1.8+offSetY);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i/scaleX+globalMinX - axisOffset/scaleX));
			gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslated((globalMinX*scaleX+globalMaxX*scaleX-axisOffset*2)*0.5+offSetX,
				0,
				globalMinY*scaleY - axisOffset*2.5 -majorTickLength - textOffsetFromAxis*1.3+offSetY);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		gl.glColor3f(1, 0, 1);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, ""+label1);
		gl.glColor3f(axisColour[0],axisColour[1],axisColour[2]);
		gl.glPopMatrix();
		//Z ruler:
		for(float i = 0; i <= globalMaxY*scaleY - globalMinY*scaleY + axisOffset; i += majorTickIntervalY){
			gl.glPushMatrix();

			gl.glTranslated(globalMinX*scaleX - axisOffset - majorTickLength+offSetX,
					0,
					globalMinY*scaleY-axisOffset + i+offSetY);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i/scaleY+globalMinY - axisOffset/scaleY));
			gl.glPopMatrix();
		}
		
		gl.glPushMatrix();
		gl.glTranslated(globalMinX*scaleX - axisOffset*2.5 -majorTickLength - textOffsetFromAxis*1.3+offSetX,
				0,
				(globalMinY*scaleY+globalMaxY*scaleY-axisOffset*2)*0.5+offSetY);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		gl.glColor3f(1, 0, 1);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, ""+label3);
		gl.glColor3f(axisColour[0],axisColour[1],axisColour[2]);
		gl.glPopMatrix();
		
		//Draw text:
		df.applyPattern("0.00");
		gl.glPopMatrix();
		gl.glOrtho(-1, 1, -1, 1, -1, 1);
		gl.glTranslated(0, 0, 0.1);
		gl.glColor3f(0,0,0);
	}
	/*********************************************************************************************************************************************************
	 * OPENGL SETUP SECTION
	 *********************************************************************************************************************************************************/
	
	// Setup OpenGL Graphics Renderer:
	// ------ Implement methods declared in GLEventListener ------
	private GLU glu;  // for the GL Utility
	private GLUT glut;
	/**
	 * Called back immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
		glu = new GLU();                         // get GL Utilities
		glut = new GLUT();						//Utilities Toolkit
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f);      // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
		gl.glShadeModel(GL_SMOOTH);
		gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glPointSize(2);
	}

	/**
	 * Call-back handler for window re-size event. Also called when the drawable is
	 * first set to visible.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

		if (height == 0) height = 1;   // prevent divide by zero
		float aspect = (float)width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
		gl.glLoadIdentity();             // reset projection matrix
		glu.gluPerspective(45.0, aspect, zNear, zFar); // fovy, aspect, zNear, zFar
		
		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
	}
	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
		if (shouldRecompileLists){
			loadDisplayLists(gl);
			shouldRecompileLists = false;
		}
		if (shouldRecompileAxis){
			gl.glDeleteLists(axisList, 1); //by this point a display list already exists
			axisList = gl.glGenLists(1);
			createAxis(gl);
			shouldRecompileAxis = false;
		}
		gl.glClearColor(1-axisColour[0], 1-axisColour[1], 1-axisColour[2], 1);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
		gl.glLoadIdentity();  // reset the model-view matrix
		//Scale, Rotate and translate world:
		gl.glPushMatrix();
		gl.glTranslated(0,0,-zoomFactor);
			
		gl.glRotatef(angleY,-1,0,0);
		gl.glRotatef(angleX,0,1,0);
		gl.glTranslated(-(globalMaxX-globalMinX)*scaleX/2,-(globalMaxY-globalMinY)*scaleY/2,-(globalMaxH-globalMinH)*scaleZ/2);
		
		//save the matrices' states
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, viewMatrix, 0);
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		//Draw points
		gl.glCallList(pointsList);
		//draw selected point in axis color:
				if (DataHolder.getSelectedPoint() != null){
					gl.glColor3f(axisColour[0], axisColour[1], axisColour[2]);
					gl.glPointSize(5);
					gl.glBegin(GL_POINTS);
					switch(DataHolder.getFixedDimension(0)){
					case 0:
						gl.glVertex3d(DataHolder.getSelectedPoint()[1]*scaleX,
								DataHolder.getSelectedPoint()[2]*scaleY,
								DataHolder.getSelectedPoint()[3]*scaleZ);
						break;
					case 1:
						gl.glVertex3d(DataHolder.getSelectedPoint()[0]*scaleX,
								DataHolder.getSelectedPoint()[2]*scaleY,
								DataHolder.getSelectedPoint()[3]*scaleZ);
						break;
					case 2:
						gl.glVertex3d(DataHolder.getSelectedPoint()[0]*scaleX,
								DataHolder.getSelectedPoint()[1]*scaleY,
								DataHolder.getSelectedPoint()[3]*scaleZ);
						break;
					case 3:
						gl.glVertex3d(DataHolder.getSelectedPoint()[0]*scaleX,
								DataHolder.getSelectedPoint()[1]*scaleY,
								DataHolder.getSelectedPoint()[2]*scaleZ);
						break;
					}
					gl.glEnd();
					gl.glPointSize(2);
				}
		//Draw the rotating text on the rulers:
		gl.glCallList(axisList);
		drawRullerText(gl);
		
		//Draw color range:
		gl.glMatrixMode(GL_PROJECTION); //setup ortho in the projection matrix
		gl.glPushMatrix(); //save perspective matrix
		gl.glLoadIdentity();
		gl.glOrtho(0.0, getWidth(), 0.0, getHeight(), -1.0, 1.0);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		//draw a coloured strip (blue to yellow to red)
		gl.glBegin(GL2.GL_QUAD_STRIP);
		gl.glColor3f(0, 0, 1);
		gl.glVertex2f(0,0);
		gl.glVertex2f(0,15f);
		gl.glColor3f(1, 1, 0);
		gl.glVertex2f(getWidth()/2,0);
		gl.glVertex2f(getWidth()/2,15f);
		gl.glColor3f(1, 0, 0);
		gl.glVertex2f(getWidth(),0);
		gl.glVertex2f(getWidth(),15f);
		gl.glEnd();
		gl.glColor3f(axisColour[0], axisColour[1], axisColour[2]);
		if (DataHolder.getSelectedPoint() != null){
			//Draw triangle at the heat of the selected point:
			double xMid = (DataHolder.getSelectedPoint()[4]-DataHolder.data.getMinData(4))/(DataHolder.data.getMaxData(4)-DataHolder.data.getMinData(4))*getWidth();
			gl.glBegin(GL2.GL_TRIANGLES);
			gl.glVertex2d(xMid-5, 20);
			gl.glVertex2d(xMid, 15);
			gl.glVertex2d(xMid+5, 20);
			gl.glEnd();
		}
		
		//draw min, max and selected heat as text on the strip:
		gl.glColor3f(1, 1, 1);
		gl.glDisable(GL_DEPTH_TEST);
		gl.glRasterPos2i(3, 3);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, ""+String.format("%.3f%n", DataHolder.data.getMinData(4)));
		String max = String.format("%.3f%n", DataHolder.data.getMaxData(4));
		gl.glRasterPos2i(getWidth()-max.length()*5, 3);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, ""+max);
		//draw heat text
		if (DataHolder.getSelectedPoint() != null){
			String heat = String.format("%.3f%n", DataHolder.getSelectedPoint()[4]);
			float x = (DataHolder.getSelectedPoint()[4]-DataHolder.data.getMinData(4))/(DataHolder.data.getMaxData(4)-DataHolder.data.getMinData(4))*getWidth()
					-heat.length()*3/2;
			float overlap = x <= 0 ? x : 
				x+heat.length()*4 > getWidth() ? x+heat.length()*4 - getWidth() : 0;
			gl.glColor3f(1-axisColour[0], 1-axisColour[1], 1-axisColour[2]);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(x, 35);
			gl.glVertex2d(x, 25);
			gl.glVertex2d(x+heat.length()*4, 25);
			gl.glVertex2d(x+heat.length()*4, 35);
			gl.glEnd();
			gl.glColor3f(axisColour[0], axisColour[1], axisColour[2]);
			gl.glRasterPos2i((int)Math.ceil(x - overlap), 
					25);
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, ""+heat);
		}
		gl.glEnable(GL_DEPTH_TEST);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPopMatrix(); //back to perspective matrix
		gl.glMatrixMode(GL_MODELVIEW); //back to model view matrix multiplication
	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such as buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) { animator.stop(); }
	/**
	 * Does a rotation according to mouse movement
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (isMouseRightDown){
			angleX -= (arg0.getX()-prevX);
			angleY += (arg0.getY()-prevY);
			if (angleX > 360)
				angleX -= 360;
			else if (angleX < -360)
				angleX += 360;
			if (angleY > 360)
				angleY -= 360;
			else if (angleY < -360)
				angleY += 360;
			prevX = arg0.getX();
			prevY = arg0.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		select(arg0);
	}
	/**
	 * Selects a point in 3D space by unprojecting the cursor point on the near and far planes and then
	 * linearly looping (may be inefficient!!!) through the points and testing distance between point and line
	 * to determine if the user has clicked near the point in unprojected 3space.
	 * @param arg0 MouseEvent
	 */
	private void select(MouseEvent arg0){
		float realy = viewport[3] - (int) arg0.getY() - 1;
		glu.gluUnProject(arg0.getX(),realy, 0.1,viewMatrix,0,projectionMatrix,0,viewport,0,worldspaceCoordNear,0);
		glu.gluUnProject(arg0.getX(),realy, 1,viewMatrix,0,projectionMatrix,0,viewport,0,worldspaceCoordFar,0);			

		double vX = - worldspaceCoordNear[0] + worldspaceCoordFar[0];
		double vY = - worldspaceCoordNear[1] + worldspaceCoordFar[1];
		double vZ = - worldspaceCoordNear[2] + worldspaceCoordFar[2];
		double vDist = Math.sqrt(vX*vX + vY*vY + vZ*vZ);
		Vector<Float[]> pointList = new Vector<Float[]>();
		
		//foreach point in 3 unfixed dimensions
		switch(DataHolder.getFixedDimension(0)){
		case 0:
			double stepD = (DataHolder.data.getMaxData(0) - DataHolder.data.getMinData(0))/DataHolder.data.getLength(0);
			double stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(1)-1);
			double stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(2)-1);
			double stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			
			for (int i = (int)DataHolder.getMinFilter(1); i <= Math.min(DataHolder.data.getLength(1)-1,(int)DataHolder.getMaxFilter(1)); ++i)
				for (int j = (int)DataHolder.getMinFilter(2); j <= Math.min(DataHolder.data.getLength(2)-1,(int)DataHolder.getMaxFilter(2)); ++j)
					for (int k = (int)DataHolder.getMinFilter(3); k <= Math.min(DataHolder.data.getLength(3)-1,(int)DataHolder.getMaxFilter(3)); ++k)
					{
						if (DataHolder.data.getData()[DataHolder.getFixedDimensionStep(0)][i][j][k] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[DataHolder.getFixedDimensionStep(0)][i][j][k] > DataHolder.getMaxFilter(4))
							continue;
						
						Float[] pt = {
								new Float(DataHolder.data.getMinData(0) + DataHolder.getFixedDimensionStep(0)*stepD),
								new Float(DataHolder.data.getMinData(1) + i*stepX),
								new Float(DataHolder.data.getMinData(2) + j*stepY),
								new Float(DataHolder.data.getMinData(3) + k*stepH),
								new Float(DataHolder.data.getData()[DataHolder.getFixedDimensionStep(0)][i][j][k])};
						//distance point to line in 3D:
						double dX = worldspaceCoordNear[0] - pt[1];
						double dY = worldspaceCoordNear[1] - pt[2];
						double dZ = worldspaceCoordNear[2] - pt[3];
						double cX = dY*vZ - dZ*vY;
						double cY = dZ*vX - dX*vZ;
						double cZ = dX*vY - dY*vX;
						double dist = Math.sqrt(cX*cX + cY*cY + cZ*cZ)/vDist;
				
						if (dist < 1)
							pointList.add(pt);
					}
			break;
		case 1:
			stepD = (DataHolder.data.getMaxData(1) - DataHolder.data.getMinData(1))/DataHolder.data.getLength(1);
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(2)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			
			for (int i = (int)DataHolder.getMinFilter(0); i <= Math.min(DataHolder.data.getLength(0)-1,(int)DataHolder.getMaxFilter(0)); ++i)
				for (int j = (int)DataHolder.getMinFilter(2); j <= Math.min(DataHolder.data.getLength(2)-1,(int)DataHolder.getMaxFilter(2)); ++j)
					for (int k = (int)DataHolder.getMinFilter(3); k <= Math.min(DataHolder.data.getLength(3)-1,(int)DataHolder.getMaxFilter(3)); ++k)
					{
						if (DataHolder.data.getData()[i][DataHolder.getFixedDimensionStep(0)][j][k] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[i][DataHolder.getFixedDimensionStep(0)][j][k] > DataHolder.getMaxFilter(4))
							continue;
						
						Float[] pt = {new Float(DataHolder.data.getMinData(0) + i*stepX),
								new Float(DataHolder.data.getMinData(1) + DataHolder.getFixedDimensionStep(1)*stepD),
								new Float(DataHolder.data.getMinData(2) + j*stepY),
								new Float(DataHolder.data.getMinData(3) + k*stepH),
								new Float(DataHolder.data.getData()[i][DataHolder.getFixedDimensionStep(0)][j][k])};
						//distance point to line in 3D:
						double dX = worldspaceCoordNear[0] - pt[0];
						double dY = worldspaceCoordNear[1] - pt[2];
						double dZ = worldspaceCoordNear[2] - pt[3];
						double cX = dY*vZ - dZ*vY;
						double cY = dZ*vX - dX*vZ;
						double cZ = dX*vY - dY*vX;
						double dist = Math.sqrt(cX*cX + cY*cY + cZ*cZ)/vDist;
				
						if (dist < 1)
							pointList.add(pt);
					}
			break;
		case 2:
			stepD = (DataHolder.data.getMaxData(2) - DataHolder.data.getMinData(2))/DataHolder.data.getLength(2);
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(1)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(3)-1);
			
			for (int i = (int)DataHolder.getMinFilter(0); i <= Math.min(DataHolder.data.getLength(0)-1,(int)DataHolder.getMaxFilter(0)); ++i)
				for (int j = (int)DataHolder.getMinFilter(1); j <= Math.min(DataHolder.data.getLength(1)-1,(int)DataHolder.getMaxFilter(1)); ++j)
					for (int k = (int)DataHolder.getMinFilter(3); k <= Math.min(DataHolder.data.getLength(3)-1,(int)DataHolder.getMaxFilter(3)); ++k)
					{
						if (DataHolder.data.getData()[i][j][DataHolder.getFixedDimensionStep(0)][k] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[i][j][DataHolder.getFixedDimensionStep(0)][k] > DataHolder.getMaxFilter(4))
							continue;
						
						Float[] pt = {new Float(DataHolder.data.getMinData(0) + i*stepX),
								new Float(DataHolder.data.getMinData(1) + j*stepY),
								new Float(DataHolder.data.getMinData(2) + DataHolder.getFixedDimensionStep(2)*stepD),
								new Float(DataHolder.data.getMinData(3) + k*stepH),
								new Float(DataHolder.data.getData()[i][j][DataHolder.getFixedDimensionStep(0)][k])};
						//distance point to line in 3D:
						double dX = worldspaceCoordNear[0] - pt[0];
						double dY = worldspaceCoordNear[1] - pt[1];
						double dZ = worldspaceCoordNear[2] - pt[3];
						double cX = dY*vZ - dZ*vY;
						double cY = dZ*vX - dX*vZ;
						double cZ = dX*vY - dY*vX;
						double dist = Math.sqrt(cX*cX + cY*cY + cZ*cZ)/vDist;
				
						if (dist < 1)
							pointList.add(pt);
					}
			break;
		case 3:
			stepD = (DataHolder.data.getMaxData(3) - DataHolder.data.getMinData(3))/DataHolder.data.getLength(3);
			stepX = (globalMaxX - globalMinX)*scaleX/(DataHolder.data.getLength(0)-1);
			stepY = (globalMaxY - globalMinY)*scaleY/(DataHolder.data.getLength(1)-1);
			stepH = (globalMaxH - globalMinH)*scaleZ/(DataHolder.data.getLength(2)-1);
			
			for (int i = (int)DataHolder.getMinFilter(0); i <= Math.min(DataHolder.data.getLength(0)-1,(int)DataHolder.getMaxFilter(0)); ++i)
				for (int j = (int)DataHolder.getMinFilter(1); j <= Math.min(DataHolder.data.getLength(1)-1,(int)DataHolder.getMaxFilter(1)); ++j)
					for (int k = (int)DataHolder.getMinFilter(2); k <= Math.min(DataHolder.data.getLength(2)-1,(int)DataHolder.getMaxFilter(2)); ++k)
					{
						if (DataHolder.data.getData()[i][j][k][DataHolder.getFixedDimensionStep(0)] < DataHolder.getMinFilter(4) || 
								DataHolder.data.getData()[i][j][k][DataHolder.getFixedDimensionStep(0)] > DataHolder.getMaxFilter(4))
							continue;
						
						Float[] pt = {new Float(DataHolder.data.getMinData(0) + i*stepX),
								new Float(DataHolder.data.getMinData(1) + j*stepY),
								new Float(DataHolder.data.getMinData(2) + k*stepH),
								new Float(DataHolder.data.getMinData(3) + DataHolder.getFixedDimensionStep(3)*stepD),
								new Float(DataHolder.data.getData()[i][j][k][DataHolder.getFixedDimensionStep(0)])};
						//distance point to line in 3D:
						double dX = worldspaceCoordNear[0] - pt[0];
						double dY = worldspaceCoordNear[1] - pt[1];
						double dZ = worldspaceCoordNear[2] - pt[2];
						double cX = dY*vZ - dZ*vY;
						double cY = dZ*vX - dX*vZ;
						double cZ = dX*vY - dY*vX;
						double dist = Math.sqrt(cX*cX + cY*cY + cZ*cZ)/vDist;
				
						if (dist < 1)
							pointList.add(pt);
					}
			break;
		}
		
		//find the closest point the the viewers near plane:
		Float[] ptNearest = null;
		double nearestSq = 0;
		if (pointList.size() > 0){
			ptNearest = pointList.elementAt(0);
			switch(DataHolder.getFixedDimension(0)){
			case 0:
				nearestSq = (ptNearest[1] - worldspaceCoordNear[0])*(ptNearest[1] - worldspaceCoordNear[0]) +
					(ptNearest[2] - worldspaceCoordNear[1])*(ptNearest[2] - worldspaceCoordNear[1]) +
					(ptNearest[3] - worldspaceCoordNear[2])*(ptNearest[3] - worldspaceCoordNear[2]);
				break;
			case 1:
				nearestSq = (ptNearest[0] - worldspaceCoordNear[0])*(ptNearest[0] - worldspaceCoordNear[0]) +
				(ptNearest[2] - worldspaceCoordNear[1])*(ptNearest[2] - worldspaceCoordNear[1]) +
				(ptNearest[3] - worldspaceCoordNear[2])*(ptNearest[3] - worldspaceCoordNear[2]);
				break;
			case 2:
				nearestSq = (ptNearest[0] - worldspaceCoordNear[0])*(ptNearest[0] - worldspaceCoordNear[0]) +
				(ptNearest[1] - worldspaceCoordNear[1])*(ptNearest[1] - worldspaceCoordNear[1]) +
				(ptNearest[3] - worldspaceCoordNear[2])*(ptNearest[3] - worldspaceCoordNear[2]);
				break;
			case 3:
				nearestSq = (ptNearest[0] - worldspaceCoordNear[0])*(ptNearest[0] - worldspaceCoordNear[0]) +
				(ptNearest[1] - worldspaceCoordNear[1])*(ptNearest[1] - worldspaceCoordNear[1]) +
				(ptNearest[2] - worldspaceCoordNear[2])*(ptNearest[2] - worldspaceCoordNear[2]);
				break;
			}
			for (int i = 1; i < pointList.size(); ++i){
				Float[] ptQ = pointList.elementAt(i);
				double distSqQ = 0;
				switch(DataHolder.getFixedDimension(0)){
				case 0:
					distSqQ = (ptQ[1] - worldspaceCoordNear[0])*(ptQ[1] - worldspaceCoordNear[0]) +
						(ptQ[2] - worldspaceCoordNear[1])*(ptQ[2] - worldspaceCoordNear[1]) +
						(ptQ[3] - worldspaceCoordNear[2])*(ptQ[3] - worldspaceCoordNear[2]);
					break;
				case 1:
					distSqQ = (ptQ[0] - worldspaceCoordNear[0])*(ptQ[0] - worldspaceCoordNear[0]) +
					(ptQ[2] - worldspaceCoordNear[1])*(ptQ[2] - worldspaceCoordNear[1]) +
					(ptQ[3] - worldspaceCoordNear[2])*(ptQ[3] - worldspaceCoordNear[2]);
					break;
				case 2:
					distSqQ = (ptQ[0] - worldspaceCoordNear[0])*(ptQ[0] - worldspaceCoordNear[0]) +
					(ptQ[1] - worldspaceCoordNear[1])*(ptQ[1] - worldspaceCoordNear[1]) +
					(ptQ[3] - worldspaceCoordNear[2])*(ptQ[3] - worldspaceCoordNear[2]);
					break;
				case 3:
					distSqQ = (ptQ[0] - worldspaceCoordNear[0])*(ptQ[0] - worldspaceCoordNear[0]) +
					(ptQ[1] - worldspaceCoordNear[1])*(ptQ[1] - worldspaceCoordNear[1]) +
					(ptQ[2] - worldspaceCoordNear[2])*(ptQ[2] - worldspaceCoordNear[2]);
					break;
				}
				if (distSqQ < nearestSq){
					nearestSq = distSqQ;
					ptNearest = ptQ;
				}
			}
			switch(DataHolder.getFixedDimension(0)){
			case 0:
				ptNearest[1] /= (float)scaleX;
				ptNearest[2] /= (float)scaleY;
				ptNearest[3] /= (float)scaleZ;
				break;
			case 1:
				ptNearest[0] /= (float)scaleX;
				ptNearest[2] /= (float)scaleY;
				ptNearest[3] /= (float)scaleZ;
				break;
			case 2:
				ptNearest[0] /= (float)scaleX;
				ptNearest[1] /= (float)scaleY;
				ptNearest[3] /= (float)scaleZ;
				break;
			case 3:
				ptNearest[0] /= (float)scaleX;
				ptNearest[1] /= (float)scaleY;
				ptNearest[2] /= (float)scaleZ;
				break;
			}
		}
		DataHolder.setSelectedPoint(ptNearest);
		/*if (ptNearest != null)
			((frmPlot)this.getParent().getParent().getParent().getParent()).setTitle(
					String.format("3D Viewer --- Selected [%.3f%n,%.3f%n,%.3f%n,%.3f%n,%.3f%n]", 
					ptNearest[0],ptNearest[1],ptNearest[2],ptNearest[3],ptNearest[4]));*/
		/*double vLength = Math.sqrt(vX*vX + vY*vY + vZ*vZ);		
		vX = vX / vLength;
		vY = vY / vLength;
		vZ = vZ / vLength;
		double t = (-worldspaceCoordNear[1])/(vY+0.000000000001);
		
		XI = (worldspaceCoordNear[0] + t*vX);
		YI = 0; //XZ-Plane
		ZI = (worldspaceCoordNear[2] + t*vZ);*/
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		isMouseRightDown = false;
		isMouseLeftDown = false;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		isMouseRightDown = false;
		isMouseLeftDown = false;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON3)
		{
			prevX = arg0.getX();
			prevY = arg0.getY();
			isMouseRightDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON3)
			isMouseRightDown = false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		zoomFactor += arg0.getWheelRotation()*10;
		zoomFactor = Math.max(zoomFactor,0.5f);
	}
	/////////////////////////////////////////////////////////
	//Accessors and mutators
	/////////////////////////////////////////////////////////
	public void setNumTicksX(int x){
		this.wantTicksX = x;
		this.shouldRecompileAxis = true;
	}
	public void setNumTicksZ(int z){
		this.wantTicksY = z;
		this.shouldRecompileAxis = true;
	}
	public void setNumTicksH(int h){
		this.wantTicksH = h;
		this.shouldRecompileAxis = true;
	}
	public int getNumTicksX(){
		return this.wantTicksX;
	}
	public int getNumTicksH(){
		return this.wantTicksH;
	}
	public int getNumTicksZ(){
		return this.wantTicksY;
	}
	public void setNumMinorTicksX(int x){
		this.wantMinorTicksX = x;
		this.shouldRecompileAxis = true;
	}
	public void setNumMinorTicksZ(int z){
		this.wantMinorTicksY = z;
		this.shouldRecompileAxis = true;
	}
	public void setNumMinorTicksH(int h){
		this.wantMinorTicksH = h;
		this.shouldRecompileAxis = true;
	}
	public int getNumMinorTicksX(){
		return this.wantMinorTicksX;
	}
	public int getNumMinorTicksH(){
		return this.wantMinorTicksH;
	}
	public int getNumMinorTicksZ(){
		return this.wantMinorTicksY;
	}
	public void setTextZoom(double value){
		percentOfTextScale = value;
	}
	public void setZScale(float scale){
		heightScale = scale;
	}
	public float getZScale(){
		return heightScale;
	}
	public void reload(){
		shouldRecompileLists = true;
	}
	public void setDarkTheme(boolean dark){
		if (dark){
			axisColour[0] = 1;
			axisColour[1] = 1;
			axisColour[2] = 1;
		} else {
			axisColour[0] = 0;
			axisColour[1] = 0;
			axisColour[2] = 0;
		}
		shouldRecompileAxis = true;
	}
}
