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
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;


import Data.DataChangeListener;
import Data.Loader;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
public class Plotter3D extends GLJPanel implements GLEventListener,
				MouseMotionListener,MouseListener,MouseWheelListener,DataChangeListener{
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
	int minorTickLength = 2;
	int majorTickLength = 6;
	int textOffsetFromAxis = 25;
	double textScale = 0.045;
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
	//Pin Setup:
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
		this.majorTickIntervalX = (globalMaxX - globalMinX)/(float)this.wantTicksX;
		this.majorTickIntervalH = (globalMaxH - globalMinH)/(float)this.wantTicksH;
		this.majorTickIntervalY = (globalMaxY - globalMinY)/(float)this.wantTicksY;
		double minorTickIntervalX = this.majorTickIntervalX / (wantMinorTicksX+1);
		double minorTickIntervalH = this.majorTickIntervalH / (wantMinorTicksH+1);
		double minorTickIntervalY = this.majorTickIntervalY / (wantMinorTicksY+1);
		axisOffset = ((globalMaxX - globalMinX)*0.1f + (globalMaxY - globalMinY)*0.1f)*0.5f;
		//draw axis
		gl.glColor3f(1,1,1);
		gl.glBegin(GL.GL_LINES);

		//X:
		gl.glVertex3d(globalMaxX,0,globalMinY-axisOffset);
		gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset);
		//Height:
		gl.glVertex3d(globalMaxX,globalMaxH,globalMinY-axisOffset);
		gl.glVertex3d(globalMaxX,globalMinH,globalMinY-axisOffset);
		//Z:
		gl.glVertex3d(globalMinX-axisOffset,0,globalMaxY);
		gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset);
		//X ruler:
		for(float i = 0; i < globalMaxX - globalMinX + axisOffset; i+=minorTickIntervalX){
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset);
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset - minorTickLength);
		}
		//Height ruler:
		for(float i = 0; i < globalMaxH - globalMinH; i+=minorTickIntervalH){
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset);
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset + minorTickLength);
		}
		//Z ruler:
		for(float i = 0; i < globalMaxY - globalMinY + axisOffset; i+=minorTickIntervalY){
			gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset + i);
			gl.glVertex3d(globalMinX-axisOffset - minorTickLength,0,globalMinY-axisOffset + i);
		}
		//Major ticks
		//X ruler:
		for(float i = 0; i < globalMaxX - globalMinX + axisOffset; i+=majorTickIntervalX){
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset);
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset - majorTickLength);
		}
		//Height ruler:
		for(float i = 0; i < globalMaxH - globalMinH; i+=majorTickIntervalH){
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset);
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset + majorTickLength);
		}
		//Z ruler:
		for(float i = 0; i < globalMaxY - globalMinY + axisOffset; i+=majorTickIntervalY){
			gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset + i);
			gl.glVertex3d(globalMinX-axisOffset - majorTickLength,0,globalMinY-axisOffset + i);
		}
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
		double offSetX = 0;
		double offSetY = 0;
		double offSetH = 0;
		
		switch(Loader.getFixedDimension(0)){
		case 0: 
			double stepX = (globalMaxX - globalMinX)/Loader.getSize(1);
			double stepY = (globalMaxY - globalMinY)/Loader.getSize(2);
			double stepH = (globalMaxH - globalMinH)/Loader.getSize(3);
			
			for (int i = Loader.getMinFilter(1); i <= Math.min(Loader.getSize(1)-1,Loader.getMaxFilter(1)); ++i)
				for (int j = Loader.getMinFilter(2); j <= Math.min(Loader.getSize(2)-1,Loader.getMaxFilter(2)); ++j)
					for (int k = Loader.getMinFilter(3); k <= Math.min(Loader.getSize(3)-1,Loader.getMaxFilter(3)); ++k)
					{
						if (Loader.getData()[Loader.getFixedDimensionStep(0)][i][j][k] < Loader.getHeatMinFilter() || 
								Loader.getData()[Loader.getFixedDimensionStep(0)][i][j][k] > Loader.getHeatMaxFilter())
							continue;
						float colVal = (Loader.getData()[Loader.getFixedDimensionStep(0)][i][j][k] - Loader.getvMin(4))/Loader.getvMax(4);
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
			stepX = (globalMaxX - globalMinX)/Loader.getSize(0);
			stepY = (globalMaxY - globalMinY)/Loader.getSize(2);
			stepH = (globalMaxH - globalMinH)/Loader.getSize(3);
			
			for (int i = Loader.getMinFilter(0); i <= Math.min(Loader.getSize(0)-1,Loader.getMaxFilter(0)); ++i)
				for (int j = Loader.getMinFilter(2); j <= Math.min(Loader.getSize(2)-1,Loader.getMaxFilter(2)); ++j)
					for (int k = Loader.getMinFilter(3); k <= Math.min(Loader.getSize(3)-1,Loader.getMaxFilter(3)); ++k)
					{
						if (Loader.getData()[i][Loader.getFixedDimensionStep(0)][j][k] < Loader.getHeatMinFilter() || 
								Loader.getData()[i][Loader.getFixedDimensionStep(0)][j][k] > Loader.getHeatMaxFilter())
							continue;
						float colVal = (Loader.getData()[i][Loader.getFixedDimensionStep(0)][j][k] - Loader.getvMin(4))/Loader.getvMax(4);
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
			stepX = (globalMaxX - globalMinX)/Loader.getSize(0);
			stepY = (globalMaxY - globalMinY)/Loader.getSize(1);
			stepH = (globalMaxH - globalMinH)/Loader.getSize(3);
			
			for (int i = Loader.getMinFilter(0); i <= Math.min(Loader.getSize(0)-1,Loader.getMaxFilter(0)); ++i)
				for (int j = Loader.getMinFilter(1); j <= Math.min(Loader.getSize(1)-1,Loader.getMaxFilter(1)); ++j)
					for (int k = Loader.getMinFilter(3); k <= Math.min(Loader.getSize(3)-1,Loader.getMaxFilter(3)); ++k)
					{
						if (Loader.getData()[i][j][Loader.getFixedDimensionStep(0)][k] < Loader.getHeatMinFilter() || 
								Loader.getData()[i][j][Loader.getFixedDimensionStep(0)][k] > Loader.getHeatMaxFilter())
							continue;
						float colVal = (Loader.getData()[i][j][Loader.getFixedDimensionStep(0)][k] - Loader.getvMin(4))/Loader.getvMax(4);
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
			stepX = (globalMaxX - globalMinX)/Loader.getSize(0);
			stepY = (globalMaxY - globalMinY)/Loader.getSize(1);
			stepH = (globalMaxH - globalMinH)/Loader.getSize(2);
			
			for (int i = Loader.getMinFilter(0); i <= Math.min(Loader.getSize(0)-1,Loader.getMaxFilter(0)); ++i)
				for (int j = Loader.getMinFilter(1); j <= Math.min(Loader.getSize(1)-1,Loader.getMaxFilter(1)); ++j)
					for (int k = Loader.getMinFilter(2); k <= Math.min(Loader.getSize(2)-1,Loader.getMaxFilter(2)); ++k)
					{
						if (Loader.getData()[i][j][k][Loader.getFixedDimensionStep(0)] < Loader.getHeatMinFilter() || 
								Loader.getData()[i][j][k][Loader.getFixedDimensionStep(0)] > Loader.getHeatMaxFilter())
							continue;
						float colVal = (Loader.getData()[i][j][k][Loader.getFixedDimensionStep(0)] - Loader.getvMin(4))/(Loader.getvMax(4)- Loader.getvMin(4));
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
		switch(Loader.getFixedDimension(0)){
		case 0: 
			globalMaxX = Loader.getvMax(1);
			globalMaxY = Loader.getvMax(2);
			globalMaxH = Loader.getvMax(3);
			globalMaxC = Loader.getvMax(4);
			globalMinX = Loader.getvMin(1);
			globalMinY = Loader.getvMin(2);
			globalMinH = Loader.getvMin(3);
			globalMinC = Loader.getvMin(4);
			label1 = Loader.getLabels(1);
			label2 = Loader.getLabels(2);
			label3 = Loader.getLabels(3);
			break;
		case 1: 
			globalMaxX = Loader.getvMax(0);
			globalMaxY = Loader.getvMax(2);
			globalMaxH = Loader.getvMax(3);
			globalMaxC = Loader.getvMax(4);
			globalMinX = Loader.getvMin(0);
			globalMinY = Loader.getvMin(2);
			globalMinH = Loader.getvMin(3);
			globalMinC = Loader.getvMin(4);
			label1 = Loader.getLabels(0);
			label2 = Loader.getLabels(2);
			label3 = Loader.getLabels(3);
			break;
		case 2: 
			globalMaxX = Loader.getvMax(0);
			globalMaxY = Loader.getvMax(1);
			globalMaxH = Loader.getvMax(3);
			globalMaxC = Loader.getvMax(4);
			globalMinX = Loader.getvMin(0);
			globalMinY = Loader.getvMin(1);
			globalMinH = Loader.getvMin(3);
			globalMinC = Loader.getvMin(4);
			label1 = Loader.getLabels(0);
			label2 = Loader.getLabels(1);
			label3 = Loader.getLabels(3);
			break;
		case 3:
			globalMaxX = Loader.getvMax(0);
			globalMaxY = Loader.getvMax(1);
			globalMaxH = Loader.getvMax(2);
			globalMaxC = Loader.getvMax(4);
			globalMinX = Loader.getvMin(0);
			globalMinY = Loader.getvMin(1);
			globalMinH = Loader.getvMin(2);
			globalMinC = Loader.getvMin(4);
			label1 = Loader.getLabels(0);
			label2 = Loader.getLabels(1);
			label3 = Loader.getLabels(2);
			break;
		}
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
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0");
		gl.glScaled(1, 1/this.heightScale, 1);
		//Height ruler:
		for(float i = 0; i < globalMaxH - globalMinH; i += majorTickIntervalH){
			gl.glPushMatrix();

			gl.glTranslated(globalMaxX,(i + globalMinH)*this.heightScale,globalMinY - axisOffset + textOffsetFromAxis*0.3);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i+Math.floor(globalMinH)));
			gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslated(globalMaxX,
				(globalMaxH+globalMinH)*0.5*heightScale,
				globalMinY  - textOffsetFromAxis - textOffsetFromAxis*0.9);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, ""+label2);
		gl.glPopMatrix();
		//X ruler:
		for(float i = 0; i < globalMaxX - globalMinX + axisOffset; i += majorTickIntervalX){
			gl.glPushMatrix();

			gl.glTranslated(globalMinX-axisOffset + i,0,globalMinY - axisOffset - textOffsetFromAxis*0.9);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i+Math.floor(globalMinX) - axisOffset));
			gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslated((globalMinX+globalMaxX-axisOffset*2)*0.5,
				0,
				globalMinY - axisOffset*2.5 - textOffsetFromAxis*percentOfTextScale*0.9);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, ""+label1);
		gl.glPopMatrix();
		//Z ruler:
		for(float i = 0; i < globalMaxY - globalMinY + axisOffset; i += majorTickIntervalY){
			gl.glPushMatrix();

			gl.glTranslated(globalMinY - axisOffset - textOffsetFromAxis*0.9,0,globalMinY-axisOffset + i);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i+Math.floor(globalMinX) - axisOffset));
			gl.glPopMatrix();
		}
		
		gl.glPushMatrix();
		gl.glTranslated(globalMinY - axisOffset*2.5 - textOffsetFromAxis*percentOfTextScale*0.9,0,(globalMinY+globalMaxY-axisOffset*2)*0.5);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, ""+label3);
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
		gl.glPointSize(5);
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
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
		gl.glLoadIdentity();  // reset the model-view matrix
		//Scale, Rotate and translate world:
		gl.glPushMatrix();
		gl.glTranslated(0,0,-zoomFactor);
			
		gl.glRotatef(angleY,-1,0,0);
		gl.glRotatef(angleX,0,1,0);
		gl.glTranslated(-(globalMaxX+globalMinX)/2,-(globalMaxY+globalMinY)/2,-(globalMaxH+globalMinH)/2);
		gl.glScaled(1, this.heightScale, 1);
		
		//save the matrices' states
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, viewMatrix, 0);
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		//Draw points
		gl.glCallList(pointsList);
		//Draw the rotating text on the rulers:
		gl.glCallList(axisList);
		drawRullerText(gl);
	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such as buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) { animator.stop(); }

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (isMouseRightDown){
			unproject(arg0);
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
		unproject(arg0);
	}
	private void unproject(MouseEvent arg0){
		float realy = viewport[3] - (int) arg0.getY() - 1;
		glu.gluUnProject(arg0.getX(),realy, 0.1,viewMatrix,0,projectionMatrix,0,viewport,0,worldspaceCoordNear,0);
		glu.gluUnProject(arg0.getX(),realy, 1,viewMatrix,0,projectionMatrix,0,viewport,0,worldspaceCoordFar,0);			

		double vX = - worldspaceCoordNear[0] + worldspaceCoordFar[0];
		double vY = - worldspaceCoordNear[1] + worldspaceCoordFar[1];
		double vZ = - worldspaceCoordNear[2] + worldspaceCoordFar[2];
		double vLength = Math.sqrt(vX*vX + vY*vY + vZ*vZ);		
		vX = vX / vLength;
		vY = vY / vLength;
		vZ = vZ / vLength;
		double t = (-worldspaceCoordNear[1])/(vY+0.000000000001); 
		XI = (worldspaceCoordNear[0] + t*vX);
		YI = 0; //XZ-Plane
		ZI = (worldspaceCoordNear[2] + t*vZ);
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
	@Override
	public void reloadEvent() {
		shouldRecompileLists = true;
	}
}
