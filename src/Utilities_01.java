import java.awt.Rectangle;

import javax.swing.JTextArea;

import magick.ColorspaceType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.MontageInfo;
import magick.PixelPacket;
import magick.util.MagickWindow;

public class Utilities_01
{
	// main static instance
	// access this for loaded partitioned 6x6 image
	// original instance, no adjustments made
	private static MagickImage[][] instance;
	private static MagickImage[] cellgrid;
	private static MagickImage[][] instance3;
	private static MagickImage[] cellgrid3;
	
	
	

	// load an image and partition it 6x6
	// used by Driver.java
	public static void loadImage(String dir) throws MagickException
	{
		instance = partitionIntoGrid(dir, 6, 6);
		
		
		// 9/19/12 this cuts the partition 2d array into a single
		// loads the cellgrid after, it shouldn't be called after
		// apply call from driver
		MagickImage images[] = new MagickImage[36];
		int n = 0;
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 6; j++)
			{
				images[n] = instance[i][j];
				n++;
			}
		}
		
		cellgrid = images;
	}
	// load an image and partition it 8 X 8
	// used by Utilities.java
	public static void gridLoadImage3(String dir) throws MagickException
		{
		Utilities.instance3 = partitionIntoGrid(dir, 8, 8);
				
				
		// 9/19/12 this cuts the partition 2d array into a single
		// loads the cellgrid after, it shouldn't be called after
		// apply call from driver
		MagickImage images[] = new MagickImage[64];
		int n = 0;
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				images[n] = Utilities.instance3[i][j];
				n++;
			}
		}
				
		Utilities.cellgrid3 = images;
		}
	
	
	
	// returns the current image instance
	public static MagickImage[][] getInstance()
	{
		return instance;
	}
	// returns the current image instance used by project 3
		public static MagickImage[][] getInstance3()
		{
			return Utilities.instance3;
		}
	// displays the partitioned image, in montage view
	// adjust as needed
	public static void viewImage() throws MagickException
	{
		if (instance != null)
		{
			// creates a temp image array to hold the
			// image partitions
			/*
			MagickImage images[] = new MagickImage[36];
			int n = 0;
			for (int i = 0; i < 6; i++)
			{
				for (int j = 0; j < 6; j++)
				{
					images[n] = instance[i][j];
					n++;
				}
			}
			*/

			MagickImage seqImage = new MagickImage(cellgrid);
			MontageInfo montageInfo = new MontageInfo(new ImageInfo());

			// montageInfo.setTitle("Melbourne");
			// montageInfo.setBorderWidth(0);

			MagickImage montage = seqImage.montageImages(montageInfo);

			// writes the image to default directory
			// montageInfo.setFileName("montage.jpg");
			// montage.writeImage(new ImageInfo());

			// ////////////////////
			// String f = instance.getFileName();
			MagickWindow window = new MagickWindow(montage);
			window.setSize(800, 800);
			window.setTitle("");
			window.setVisible(true);
			// return "Applying options to " + f + "\n";
			//cellgrid = images;
		}
	}
	
	// start code

	// displays the partitioned image, in montage view

	// adjust as needed

	 
	// end code

	public static MagickImage[][] partitionIntoGrid(String fileNamePath, int rows, int columns)
	{
		try
		{
			ImageInfo ii = new ImageInfo(fileNamePath);
			MagickImage mi = new MagickImage(ii);
			//mi.setFileName(fileNamePath);

			// get dimensions of the full image
			int width = mi.getDimension().width;
			int height = mi.getDimension().height;

			// calculate dimensions of the grid
			float gridWidth = width / columns;
			float gridHeight = height / rows;

			// define the grid
			MagickImage[][] cropped = new MagickImage[rows][columns];

			// divide and store the full image into the defined rowsXcolumns
			// grid
			for (int row = 0; row < rows; row++)
			{
				for (int column = 0; column < columns; column++)
				{
					cropped[row][column] = mi.cropImage(new Rectangle(
							(int) (gridWidth * column),
							(int) (gridHeight * row), (int) gridWidth,
							(int) gridHeight));
				}
			}

			// return the grid
			return cropped;
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		// return null if fail
		return null;
	}
	
	public static int getMostSimilarAverage(int colorspace, int selectedCellIndex)
	{
		try
		{
			// first, get the average color values for each cell and store in
			// pixel packet array
			double[][] averagecolors = processAverageColorsArray(colorspace);
			float currentdistance;
			float lowestdistance = -1;
			int lowestx = selectedCellIndex;
			double[] comparecolor = averagecolors[selectedCellIndex];
			// iterate through the average colors and compare all to the
			// selected cell.
			for (int x = 0; x < cellgrid.length; x++)
			{
				if (x != selectedCellIndex)
				{
					// calculate euclidean distance between current cell and the
					// selected cell
					currentdistance = calculateEuclidean(averagecolors[x],
							comparecolor);
					// update the lowest distance
					if (lowestdistance == -1
							|| currentdistance < lowestdistance)
					{
						lowestdistance = currentdistance;
						lowestx = x;
					}
				}
			}
			// return the cell in the grid with the lowest euclidean distance
			// value
			return lowestx;

		}
		catch (Exception ex)
		{
			// System.out.println(ex.getMessage());
		}
		return -1;
	}

	
	public static float calculateEuclidean(double[] a, double[] b)
	{
		// utility function that takes two pixelpackets and gets their distance
		// using RGB components
		return (float) Math.sqrt(Math.abs(
				  ((b[0] - a[0]) * (b[0] - a[0]))
				+ ((b[1] - a[1]) * (b[1] - a[1]))
				+ ((b[2] - a[2]) * (b[2] - a[2]))
				));
	}

	public static int getMagickColorSpace(int menucolorspace)
	{
		switch (menucolorspace)
		{
			case 0:
				return ColorspaceType.RGBColorspace;
			case 1:
				return ColorspaceType.XYZColorspace;
			case 2:
				return ColorspaceType.LABColorspace;
			case 3:
				return ColorspaceType.YUVColorspace;
			case 4:
				return ColorspaceType.YCbCrColorspace;
			case 5:
				return ColorspaceType.YIQColorspace;
			case 6:
				return ColorspaceType.HSLColorspace;
			case 7:
				return ColorspaceType.HSBColorspace;
			default:
				return ColorspaceType.RGBColorspace;
		}
	}
	
	// called by driver, passes log field to print output and uses driver's selected
	// color space
	public static void calculateAverageColors(JTextArea log, int colorSpace) throws MagickException
	{
		log.append("Average colors, total [" + cellgrid.length + "] cells\n");
		log.append("Applying color space " + getMagickColorSpace(colorSpace) + "\n");
		
		for(int i = 0; i < cellgrid.length; i++)
		{
			double[] result = calculateAverageColors(getMagickColorSpace(colorSpace), cellgrid[i]);
			log.append(i + " " + result[0] + "," + result[1] + "," + result[2] + "\n");
		}
	}
	
	public static double[][] processAverageColorsArray(int colorspace)
	{
		double[][] output = new double[cellgrid.length][4];
		try
		{
			for(int i = 0; i < output.length; i++)
			{
				output[i] = calculateAverageColors(colorspace, cellgrid[i]);
			}
		}
		catch(Exception e)
		{
			
		}
		return output;
	}
	
	public static void toptwenty(int selectedCell) throws MagickException
	{
		MagickImage pullout = cellgrid[selectedCell].cloneImage(cellgrid[selectedCell].getDimension().width, cellgrid[selectedCell].getDimension().height, true);
		int width = pullout.getDimension().width;
		int height = pullout.getDimension().height;
		int totalpixels = height * width;
		double[] rgbpixels = new double[4 * width * height];
		double[] labpixels = new double[4 * width * height];
		double[] xyzpixels = new double[4 * width * height];
		double[] yuvpixels = new double[4 * width * height];
		double[] ycbcrpixels = new double[4 * width * height];
		double[] yiqpixels = new double[4 * width * height];
		double[] hslpixels = new double[4 * width * height];
		double[] hsvpixels = new double[4 * width * height];
		
		double[] rgbblues = new double[totalpixels];
		double[] labblues = new double[totalpixels];
		double[] xyzblues = new double[totalpixels];
		double[] yuvblues = new double[totalpixels];
		double[] ycbcrblues = new double[totalpixels];
		double[] yiqblues = new double[totalpixels];
		double[] hslblues = new double[totalpixels];
		double[] hsvblues = new double[totalpixels];
		
		int[] rgbpos = new int[totalpixels];
		int[] labpos = new int[totalpixels];
		int[] xyzpos = new int[totalpixels];
		int[] yuvpos = new int[totalpixels];
		int[] ycbcrpos = new int[totalpixels];
		int[] yiqpos = new int[totalpixels];
		int[] hslpos = new int[totalpixels];
		
		int twentypercent = totalpixels * 2 / 10;
		int[] top20rgb = new int[twentypercent];
		int[] top20lab = new int[twentypercent];
		int[] top20xyz = new int[twentypercent];
		int[] top20yuv = new int[twentypercent];
		int[] top20ycbcr = new int[twentypercent];
		int[] top20yiq = new int[twentypercent];
		int[] top20hsl = new int[twentypercent];
		
		for(int h = 0; h < totalpixels; h++)
		{
			rgbpos[h] = h;
			labpos[h] = h;
			xyzpos[h] = h;
			yuvpos[h] = h;
			ycbcrpos[h] = h;
			yiqpos[h] = h;
			hslpos[h] = h;
		}

		int n = 0;
		MagickImage[] sevenImages = new MagickImage[7];
		byte[] pixels = new byte[width * height * 4];
		double[] outconvert;
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				PixelPacket current = pullout.getOnePixel(j, i);
				rgbpixels[n] = current.getRed() / 256;
				rgbpixels[n+1] = current.getGreen() / 256;
				rgbpixels[n+2] = current.getBlue() / 256;
				rgbpixels[n+3] = current.getOpacity() / 256;
				
				outconvert = rgbToLAB(rgbpixels[n], rgbpixels[n+1], rgbpixels[n+2]);
				labpixels[n] = outconvert[0];
				labpixels[n+1] = outconvert[1];
				labpixels[n+2] = outconvert[2];
				labpixels[n+3] = rgbpixels[n+3];
				
				outconvert = rgbToXYZ(rgbpixels[n], rgbpixels[n+1], rgbpixels[n+2]);
				xyzpixels[n] = outconvert[0];
				xyzpixels[n+1] = outconvert[1];
				xyzpixels[n+2] = outconvert[2];
				xyzpixels[n+3] = rgbpixels[n+3];
				
				outconvert = rgbToYUV(rgbpixels[n], rgbpixels[n+1], rgbpixels[n+2]);
				yuvpixels[n] = outconvert[0];
				yuvpixels[n+1] = outconvert[1];
				yuvpixels[n+2] = outconvert[2];
				yuvpixels[n+3] = rgbpixels[n+3];
				
				outconvert = rgbToYCbCr(rgbpixels[n], rgbpixels[n+1], rgbpixels[n+2]);
				ycbcrpixels[n] = outconvert[0];
				ycbcrpixels[n+1] = outconvert[1];
				ycbcrpixels[n+2] = outconvert[2];
				ycbcrpixels[n+3] = rgbpixels[n+3];
				
				outconvert = rgbToYIQ(rgbpixels[n], rgbpixels[n+1], rgbpixels[n+2]);
				yiqpixels[n] = outconvert[0];
				yiqpixels[n+1] = outconvert[1];
				yiqpixels[n+2] = outconvert[2];
				yiqpixels[n+3] = rgbpixels[n+3];
				
				outconvert = rgbToHSL(rgbpixels[n], rgbpixels[n+1], rgbpixels[n+2]);
				hslpixels[n] = outconvert[0];
				hslpixels[n+1] = outconvert[1];
				hslpixels[n+2] = outconvert[2];
				hslpixels[n+3] = rgbpixels[n+3];
				n += 4;
			}
		}
		
		for(int o = 0; o < totalpixels; o++)
		{
			rgbblues[o] = rgbpixels[4*o + 2];
			labblues[o] = labpixels[4*o + 2];
			xyzblues[o] = xyzpixels[4*o + 2];
			yuvblues[o] = yuvpixels[4*o + 2];
			ycbcrblues[o] = ycbcrpixels[4*o + 2];
			yiqblues[o] = yiqpixels[4*o + 2];
			hslblues[o] = hslpixels[4*o + 2];
		}
		
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = rgbblues[i];
			int RGBXY = rgbpos[i];
			while((j > 0) && (rgbblues[j-1] < RGBB)) {
				rgbblues[j] = rgbblues[j-1];
				rgbpos[j] = rgbpos[j-1];
				j--;
			}
			rgbblues[j] = RGBB;
			rgbpos[j] = RGBXY;
		}
		
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = labblues[i];
			int RGBXY = labpos[i];
			while((j > 0) && (labblues[j-1] < RGBB)) {
				labblues[j] = labblues[j-1];
				labpos[j] = labpos[j-1];
				j--;
			}
			labblues[j] = RGBB;
			labpos[j] = RGBXY;
		}
		
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = xyzblues[i];
			int RGBXY = xyzpos[i];
			while((j > 0) && (xyzblues[j-1] < RGBB)) {
				xyzblues[j] = xyzblues[j-1];
				xyzpos[j] = xyzpos[j-1];
				j--;
			}
			xyzblues[j] = RGBB;
			xyzpos[j] = RGBXY;
		}
		
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = yuvblues[i];
			int RGBXY = yuvpos[i];
			while((j > 0) && (yuvblues[j-1] < RGBB)) {
				yuvblues[j] = yuvblues[j-1];
				yuvpos[j] = yuvpos[j-1];
				j--;
			}
			yuvblues[j] = RGBB;
			yuvpos[j] = RGBXY;
		}
		
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = ycbcrblues[i];
			int RGBXY = ycbcrpos[i];
			while((j > 0) && (ycbcrblues[j-1] < RGBB)) {
				ycbcrblues[j] = ycbcrblues[j-1];
				ycbcrpos[j] = ycbcrpos[j-1];
				j--;
			}
			ycbcrblues[j] = RGBB;
			ycbcrpos[j] = RGBXY;
		}
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = yiqblues[i];
			int RGBXY = yiqpos[i];
			while((j > 0) && (yiqblues[j-1] < RGBB)) {
				yiqblues[j] = yiqblues[j-1];
				yiqpos[j] = yiqpos[j-1];
				j--;
			}
			yiqblues[j] = RGBB;
			yiqpos[j] = RGBXY;
		}
		for(int i = 1; i < totalpixels; i++)
		{
			int j = i;
			double RGBB = hslblues[i];
			int RGBXY = hslpos[i];
			while((j > 0) && (hslblues[j-1] < RGBB)) {
				hslblues[j] = hslblues[j-1];
				hslpos[j] = hslpos[j-1];
				j--;
			}
			hslblues[j] = RGBB;
			hslpos[j] = RGBXY;
		}
		
		for(int z = 0; z < twentypercent; z++)
		{
			top20rgb[z] = rgbpos[z];
			top20lab[z] = labpos[z];
			top20xyz[z] = xyzpos[z];
			top20yuv[z] = yuvpos[z];
			top20ycbcr[z] = ycbcrpos[z];
			top20yiq[z] = yiqpos[z];
			top20hsl[z] = hslpos[z];
		}
		
		int FP = -1;
		
		for(int i = 0; i < 7; i++)
		{
			sevenImages[i] = cellgrid[selectedCell].cloneImage(width, height, true);
			sevenImages[i].dispatchImage(0, 0, width, height, "RGBA", pixels);
			for(int j = 0; j < twentypercent; j++)
			{
				if(i==0)
				{
					FP = rgbpos[j];
				}
				else if(i == 1)
				{
					FP = labpos[j];
				}
				else if(i == 2)
				{
					FP = xyzpos[j];
				}
				else if(i == 3)
				{
					FP = yuvpos[j];
				}
				else if(i == 4)
				{
					FP = ycbcrpos[j];
				}
				else if(i == 5)
				{
					FP = yiqpos[j];
				}
				else if(i == 6)
				{
					FP = hslpos[j];
				}
				FP = 4 * FP;
				pixels[FP] = 0;
				pixels[FP + 1] = 0;
				pixels[FP + 2] = 0;
			}
			sevenImages[i].constituteImage(width, height, "RGBA", pixels);
		}
		MagickImage seqImage = new MagickImage(sevenImages);
		MontageInfo montageInfo = new MontageInfo(new ImageInfo());
		MagickImage montage = seqImage.montageImages(montageInfo);
		MagickWindow window = new MagickWindow(montage);
		window.setTitle("");
		window.setVisible(true);
		
	}
		
	//  calculate a single cell/image average color
	// and return its corresponding color space values
	public static double[] calculateAverageColors(int colorSpace, MagickImage image) throws MagickException
	{
		int r, g, b, o;
		r = g = b = o = 0;

		int sampleSize = 0;

		// obtain the average colors from 16bit 8bit rgb
		for (int i = 0; i < image.getDimension().height; i++)
		{
			for (int j = 0; j < image.getDimension().width; j++)
			{
				r += image.getOnePixel(i, j).getRed() / 256;
				g += image.getOnePixel(i, j).getGreen() / 256;
				b += image.getOnePixel(i, j).getBlue() / 256;
				o += image.getOnePixel(i, j).getOpacity() / 256;

				sampleSize++;
			}
		}

		int avgr = r / sampleSize;
		int avgg = g / sampleSize;
		int avgb = b / sampleSize;
	//	int avgo = o / sampleSize;

		/*
		 * RGB = 1 GREY = 2 HSL = 15 LAB = 5 XYZ = 6 YCbCr = 7 YIQ = 9 YUV = 11, HSV=HSB
		 */
		double[] average = new double[3];

		switch (colorSpace)
		{
			// RGB
			case (ColorspaceType.RGBColorspace):
				average[0] = avgr;
				average[1] = avgg;
				average[2] = avgb;
				break;
			case (ColorspaceType.LABColorspace):
				average = rgbToLAB(avgr, avgg, avgb);
				break;
			case (ColorspaceType.XYZColorspace):
				average = rgbToXYZ(avgr, avgg, avgb);
				break;
			case (ColorspaceType.YUVColorspace):
				average = rgbToYUV(avgr, avgg, avgb);
				break;
			case (ColorspaceType.YCbCrColorspace):
				average = rgbToYCbCr(avgr, avgg, avgb);
				break;
			case (ColorspaceType.YIQColorspace):
				average = rgbToYIQ(avgr, avgg, avgb);
				break;
			case (ColorspaceType.HSLColorspace):
				average = rgbToHSL(avgr, avgg, avgb);
				break;
		}

		return average;
	}
	
	// added rgb to yiq
	public static double[] rgbToYIQ(double R, double G, double B)
	{
		double y = ((0.299f * R) + (0.587f * G) + (0.114f * B)) / 256;
		double i = ((0.596f * R) - (0.274f * G) - (0.322f * B)) / 256;
		double q = ((0.212f * R) - (0.523f * G) + (0.311f * B)) / 256;
		
		double[] r = {y, i, q};
		
		return r;
	}
	
	// added rgb to yuv
	public static double[] rgbToYUV(double R, double G, double B)
	{
		double y = R *  .299000 + G *  .587000 + B *  .114000;
		double u = R * -.168736 + G * -.331264 + B *  .500000 + 128;
		double v = R *  .500000 + G * -.418688 + B * -.081312 + 128;
		
		double[] r = {y, u, v};
		
		return r;
	}
	
	// added rgb to YCbCR
	public static double[] rgbToYCbCr(double R, double G, double B)
	{
		int y  = (int)( 0.299   * R + 0.587   * G + 0.114   * B);
		int cb = (int)(-0.16874 * R - 0.33126 * G + 0.50000 * B);
		int cr = (int)( 0.50000 * R - 0.41869 * G - 0.08131 * B);
		
		double[] retval = {y, cb, cr};
		
		return retval;
	}
	
	public static double[] rgbToXYZ(double R, double G, double B)
	{
		double var_R = (R / 255.0); // R from 0 to 255
		double var_G = (G / 255.0); // G from 0 to 255
		double var_B = (B / 255.0); // B from 0 to 255

		if (var_R > 0.04045)
			var_R = (Math.pow(((var_R + 0.055) / 1.055), 2.4));
		else
			var_R = var_R / 12.92;
		if (var_G > 0.04045)
			var_G = (Math.pow(((var_G + 0.055) / 1.055), 2.4));
		else
			var_G = var_G / 12.92;
		if (var_B > 0.04045)
			var_B = (Math.pow(((var_B + 0.055) / 1.055), 2.4));
		else
			var_B = var_B / 12.92;

		var_R = var_R * 100.0;
		var_G = var_G * 100.0;
		var_B = var_B * 100.0;

		double xyz[] = new double[3];
		xyz[0] = (var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805);
		xyz[1] = (var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722);
		xyz[2] = (var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505);

		return xyz;
	}

	public static double[] xyzToLAB(double X, double Y, double Z)
	{
		double ref_X = 95.047;
		double var_X = (double) (X / ref_X); // ref_X = 95.047 Observer= 2°,
												// Illuminant= D65
		double var_Y = (double) Y / 100.000; // ref_Y = 100.000
		double var_Z = (double) Z / 108.883; // ref_Z = 108.883

		if (var_X > 0.008856)
			var_X = Math.pow(var_X, (1.0 / 3.0));
		else
			var_X = (7.787 * var_X) + (16.0 / 116.0);
		if (var_Y > 0.008856)
			var_Y = Math.pow(var_Y, (1.0 / 3.0));
		else
			var_Y = (7.787 * var_Y) + (16.0 / 116.0);
		if (var_Z > 0.008856)
			var_Z = Math.pow(var_Z, (1.0 / 3.0));
		else
			var_Z = (7.787 * var_Z) + (16.0 / 116.0);

		double lab[] = new double[3];
		lab[0] = (116 * var_Y) - 16;
		lab[1] = 500 * (var_X - var_Y);
		lab[2] = 200 * (var_Y - var_Z);

		return lab;
	}

	public static double[] labToXYZ(double L, double A, double B)
	{
		double var_Y = (L + 16) / 116;
		double var_X = A / 500 + var_Y;
		double var_Z = var_Y - B / 200;

		if (Math.pow(var_Y, 3) > 0.008856)
			var_Y = Math.pow(var_Y, 3);
		else
			var_Y = (var_Y - 16 / 116) / 7.787;
		if (Math.pow(var_X, 3) > 0.008856)
			var_X = Math.pow(var_X, 3);
		else
			var_X = (var_X - 16 / 116) / 7.787;
		if (Math.pow(var_Z, 3) > 0.008856)
			var_Z = Math.pow(var_Z, 3);
		else
			var_Z = (var_Z - 16 / 116) / 7.787;

		double xyz[] = new double[3];
		xyz[0] = 95.047 * var_X; // ref_X = 95.047 Observer= 2°, Illuminant= D65
		xyz[1] = 100.000 * var_Y; // ref_Y =
		xyz[2] = 108.883 * var_Z; // ref_Z =

		return xyz;
	}

	public static double[] xyzToRGB(double X, double Y, double Z)
	{
		double var_X = X / 100; // X from 0 to 95.047 (Observer = 2°, Illuminant
								// = D65)
		double var_Y = Y / 100; // Y from 0 to 100.000
		double var_Z = Z / 100; // Z from 0 to 108.883

		double var_R = var_X * 3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
		double var_G = var_X * -0.9689 + var_Y * 1.8758 + var_Z * 0.0415;
		double var_B = var_X * 0.0557 + var_Y * -0.2040 + var_Z * 1.0570;

		if (var_R > 0.0031308)
			var_R = 1.055 * (Math.pow(var_R, (1 / 2.4))) - 0.055;
		else
			var_R = 12.92 * var_R;
		if (var_G > 0.0031308)
			var_G = 1.055 * (Math.pow(var_G, (1 / 2.4))) - 0.055;
		else
			var_G = 12.92 * var_G;
		if (var_B > 0.0031308)
			var_B = 1.055 * (Math.pow(var_B, (1 / 2.4))) - 0.055;
		else
			var_B = 12.92 * var_B;

		double rgb[] = new double[3];

		rgb[0] = var_R * 255;
		rgb[1] = var_G * 255;
		rgb[2] = var_B * 255;

		return rgb;
	}

	public static double[] rgbToLAB(double R, double G, double B)
	{
		double xyz[] = new double[3];
		double lab[] = new double[3];

		xyz = rgbToXYZ(R, G, B);
		lab = xyzToLAB(xyz[0], xyz[1], xyz[2]);

		return lab;
	}

	public static double[] labToRGB(double L, double A, double B)
	{
		double xyz[] = new double[3];
		double rgb[] = new double[3];

		xyz = labToXYZ(L, A, B);
		rgb = xyzToRGB(xyz[0], xyz[1], xyz[2]);

		return rgb;
	}

	public static double min(double a, double b, double c)
	{
		return ((a < b ? a : b) < c ? (a < b ? a : b) : c);
	}

	public static double max(double a, double b, double c)
	{
		return ((a > b ? a : b) > c ? (a > b ? a : b) : c);
	}

	public static double[] rgbToHSL(double R, double G, double B)
	{
		double var_R = (R / 255.0); // RGB from 0 to 255
		double var_G = (G / 255.0);
		double var_B = (B / 255.0);

		double var_Min = min(var_R, var_G, var_B); // Min. value of RGB
		double var_Max = max(var_R, var_G, var_B); // Max. value of RGB
		double del_Max = var_Max - var_Min; // Delta RGB value

		double L = (var_Max + var_Min) / 2.0;
		double H = 0.0, S = 0.0;

		if (del_Max == 0.0) // This is a gray, no chroma...
		{
			H = 0.0; // HSL results from 0 to 1
			S = 0.0;
		}
		else
		// Chromatic data...
		{
			if (L < 0.5)
				S = del_Max / (var_Max + var_Min);
			else
				S = del_Max / (2.0 - var_Max - var_Min);

			double del_R = (((var_Max - var_R) / 6.0) + (del_Max / 2.0))
					/ del_Max;
			double del_G = (((var_Max - var_G) / 6.0) + (del_Max / 2.0))
					/ del_Max;
			double del_B = (((var_Max - var_B) / 6.0) + (del_Max / 2.0))
					/ del_Max;

			if (var_R == var_Max)
				H = del_B - del_G;
			else if (var_G == var_Max)
				H = (1.0 / 3.0) + del_R - del_B;
			else if (var_B == var_Max)
				H = (2.0 / 3.0) + del_G - del_R;

			if (H < 0.0)
				H += 1.0;
			if (H > 1.0)
				H -= 1.0;
		}

		double HSL[] = { H, S, L };
		return HSL;

	}

	public static double Hue_2_RGB(double v1, double v2, double vH)
	{
		if (vH < 0.0)
			vH += 1.0;
		if (vH > 1.0)
			vH -= 1.0;
		if ((6.0 * vH) < 1.0)
			return (v1 + (v2 - v1) * 6.0 * vH);
		if ((2.0 * vH) < 1.0)
			return (v2);
		if ((3.0 * vH) < 2.0)
			return (v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0);
		return (v1);
	}

	public static double[] hslToRGB(double H, double S, double L)
	{
		double R, G, B, var_1, var_2;
		if (S == 0.0) // HSL from 0 to 1
		{
			R = L * 255.0; // RGB results from 0 to 255
			G = L * 255.0;
			B = L * 255.0;
		}
		else
		{
			if (L < 0.5)
				var_2 = L * (1.0 + S);
			else
				var_2 = (L + S) - (S * L);

			var_1 = 2.0 * L - var_2;

			R = 255.0 * Hue_2_RGB(var_1, var_2, H + (1.0 / 3.0));
			G = 255.0 * Hue_2_RGB(var_1, var_2, H);
			B = 255.0 * Hue_2_RGB(var_1, var_2, H - (1.0 / 3.0));
		}

		double RGB[] = { R, G, B };
		return RGB;
	}
	
	public static MagickImage decreaseSaturationWithHSL(MagickImage newImage) throws MagickException
	{
		
		//Create a MagickImage object to read the image pixels
		//MagickImage newImage = new MagickImage(info);

		//Define the pixel array to retrieve pixels from original image
		byte[] pixels = new byte[newImage.getDimension().width * newImage.getDimension().height * 4];
		
		//Define pixel to perform operations on each of the retrieved pixels
		double[] onePixel = new double[3];
		
		//Get the pixels into the array
		newImage.dispatchImage(0, 0, newImage.getDimension().width, newImage.getDimension().height, "RGBA", pixels);

		//Modify the pixel array to new values of decreased saturation
		for(int i = 0 ; i < (newImage.getDimension().width * newImage.getDimension().height) ; i++)
		{

			//Correct pixel value
			CPixelInfo pix = new CPixelInfo(0,0,pixels[4 * i],pixels[4 * i + 1],pixels[4 * i + 2]); 

			//Convert i-th pixel to HSL Colorspace
			onePixel = rgbToHSL(pix.getRed(), pix.getGreen(), pix.getBlue());
			
			//Change the HSL values to decreased saturation, maintaining the pixel energy
			onePixel[1] = 0.9 * onePixel[1];
			onePixel[2] = Math.sqrt(0.21 * Math.pow(onePixel[1],2) + Math.pow(onePixel[2],2));
			

			if(onePixel[2] < -1) onePixel[2] = -1.0;
			else if(onePixel[2] > 1) onePixel[2] = 1.0;
			
			//Convert i-th pixel back to RGB Colorspace
			onePixel = hslToRGB(onePixel[0], onePixel[1], onePixel[2]);

			//Modify the pixel array with new i-th pixel values
			pixels[4 * i] = (byte) onePixel[0];
			pixels[4 * i + 1] = (byte) onePixel[1];
			pixels[4 * i + 2] = (byte) onePixel[2];
			
		}
		
		//Constitute new image with new pixel values
		newImage.constituteImage(newImage.getDimension().width, newImage.getDimension().height, "RGBA", pixels);

		return newImage;
		
	}
	
	public static MagickImage increaseSaturationWithHSL(MagickImage newImage, int q) throws MagickException
	{
		
		
		//Create a MagickImage object to read the image pixels
		//MagickImage newImage = img.cloneImage(img.getDimension().width, img.getDimension().height, true);

		//Define the pixel array to retrieve pixels from original image
		int[] pixels = new int[newImage.getDimension().width * newImage.getDimension().height * 4];
		
		//Define pixel to perform operations on each of the retrieved pixels
		double[] onePixel = new double[3];
		
		//Get the pixels into the array
		newImage.dispatchImage(0, 0, newImage.getDimension().width, newImage.getDimension().height, "RGBA", pixels);

		//Modify the pixel array to new values of decreased saturation
		for(int i = 0 ; i < (newImage.getDimension().width * newImage.getDimension().height) ; i++)
		{

			//Correct pixel value
			CPixelInfo pix = new CPixelInfo(0,0,pixels[4 * i],pixels[4 * i + 1],pixels[4 * i + 2]); 
			onePixel = rgbToHSL(pix.getRed(), pix.getGreen(), pix.getBlue());

			//Change the HSL values to decreased saturation, maintaining the pixel energy
			onePixel[1] = 1.1 * onePixel[1];
			onePixel[2] = Math.sqrt(-0.19 * Math.pow(onePixel[1],2) + Math.pow(onePixel[2],2));

			//Correct the Saturaion values
			if(onePixel[1] < -1) onePixel[1] = -1.0;
			else if(onePixel[1] > 1) onePixel[1] = 1.0;

			//Convert i-th pixel back to RGB Colorspace
			onePixel = hslToRGB(onePixel[0], onePixel[1], onePixel[2]);

			//Modify the pixel array with new i-th pixel values
			pixels[4 * i] = (int) onePixel[0];
			pixels[4 * i + 1] = (int) onePixel[1];
			pixels[4 * i + 2] = (int) onePixel[2];
			
		}
		
		//Constitute new image with new pixel values
		newImage.constituteImage(newImage.getDimension().width, newImage.getDimension().height, "RGBA", pixels);
		newImage.setFileName(".//Output/outputsatincrease" + q + ".jpg");
		newImage.writeImage(new ImageInfo());

		return newImage;
		
	}
	
	
	public static MagickImage decreaseSaturation(MagickImage m) throws MagickException{

		//Create a MagickImage object to read the image pixels
		//MagickImage m = new MagickImage(info);
		
		//Define the pixel array to retrieve pixels from original image
		byte [] pixels = new byte[m.getDimension().width * m.getDimension().height * 4];
		
		//Get the pixels into the array
		m.dispatchImage(0, 0, m.getDimension().width, m.getDimension().height, "RGBA", pixels);

		//Define pixel to perform operations on each of the retrieved pixels
		double[] singlePixel = new double[3];
		
		//Modify the pixel array to new values of decreased saturation
		for(int i = 0 ; i < (m.getDimension().width * m.getDimension().height) ; i++){
			//Convert i-th pixel to LAB Colorspace
			singlePixel = rgbToLAB( (double) pixels[4 * i] , (double) pixels[4 * i + 1] , (double) pixels[4 * i + 2] );
			
			//Change the LAB values to decreased saturation, maintaining the pixel energy
			singlePixel = decreaseSaturationInLAB(singlePixel[0] , singlePixel[1]  ,singlePixel[2]);

			//Convert i-th pixel back to RGB Colorspace
			singlePixel = labToRGB(singlePixel[0] , singlePixel[1]  ,singlePixel[2]);
			
			//Modify the pixel array with new i-th pixel values
			pixels[4 * i] = (byte) singlePixel[0];
			pixels[4 * i + 1] = (byte) singlePixel[1];
			pixels[4 * i + 2] = (byte) singlePixel[2];

		}

		//Create new MagickImage object to constitute new image
		MagickImage newImage = new MagickImage();
		
		//Constitute new image with new pixel values
		newImage.constituteImage(m.getDimension().width, m.getDimension().height, "RGBA", pixels);
		
		return newImage;
		
	}	
	
	public static MagickImage increaseSaturation(MagickImage m) throws MagickException
	{

		// Create a MagickImage object to read the image pixels
		//MagickImage m = new MagickImage(info);
		
		// Define the pixel array to retrieve pixels from original image
		byte[] pixels = new byte[m.getDimension().width
				* m.getDimension().height * 4];

		// Get the pixels into the array
		m.dispatchImage(0, 0, m.getDimension().width, m.getDimension().height,
				"RGBA", pixels);

		// Define pixel to perform operations on each of the retrieved pixels
		double[] singlePixel = new double[3];

		// Modify the pixel array to new values of decreased saturation
		for (int i = 0; i < (m.getDimension().width * m.getDimension().height); i++)
		{
			// Convert i-th pixel to LAB Colorspace
			singlePixel = rgbToLAB((double) pixels[4 * i],
					(double) pixels[4 * i + 1], (double) pixels[4 * i + 2]);

			// Change the LAB values to increased saturation, maintaining the
			// pixel energy
			singlePixel = increaseSaturationInLAB(singlePixel[0],
					singlePixel[1], singlePixel[2]);

			// Convert i-th pixel back to RGB Colorspace
			singlePixel = labToRGB(singlePixel[0], singlePixel[1],
					singlePixel[2]);

			// Modify the pixel array with new i-th pixel values
			pixels[4 * i] = (byte) singlePixel[0];
			pixels[4 * i + 1] = (byte) singlePixel[1];
			pixels[4 * i + 2] = (byte) singlePixel[2];

		}

		// Create new MagickImage object to constitute new image
		MagickImage newImage = new MagickImage();

		// Constitute new image with new pixel values
		newImage.constituteImage(m.getDimension().width,
				m.getDimension().height, "RGBA", pixels);

		return newImage;

	}
	
	public static void firstRowSaturationIncrease()
	{
		try
		{
			for(int i = 0; i < instance[0].length; i++)
			{
				cellgrid[i] = increaseSaturationWithHSL(cellgrid[i], i);
				ImageInfo img = new ImageInfo(".//Output/outputsatincrease" + i + ".jpg");
				MagickImage mn = new MagickImage(img);
				cellgrid[i] = mn;
			}
			viewImage();
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static void firstRowSaturationDecrease()
	{
		try
		{
			for(int i = 0; i < instance[0].length; i++)
			{
				cellgrid[i] = decreaseSaturationWithHSL(cellgrid[i]);
			}
			viewImage();
		}
		catch(Exception e)
		{
			
		}
	}

	public static double[] increaseSaturationInLAB(double L, double a, double b)
	{

		double newL, newa, newb;

		newL = Math.sqrt(Math.pow(L, 2) - (0.21 * Math.pow(a, 2))
				- (0.21 * Math.pow(b, 2)));
		newa = 1.1 * a;
		newb = 1.1 * b;

		double newLab[] = { newL, newa, newb };

		return newLab;

	}

	public static double[] decreaseSaturationInLAB(double L, double a, double b)
	{

		double newL, newa, newb;

		newL = Math.sqrt(Math.pow(L, 2) + (0.19 * Math.pow(a, 2))
				+ (0.19 * Math.pow(b, 2)));
		newa = 0.9 * a;
		newb = 0.9 * b;

		double newLab[] = { newL, newa, newb };

		return newLab;

	}
	
	public static void reduceColor(JTextArea log, int selectedCell, int n, int colorspace)
	{
		MagickImage m = cellgrid[selectedCell];
		MagickImage output;
		//try {
			//if(colorspace != ColorspaceType.RGBColorspace)
			//{
				//m.rgbTransformImage(colorspace);
				//m.transformRgbImage(colorspace);
				//m.setFileName(".//Output/testing.jpg");
				//m.writeImage(new ImageInfo());
			//}
		//} catch (MagickException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		//}
		int width;
		int height;
		MedianCut mc = new MedianCut();
		MedianCutDouble md = new MedianCutDouble();

		//  this function directly takes the magick image and the N value, and the color space to transform to, it will automatically transform the input image into the specified color space(using our functions) and then perform median cut(using doubles) and the transform them back to RGB write them to another MagickImage and returns it.

		// after the above two calls, show newImage on display;

		//for RGB do not change how we are doing it right now; this MedianCutDouble.medianCut should only be called if you are running reduce color on a different color space other than RGB
		try {
			
			//m.setFileName(".//Output/gridcell" + selectedCell + ".jpg");
			//m.writeImage(new ImageInfo());
			//m[1].rgbTransformImage(ColorspaceType.XYZColorspace);
			//m[2].rgbTransformImage(ColorspaceType.HSLColorspace);
			//m[3].rgbTransformImage(ColorspaceType.YUVColorspace);
			//m[4].rgbTransformImage(ColorspaceType.YIQColorspace);
			//m[5].rgbTransformImage(ColorspaceType.LABColorspace);
			//m[6].rgbTransformImage(ColorspaceType.YCbCrColorspace);
			
			//System.out.println("Dimensions: " + m.getDimension().width + " x " + m.getDimension().height);
			log.append("Original color instances: " + mc.getColorInstances(m) + "\n");
			width = m.getDimension().width;
			height = m.getDimension().height;
			
			int[] pixels = new int[width * height * 4];
			m.dispatchImage(0, 0, width, height, "RGBA", pixels);
			int[] medianCut = mc.medianCut(m, n, pixels);
			//output = new MagickImage(new ImageInfo());
			output = mc.getImageFromArray(m, medianCut);//.cloneImage(width, height, true);
			MagickImage newImage = new MagickImage();
			//if(colorspace != ColorspaceType.RGBColorspace)
			//	newImage = md.medianCut(m, mc.getColorInstances(m), colorspace);
			log.append("Reduced color instances: " + mc.getColorInstances(output));
			//System.out.println("Finished an image with index " + 0);
			//if(colorspace!= ColorspaceType.RGBColorspace)
			//	output.transformRgbImage(colorspace);
			ImageInfo inf = new ImageInfo(".//Output/result" + 0 + ".jpg");
			//if(colorspace == ColorspaceType.RGBColorspace)
			//{
				output.setFileName(".//Output/result" + 0 + ".jpg");
				output.writeImage(inf);
			//}
			//else
			//{
			//	newImage.setFileName(".//Output/result" + 0 + ".jpg");
			//	newImage.writeImage(inf);
			//}
			
			
			
			
		/*	for(int j = 0; j < 1; j++)
			{
				System.out.println("Dimensions: " + m[j].getDimension().width + " x " + m[j].getDimension().height);
				System.out.println("Original color instances: " + mc.getColorInstances(m[j]));
				width = m[j].getDimension().width;
				height = m[j].getDimension().height;
				int[] pixels = new int[width * height * 4];
				m[j].dispatchImage(0, 0, width, height, "RGBA", pixels);
				int[] medianCut = mc.medianCut(m[j], n, pixels);
				output[j] = mc.getImageFromArray(m[j], medianCut);
				System.out.println("Reduced color instances: " + mc.getColorInstances(output[j]));
				System.out.println("Finished an image with index " + j);
				ImageInfo inf = new ImageInfo(".//Output/result" + j + ".jpg");
				output[j].setFileName(".//Output/result" + j + ".jpg");
				output[j].writeImage(inf);
			} */
		
			
			
			//MontageInfo montageInfo = new MontageInfo(new ImageInfo());

			// montageInfo.setTitle("Melbourne");
			// montageInfo.setBorderWidth(0);

			//MagickImage montage = output.montageImages(montageInfo);

			// writes the image to default directory
			// montageInfo.setFileName("montage.jpg");
			// montage.writeImage(new ImageInfo());

			// ////////////////////
			// String f = instance.getFileName();
			ImageInfo x = new ImageInfo(".//Output/result" + 0 + ".jpg");
			MagickImage outimage = new MagickImage(x);
			MagickWindow window = new MagickWindow(outimage);
			window.setSize(800, 400);
			window.setTitle("");
			window.setVisible(true);
			
		} catch (MagickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	
}