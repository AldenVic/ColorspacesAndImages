import magick.*;
import magick.util.MagickWindow;

import java.io.FileWriter;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

public class DWTUtils {
	
	//writes out the DWT feature entries to a CSV
	public static void writeDWTtoCSV(ArrayList<DWTFeature> features, String fn)
	{
		try
		{
			CSVWriter writer = new CSVWriter(new FileWriter(fn));
			String[] rowofentries = new String[5];
			for(int i = 0; i < features.size(); i++)
			{
				//write one row.
				rowofentries[0] = features.get(i).getImageId();
				rowofentries[1] = features.get(i).getCellId() + "";
				rowofentries[2] = features.get(i).getChannelId() + "";
				rowofentries[3] = features.get(i).getBinId() + "";
				rowofentries[4] = features.get(i).getValue() + "";
				writer.writeNext(rowofentries);
			}

			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	//performs DWT on all the 8x8 cells in a series of images, and writes out the 16 significant values for each channel for each cell.
	public static ArrayList<DWTFeature> task6(MagickImage[] images)
	{
		ArrayList<DWTFeature> featureslist = new ArrayList<DWTFeature>();
		String currentimageID = "";
		int currentcellID;
		int currentchannelID; //0 for R, 1 for G, 2 for B.
		int currentbinID;
		double currentvalue;
		byte[] data, channeldata;
		int w, h;
		int[] celldimensions; //this image is AxB "cells". each cell is 8x8 pixels. the remaining portion on the right and bottom are cropped.
		MagickImage current;

		int[][] currentcell = new int[8][8];
		int[][] dwtTransformedCurrentCell = new int[8][8];
		int[][] dwtFeaturesCurrentCell = new int[16][2];
		for(int c = 0; c < images.length; c++) //c is the image_id
		{
			currentimageID = "image" + c;
			currentcellID = 0;
			try
			{
				current = images[c];
				w = current.getDimension().width;
				h = current.getDimension().height;
				data = new byte[w * h * 3];
				current.dispatchImage(0, 0, w, h, "RGB", data);
				celldimensions = divisions(w, h);
				for(int y = 0; y < celldimensions[1]; y++) //the Y and X for the cellid. cellcounter is the cell id.
				{
					for(int x = 0; x < celldimensions[0]; x++)
					{
						for(currentchannelID = 0; currentchannelID < 3; currentchannelID++)
						{
							channeldata = getsinglechannel(data, currentchannelID);
							currentcell = channeltocell(channeldata, x, y, w, 8, 8);
							dwtTransformedCurrentCell = dwt(currentcell, 1);
							dwtFeaturesCurrentCell = extractSignificantDWTFeatures(dwtTransformedCurrentCell);
							for(int z = 0; z < dwtFeaturesCurrentCell.length; z++)
							{
								currentvalue = dwtFeaturesCurrentCell[z][0];
								currentbinID = dwtFeaturesCurrentCell[z][1];
								//output one entry
								featureslist.add(new DWTFeature(currentimageID, currentcellID, currentchannelID, currentbinID, (double)currentvalue));
							}
						}
						currentcellID++; //next cell id is 1 more then this one.
					}
				} //end cell iterations of image
			}
			catch(Exception e)
			{
				System.out.println("Error.");
			}
		} //end iteration over all images

		return featureslist;
	}

	//takes a dwt transform and extracts 16 significant values, along with their bins 0-16, and returns them as a 16x2 array.
	//the values chosen are the 16 pixels of the LL band after DWT is performed. they are traversed as a 4x4 square, left-to-right, top-to-bottom.
	public static int[][] extractSignificantDWTFeatures(int[][] dwtTransform)
	{
		int[][] output = new int[16][2];
		int counter = 0;
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
			{
				output[counter][0] = dwtTransform[j][i];
				output[counter][1] = counter;
				counter++;
			}
		return output;
	}
	
	//extracts one channel from a signal. assumes the signal is in three-component (for example, "RGB") format.
	public static byte[] getsinglechannel(byte[] signal, int channel)
	{
		byte[] output = new byte[signal.length / 3];
		for(int i = 0; i < output.length; i++)
		{
			output[i] = signal[3*i + channel];
		}
		return output;
	}

	//determines the number of 8x8 cells possible given the image width and height
	public static int[] divisions(int w, int h)
	{
		int wdivisions = w / 8;
		int hdivisions = h / 8;
		int[] output = new int[2];
		output[0] = wdivisions;
		output[1] = hdivisions;
		return output;
	}
	
	//converts a piece of the dispatched byte channel (single color channel) into a 2 dimensional nxn cell
	public static int[][] channeltocell(byte[] channel, int cellx, int celly, int w, int cellw, int cellh)
	{
		int[][] signal = new int[cellw][cellh];
		
		for(int i = 0; i < cellw; i++)
			for(int j = 0; j < cellh; j++)
			{
				signal[i][j] = (int)(((short)channel[(cellw * cellx + w * celly) + i + w * j]) & 0xff);
			}
		return signal;
	}
	
	//runs 2D-DWT on a cell, with X iterations. returns the result
	public static int[][] dwt(int[][] cell, int iterations)
	{
		int[][] output = cell;
		//NOTE: Uses Antonini 9/7 bi-orthogonal wavelet filter. coefficients below:
		double[] h0 = {0.038, -0.024, -0.111, 0.377, 0.853, 0.377, -0.111, -0.024, 0.038};
		double[] _h0 = {-0.065, -0.041, 0.418, 0.788, 0.418, -0.041, -0.065};
		int h0pivot = 4;
		int _h0pivot = 3;
		double[] h1 = geth1(_h0, _h0pivot);
		int h1pivot = h0pivot;
		
		for(int i = 0; i < iterations; i++)
		{
			output = convolverows(output, h0, h0pivot, h1, h1pivot);
			output = convolvecolumns(output, h0, h0pivot, h1, h1pivot);
		}
		
		return output;
	}
	
	//convolution in 1 dimension using a filter
	public static double[] convolve(int[] signal, double[] filter, int pivot)
	{
		int filtern;
		double[] output = new double[signal.length];
		for(int n = 0; n < output.length; n++)
		{
			for(int j = 0; j < signal.length; j++)
			{
				filtern = pivot + n - j;
				if(filtern >= filter.length || filtern < 0)
					output[n] += 0;
				else
					output[n] += signal[j] * filter[filtern];		
			}
		}
		return output;
	}
	
	//takes two data components (the convolved outputs), rounds them out to integers, takes out odd number index values, and combines them.
	public static int[] roundFilterCombine(double[] piece1, double[] piece2)
	{
		int[] output = new int[piece1.length/2 + piece2.length / 2];
		int[] part1 = new int[piece1.length / 2];
		int[] part2 = new int[piece2.length / 2];
		
		for(int i = 0; i < part1.length; i++)
		{
			part1[i] = (int)piece1[2*i];
		}
		
		for(int i = 0; i < part2.length; i++)
		{
			part2[i] = (int)piece2[2*i];
		}
		
		for(int j = 0; j < output.length; j++)
		{
			if(j < part1.length)
				output[j] = part1[j];
			else
				output[j] = part2[j - part1.length];
		}
		
		return output;
	}
	
	//calculates h1 from ~h0 or ~h1 from h0 using the formula h1[n] = (-1)^n*~h0[1-n]
	public static double[] geth1(double[] hbar0, int pivot)
	{
		int multiplier;
		
		if(pivot % 2 == 0)
			multiplier = -1;
		else
			multiplier = 1;
		
		double[] output = new double[hbar0.length];
		
		for(int i = 0; i < output.length; i++)
		{
			output[i] = hbar0[hbar0.length - 1 - i] * multiplier;
			multiplier *= -1;
		}
		
		return output;
	}
	
	//extracts a column of data from an nxn cell
	public static int[] extractcolumn(int[][] cell, int index)
	{
		int[] output = new int[cell[0].length];
		for(int i = 0; i < output.length; i++)
		{
			output[i] = cell[index][i];
		}
		return output;
	}
	
	//extracts a row of data from an nxn cell
	public static int[] extractrow(int[][] cell, int index)
	{
		int[] output = new int[cell.length];
		for(int i = 0; i < output.length; i++)
		{
			output[i] = cell[i][index];
		}
		return output;
	}
	
	//convolves a cell by row using the given filters
	public static int[][] convolverows(int[][] cell, double[] h0, int h0pivot, double[] h1, int h1pivot)
	{
		int[][] output = new int[cell.length][cell[0].length];
		int[] rowdata;
		double[] outpiece1;
		double[] outpiece2;
		int[] outputrow;
		for(int i = 0; i < cell[0].length; i++)
		{
			rowdata = extractrow(cell, i);
			outpiece1 = convolve(rowdata, h0, h0pivot);
			outpiece2 = convolve(rowdata, h1, h1pivot);
			outputrow = roundFilterCombine(outpiece1, outpiece2);
			for(int j = 0; j < cell.length; j++)
			{
				output[j][i] = outputrow[j];
			}
		}
		return output;
	}
	
	//convolves a cell by column using the given filters
	public static int[][] convolvecolumns(int[][] cell, double[] h0, int h0pivot, double[] h1, int h1pivot)
	{
		int[][] output = new int[cell.length][cell[0].length];
		int[] columndata;
		double[] outpiece1;
		double[] outpiece2;
		int[] outputcolumn;
		for(int i = 0; i < cell.length; i++)
		{
			columndata = extractcolumn(cell, i);
			outpiece1 = convolve(columndata, h0, h0pivot);
			outpiece2 = convolve(columndata, h1, h1pivot);
			outputcolumn = roundFilterCombine(outpiece1, outpiece2);
			output[i] = outputcolumn;
		}
		return output;
	}

	
}
