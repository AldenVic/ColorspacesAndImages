import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.MontageInfo;
import magick.util.MagickWindow;

import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
 






public class UtilFuncs {

	/**
	 * Displays given {@link MagickImage} in a window.
	 * 
	 * @param img The {@link MagickImage} object
	 * @param s The title of the window
	 */	
	
	public static void gridViewImage3(String s, String _selectedIndex) throws MagickException{
				
		try {
			
			File file = new File(s);
			BufferedImage image_new2 = ImageIO.read(file);
			JLabel label = new JLabel (new ImageIcon(image_new2));
			JFrame f = new JFrame(_selectedIndex + " " + s);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setSize(new Dimension( image_new2.getWidth() + 8, image_new2.getHeight() + 8));
			f.setResizable(true);
			f.setMinimumSize(new Dimension( image_new2.getWidth() + 8, image_new2.getHeight() + 8));
			f.getContentPane().add(label);
			//f.setSize(loadedImage1.getW(),loadedImage1.getH());
			//f.setMinimumSize()
			//f.pack();
		
			//!!! to access Tiles/cells in image use the image_new2 object
						
			f.setVisible(true);
			}
		catch ( IOException e)
			{	e.printStackTrace();} 
		
	}
		
		
			
	
public static void gridDisplayImage3(String s, String _selectedIndex) throws MagickException{

	
	if (Utilities.instance3 != null)
	{
		MagickImage seqImage = new MagickImage(Utilities.cellgrid3);
		MontageInfo montageInfo = new MontageInfo(new ImageInfo());
		MagickImage montage = seqImage.montageImages(montageInfo);
	
				
		// writes the image to working directory
		montage.setFileName(s);	
		montage.writeImage(new ImageInfo(montage.getFileName()));
		/*if (montage.writeImage(new ImageInfo(montage.getFileName())))	
			{System.out.println(montage.getFileName());}
		else
			System.out.println("mm" + montage.getFileName() + "did not write");
		*/
	}
	


	gridViewImage3(s, _selectedIndex);
	
/*		
	try {
		
			
		
		File file = new File(s);
		BufferedImage image_new2 = ImageIO.read(file);
		JLabel label = new JLabel (new ImageIcon(image_new2));
		JFrame f = new JFrame(_selectedIndex + " " + s);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setSize(new Dimension( image_new2.getWidth() + 8, image_new2.getHeight() + 8));
		f.setResizable(true);
		f.setMinimumSize(new Dimension( image_new2.getWidth() + 8, image_new2.getHeight() + 8));
		f.getContentPane().add(label);
		//f.setSize(loadedImage1.getW(),loadedImage1.getH());
		//f.setMinimumSize()
		//f.pack();
		
		f.setVisible(true);
		}
	catch ( IOException e)
		{	e.printStackTrace();} */
	
}
	
	
	
	
	
	/**
	 * Displays given {@link MagickImage} in a window.
	 * 
	 * @param img The {@link MagickImage} object
	 * @param s The title of the window
	 */	
public static void displayImage2(MagickImage img, String s, String _selectedIndex) {
	try {
		File file = new File(s);
		BufferedImage image_new2 = ImageIO.read(file);
		JLabel label = new JLabel (new ImageIcon(image_new2));
		JFrame f = new JFrame(_selectedIndex + " " + s);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.getContentPane().add(label);
		f.pack();
		f.setVisible(true);
		}
	catch ( IOException e)
		{	e.printStackTrace();} 
	
	
	
	
}
	



	/**
	 * Displays given {@link MagickImage} in a window.
	 * 
	 * @param img The {@link MagickImage} object
	 * @param s The title of the window
	 */
	public static void displayImage(MagickImage img, String s) {

		// setup structures to display image
		String path = null;
		ImageIcon ii = null;
		JLabel label = null;
		JScrollPane jsp = null;
		int width = 0;
		int height = 0;

		// get image path location
		try {
			width = img.getDimension().width;
			height = img.getDimension().height;
			path = img.getFileName();
		} catch (MagickException e) {
		}

		// create and set up independent the window
		JFrame indFrame = new JFrame(s + ", Location: " + path );
		indFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if (path != null) {
			ii = new ImageIcon(path);
			label = new JLabel(ii);
			jsp = new JScrollPane(label);
			indFrame.getContentPane().add(jsp);
			indFrame.getContentPane().add(jsp);
		}

		// display the window.
		indFrame.setSize(width + 60, height + 60);
		indFrame.setResizable(true);
		indFrame.setVisible(true);
		indFrame.repaint();

	}
	
	

	
	

	/**
	 * Given a bitstream as a list of entries writes them out to file. This
	 * function is tightly coupled with list of entries, it does no validation
	 * for the actual values in the code.
	 * 
	 * @param fileName The full file name including the path
	 * @param entries The validated list of entries
	 */
	public static void writeToFile(String fileName, List<Entry> entries) {
		try {
			BinaryOut out = new BinaryOut(fileName);
			for (Entry entry : entries) {
				String code = entry.getCode();
				for (int j = 0; j < code.length(); j++) {
					if (code.charAt(j) == '0') {
						out.write(false);
					} else {
						out.write(true);
					}
				}
			}
			out.flush();
		} catch (Exception ex) {
			throw new RuntimeException("Error writing the bit stream to file");
		}
	}

	/**
	 * Given a byte array, writes it out to a file.
	 * 
	 * @param fileName The full file name including the path
	 * @param data The byte array
	 */
	public static void writeToFile(String fileName, byte[] data) {
		BinaryOut bOut = new BinaryOut(fileName);
		for (int i = 0; i < data.length; i++) {
			bOut.write(data[i]);
		}
		bOut.flush();
	}

	/**
	 * Given a byte array with a given max value and required number of bins
	 * quantizes the values and returns the bit stream as a list of entries.
	 * 
	 * @param data The actual byte array that is to be quantized
	 * @param bins The required number of bins
	 * @param max The max size to be assumed
	 * @return The list of entries
	 */
	public static List<Entry> getQuantizedSymbols(byte[] data, int bins, int max) {
		int binSize;
		int bitCount = (int) Math.ceil(Math.log(bins) / Math.log(2));

		if (max < bins) {
			binSize = 1;
		} else {
			binSize = max / bins;
		}

		List<Entry> out = new ArrayList<Entry>();

		// Quantize the data
		for (int j = 0; j < data.length; j++) {
			int frombyte = data[j] & 0xff;
			int lookupIndex = frombyte / binSize;
			String bitString = "";
			for (int i = 0; i < bitCount; i++) {
				if ((lookupIndex & 0x1) == 0) {
					bitString = "0" + bitString;
				} else if ((lookupIndex & 0x1) == 1) {
					bitString = "1" + bitString;
				}
				lookupIndex = lookupIndex >> 1;
			}
			out.add(new Entry(frombyte / binSize, bitString));
		}

		return out;
	}

	/**
	 * Returns the basic byte array from a given list of entries
	 * 
	 * @param entries The entry list
	 * @return The byte array
	 */
	public static byte[] getByteArray(List<Entry> entries) {
		byte[] data = new byte[entries.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) entries.get(i).getSymbol();
		}
		return data;
	}

	/**
	 * Given a file, which was generated using
	 * {@link #writeToFile(String, List)} reads the entire bit stream from the
	 * file based on the given codeSize and formats and returns them as a list
	 * of entries.
	 * 
	 * @param fileName The full file name including the path
	 * @param bins The number of bins
	 * @return The list of entries
	 */
	public static List<Entry> readFromFile(String fileName, int bins1, int bins2, int bins3) {

		List<Entry> entries = new ArrayList<>();

		BinaryIn in = new BinaryIn(fileName);
		int bitCount1 = (int) Math.ceil(Math.log(bins1) / Math.log(2));
		int bitCount2 = (int) Math.ceil(Math.log(bins2) / Math.log(2));
		int bitCount3 = (int) Math.ceil(Math.log(bins3) / Math.log(2));
		while (!in.isEmpty()) {
			String code = "";
			int symbol = 0;
			for (int i = 0; i < bitCount1; i++) {
				boolean readBit = in.readBoolean();
				code += readBit ? '1' : '0';
				symbol += Math.pow(2, bitCount1 - 1 - i) * (readBit ? 1 : 0);
			}
			entries.add(new Entry(symbol, code));
			code = "";
			symbol = 0;
			for (int i = 0; i < bitCount2; i++) {
				boolean readBit = in.readBoolean();
				code += readBit ? '1' : '0';
				symbol += Math.pow(2, bitCount2 - 1 - i) * (readBit ? 1 : 0);
			}
			entries.add(new Entry(symbol, code));
			code = "";
			symbol = 0;
			for (int i = 0; i < bitCount3; i++) {
				boolean readBit = in.readBoolean();
				code += readBit ? '1' : '0';
				symbol += Math.pow(2, bitCount3 - 1 - i) * (readBit ? 1 : 0);
			}
			entries.add(new Entry(symbol, code));
		}

		return entries;
	}


	public static long getSize(String fileName) {
		File file = new File(fileName);
		return file.length();
	}

	/**
	 * Given a bit stream as a list of entries, which are assumed to be
	 * quantized using a given bin size, the function restores the byte array
	 * using the bin size and the given list of entries
	 * 
	 * IMP: No validation
	 * 
	 * @param entries The list of entries
	 * @param binSize The bin size
	 * @return The byte array
	 */
	public static byte[] restoreSymbols(List<Entry> entries, int binSize1, int binSize2, int binSize3) {

		byte[] data = new byte[entries.size()];

		for (int i = 0; i < data.length / 3; i++) {
			data[i * 3 + 0] = (byte) (entries.get(i * 3 + 0).getSymbol() * binSize1);
			data[i * 3 + 1] = (byte) (entries.get(i * 3 + 1).getSymbol() * binSize2);
			data[i * 3 + 2] = (byte) (entries.get(i * 3 + 2).getSymbol() * binSize3);
		}

		return data;
	}

	/**
	 * Given an int number and the number of least significant bits to be accounted, extracts
	 * them in binary format as a string
	 * 
	 * @param number The in number
	 * @param bitCount The number of most significant bits to be used
	 * @return The binary number as a String
	 */
	public static String getBitString(int number, int bitCount) {
		String bitString = "";
		for (int i = 0; i < bitCount; i++) {
			if ((number & 0x1) == 0) {
				bitString = "0" + bitString;
			} else if ((number & 0x1) == 1) {
				bitString = "1" + bitString;
			}
			number = number >> 1;
		}
		return bitString;
	}

	/**
	 * Given a byte array and the number of bits it was quantized to this method generates
	 * the entry list
	 * 
	 * @param data The byte array
	 * @param bins The number of bins
	 * @return The entry list
	 */
	public static List<Entry> getEntryList(byte[] data, int bins) {
		int bitCount = (int) Math.ceil(Math.log(bins) / Math.log(2));
		List<Entry> entries = new ArrayList<Entry>();
		for (int i = 0; i < data.length; i++) {
			int symbol = data[i];
			String code = getBitString(symbol, bitCount);
			entries.add(new Entry(symbol, code));
		}
		return entries;
	}

}
