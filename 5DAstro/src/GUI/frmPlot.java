package GUI;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import Data.DataHolder;
import Drawing3D.Plotter3D;

public class frmPlot extends JFrame implements ActionListener,ChangeListener{
	private static final long serialVersionUID = 918447268729291631L;
	Plotter3D frmPlot;
	JMenuBar mbrMain = new JMenuBar();
	JMenu mnuOptions = new JMenu("Options");
	JMenu mnuAxis = new JMenu("Axis");
	JMenu mnuStyle = new JMenu("Style");
	JMenu mnuFontSize = new JMenu("Tick Font Size");
	JMenu mnuTicksOnXAxis = new JMenu("X axis");
	JMenu mnuTicksOnYAxis = new JMenu("Y axis");
	JMenu mnuTicksOnZAxis = new JMenu("Z axis");
	JMenu mnuMinorTicksOnXAxis = new JMenu("X axis");
	JMenu mnuMinorTicksOnYAxis = new JMenu("Y axis");
	JMenu mnuMinorTicksOnZAxis = new JMenu("Z axis");
	JMenu mnuMinor = new JMenu("Minor Ticks");
	JMenu mnuMajor = new JMenu("Major Ticks");
	JRadioButton mitRenderDark = new JRadioButton("Dark",true);
	JRadioButton mitRenderLight = new JRadioButton("Light");
	
	JMenuItem mitSnapshot = new JMenuItem("Take snapshot");
	JSlider jslAxisFontSize = new JSlider();
	JSpinner jspNumXTicks = new JSpinner();
	JSpinner jspNumZTicks = new JSpinner();
	JSpinner jspNumHTicks = new JSpinner();
	JSpinner jspNumMinorXTicks = new JSpinner();
	JSpinner jspNumMinorZTicks = new JSpinner();
	JSpinner jspNumMinorHTicks = new JSpinner();
	/**
	 * Sets up the menus
	 */
	private void setupMenus(){
		mbrMain.add(mnuOptions);
		mitRenderDark.addActionListener(this);
		mitRenderLight.addActionListener(this);
		
		mnuOptions.add(mnuAxis);
		mnuOptions.add(mnuStyle);
		mnuStyle.add(mitRenderDark);
		mnuStyle.add(mitRenderLight);
		mnuOptions.add(mitSnapshot);
		mitSnapshot.addActionListener(this);
		mnuAxis.add(mnuFontSize);
		mnuAxis.add(mnuMinor);
		mnuAxis.add(mnuMajor);
		mnuMajor.add(mnuTicksOnXAxis);
		mnuMajor.add(mnuTicksOnYAxis);
		mnuMajor.add(mnuTicksOnZAxis);
		mnuMinor.add(mnuMinorTicksOnXAxis);
		mnuMinor.add(mnuMinorTicksOnYAxis);
		mnuMinor.add(mnuMinorTicksOnZAxis);
		jslAxisFontSize.setMinimum(50);
		jslAxisFontSize.setMaximum(400);
		jslAxisFontSize.setValue(100);
		jslAxisFontSize.setMajorTickSpacing(15);
		jslAxisFontSize.addChangeListener(this);
		mnuFontSize.add(jslAxisFontSize);
		mnuTicksOnXAxis.add(jspNumXTicks);
		//X -ticks
		SpinnerNumberModel sp1 = new SpinnerNumberModel();
		sp1.setMinimum(1);
		jspNumXTicks.setModel(sp1);
		jspNumXTicks.setPreferredSize(new Dimension(60,20));
		jspNumXTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumXTicks.setValue(frmPlot.getNumTicksX());
		jspNumXTicks.addChangeListener(this);
		
		
		mnuTicksOnYAxis.add(jspNumHTicks);
		//Y ticks
		SpinnerNumberModel spY = new SpinnerNumberModel();
		spY.setMinimum(1);
		jspNumHTicks.setModel(spY);
		jspNumHTicks.setPreferredSize(new Dimension(60,20));
		jspNumHTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumHTicks.setValue(frmPlot.getNumTicksH());
		jspNumHTicks.addChangeListener(this);
		
		//Z ticks
		SpinnerNumberModel sp2 = new SpinnerNumberModel();
		sp2.setMinimum(1);
		mnuTicksOnZAxis.add(jspNumZTicks);
		jspNumZTicks.setModel(sp2);
		jspNumZTicks.setPreferredSize(new Dimension(60,20));
		jspNumZTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumZTicks.setValue(frmPlot.getNumTicksZ());
		jspNumZTicks.addChangeListener(this);
		
		//MINOR TICKS:
		
		mnuMinorTicksOnXAxis.add(jspNumMinorXTicks);
		//X -ticks
		SpinnerNumberModel spM1 = new SpinnerNumberModel();
		spM1.setMinimum(1);
		jspNumMinorXTicks.setModel(spM1);
		jspNumMinorXTicks.setPreferredSize(new Dimension(60,20));
		jspNumMinorXTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumMinorXTicks.setValue(frmPlot.getNumMinorTicksX());
		jspNumMinorXTicks.addChangeListener(this);
		
		
		mnuMinorTicksOnYAxis.add(jspNumMinorHTicks);
		//Y ticks
		SpinnerNumberModel spMY = new SpinnerNumberModel();
		spMY.setMinimum(1);
		jspNumMinorHTicks.setModel(spMY);
		jspNumMinorHTicks.setPreferredSize(new Dimension(60,20));
		jspNumMinorHTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumMinorHTicks.setValue(frmPlot.getNumMinorTicksH());
		jspNumMinorHTicks.addChangeListener(this);
		
		//Z ticks
		SpinnerNumberModel spM2 = new SpinnerNumberModel();
		spM2.setMinimum(1);
		mnuMinorTicksOnZAxis.add(jspNumMinorZTicks);
		jspNumMinorZTicks.setModel(spM2);
		jspNumMinorZTicks.setPreferredSize(new Dimension(60,20));
		jspNumMinorZTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumMinorZTicks.setValue(frmPlot.getNumMinorTicksZ());
		jspNumMinorZTicks.addChangeListener(this);	
	}
	
	public frmPlot(){
		super("3D Viewer");
		setSize(800, 600);

this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				((JFrame)arg0.getSource()).setVisible(false);
				DataHolder.refreshButtons();
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});

		//this.setExtendedState(MAXIMIZED_BOTH);
		frmPlot = new Plotter3D();
		setupMenus();
		this.setJMenuBar(mbrMain);
		this.add(frmPlot);
		this.setVisible(true);
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == jspNumXTicks)
		{
			jspNumXTicks.validate();
			frmPlot.setNumTicksX(Integer.parseInt(jspNumXTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumHTicks)
		{
			jspNumHTicks.validate();
			frmPlot.setNumTicksH(Integer.parseInt(jspNumHTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumZTicks)
		{
			jspNumZTicks.validate();
			frmPlot.setNumTicksZ(Integer.parseInt(jspNumZTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumMinorXTicks)
		{
			jspNumMinorXTicks.validate();
			frmPlot.setNumMinorTicksX(Integer.parseInt(jspNumMinorXTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumMinorHTicks)
		{
			jspNumMinorHTicks.validate();
			frmPlot.setNumMinorTicksH(Integer.parseInt(jspNumMinorHTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumMinorZTicks)
		{
			jspNumMinorZTicks.validate();
			frmPlot.setNumMinorTicksZ(Integer.parseInt(jspNumMinorZTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jslAxisFontSize){
			frmPlot.setTextZoom(jslAxisFontSize.getValue()/(float)100);
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == mitRenderDark)
		{
			frmPlot.setDarkTheme(true);
			mitRenderLight.setSelected(false);
		}
		else if (arg0.getSource() == mitRenderLight)
		{
			frmPlot.setDarkTheme(false);
			mitRenderDark.setSelected(false);
		}
		else if (arg0.getSource() == mitSnapshot)
		{
			BufferedImage image = new BufferedImage(this.frmPlot.getWidth(), this.frmPlot.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            this.frmPlot.paint(graphics2D);
            try{
            	JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "PNG Image", "png");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                	ImageIO.write(image,"png", new File(chooser.getSelectedFile().getAbsolutePath()+".png"));
                }
            }
            catch (Exception e){
            	JOptionPane.showMessageDialog(this,
    				    "Unable to save image. Please choose a different location.",
    				    "I/O Error",
    				    JOptionPane.ERROR_MESSAGE);
            }
		}
		
	}
	public void reload(){
		this.frmPlot.reload();
	}
}
