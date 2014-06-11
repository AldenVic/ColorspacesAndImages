/*
 /*
 * Driver.java
 * graphical user interface for phase two
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

public class Driver implements ActionListener {
	private static final String S1 = "1";
	private static final String S2 = "1";
	private static final String S3 = "1";
	private static final String M = "64";
	private static final String N1 = "128";
	private static final String N2 = "128";
	private static final String N3 = "128";
	private static final String D = "8";
	private static final String chID = "1";
	private static final String TLx_pxl = "0";
	private static final String TLy_pxl = "0";

	private String about = "last updated 10/21/2012 (1:01pm)\n";
	private static JTextArea debug;
	private ButtonGroup[] bg; 
	private JCheckBoxMenuItem[] t2, t3, t4, t5, cspc,selectedImage;
	private JTextField path;
	private JTextField givenS1;
	private JTextField givenS2;
	private JTextField givenS3;
	private JTextField givenN1;
	private JTextField givenN2;
	private JTextField givenN3;
	private JTextField givenM;
	private JTextField givenD;
	private JTextField givenchID;
	private JTextField givenTLx_pxl;
	private JTextField givenTLy_pxl;

	/*
	 * image instance
	 */
	private Image loadedImage = null;

	/*
	 * returns an array of currently selected indexes (settings) array indexes
	 * are as follows 0 : quantization schemes 1 : predictive schemes 2 : error
	 * quantization schemes 3 : encoding schemes 4 : color mode the above are in
	 * accordance to project two's instruction
	 */
	public int[] getSelectedIndexes() {

		int[] s = new int[6];
		int n = 0;
		boolean found = false;

		for (int i = 0; i < bg.length - 1; i++) {

			Enumeration<AbstractButton> checkBox = bg[i].getElements();

			while (checkBox.hasMoreElements() && found == false) {
				JCheckBoxMenuItem t = (JCheckBoxMenuItem) checkBox
						.nextElement();

				if (t.isSelected()) {
					s[i] = n;
					found = true;
				}
				n++;
			}

			found = false;
			n = 0;
		}
		return s;
	}

	/*
	 * wrapper for appending string to debug window
	 */
	public void write(String str) {
		debug.append(str + "\n");
	}

	/*
	 * creates and populate the menu
	 */
	public JMenuBar createMenu() {

		JMenuBar bar;
		JMenu menu;
		JMenuItem item;
		JCheckBoxMenuItem checkBox;
		bg = new ButtonGroup[7];

		// create the menu bar.
		bar = new JMenuBar();

		/**
		 * add a file menu to the bar
		 */
		menu = new JMenu("file");
		menu.setMnemonic(KeyEvent.VK_F);
		bar.add(menu);


		// add open to the file checkBox
		item = new JMenuItem("add Img to set");
		item.setActionCommand("ADDIMGTOSET");
		item.addActionListener(this);
		menu.add(item);
		
		// add open to the file checkBox
		item = new JMenuItem("View selected image");
		item.setActionCommand("VIEWSELECTEDIMAGE");
		item.addActionListener(this);
		menu.add(item);
		
		// add open to the file checkBox
		item = new JMenuItem("ViewAllImages");
		item.setActionCommand("VIEWALLIMAGES");
		item.addActionListener(this);
		menu.add(item);
		
		
		// separator
		menu.addSeparator();
		
		
		// add open to the file checkBox
		item = new JMenuItem("AddQueryImage");
		item.setActionCommand("ADDQUERYIMAGE");
		item.addActionListener(this);
		menu.add(item);

	
		
		// add open to the file checkBox
		item = new JMenuItem("Show QueryImage");
		item.setActionCommand("VIEWQUERYIMAGE");
		item.addActionListener(this);
		menu.add(item);
		

		// separator
		menu.addSeparator();		





		

		// add open to the file checkBox
		item = new JMenuItem("init db");
		item.setActionCommand("INITDB");
		item.addActionListener(this);
		menu.add(item);
		
		// add open to the file checkBox
		item = new JMenuItem("init db for query image");
		item.setActionCommand("INITQDB");
		item.addActionListener(this);
		menu.add(item);
		
		// add open to the file checkBox
		item = new JMenuItem("find top ten similarities per feature");
		item.setActionCommand("FINDTOP");
		item.addActionListener(this);
		menu.add(item);
		

		// add open to the file checkBox
		item = new JMenuItem(" print region on query image");
		item.setActionCommand("PRINTREGION");
		item.addActionListener(this);
		menu.add(item);
		
		
		// separator
		menu.addSeparator();
		

		// add open to the file checkBox
		item = new JMenuItem(" open proj2");
		item.setActionCommand("OPEN");
		item.addActionListener(this);
		menu.add(item);


		
		
		
		
		// separator
		menu.addSeparator();
		

		// add encode to the file checkBox
		item = new JMenuItem("Task2 encodeToCelledImage");
		item.setActionCommand("EncodeToCelledImage");
		item.addActionListener(this);
		menu.add(item);

		// add encode to the file checkBox
		item = new JMenuItem("Task2 viewCelledImage");
		item.setActionCommand("ViewCelledImage");
		item.addActionListener(this);
		menu.add(item);
		
		// add encode to the file checkBox
		item = new JMenuItem("Task 2encodeToAndViewCelledImage");
		item.setActionCommand("encodeToAndViewCelledImage");
		item.addActionListener(this);
		menu.add(item);
		
		// separator
		menu.addSeparator();
			


		// add encode to the file checkBox
		item = new JMenuItem("encode");
		item.setActionCommand("ENCODE");
		item.addActionListener(this);
		menu.add(item);
		

		// add decode to the file checkBox
		item = new JMenuItem("decode");
		item.setActionCommand("DECODE");
		item.addActionListener(this);
		menu.add(item);


		// add encode to the file checkBox
		item = new JMenuItem("viewDecodedCelledImage");
		item.setActionCommand("viewDecodedCelledImage");
		item.addActionListener(this);
		menu.add(item);
		
		
		
		
		// separator
		menu.addSeparator();

		// add close to the file checkBox
		item = new JMenuItem("close");
		item.setActionCommand("CLOSE");
		item.addActionListener(this);
		menu.add(item);

		/**
		 * add settings to the menu bar
		 */
		menu = new JMenu("settings");
		menu.setMnemonic(KeyEvent.VK_E);
		bar.add(menu);

		t2 = new JCheckBoxMenuItem[2];

		// add quantization schemes to the settings selection
		item = new JMenu("quantization schemes");
		bg[0] = new ButtonGroup();

		// sub selection for quantization schemes
		checkBox = new JCheckBoxMenuItem("no quantization");
		bg[0].add(checkBox);
		t2[0] = checkBox;
		item.add(checkBox);

		// set default option
		bg[0].setSelected(checkBox.getModel(), true);

		// sub selection for quantization schemes
		checkBox = new JCheckBoxMenuItem("uniform quantization");
		bg[0].add(checkBox);
		t2[1] = checkBox;
		item.add(checkBox);
		menu.add(item);

		// separator
		menu.addSeparator();

		t3 = new JCheckBoxMenuItem[8];
		// add predictive schemes to the settings selection
		item = new JMenu("predictive schemes");
		bg[1] = new ButtonGroup();

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("no predictive encoding");
		bg[1].add(checkBox);
		t3[0] = checkBox;
		item.add(checkBox);

		// set default option
		bg[1].setSelected(checkBox.getModel(), true);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("predictor a");
		bg[1].add(checkBox);
		t3[1] = checkBox;
		item.add(checkBox);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("predictor b");
		bg[1].add(checkBox);
		t3[2] = checkBox;
		item.add(checkBox);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("predictor c");
		bg[1].add(checkBox);
		t3[3] = checkBox;
		item.add(checkBox);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("predictor (a+b+c)/3");
		bg[1].add(checkBox);
		t3[4] = checkBox;
		item.add(checkBox);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("predictor a+(b-c)=b+(a-c)");
		bg[1].add(checkBox);
		t3[5] = checkBox;
		item.add(checkBox);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("predictor (a+b)/2");
		bg[1].add(checkBox);
		t3[6] = checkBox;
		item.add(checkBox);

		// sub selection for predictive schemes
		checkBox = new JCheckBoxMenuItem("condition predictor");
		bg[1].add(checkBox);
		t3[7] = checkBox;
		item.add(checkBox);

		menu.add(item);

		// separator
		menu.addSeparator();

		t4 = new JCheckBoxMenuItem[2];

		// add error quantization schemes to the settings selection
		item = new JMenu("error quantization schemes");
		bg[2] = new ButtonGroup();

		// sub selection for error quantization schemes
		checkBox = new JCheckBoxMenuItem("no error quantization");
		bg[2].add(checkBox);
		t4[0] = checkBox;
		item.add(checkBox);

		// set default option
		bg[2].setSelected(checkBox.getModel(), true);

		// sub selection for error quantization schemes
		checkBox = new JCheckBoxMenuItem("error quantization into m bins");
		bg[2].add(checkBox);
		t4[1] = checkBox;
		item.add(checkBox);

		menu.add(item);

		// separator
		menu.addSeparator();

		t5 = new JCheckBoxMenuItem[3];

		// add encoding schemes to the settings selection
		item = new JMenu("encoding schemes");
		bg[3] = new ButtonGroup();

		// sub selection for encoding schemes
		checkBox = new JCheckBoxMenuItem("no encoding");
		bg[3].add(checkBox);
		t5[0] = checkBox;
		item.add(checkBox);

		// set default option
		bg[3].setSelected(checkBox.getModel(), true);

		// sub selection for encoding schemes
		checkBox = new JCheckBoxMenuItem("shannon-fano encoding");
		bg[3].add(checkBox);
		t5[1] = checkBox;
		item.add(checkBox);

		// sub selection for encoding schemes
		checkBox = new JCheckBoxMenuItem("lzw encoding");
		bg[3].add(checkBox);
		t5[2] = checkBox;
		item.add(checkBox);

		menu.add(item);

		/**
		 * add mode to the menu bar
		 */
		menu = new JMenu("mode");
		menu.setMnemonic(KeyEvent.VK_O);
		bar.add(menu);

		bg[4] = new ButtonGroup();
		cspc = new JCheckBoxMenuItem[3];

		// add rgb to the mode selection
		checkBox = new JCheckBoxMenuItem("rgb");
		cspc[0] = checkBox;
		bg[4].add(checkBox);
		menu.add(checkBox);

		// set default option
		bg[4].setSelected(checkBox.getModel(), true);

		// add ycbcr to the mode selection
		checkBox = new JCheckBoxMenuItem("yuv");
		cspc[1] = checkBox;
		bg[4].add(checkBox);
		menu.add(checkBox);

		// add ycbcr to the mode selection
		checkBox = new JCheckBoxMenuItem("hsv");
		cspc[2] = checkBox;
		bg[4].add(checkBox);
		menu.add(checkBox);

		// add ycbcr to the mode selection
		//checkBox = new JCheckBoxMenuItem("ycbcr");
		//cspc[3] = checkBox;
		//bg[4].add(checkBox);
		//menu.add(checkBox);
		


		

		/**
		 * add debug to the menu bar
		 */
		menu = new JMenu("debug");
		bar.add(menu);

		// add about to the help selection
		item = new JMenuItem("getSelectedIndexes();");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] s = getSelectedIndexes();
				write("[" + s[0] + "," + s[1] + "," + s[2] + "," + s[3] + ","
						+ s[4] + "]");
				// write("insideddd");
			}

		});

		menu.add(item);

		/**
		 * add a tasks menu to the bar
		 */
		menu = new JMenu("tasks");
		menu.setMnemonic(KeyEvent.VK_T);
		bar.add(menu);

		// display original image
		item = new JMenuItem("show_inputImage");
		item.setActionCommand("SHOW_INPUTIMAGE");
		item.addActionListener(this);
		menu.add(item);

		// separator
		menu.addSeparator();

		// display original image with s1,s2,s3 reduction for for each color
		// space
		item = new JMenuItem("task1 with s1,s1,s3 added");
		item.setActionCommand("TASK1");
		item.addActionListener(this);
		menu.add(item);

		// NO quantization OR display image using quantization of the color
		// spaces using bins for each color space
		item = new JMenuItem("task2 with n1,n2,n3 added");
		item.setActionCommand("TASK2");
		item.addActionListener(this);
		menu.add(item);

		// separator
		menu.addSeparator();

		// add encode to the file checkBox
		item = new JMenuItem("task3");
		item.setActionCommand("TASK3");
		item.addActionListener(this);
		menu.add(item);

		// add encode to the file checkBox
		item = new JMenuItem("task4");
		item.setActionCommand("TASK4");
		item.addActionListener(this);
		menu.add(item);

		// separator
		menu.addSeparator();

		// add encode to the file checkBox
		item = new JMenuItem("task5");
		item.setActionCommand("TASK5");
		item.addActionListener(this);
		menu.add(item);

		// add encode to the file checkBox
		item = new JMenuItem("task6");
		item.setActionCommand("TASK6");
		item.addActionListener(this);
		menu.add(item);

		// add encode to the file checkBox
		item = new JMenuItem("task7");
		item.setActionCommand("TASK7");
		item.addActionListener(this);
		menu.add(item);
		
		
		
		/**
		 * add ImageIndex
		 */
		menu = new JMenu("ImgSetIndex");
		menu.setMnemonic(KeyEvent.VK_M);
		bar.add(menu);

		bg[5] = new ButtonGroup();
		selectedImage = new JCheckBoxMenuItem[15];

		// add rgb to the mode selection
		checkBox = new JCheckBoxMenuItem("0");
		selectedImage[0] = checkBox;
		bg[5].add(checkBox);
		menu.add(checkBox);

		// set default option
		bg[5].setSelected(checkBox.getModel(), true);

		// add ycbcr to the mode selection
		checkBox = new JCheckBoxMenuItem("1");
		selectedImage[1] = checkBox;
		bg[5].add(checkBox);
		menu.add(checkBox);

		// add ycbcr to the mode selection
		checkBox = new JCheckBoxMenuItem("2");
		selectedImage[2] = checkBox;
		bg[5].add(checkBox);
		menu.add(checkBox);

		// add ycbcr to the mode selection
		checkBox = new JCheckBoxMenuItem("3");
		selectedImage[3] = checkBox;
		bg[5].add(checkBox);
		menu.add(checkBox);
		
		// add rgb to the mode selection
				checkBox = new JCheckBoxMenuItem("4");
				selectedImage[4] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				
				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("5");
				selectedImage[5] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("6");
				selectedImage[6] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("7");
				selectedImage[7] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);
				// add rgb to the mode selection
				checkBox = new JCheckBoxMenuItem("8");
				selectedImage[8] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);
				

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("9");
				selectedImage[9] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("10");
				selectedImage[10] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("11");
				selectedImage[11] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("12");
				selectedImage[12] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);

				// add ycbcr to the mode selection
				checkBox = new JCheckBoxMenuItem("13");
				selectedImage[13] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);
				// add rgb to the mode selection
				checkBox = new JCheckBoxMenuItem("14");
				selectedImage[14] = checkBox;
				bg[5].add(checkBox);
				menu.add(checkBox);
		
		

		/**
		 * add help to the menu bar
		 */
		menu = new JMenu("help");
		bar.add(menu);

		// add about to the help selection
		item = new JMenuItem("about");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				debug.append(about);
			}

		});

		menu.add(item);

		return bar;
	}

	/*
	 * creates the container panels
	 */
	public Container createContentPane() {

		// initializes the panel
		JPanel container = new JPanel();
		container.setLayout(null);
		// container.setBounds(0,0,300,300);
		// container.setOpaque(false);

		JPanel optionContainer = new JPanel();
		optionContainer.setLayout(null);
		optionContainer.setBounds(0, 0, 582, 96 - 12 + 24);
		optionContainer.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(0), "constants"));
		container.add(optionContainer);

		/*
		 * removed old s/n field //// start s1, s2, s3 // values directory panel
		 * JPanel valuesPanel = new JPanel(); //commandPanel.setLayout(new
		 * FlowLayout()); valuesPanel.setBounds(5,16, 96, 55);
		 * valuesPanel.setBorder
		 * (BorderFactory.createTitledBorder(BorderFactory.createBevelBorder
		 * (0),"s/n"));
		 * 
		 * // parameter cmd = new JTextField("1,1,1,1", 8);
		 * cmd.setBorder(BorderFactory.createBevelBorder(1));
		 * valuesPanel.add(cmd); optionContainer.add(valuesPanel);
		 */

		int xoffset = 110;
		int yoffset = 10;

///proj 3
		
		// values directory panel

				JPanel valuesPanelchID = new JPanel();
				valuesPanelchID.setBounds(460 - xoffset - 320, 45 + yoffset + 5, 44, 44);
				valuesPanelchID.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(Color.gray), "chID"));

				givenchID = new JTextField(chID, 4);
				givenchID.setHorizontalAlignment(JTextField.RIGHT);
				givenchID.setBorder(BorderFactory.createLineBorder(Color.gray));
				valuesPanelchID.add(givenchID);
				optionContainer.add(valuesPanelchID);

				JPanel valuesPanelTLx_pxl = new JPanel();
				valuesPanelTLx_pxl.setBounds(520 - xoffset - 320, 45 + yoffset + 5, 80, 44);
				valuesPanelTLx_pxl.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(Color.gray), "TLx_pxl"));

				givenTLx_pxl = new JTextField(TLx_pxl, 4);
				givenTLx_pxl.setHorizontalAlignment(JTextField.RIGHT);
				givenTLx_pxl.setBorder(BorderFactory.createLineBorder(Color.gray));
				valuesPanelTLx_pxl.add(givenTLx_pxl);
				optionContainer.add(valuesPanelTLx_pxl);

				JPanel valuesPanelTLy_pxl = new JPanel();
				valuesPanelTLy_pxl.setBounds(580 - xoffset - 280, 45 + yoffset + 5, 80, 44);
				valuesPanelTLy_pxl.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(Color.gray), "TLy_pxl"));

				givenTLy_pxl = new JTextField(TLy_pxl, 4);
				givenTLy_pxl.setHorizontalAlignment(JTextField.RIGHT);
				givenTLy_pxl.setBorder(BorderFactory.createLineBorder(Color.gray));
				valuesPanelTLy_pxl.add(givenTLy_pxl);
				optionContainer.add(valuesPanelTLy_pxl);

	
		
		JPanel valuesPanels1 = new JPanel();
		valuesPanels1.setBounds(460 - xoffset, 0 + yoffset, 44, 44);
		valuesPanels1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "s1"));

		givenS1 = new JTextField(S1, 4);
		givenS1.setHorizontalAlignment(JTextField.RIGHT);
		givenS1.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPanels1.add(givenS1);
		optionContainer.add(valuesPanels1);

		JPanel valuesPanels2 = new JPanel();
		valuesPanels2.setBounds(520 - xoffset, 0 + yoffset, 44, 44);
		valuesPanels2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "s2"));

		givenS2 = new JTextField(S2, 4);
		givenS2.setHorizontalAlignment(JTextField.RIGHT);
		givenS2.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPanels2.add(givenS2);
		optionContainer.add(valuesPanels2);

		JPanel valuesPanels3 = new JPanel();
		valuesPanels3.setBounds(580 - xoffset, 0 + yoffset, 44, 44);
		valuesPanels3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "s3"));

		givenS3 = new JTextField(S3, 4);
		givenS3.setHorizontalAlignment(JTextField.RIGHT);
		givenS3.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPanels3.add(givenS3);
		optionContainer.add(valuesPanels3);

		JPanel valuesPanelM = new JPanel();
		valuesPanelM.setBounds(640 - xoffset, 0 + yoffset, 44, 44);
		valuesPanelM.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "M"));

		givenM = new JTextField(M, 4);
		givenM.setHorizontalAlignment(JTextField.RIGHT);
		givenM.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPanelM.add(givenM);
		optionContainer.add(valuesPanelM);

		// /// end s1, s2, s3
		// /////start n1,n2,n3

		// values directory panel

		JPanel valuesPaneln1 = new JPanel();
		valuesPaneln1.setBounds(460 - xoffset, 45 + yoffset, 44, 44);
		valuesPaneln1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "n1"));

		givenN1 = new JTextField(N1, 4);
		givenN1.setHorizontalAlignment(JTextField.RIGHT);
		givenN1.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPaneln1.add(givenN1);
		optionContainer.add(valuesPaneln1);

		JPanel valuesPaneln2 = new JPanel();
		valuesPaneln2.setBounds(520 - xoffset, 45 + yoffset, 44, 44);
		valuesPaneln2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "n2"));

		givenN2 = new JTextField(N2, 4);
		givenN2.setHorizontalAlignment(JTextField.RIGHT);
		givenN2.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPaneln2.add(givenN2);
		optionContainer.add(valuesPaneln2);

		JPanel valuesPaneln3 = new JPanel();
		valuesPaneln3.setBounds(580 - xoffset, 45 + yoffset, 44, 44);
		valuesPaneln3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "n3"));

		givenN3 = new JTextField(N3, 4);
		givenN3.setHorizontalAlignment(JTextField.RIGHT);
		givenN3.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPaneln3.add(givenN3);
		optionContainer.add(valuesPaneln3);

		JPanel valuesPanelD = new JPanel();
		valuesPanelD.setBounds(640 - xoffset, 45 + yoffset, 44, 44);
		valuesPanelD.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.gray), "D"));

		givenD = new JTextField(D, 4);
		givenD.setHorizontalAlignment(JTextField.RIGHT);
		givenD.setBorder(BorderFactory.createLineBorder(Color.gray));
		valuesPanelD.add(givenD);
		optionContainer.add(valuesPanelD);

		// //////end n1,n2 n3

		// output directory panel
		JPanel outputDirectoryPanel = new JPanel();
		outputDirectoryPanel.setLayout(new FlowLayout());
		outputDirectoryPanel.setBounds(8, 16, 340, 55);
		outputDirectoryPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(0), "output directory"));

		// directive
		path = new JTextField("\\data\\save\\", 37);
		path.setBorder(BorderFactory.createBevelBorder(1));
		outputDirectoryPanel.add(path);
		optionContainer.add(outputDirectoryPanel);

		// create the debug area
		debug = new JTextArea(10, 50);
		debug.setEditable(false);
		debug.setLineWrap(false);
		debug.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(debug);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 100 + 12, 582, 200);
		scrollPane.setBorder(BorderFactory.createBevelBorder(1));

		// add the debug area to the container
		container.add(scrollPane);

		return container;
	}

	/*
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		// create and set up the window
		JFrame frame = new JFrame("T6P2");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create and set up the content pane
		Driver m = new Driver();
		frame.setJMenuBar(m.createMenu());
		frame.setContentPane(m.createContentPane());

		// display the window.
		frame.setSize(588, 362);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	/*
	 * main
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				/*
				 * try { String windows =
				 * "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; String
				 * gtk = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
				 * UIManager.setLookAndFeel(gtk); } catch
				 * (ClassNotFoundException e) { e.printStackTrace(); } catch
				 * (InstantiationException e) { e.printStackTrace(); } catch
				 * (IllegalAccessException e) { e.printStackTrace(); } catch
				 * (UnsupportedLookAndFeelException e) { e.printStackTrace(); }
				 */

				createAndShowGUI();
			}
		});
	}

	/*
	 * returns the current loaded image
	 */
	//public Image getCurrentImage() {
	//	return loadedImage;
	//}

	/*
	 * action performed by menu
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String ts = null;
		String item = null;

		try {
			Utilities._s1 = Integer.parseInt(givenS1.getText().toString());
			Utilities._s2 = Integer.parseInt(givenS2.getText().toString());
			Utilities._s3 = Integer.parseInt(givenS3.getText().toString());
			Utilities._n1 = Integer.parseInt(givenN1.getText().toString());
			Utilities._n2 = Integer.parseInt(givenN2.getText().toString());
			Utilities._n3 = Integer.parseInt(givenN3.getText().toString());
			Utilities._m = Integer.parseInt(givenM.getText().toString());
			Utilities._d = Integer.parseInt(givenD.getText().toString());
			Utilities._task2 = 0;
			Utilities._task4 = 0;

			Utilities._chID = Integer.parseInt(givenchID.getText().toString());
			Utilities._TLx_pxl = Integer.parseInt(givenTLx_pxl.getText().toString());
			Utilities._TLy_pxl = Integer.parseInt(givenTLy_pxl.getText().toString());

int[] s1 = getSelectedIndexes();
Utilities._selectedIndex = "[" + s1[0] + "," + s1[1] + "," + s1[2] + "," + s1[3] + "," + s1[4] + "]";
			
		
	for(int vv = 0; vv < selectedImage.length; vv++)
		{ if(selectedImage[vv].isSelected())
			Utilities.Gui_selectedImageIndex = vv;
		}




			for (int i = 0; i < 2; i++) {
				if (t2[i].isSelected())
					Utilities._task2 = i;
				if (t4[i].isSelected())
					Utilities._task4 = i;
				if (cspc[i].isSelected())
					Utilities._colorspace = i;
					
			}			

			for (int i = 0; i < 3; i++) {
				if (cspc[i].isSelected())
					Utilities._colorspace = i;
					
			}			
			

			Utilities._task5 = 0;
			for (int i = 0; i < 3; i++) {
				if (t5[i].isSelected())
					Utilities._task5 = i;
			}
			Utilities._task3 = 0;
			for (int i = 0; i < 8; i++) {
				if (t3[i].isSelected())
					Utilities._task3 = i;
			}
		} catch (NumberFormatException e1) {
			write("E: You need to put digits for the s1, s2, s3 n1, n2, n3 values: " + e1);
		}


		int totalmembers = Utilities.totalMembers;
		switch (e.getActionCommand()) {
		case "SHOW_INPUTIMAGE":
			item = "input image";
			write("C: Show Input Image");
			ts = Utilities.showInputImage(e, item);

			if (ts == null) {
				write("E: Go to file and click open image first.");
			} else {
				
			}

			break;
		case "TASK1":
			write("TASK1");
			break;
		case "TASK2":
			write("TASK2");
			break;
		case "TASK3":
			dbProj3.add_t3_frequencyHistogram(Utilities.imageSetMembers, Utilities._colorspace);
			write("TASK3");
			break;
		case "TASK4":
			dbProj3.add_t4_angleHistogram(Utilities.imageSetMembers, Utilities._colorspace);
			write("TASK4");
			break;
		case "TASK5":
			dbProj3.add_t5_amplitudeHistogram(Utilities.imageSetMembers, Utilities._colorspace);
			write("TASK5");
			break;
		case "TASK6":
			dbProj3.add_t6_waveletHistogram(Utilities.imageSetMembers, Utilities._colorspace);
			write("TASK6");
			break;
		case "TASK7":
			write("TASK7");
			break;
		case "OPEN":
			String s = Utilities.open(e);
			if (s != null) {
				write("C: Open - " + s);
			}
			break;
		case "ENCODE":
			write("C: Encode");
			write(Utilities.encode3());
			break;
		case "ADDIMGTOSET":
			//write("sdfs");
		String ss = Utilities.openProj3(e);
		if (ss != null) {
			write("C: added to Set - " + ss);
		}
		break;
		case "VIEWSELECTEDIMAGE":			
			if (totalmembers > 0)
				{			
				//if (_loadedImage != null) {
				//	UtilFuncs.displayImage(_loadedImage.getInstance(), task);
				//	out = "done";
				//}
				
				int gsi = Utilities.Gui_selectedImageIndex;
				//write("total image in set:" + totalmembers  +  "   selected image index: " + Utilities.Gui_selectedImageIndex);
				if ( gsi<= (totalmembers - 1))
					{	imageSetMember ism = Utilities.imageSetMembers[gsi];
					if (ism.getLoadedImage(Utilities._colorspace).getInstance() != null) {
						Image img =ism.getLoadedImage(Utilities._colorspace);
						try{
							img.getInstance().writeImage(new ImageInfo()); 
							UtilFuncs.displayImage2(img.getInstance(), "lastInputFile.jpg", "lastInputFile.jpg");
							}
						catch (MagickException e4)
							{	e4.printStackTrace();	} 
						
						write("Total Images in set: " + Utilities.totalMembers + ",     viewing selected image at: "  + ism.getMyIndex() + " cs:" + Utilities._colorspace + img.get_csS());
							
					  	}
					else
						{write(" index selected should be <= : "  + (totalmembers - 1) + " you selected: " + Utilities.Gui_selectedImageIndex);
						}
					}
				else
					{write("Add image to image set: Total image in set: " + totalmembers);}
				}
			else
				{write("Add image to image set: Total image in set: " + totalmembers);}
		break;
		case "VIEWALLIMAGES":
			
			if (Utilities.totalMembers > 0)
				{write("total image in set:" + totalmembers  +  "   selected image index: All");
				write(Utilities.printImageset(Utilities._colorspace, debug));				
				}
			else
				{write("Add image to image set: Total image in set: " + totalmembers);}
		break;
		case "ADDQUERYIMAGE":
			String ss1 = Utilities.addQueryImage(e);
			if (ss1 != null) {
				write("C: opened Query Image:" + ss1);
				}
					
			break;
		case "VIEWQUERYIMAGE":
			imageSetMember ism = Utilities._queryImage;
			if (ism != null) {
				Image img =ism.getLoadedImage(Utilities._colorspace);
				try{
					img.getInstance().writeImage(new ImageInfo()); 
					UtilFuncs.displayImage2(img.getInstance(), "lastInputFile.jpg", "lastInputFile.jpg");
					}
				catch (MagickException e4)
					{	e4.printStackTrace();	} 
				
				write("Viewign query Image");
					
			  }
			else
				{write("PLease load Query Image");			}
		
			break;
		case "INITDB":
			if (Utilities.imageSetMembers != null){
				write( dbProj3.DB_init(dbProj3.DB_FILENAME, debug));}
			else
			{write("images not yet initialized.");}
			break;
		case "INITQDB":
			if (Utilities._queryImage != null){
				write(dbProj3.targetimage_generateHistograms(Utilities._queryImage, Utilities._colorspace));
				}
			else
			{write("target image is not initialized");}
			break;
		case "FINDTOP":
			if (Utilities._queryImage != null && Utilities.imageSetMembers != null){
				
				write(dbProj3.t8_findSimilarities(Integer.parseInt(givenTLx_pxl.getText()), Integer.parseInt(givenTLy_pxl.getText()), Utilities._queryImage, Integer.parseInt(givenchID.getText())));
				}
			else
			{write("images are not initialized");}
			break;
		case "PRINTREGION":
			if (Utilities._queryImage != null){
				Utilities.regioncrop(Utilities._queryImage.getLoadedImage(Utilities._colorspace).getInstance(), Utilities._TLx_pxl, Utilities._TLy_pxl);
			}
			else
				{write("images are not initialized");}
			break;
		case "EncodeToCelledImage":
			write("C: EncodeToCelledImage");
			write(Utilities.encode_ImageIntoCells( Utilities._imageLocation, Utilities.OUT_gridImage_FILENAME));
			break;
		case "encodeToAndViewCelledImage":
			write("C: encodeToAndViewCelledImage");
			write(Utilities.encodeView_ImageIntoCells( Utilities._imageLocation, Utilities.OUT_gridImage_FILENAME,Utilities._selectedIndex));
			break;
		case "ViewCelledImage":
			write("C: ViewCelledImage");
			write(Utilities.view_ImageIntoCells(Utilities.OUT_gridImage_FILENAME, Utilities._selectedIndex));
			break;
		case "viewDecodedCelledImage":
			write("C: DisplayImage viewDecodedCelledImage");
			try{
				Utilities_01.gridLoadImage3(Utilities.OUT_IMAGE_FILENAME);
				UtilFuncs.gridDisplayImage3(Utilities.OUT_gridImage_FILENAME, Utilities._selectedIndex);
			} catch (MagickException e2) {
				e2.printStackTrace();
			} 
			
			break;
		case "DECODE":
			write("C: Decode");
			write(Utilities.decode3());
			break;
		case "CLOSE":
			System.exit(0);
			break;
		default:
		}
	}
}