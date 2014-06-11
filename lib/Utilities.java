/*
 * utilities class for Driver.java
 */
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.MontageInfo;
import magick.util.MagickWindow;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTextArea;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


public class Utilities {

	// CONSTANTS
	private static final String OUT_ORIGINAL_BIN = "out/original.bin";
	public static final String OUT_IMAGE_FILENAME = "out/output.jpg";
	private static final String OUT_IMAGE_FILENAME0 = "out/output0.jpg";
	private static final String OUT_IMAGE_FILENAME1 = "out/output1.jpg";
	public static final String OUT_OBJECT_RGB_FILENAME = "out/out.RGB";
	public static final String OUT_OBJECT_YBR_FILENAME = "out/out.YBR";
	public static final String OUT_OBJECT_YUV_FILENAME = "out/out.yuv";
	public static final String OUT_OBJECT_HSV_FILENAME = "out/out.hsv";
	public static final String OUT_gridImage_FILENAME = "grid_output.jpg"; // will cause prob if dir use


	// Loaded image information
	public static String _imageLocation = null;
	public static Image _loadedImage = null;

	public static imageSetMember[] imageSetMembers = new imageSetMember[100];
	public static int totalMembers = 0;
	public static int Gui_selectedImageIndex = -1;

	public static int _width;
	public static int _height;
	public static byte[] _dispatched = null;
	public static int _reducedWidth;
	public static int _reducedHeight;
	public static byte[] _reducedDispatch = null;

	// Loaded Query image information
	public static String _imageLocationQuery = null;
	public static Image _loadedImageQuery = null;
	public static int _widthQuery;
	public static int _heightQuery;
	public static byte[] _dispatchedQuery = null;
	public static imageSetMember _queryImage = null;

	public static int _chID;
	public static int _TLx_pxl;
	public static int _TLy_pxl;


	// parameters
	public static int _s1;
	public static int _s2;
	public static int _s3;
	public static int _n1;
	public static int _n2;
	public static int _n3;
	public static int _m;
	public static int _d;
	public static int _colorspace;
	public static final String _csRGB = "RGB";
	public static final String _csYUV = "YUV";
	public static final String _csHSV = "HSV";
	public static  String _selectedIndex;

	// task config
	public static int _task2;
	public static int _task3;
	public static int _task4;
	public static int _task5;

	// the symbol table instance & image instance for funcs
	private static final SymbolTable _symbolTable = new SymbolTable();
	private static final Image _imgInstance = new Image();

	public static byte[] orig;
	public static byte[] pcsignsr, pcsignsg, pcsignsb;

	public static MagickImage[][] instance;
	public static MagickImage[] cellgrid;
	public static MagickImage[][] instance3;
	public static MagickImage[] cellgrid3;
	public static MagickImage[][] instance3_2D_DCT_task3;
	public static MagickImage[] cellgrid3_2D_DCT_task3;
	public static MagickImage[][] instance3_Sobel_angle_task4;
	public static MagickImage[] cellgrid3_Sobel_angle_taks4;
	public static MagickImage[][] instance3_soble_amplitued_taks5;
	public static MagickImage[] cellgrid3_soble_amplitude_task5;
	public static MagickImage[][] instance3_2D_DWT_task6;
	public static MagickImage[] cellgrid3_2D_DWT_task6;
	public static final  int squaredDimensions = 8;



	public static String showInputImage(ActionEvent e, String task) {

		String out = null;

		if (e.getActionCommand().equals("SHOW_INPUTIMAGE")) {
			if (_loadedImage != null) {
				UtilFuncs.displayImage(_loadedImage.getInstance(), task);
				out = "done";
			}
		}

		return out;
	}


	public static String printImageset(int cs, JTextArea debug) {
		int count1 = 0;
		String out = null;

		debug.append("     Start printing: ");
		while (count1 <= (totalMembers - 1))
		{

		Image ism = imageSetMembers[count1].getLoadedImage(Utilities._colorspace);
		if (ism.getInstance() != null) {

			try{
				debug.append("    " +  count1);
				ism.getInstance().writeImage(new ImageInfo());
				UtilFuncs.displayImage2(ism.getInstance(), "lastInputFile.jpg", "lastInputFile.jpg");
				debug.append("," +  imageSetMembers[count1].getMyIndex());
				}
			catch (MagickException e4)
				{	e4.printStackTrace();	}
			}
		else  {
			debug.append("\n found null image at imageSetMember index: " + count1++);

			return out;
			}
		count1++;
		}

		out = "\nTotal Images printed: "  + count1;


		return out;
	}



	public static String open(ActionEvent e) {

		String out = null;

		if (e.getActionCommand().equals("OPEN")) {

			JFileChooser fileChooser = new JFileChooser(".");
			FileFilter filter = new ExtensionFileFilter("JPG,JPEG,BMP,GIF,TIFF,PNG", new String[] { "JPG", "JPEG",
					"BMP", "GIF", "TIFF", "PNG" });
			fileChooser.setFileFilter(filter);

			int status = fileChooser.showOpenDialog(null);

			if (status == JFileChooser.APPROVE_OPTION) {
				out = fileChooser.getSelectedFile().getPath();
				_imageLocation = out;

				_loadedImage = new Image(_imageLocation);
				_loadedImage.dispatch();

				_width = _loadedImage.getW();
				_height = _loadedImage.getH();

				byte[] red = _loadedImage.getLayer(0);
				byte[] green = _loadedImage.getLayer(1);
				byte[] blue = _loadedImage.getLayer(2);

				_dispatched = new byte[_width * _height * 3];
				for (int i = 0; i < red.length; i++) {
					_dispatched[3 * i] = red[i];
					_dispatched[3 * i + 1] = green[i];
					_dispatched[3 * i + 2] = blue[i];
				}
			}
		}

		return out;
	}

	public static String addQueryImage(ActionEvent e) {
		String out = null;
		if (e.getActionCommand().equals("ADDQUERYIMAGE")) {

			JFileChooser fileChooser = new JFileChooser(".");
			FileFilter filter = new ExtensionFileFilter("JPG,JPEG,BMP,GIF,TIFF,PNG", new String[] { "JPG", "JPEG",
					"BMP", "GIF", "TIFF", "PNG" });
			fileChooser.setFileFilter(filter);

			int status = fileChooser.showOpenDialog(null);

			if (status == JFileChooser.APPROVE_OPTION) {
				out = fileChooser.getSelectedFile().getPath();
				_imageLocationQuery = out;

				_loadedImageQuery = new Image(_imageLocationQuery);
				_loadedImageQuery.dispatch();

				_widthQuery = _loadedImageQuery.getW();
				_heightQuery = _loadedImageQuery.getH();

				byte[] red = _loadedImageQuery.getLayer(0);
				byte[] green = _loadedImageQuery.getLayer(1);
				byte[] blue = _loadedImageQuery.getLayer(2);

				_dispatchedQuery = new byte[_widthQuery * _heightQuery * 3];
				for (int i = 0; i < red.length; i++) {
					_dispatchedQuery[3 * i] = red[i];
					_dispatchedQuery[3 * i + 1] = green[i];
					_dispatchedQuery[3 * i + 2] = blue[i];
				}

				_queryImage = new imageSetMember();
				_queryImage.newImage(_imageLocationQuery );//setImageLocation(_imageLocation);

				
			}
		}

		return out ;
	}


	public static String openProj3(ActionEvent e) {

		String out = null;

		if (e.getActionCommand().equals("ADDIMGTOSET")) {

			JFileChooser fileChooser = new JFileChooser(".");
			FileFilter filter = new ExtensionFileFilter("JPG,JPEG,BMP,GIF,TIFF,PNG", new String[] { "JPG", "JPEG",
					"BMP", "GIF", "TIFF", "PNG" });
			fileChooser.setFileFilter(filter);

			int status = fileChooser.showOpenDialog(null);

			if (status == JFileChooser.APPROVE_OPTION) {
				out = fileChooser.getSelectedFile().getPath();
				_imageLocation = out;

				_loadedImage = new Image(_imageLocation);
				_loadedImage.dispatch();

				_width = _loadedImage.getW();
				_height = _loadedImage.getH();

				byte[] red = _loadedImage.getLayer(0);
				byte[] green = _loadedImage.getLayer(1);
				byte[] blue = _loadedImage.getLayer(2);

				_dispatched = new byte[_width * _height * 3];
				for (int i = 0; i < red.length; i++) {
					_dispatched[3 * i] = red[i];
					_dispatched[3 * i + 1] = green[i];
					_dispatched[3 * i + 2] = blue[i];
				}

				imageSetMember a = new imageSetMember();
				a.newImage(_imageLocation );//setImageLocation(_imageLocation);
				//a.setLoadedImage();
				//a.getLoadedImage().dispatch();
				//a.set_dispatched_arrays();

				//createMember(_imageLocation);
				imageSetMembers[totalMembers++] = a;
				out = out + "index:" + a.getMyIndex();
			}
		}

		return out ;
	}



	public static String encode() {
		if (_loadedImage == null) return "Error. Load an image first.";

		// write out the basic uncompressed file for reference.
		UtilFuncs.writeToFile(OUT_ORIGINAL_BIN, _dispatched);
		UtilFuncs.writeToFile("out/myTestW.YBR",_dispatched);

		byte[] dispatched = _loadedImage.getDispatched();
		byte[] red = _loadedImage.getLayer(0);
		byte[] green = _loadedImage.getLayer(1);
		byte[] blue = _loadedImage.getLayer(2);

		// Color Space Conversion
		if (_colorspace > 0) {

			byte[] extendedYbr = new byte[(_dispatched.length / 3) * 4];
			for (int i = 0; i < _dispatched.length / 3; i++) {
				// convert RGB to YCbCr
				double[] currentconv = _imgInstance.yuv(
						_dispatched[3 * i] & 0xFF,
						_dispatched[3 * i + 1] & 0xFF,
						_dispatched[3 * i + 2] & 0xFF);

				extendedYbr[4 * i] = (byte) currentconv[0];
				extendedYbr[4 * i + 1] = (byte) currentconv[1];
				extendedYbr[4 * i + 2] = (byte) currentconv[2];
				extendedYbr[4 * i + 3] = 1;
			}

			dispatched = extendedYbr;
		}

		orig = new byte[dispatched.length * 3 / 4];
		for(int i = 0; i < dispatched.length / 4; i++)
		{
			orig[3*i] = dispatched[4*i];
			orig[3*i+1] = dispatched[4*i+1];
			orig[3*i+2] = dispatched[4*i+2];
		}


		// TASK 1 - Resolution Reduction
		int minS = (_s1 <= _s2 && _s1 <= _s3) ? _s1 : ((_s2 <= _s1 && _s2 <= _s3) ? _s2 : _s3);
		//if (minS > 1)
		{
			red = ResolutionReduction.reduceResolution(dispatched, _width, _height, minS, 0);
			green = ResolutionReduction.reduceResolution(dispatched, _width, _height, minS, 1);
			blue = ResolutionReduction.reduceResolution(dispatched, _width, _height, minS, 2);
		}

		_reducedWidth = _width / minS;
		_reducedHeight = _height / minS;

		_reducedDispatch = new byte[red.length * 3];
		for (int i = 0; i < red.length; i++) {
			_reducedDispatch[3 * i] = red[i];
			_reducedDispatch[3 * i + 1] = green[i];
			_reducedDispatch[3 * i + 2] = blue[i];
		}

		// TASK 2 - Quantization
		List<Entry> redSymbols = null;
		List<Entry> greenSymbols = null;
		List<Entry> blueSymbols = null;

		if (_task2 > 0 && _n1 > 0 && _n2 > 0 && _n3 > 0) {
			redSymbols = UtilFuncs.getQuantizedSymbols(red, _n1, 256);
			greenSymbols = UtilFuncs.getQuantizedSymbols(green, _n2, 256);
			blueSymbols = UtilFuncs.getQuantizedSymbols(blue, _n3, 256);

			// update the component values with the bin indices
			red = UtilFuncs.getByteArray(redSymbols);
			green = UtilFuncs.getByteArray(greenSymbols);
			blue = UtilFuncs.getByteArray(blueSymbols);
		}

		// TASK 3 - Predictive Coding
		if (_task3 > 0) {
			int[] intRed = Image.correctedPixels(red);
			int[] pcr = Image.applyPredictiveCoding(_task3 + 1, intRed, _reducedWidth);

			int[] intGreen = Image.correctedPixels(green);
			int[] pcg = Image.applyPredictiveCoding(_task3 + 1, intGreen, _reducedWidth);

			int[] intBlue = Image.correctedPixels(blue);
			int[] pcb = Image.applyPredictiveCoding(_task3 + 1, intBlue, _reducedWidth);

			// TODO some big conceptual issue, where the int value range can be from -255 to 255!

			// update the components with new predicted values
			pcsignsr = new byte[pcr.length];
			pcsignsg = new byte[pcg.length];
			pcsignsb = new byte[pcb.length];
			for (int i = 0; i < intRed.length; i++) {
				if(pcr[i] < 0)
				{
					pcr[i] = -pcr[i];
					pcsignsr[i] = 1;
				}
				else
				{
					pcsignsr[i] = 0;
				}
				red[i] = (byte) pcr[i];
				if(pcg[i] < 0)
				{
					pcg[i] = -pcg[i];
					pcsignsg[i] = 1;
				}
				else
				{
					pcsignsg[i] = 0;
				}
				green[i] = (byte) pcg[i];
				if(pcb[i] < 0)
				{
					pcb[i] = -pcb[i];
					pcsignsb[i] = 1;
				}
				else
				{
					pcsignsb[i] = 0;
				}
				blue[i] = (byte) pcb[i];
			}

			// update the entry list with the predicted values
			if (redSymbols != null && _task4 == 0) {
				redSymbols = UtilFuncs.getEntryList(red, _n1);
				greenSymbols = UtilFuncs.getEntryList(green, _n2);
				blueSymbols = UtilFuncs.getEntryList(blue, _n3);
			}
		}

		// TASK 4 - Error Quantization
		if (_task4 > 0) {
			redSymbols = UtilFuncs.getQuantizedSymbols(red, _m, _task2 > 0? _n1: 256);
			greenSymbols = UtilFuncs.getQuantizedSymbols(green, _m, _task2 > 0? _n2: 256);
			blueSymbols = UtilFuncs.getQuantizedSymbols(blue, _m, _task2 > 0? _n3: 256);

			// update the component values with the bin indices
			red = UtilFuncs.getByteArray(redSymbols);
			green = UtilFuncs.getByteArray(greenSymbols);
			blue = UtilFuncs.getByteArray(blueSymbols);
		}

		// TASK 5 - Variable length encoding (Shannon-Fano or LZW)
		byte[] combined = new byte[red.length * 3];
		for (int i = 0; i < red.length; i++) {
			combined[3 * i] = red[i];
			combined[3 * i + 1] = green[i];
			combined[3 * i + 2] = blue[i];
		}

		if (_task5 == 0) {
			// No VLC selected, write out using the entry list or the byte array
			String fileName;
			if (_colorspace == 0) {
				fileName = OUT_OBJECT_RGB_FILENAME;
			} else {
				fileName = OUT_OBJECT_YBR_FILENAME;
			}
			// if quantization is done
			if (redSymbols != null) {
				// combine the RGB as single entry list before writing
				List<Entry> combinedEntries = new ArrayList<Entry>();
				for (int i = 0; i < redSymbols.size(); i++) {
					combinedEntries.add(redSymbols.get(i));
					combinedEntries.add(greenSymbols.get(i));
					combinedEntries.add(blueSymbols.get(i));
				}
				UtilFuncs.writeToFile(fileName, combinedEntries);
				UtilFuncs.writeToFile("out/myTestX.YBR", combined);
			} else {
				// no quantization, write out the combined byte sequence
				UtilFuncs.writeToFile(fileName, combined);
				UtilFuncs.writeToFile("out/myTestY.YBR", combined);

			}

		// TODO the delegated file writes use a different file location, fix that
		} else if (_task5 == 1) {
			// Shannon-Fano selected, delegate writing out to SymbolTable
			_symbolTable.buildTableViaSF(combined);
			_symbolTable.writeOut(combined, _colorspace);
		} else {
			// LZW selected, delegate writing out to LZW
			LZW.writeOut(combined, _d, _colorspace);
		}

		return "Successfully encoded file." + _s1;
	}



	public static String decode() {
		byte[] decoded;
		byte[] red, green, blue;

		String fileName;
		if (_colorspace == 0) {
			fileName = OUT_OBJECT_RGB_FILENAME;
		} else {
			fileName = OUT_OBJECT_YBR_FILENAME;
		}

		// read the binary file according to how it was encoded
		if (_task5 == 0) {
			if (_task4 > 0) {
				decoded = UtilFuncs.getByteArray(UtilFuncs.readFromFile(fileName, _m, _m, _m));
			} else if (_task2 > 0) {
				decoded = UtilFuncs.getByteArray(UtilFuncs.readFromFile(fileName, _n1, _n2, _n3));
			} else {
				decoded = UtilFuncs.getByteArray(UtilFuncs.readFromFile(fileName, 256, 256, 256));
			}
		} else if (_task5 == 1) {
			decoded = _symbolTable.readIn(_colorspace);
		} else {
			decoded = LZW.readIn(_colorspace);
		}

		int sml = decoded.length / 3;

		red = new byte[sml];
		green = new byte[sml];
		blue = new byte[sml];

		for (int i = 0; i < red.length; i++) {
			red[i] = decoded[3 * i];
			green[i] = decoded[3 * i + 1];
			blue[i] = decoded[3 * i + 2];
		}

		// TODO de-quantize errors if at all they were quantized
		if (_task4 > 0) {
			for (int i = 0; i < red.length; i++) {
				decoded[3 * i] = (byte) ((int)(red[i] & 0xff) * (int)((_task2 > 0? _n1:256) / _m));
				decoded[3 * i + 1] = (byte) ((int)(green[i] & 0xff) * (int)((_task2 > 0? _n2:256) / _m));
				decoded[3 * i + 2] = (byte) ((int)(blue[i] & 0xff) * (int)((_task2 > 0? _n3:256) / _m));
				red[i] = decoded[3 * i];
				green[i] = decoded[3 * i + 1];
				blue[i] = decoded[3 * i + 2];
			}
		}

		// remove predic coding
		if (_task3 > 0) {

			int[] int_red = Image.correctedPixels(red);
			for(int i = 0; i < int_red.length; i++)
			{
				if(pcsignsr[i] == 1)
					int_red[i] = -int_red[i];
			}
			int[] pcr = Image.removePredictiveCoding(_task3 + 1, int_red, _reducedWidth);

			int[] int_green = Image.correctedPixels(green);
			for(int i = 0; i < int_green.length; i++)
			{
				if(pcsignsg[i] == 1)
					int_green[i] = -int_green[i];
			}
			int[] pcg = Image.removePredictiveCoding(_task3 + 1, int_green, _reducedWidth);

			int[] int_blue = Image.correctedPixels(blue);
			for(int i = 0; i < int_blue.length; i++)
			{
				if(pcsignsb[i] == 1)
					int_blue[i] = -int_blue[i];
			}
			int[] pcb = Image.removePredictiveCoding(_task3 + 1, int_blue, _reducedWidth);

			for (int i = 0; i < int_red.length; i++) {
				red[i] = (byte) pcr[i];
				green[i] = (byte) pcg[i];
				blue[i] = (byte) pcb[i];
			}

		}

		// restore bin indices with their values (de-quantization)
		for (int i = 0; i < red.length; i++) {
			decoded[3 * i] = (byte) (_task2 > 0 ? ((int)(red[i] & 0xff) * (int)(256 / _n1)) : red[i]);
			decoded[3 * i + 1] = (byte) (_task2 > 0 ? ((int)(green[i] & 0xff) * (int)(256 / _n2)) : green[i]);
			decoded[3 * i + 2] = (byte) (_task2 > 0 ? ((int)(blue[i] & 0xff) * (int)(256 / _n3)) : blue[i]);
		}




		// write out the final image and display it
		try {
			MagickImage image = new MagickImage();
			image.constituteImage(_reducedWidth, _reducedHeight, "RGB", decoded);
			image.setFileName(OUT_IMAGE_FILENAME);
			image.writeImage(new ImageInfo());

			// use for porject 2
			UtilFuncs.displayImage2(image, OUT_IMAGE_FILENAME, _selectedIndex);
			//use for project 3
			 // partition the image and putit in instance3 and cellgrid3

			//Utilities_01.gridLoadImage3(OUT_IMAGE_FILENAME);// sets instance3 and cellgrid3
			//UtilFuncs.gridDisplayImage3(OUT_gridImage_FILENAME, _selectedIndex);
			//System.out.println(encodeView_ImageIntoCells( _imageLocation,"ve_" + OUT_gridImage_FILENAME,_selectedIndex));
			//System.out.println(encode_ImageIntoCells( _imageLocation,"e_" + OUT_gridImage_FILENAME));
			//System.out.println(view_ImageIntoCells("v_" + OUT_gridImage_FILENAME, "blank"));


			/*
			File file = new File(OUT_IMAGE_FILENAME);
			BufferedImage image_new2 = ImageIO.read(file);
			JLabel label = new JLabel (new ImageIcon(image_new2));
			JFrame f = new JFrame(_selectedIndex + " " + OUT_IMAGE_FILENAME);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.getContentPane().add(label);
			f.pack();
			f.setVisible(true); */

		} catch (MagickException e) {
			e.printStackTrace();
		}



		// display and output size/SNR.

		return "Image decompression complete.\n" + "Original Size: " + UtilFuncs.getSize(OUT_ORIGINAL_BIN) + " bytes.\n"
				+ "Compressed Size: " + UtilFuncs.getSize(fileName) + " bytes.\n" + "Compression Ratio: "
				+ (UtilFuncs.getSize(fileName) * 1.0) / UtilFuncs.getSize(OUT_ORIGINAL_BIN) + "\n" + "SNR: "
				+ Image.getDistortion(_reducedDispatch, decoded);
	}


	public static String encode3() {
		if (_loadedImage == null) return "Error. Load an image first.";

		byte[] dispatched = _loadedImage.getDispatched();
		byte[] red = _loadedImage.getLayer(0);
		byte[] green = _loadedImage.getLayer(1);
		byte[] blue = _loadedImage.getLayer(2);

		// Color Space Conversion
		if (_colorspace > 0) {

			byte[] extendedYbr = new byte[(_dispatched.length / 3) * 4];
			for (int i = 0; i < _dispatched.length / 3; i++) {
				// convert RGB to YCbCr
				double[] currentconv;
				if (_colorspace == 1){
						currentconv = _imgInstance.yuv(
						_dispatched[3 * i] & 0xFF,
						_dispatched[3 * i + 1] & 0xFF,
						_dispatched[3 * i + 2] & 0xFF);
				}
				else{

						currentconv = _imgInstance.hsv(
					_dispatched[3 * i] & 0xFF,
					_dispatched[3 * i + 1] & 0xFF,
					_dispatched[3 * i + 2] & 0xFF);
				}


				extendedYbr[4 * i] = (byte) currentconv[0];
				extendedYbr[4 * i + 1] = (byte) currentconv[1];
				extendedYbr[4 * i + 2] = (byte) currentconv[2];
				extendedYbr[4 * i + 3] = 1;
			}

			dispatched = extendedYbr;
		}

		orig = new byte[dispatched.length * 3 / 4];
		for(int i = 0; i < dispatched.length / 4; i++)
		{
			orig[3*i] = dispatched[4*i];
			orig[3*i+1] = dispatched[4*i+1];
			orig[3*i+2] = dispatched[4*i+2];
		}



		// TASK 5 - Variable length encoding (Shannon-Fano or LZW)
		byte[] combined = new byte[red.length * 3];
		for (int i = 0; i < red.length; i++) {
			combined[3 * i] = red[i];
			combined[3 * i + 1] = green[i];
			combined[3 * i + 2] = blue[i];
		}

			// No VLC selected, write out using the entry list or the byte array
			String fileName;
			if (_colorspace == 0) {
				fileName = OUT_OBJECT_RGB_FILENAME;}
			else if (_colorspace == 1) {
				fileName = OUT_OBJECT_YUV_FILENAME;}
			else {fileName = OUT_OBJECT_HSV_FILENAME;}
					// no quantization, write out the combined byte sequence
				UtilFuncs.writeToFile(fileName, combined);
				UtilFuncs.writeToFile("out/myTestY.YBR", combined);


		return "Successfully encoded file." + _s1;
	}



	public static String decode3() {
		byte[] decoded;
		byte[] red, green, blue;

		String fileName;

		if (_colorspace == 0) {
			fileName = OUT_OBJECT_RGB_FILENAME;}
		else if (_colorspace == 1) {
			fileName = OUT_OBJECT_YUV_FILENAME;}
		else {fileName = OUT_OBJECT_HSV_FILENAME;}
		// read the binary file according to how it was encoded
		decoded = UtilFuncs.getByteArray(UtilFuncs.readFromFile(fileName, 256, 256, 256));

		int sml = decoded.length / 3;

		red = new byte[sml];
		green = new byte[sml];
		blue = new byte[sml];

		for (int i = 0; i < red.length; i++) {
			red[i] = decoded[3 * i];
			green[i] = decoded[3 * i + 1];
			blue[i] = decoded[3 * i + 2];
		}


		// restore bin indices with their values (de-quantization)
		for (int i = 0; i < red.length; i++) {
			decoded[3 * i] = (byte) red[i];
			decoded[3 * i + 1] = (byte) green[i];
			decoded[3 * i + 2] = (byte) blue[i];
		}




		// write out the final image and display it
		try {
			MagickImage image = new MagickImage();
			image.constituteImage( _width, _height, "RGB", decoded);
			image.setFileName(OUT_IMAGE_FILENAME);
			image.writeImage(new ImageInfo());

			// use for porject 2
			UtilFuncs.displayImage2(image, OUT_IMAGE_FILENAME, _selectedIndex);
			//use for project 3
			 // partition the image and putit in instance3 and cellgrid3

			//Utilities_01.gridLoadImage3(OUT_IMAGE_FILENAME);// sets instance3 and cellgrid3
			//UtilFuncs.gridDisplayImage3(OUT_gridImage_FILENAME, _selectedIndex);
			//System.out.println(encodeView_ImageIntoCells( _imageLocation,"ve_" + OUT_gridImage_FILENAME,_selectedIndex));
			//System.out.println(encode_ImageIntoCells( _imageLocation,"e_" + OUT_gridImage_FILENAME));
			//System.out.println(view_ImageIntoCells("v_" + OUT_gridImage_FILENAME, "blank"));


			/*
			File file = new File(OUT_IMAGE_FILENAME);
			BufferedImage image_new2 = ImageIO.read(file);
			JLabel label = new JLabel (new ImageIcon(image_new2));
			JFrame f = new JFrame(_selectedIndex + " " + OUT_IMAGE_FILENAME);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.getContentPane().add(label);
			f.pack();
			f.setVisible(true); */

		} catch (MagickException e) {
			e.printStackTrace();
		}



		// display and output size/SNR.

		return "Image decompression complete.\n" + "Original Size: " + UtilFuncs.getSize(OUT_ORIGINAL_BIN) + " bytes.\n"
				+ "Compressed Size: " + UtilFuncs.getSize(fileName) + " bytes.\n" + "Compression Ratio: "
				+ (UtilFuncs.getSize(fileName) * 1.0) / UtilFuncs.getSize(OUT_ORIGINAL_BIN) + "\n" + "SNR: "
				+ Image.getDistortion(_reducedDispatch, decoded);
	}



	public static String zzzzencodeView_selectedImage(String imageLocation_source,  String selectedIndex) {
		//!!!
		try{
		Utilities_01.gridLoadImage3(imageLocation_source);// sets instance3 and cellgrid3 in class Utilities
		MagickImage seqImage = new MagickImage(Utilities.cellgrid3);
		MontageInfo montageInfo = new MontageInfo(new ImageInfo());
		MagickImage montage = seqImage.montageImages(montageInfo);


		// writes the image to working directory
		//some how only file name work for me ,
		montage.setFileName(OUT_gridImage_FILENAME);
		montage.writeImage(new ImageInfo(montage.getFileName()));

		UtilFuncs.gridViewImage3(OUT_gridImage_FILENAME, selectedIndex);


		}
		catch (MagickException e) {
			e.printStackTrace();
		}


		return "\n   Encoded Celled Image stored at: " + OUT_gridImage_FILENAME + ", celled image in variables instance3 (2d) and cellgrid3 (1D)";
	}




	public static String encodeView_ImageIntoCells(String imageLocation_source, String imageCellsfileNameNotfilePath, String selectedIndex) {
		//!!!
		try{
		Utilities_01.gridLoadImage3(imageLocation_source);// sets instance3 and cellgrid3 in class Utilities
		MagickImage seqImage = new MagickImage(Utilities.cellgrid3);
		MontageInfo montageInfo = new MontageInfo(new ImageInfo());
		MagickImage montage = seqImage.montageImages(montageInfo);


		// writes the image to working directory
		//some how only file name work for me ,
		montage.setFileName(OUT_gridImage_FILENAME);
		montage.writeImage(new ImageInfo(montage.getFileName()));

		UtilFuncs.gridViewImage3(OUT_gridImage_FILENAME, selectedIndex);


		}
		catch (MagickException e) {
			e.printStackTrace();
		}


		return "displayed Celled Image stored at: " + imageCellsfileNameNotfilePath + "\n   Encoded Celled Image stored at: " + OUT_gridImage_FILENAME + ", celled image in variables instance3 (2d) and cellgrid3 (1D)";
	}



	public static String encode_ImageIntoCells(String imageLocation_source, String imageLocation_destination) {
		try{
			Utilities_01.gridLoadImage3(imageLocation_source);// sets instance3 and cellgrid3
			MagickImage seqImage = new MagickImage(Utilities.cellgrid3);
			MontageInfo montageInfo = new MontageInfo(new ImageInfo());
			MagickImage montage = seqImage.montageImages(montageInfo);


			// writes the image to working directory
			//some how only file name work for me ,
			montage.setFileName(OUT_gridImage_FILENAME);
			montage.writeImage(new ImageInfo(montage.getFileName()));

			}
			catch (MagickException e) {
				e.printStackTrace();
			}

			return "Encoded Celled Image stored at: " + imageLocation_destination + "\n   celled image in is variables instance3 (2d) and cellgrid3 (1D)";
	}

	public static String view_ImageIntoCells(String celledImageLocation, String selectedIndex) {
		try{
			UtilFuncs.gridViewImage3(celledImageLocation,selectedIndex);

			}
			catch (MagickException e) {
				e.printStackTrace();
			}

			return "displaying Celled Image stored at: " + celledImageLocation;
	}

		//grabs features for a 3cell by 3cell grid in the target image
		public static ArrayList<Feature> grabFeaturesForGrid(int startcell, int w, int feature, int channel) throws Exception
		{
			w = w / 8 * 8;
			ArrayList<Feature> outputs = new ArrayList<Feature>();
			String[] filenames = {"colordatatarget.csv", "dctdatatarget.csv", "gAngdatatarget.csv", "gAmpdatatarget.csv", "dwtdatatarget.csv"};
			CSVReader reader = new CSVReader(new FileReader(filenames[feature]));
			String[] row;
			ArrayList<String[]> inputs = new ArrayList<String[]>();
			int cells = 0;
			while((row = reader.readNext()) != null)
			{
				inputs.add(row);
				if(Integer.parseInt(row[1]) > cells)
					cells = Integer.parseInt(row[1]);
			}
			cells++;
			if(feature >0)
			{
				double[][][] outs = new double[cells][3][16];
				for(int j = 0; j < inputs.size(); j++)
				{
					outs[Integer.parseInt((inputs.get(j))[1])][Integer.parseInt((inputs.get(j))[2])][Integer.parseInt((inputs.get(j))[3])] = Double.parseDouble((inputs.get(j))[4]);
				}

				for(int i = 0; i < outs.length; i++)
				{
					if((i==startcell)         || (i==startcell+1)         || (i==startcell+2)       ||
					   (i==startcell+(w/8))   || (i==startcell+(w/8)+1)   || (i==startcell+(w/8)+2) ||
					   (i==startcell+(2*w/8)) || (i==startcell+(2*w/8)+1) || (i==startcell+(2*w/8)+2))
						outputs.add(new Feature(outs[i][channel],"targetimage",i));
				}
			}
			else
			{
				double[][] outs = new double[cells][16];
				for(int j = 0; j < inputs.size(); j++)
				{
					outs[Integer.parseInt((inputs.get(j))[1])][Integer.parseInt((inputs.get(j))[2])] = Double.parseDouble((inputs.get(j))[3]);
				}

				for(int i = 0; i < outs.length; i++)
				{
					if((i==startcell)         || (i==startcell+1)         || (i==startcell+2)       ||
					   (i==startcell+(w/8))   || (i==startcell+(w/8)+1)   || (i==startcell+(w/8)+2) ||
					   (i==startcell+(2*w/8)) || (i==startcell+(2*w/8)+1) || (i==startcell+(2*w/8)+2))
						outputs.add(new Feature(outs[i],"targetimage",i));
				}
			}

			return outputs;
		}

		public static ArrayList<Feature> grabFeaturesForImage(String imgid, int feature, int channel) throws Exception
		{
			ArrayList<Feature> outputs = new ArrayList<Feature>();
			String[] filenames = {"colordata.csv", "dctdata.csv", "gAngdata.csv", "gAmpdata.csv", "dwtdata.csv"};
			CSVReader reader = new CSVReader(new FileReader(filenames[feature]));
			String[] row;
			ArrayList<String[]> inputs = new ArrayList<String[]>();
			int cells = 0;
			while((row = reader.readNext()) != null)
			{
				if(row[0].equals(imgid))
				{
					inputs.add(row);
					if(Integer.parseInt(row[1]) > cells)
						cells = Integer.parseInt(row[1]);
				}
			}
			cells++;
			if(feature >0)
			{
				double[][][] outs = new double[cells][3][16];
				for(int j = 0; j < inputs.size(); j++)
				{
					outs[Integer.parseInt((inputs.get(j))[1])][Integer.parseInt((inputs.get(j))[2])][Integer.parseInt((inputs.get(j))[3])] = Double.parseDouble((inputs.get(j))[4]);
				}

				for(int i = 0; i < outs.length; i++)
				{
					outputs.add(new Feature(outs[i][channel],imgid,i));
				}
			}
			else
			{
				double[][] outs = new double[cells][16];
				for(int j = 0; j < inputs.size(); j++)
				{
					outs[Integer.parseInt((inputs.get(j))[1])][Integer.parseInt((inputs.get(j))[2])] = Double.parseDouble((inputs.get(j))[3]);
				}

				for(int i = 0; i < outs.length; i++)
				{
					outputs.add(new Feature(outs[i],imgid,i));
				}
			}

			return outputs;
		}

		public static ArrayList<int[]> getWidthHeights()
		{
			ArrayList<int[]> wh = new ArrayList<int[]>();
			try
			{
				CSVReader reader = new CSVReader(new FileReader("imagedimensions.csv"));
				String[] row;
				while((row = reader.readNext()) != null)
				{
					int xw = (Integer.parseInt(row[1]) / 8) * 8;
					int xh = (Integer.parseInt(row[2]) / 8) * 8;
					int[] dim = new int[2];
					dim[0] = xw;
					dim[1] = xh;
					wh.add(dim);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return wh;
		}

		public static void generateImageSetCSV(imageSetMember[] imgs) throws Exception
		{
			CSVWriter writer = new CSVWriter(new FileWriter("imagedimensions.csv"));
			String[] row = new String[3];
			for(int i = 0; i < imgs.length; i++)
			{
				if(imgs[i] != null)
				{
					row[0] = "image" + i;
					row[1] = imgs[i].width / 8 * 8 + "";
					row[2] = imgs[i].height / 8 * 8 + "";
					writer.writeNext(row);
				}
			}
			writer.close();
		}
		
		
		public static MagickImage regioncrop(MagickImage img, int cellx, int celly)
		{
			MagickImage output = new MagickImage();
			try
			{
				//int x, y;
				//int w = img.getDimension().width / 8;
				//int h = img.getDimension().height / 8;
				//y = cellindex / w;
				//x = cellindex % w;
				Rectangle rect = new Rectangle(cellx * 8, celly * 8, 24, 24);
				output = img.cropImage(rect);
				output.setFileName("lastInputRegion.jpg");
				output.writeImage(new ImageInfo()); 
				UtilFuncs.displayImage2(output,"lastInputRegion.jpg","lastInputRegion.jpg");	
			

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return output;
		}

}
