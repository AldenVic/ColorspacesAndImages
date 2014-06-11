import magick.*;
import magick.util.MagickWindow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DCTUtils {
	
	//splits the feature array into chunks and writes out each chunk
	/*public static void splitandWriteDCTFeatures(ArrayList<DCTFeature> entries)
	{
		ArrayList<DCTFeature> partition;
		int counter;
		int offset;
		int partitionsize = 70000;
		int x = 1;
		for(int i = 0; i < entries.size(); i+= partitionsize)
		{
			partition = new ArrayList<DCTFeature>();
			offset = i;
			for(counter = i; (counter < (i+partitionsize)) && (counter < entries.size()); counter++)
			{
				partition.add(entries.get(counter));
			}
			writeDCTEntriestoDB(partition, offset);
		}
	}*/
	
	//writes a dct feature array into the spreadsheet
	/*public static void writeDCTEntriestoDB(ArrayList<DCTFeature> entries, int offset)
	{
		
		Row currentrow;
		Cell currentcell;
		DCTFeature currentfeature;
		int row;
		try
		{
		     String filename = "db_proj3Group7.xlsx";
		     String sheetname = "t3_chanIdFreqBin";
		     InputStream inp = new FileInputStream(filename);
		     XSSFWorkbook xwb = new XSSFWorkbook(inp);
		     SXSSFWorkbook wb = new SXSSFWorkbook(xwb);
		     Sheet sheet  = wb.getSheet(sheetname);
		     FileOutputStream fileOut = new FileOutputStream(filename);

		     for(int i = 0; (i < entries.size()); i++)
		     {
		    	 row = 1 + i + offset;
		    	 currentfeature = entries.get(i);
		    	 currentrow = sheet.getRow(row);
		    	 if(currentrow==null)
		    		 currentrow = sheet.createRow(row);
		    	 
		    	 //record the feature cell by cell.
		    	 System.out.println("Row " + row);
		    	 
		    	 //img id
		    	 currentcell = currentrow.getCell(1, Row.CREATE_NULL_AS_BLANK);
		    	 currentcell.setCellValue(currentfeature.getImageId());
		    	 
		    	 //cell id
		    	 currentcell = currentrow.getCell(2,  Row.CREATE_NULL_AS_BLANK);
		    	 currentcell.setCellValue(currentfeature.getCellId());
		    	 
		    	 //channel id
		    	 currentcell = currentrow.getCell(3,  Row.CREATE_NULL_AS_BLANK);
		    	 currentcell.setCellValue(currentfeature.getChannelId());
		    	 
		    	 //bin id
		    	 currentcell = currentrow.getCell(4,  Row.CREATE_NULL_AS_BLANK);
		    	 currentcell.setCellValue(currentfeature.getBinId());
		    	 
		    	 //value
		    	 currentcell = currentrow.getCell(5,  Row.CREATE_NULL_AS_BLANK);
		    	 currentcell.setCellValue(currentfeature.getValue());
		    	 
		    	
		     }    	 
		     wb.write(fileOut);
	    	 fileOut.flush();
	    	 fileOut.close();
		     
		     inp.close();

		}
		catch(Exception e)
		{	
			e.printStackTrace();
			
		}
	}*/
	
	//writes out the set of features into a CSV file.
	public static void writeDCTtoCSV(ArrayList<DCTFeature> features, String fn)
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
	
	
	//performs DCT on all the 8x8 cells in a series of images, and writes out the 16 significant values for each channel for each cell.
	public static ArrayList<DCTFeature> task3(MagickImage[] images)
	{
		ArrayList<DCTFeature> featureslist = new ArrayList<DCTFeature>();
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
		double[][] dctTransformedCurrentCell = new double[8][8];
		double[][] dctFeaturesCurrentCell = new double[16][2];
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
							dctTransformedCurrentCell = dct(currentcell);
							dctFeaturesCurrentCell = extractSignificantDCTFeatures(dctTransformedCurrentCell);
							for(int z = 0; z < dctFeaturesCurrentCell.length; z++)
							{
								currentvalue = dctFeaturesCurrentCell[z][0];
								currentbinID = (int)dctFeaturesCurrentCell[z][1];
								//output one entry
								featureslist.add(new DCTFeature(currentimageID, currentcellID, currentchannelID, currentbinID, currentvalue));
							}
						}
						currentcellID++; //next cell id is 1 more then this one.
					}
				} //end cell iterations of image
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} //end iteration over all images

		return featureslist;
	}

	//takes a dct transform and extracts 16 significant values, along with their bins 0-16, and returns them as a 16x2 array.
	//the values chosen are the low freq top left corner of the image, traversed in zig-zag fashion right, down-left, down, up-right.
	public static double[][] extractSignificantDCTFeatures(double[][] dctTransform)
	{
		double[][] output = new double[16][2];
		boolean diagflag = false, diagupdown = true;
		int x = 0, y = 0;
		for(int i = 0; i < 16; i++)
		{
			output[i][0] = dctTransform[x][y];
			output[i][1] = i;
			if(diagflag)
			{
				if(diagupdown)
				{
					y--; x++;
					if(y==0) diagflag = false;
				}
				else
				{
					x--; y++;
					if(x==0) diagflag = false;
				}		
			}
			else
			{
				if(diagupdown)
				{
					x++; diagupdown = false; diagflag = true;
				}
				else
				{
					y++; diagupdown = true; diagflag = true;
				}
			}
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

	//takes an 8x8 cell and outputs the (also 8x8) 2D-DCT transform (freq component)
	public static double[][] dct(int[][] signal)
	{
		double[][] output = new double[8][8];
		double cu;
		double cv;
		double summation;
		double pi = Math.PI;
		for(int u = 0; u < 8; u++) 
			for(int v = 0; v < 8; v++)
			{
				cu = (u == 0 ? (double)(Math.sqrt(2) / 2.0) : 1);
				cv = (v == 0 ? (double)(Math.sqrt(2) / 2.0) : 1);
				summation = 0;
				for(int i = 0; i < 8; i++) for(int j = 0; j < 8; j++)
				{
					summation += Math.cos((2*i + 1) * u * pi / 16) * Math.cos((2*j + 1) * v * pi / 16) * (double)signal[i][j];
				}
				output[u][v] = (cu * cv / 4.0) * summation;
			}
		return output;
	}
	
}
