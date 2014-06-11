import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import magick.MagickImage;
/**
 * TODO Put here a description of what this class does.
 *
 * @author Zelpha.
 *         Created Nov 27, 2012.
 *         This class adds image info to an excel file data base.
 *         
 */
public class dbProj3 {
	public static final String DB_FILENAME = "c.xls";
	
	public static Workbook ourDB_read; // = Workbook.getWorkbook(new File("db_proj3Group7.xlsx"));
	public static Sheet shr_db_tab0; // = ourDB.getSheet(0);
	public static Sheet shr_t1_hisSpec_tab1;
	public static Sheet shr_t2_colorHis_tab2;
	public static Sheet shr_t3_chanInFreqBin_tab3;
	public static Sheet shr_t4_gradAngHis_tab4;
	public static Sheet shr_t5_gradAmpHis_tab5;
	public static Sheet shr_t6_chanWavlet_tab6;
	public static Sheet shr_sheet7_tab7;
	public static Sheet shr_sheet8_tab8;
	public static Sheet shr_sheet9_tab9;
	public static Sheet shr_Help_tab10;
	
	public static final String tab1Str = "t1_hisSpec";
	public static final String tab2Str  = "t2_colorHis";
	public static final String tab3Str  = "t3_chanInFreqBin";
	public static final String tab4Str  = "t4_gradAngHis";
	public static final String tab5Str  = "t5_gradAmpHis";
	public static final String tab6Str  = "t6_chanWavlet";
	
	public static final int tab1Num = 1;
	public static final int tab2Num = 2;
	public static final int tab3Num = 3;
	public static final int tab4Num = 3;
	public static final int tab5Num = 4;
	public static final int tab6Num = 5;
	
	public static WritableWorkbook ourDB_write; // = Workbook.getWorkbook(new File("db_proj3Group7.xlsx"));
	public static WritableSheet shw_db_tab0; // = ourDB.getSheet(0);
	public static WritableSheet shw_t1_hisSpec_tab1;
	public static WritableSheet shw_t2_colorHis_tab2;
	public static WritableSheet shw_t3_chanInFreqBin_tab3;
	public static WritableSheet shw_t4_gradAngHis_tab4;
	public static WritableSheet shw_t5_gradAmpHis_tab5;
	public static WritableSheet shw_t6_chanWavlet_tab6;
	public static WritableSheet shw_sheet7_tab7;
	public static WritableSheet shw_sheet8_tab8;
	public static WritableSheet shw_sheet9_tab9;
	public static WritableSheet shw_Help_tab10;
	
	
	
	/**
	 * verify that you can read and write to excel file
	 * @param hisSpec_Ranges a 2d array wiht the ranges for the histogram column. arr[0][x] means start of range and arr[1][x] indicates end of range. This is for column at positon x, x stars
	 * @param s This is the image id to which the histogram specs corrosponds.
	 * @param xlDbFileName	the xlDbFileName
	 */	
	public static String DB_init_TestReadWriteToXLFile(String xlDbFileName, JTextArea debug){
		String s = "";		
		try{	
			
			ourDB_read = Workbook.getWorkbook(new File(xlDbFileName));
			shr_db_tab0 = ourDB_read.getSheet(0);
				
			s = s + "opened: " + xlDbFileName;
			
			if( shr_db_tab0.getCell(0,6) == null)
			{s = s = s + ", read B7: " + "null value empty A7";		}
			else 
			{Cell cell = shr_db_tab0.getCell(0,6);
			s = s = s + ", read " + cell.getType().toString() + " B7: " + cell.getContents();}
			ourDB_read.close();	
			s = s +  " and then closed it ";
		
			}
		catch (Exception ioe) {
			ioe.printStackTrace();
			}
		
		
		try{	
			
			ourDB_read = Workbook.getWorkbook(new File(xlDbFileName));//c
			ourDB_write = Workbook.createWorkbook(new File("b.xls"), ourDB_read);

			s = s + "\nopened: " + xlDbFileName + ", ";
			
			shw_db_tab0 = ourDB_write.getSheet(0);
			//WritableCell cell = shw_db_tab0.getWritableCell(0,0);
			Date now = Calendar.getInstance().getTime();
			Label label = new Label(0,1,now.toString());
			Label label2 =  new Label(0,2, "4870");
		//	cell.
			shw_db_tab0.addCell(label);
			shw_db_tab0.addCell(label2);
				
			s = s + "wrote to A1: " + now.toString() + " A2: " + "480eee";				
				ourDB_write.write();
			s = s + " wrote out workbook !!1";				
				ourDB_read.close();								 
			s = s + " close out workbook 2!!";
			
			ourDB_write.close();
			
			
			}
		catch (Exception ioe) {
			ioe.printStackTrace();
			}
		
		return (s);
		
	}
	

	/**
	 * 
	 * -creates excelfile DB.
	 * - completes task 1
	 * --- gets instance3, the array that has the input file as an image
	 * --- gets the 3 color histogram specifications of the image, one for each color space RGV, YUV and HSV.
	 * --- each color histogram specification is in its respactive 2n X 16 array
	 * --- For RGB, 2n for column 1 corrospands to RedMax, Redmin, Gmax Gmin, Bmax and Bmin 
	 * ------ assigns 100 as image id for input image
	 * ------ writes the imageID, colorspac and three 2n X 16 array (the histogram spacs) to db
	 * ------ these are written in tab t1_hisSpec in excel file
	 * ------ it assigns the imageId to B3, color space to colorSpace and array values to the bin values 
	 * ----For YUV do similar as RGB
	 * ----For HSV do similar as RGB 
	 * - completes task 2
	 * --- gets cellgrid3, the 1D array that has the celled images, ie cellgrid3 array has 64 images
	 * --- for cellgrid3[0], ie chooses the first image, image with imageid 0,
	 * ----- gets the 3 color histogram of the image, one for each color space RGB, YUV and HSV
	 * ----- each color histogram is in its respactive 3 X 16 array
	 * ---------For RGB, 3 for column 1 corrospands to RedCount, Gcount and BCount 
	 * ------------ assigns for celled image, 0 as imageid and <0,0> as cellcoord
	 * ------------ writes the imageID, cellcoord, colorspace and three 3 X 16 array (the histogram s) to db
	 * ------------ these are written in tab t2_colorHis in excel file
	 * ------------ using imageID 0, it assigns the imageId to B3, color space to colorSpace and array values to the bin values
	 * ---------FOR YUV do similar as RGB
	 * ---------FOR HSV do similar as RGB  
	 * --- For cellgrid3[1 to 63] do the same as cellgrid3[0] 
	 * - completes task 3
	 * -----similar to task 2
	 * - completes task 4
	 * -----similar to task 2
	 * - completes task 5
	 * -----similar to task 2
	 * - completes task 6
	 * -----similar to task 2
	 * -Closes the excel file
	 * @param instance3, a 2D array which contains an input image
	 * @param cellgrid3, a 1D array which contains the the 64 celled images formed by gridding instance3.
	 * @param xlDbFileName, the excel file name which will be the db
	 * @param debug, a JTextArea object to write diagnostic back to the GUI
	 */	
	public static String DB_init(String xlDbFileName, JTextArea debug){
		try{	
			// oped db for writing
			//ourDB_read = Workbook.getWorkbook(new File(xlDbFileName));//c
			//ourDB_write = Workbook.createWorkbook(new File("b.xls"), ourDB_read);
					
			// the below calls will call other functions which will write to db
			debug.append("start init db\n   ");
			debug.append(add_t1_histogramSpec(Utilities.imageSetMembers, Utilities._colorspace));
			getHistogram_Task1(Utilities.imageSetMembers, Utilities._colorspace);
			debug.append("\n   " + add_t2_colorHistogram(Utilities.imageSetMembers, Utilities._colorspace));
			debug.append("\n   " + add_t3_frequencyHistogram(Utilities.imageSetMembers, Utilities._colorspace));	
			debug.append("\n   " + add_t4_angleHistogram(Utilities.imageSetMembers, Utilities._colorspace));
			debug.append("\n   " + add_t5_amplitudeHistogram(Utilities.imageSetMembers, Utilities._colorspace));	
			debug.append("\n   " + add_t6_waveletHistogram(Utilities.imageSetMembers, Utilities._colorspace));
			debug.append("\nend init db");
			debug.append("\nstoring dimensions...");
			debug.append("\n   " + store_Dimensions(Utilities.imageSetMembers));
			debug.append("\ntask complete.");
			
			//debug.append("\ndb in xl file: " + xlDbFileName); 
			
			// close DB
			//debug.append("\n   about to write. ");				
			//ourDB_write.write();
			//debug.append(" wrote. ");			
			//ourDB_read.close();	
			//debug.append(" close Read Handle ");			
			//ourDB_write.close();	
			//debug.append(" close Write Handle \n");
			
			
			}
		catch (Exception ioe) {
			ioe.printStackTrace();
			}
		
			
	return "\nDB ready for searching\n";
	}
	
	
	public static double[][][] sorttracks(double[][][] tracks)
	{
		double[][][] outs = tracks;
		int j;                     // the number of items sorted so far
		double[] key;                // the item to be inserted
		int i; 

		for(int a = 0; a < 5; a++)
		{
			for (j = 1; j < outs[a].length; j++)    // Start with 1 (not 0)
			{
				key = outs[a][j];
				for(i = j - 1; (i >= 0) && (outs[a][i][2] < key[2]); i--)   // Smaller values are moving up
				{
					outs[a][i+1] = outs[a][i];
				}
				outs[a][i+1] = key;    // Put the key in its proper location
			}
		}
		return outs;
	}
	
	public static double[][][] consider(double[][][] tracks, int f, double[] value)
	{
		double[][][] output = tracks;
		int counter = 9;
		while(counter >= 0 && (value[2] > output[f][counter][2]))
		{
			counter--;
		}
		if(counter < 9)
		{
			for(int q = 8; q > counter; q--)
			{
				output[f][q+1] = output[f][q];
			}
			output[f][counter+1] = value;
		}
		return output;
	}
	
	public static String t8_findSimilarities(int regionx, int regiony, imageSetMember queryimg, int channel)
	{
		String output = "";
		try
		{
			int asdf = 1;
			int querywidth = queryimg.width;
			int startcell = regiony * (querywidth / 8) + regionx;
			Map<Integer, Double> currentmatch;
			//ArrayList<Map<Integer, Double>> matches = new ArrayList<Map<Integer, Double>>();
			ArrayList<int[]> imagedimensions = Utilities.getWidthHeights();
			int numimages = imagedimensions.size();
			ArrayList<Feature> currentfeatures;
			ArrayList<Feature> gridfeatures;
			double[][][] keepintrack = new double[5][10][3];
			for(int i = 0; i < 5; i++)
				for(int j = 0; j < 10; j++)
				{
					keepintrack[i][j][0] = 0;
					keepintrack[i][j][1] = 0;
					keepintrack[i][j][2] = -9999999;
				}
			int activefeature;
			double[] candidatevalue = new double[3];
			String[] featurestrings = {"Color Instances", "DCT", "Gradient Angle", "Gradient Amplitude", "DWT"};
			for(activefeature = 0; activefeature < 5; activefeature++) //CHANGE TO activefeature = 0 WHEN YOU HAVE TASK2 ADDED
			{
				for(int i = 0; i < numimages; i++) 
				{
					//sorttracks(keepintrack);
					currentfeatures = Utilities.grabFeaturesForImage("image" + i, activefeature, channel);
					gridfeatures = Utilities.grabFeaturesForGrid(startcell, querywidth, activefeature, channel);
					currentmatch = BlockMatching.getTopNSimilar(gridfeatures, currentfeatures, imagedimensions.get(i)[0], imagedimensions.get(i)[1]);
					for(Map.Entry<Integer, Double> value  : currentmatch.entrySet())
					{
						candidatevalue = new double[3];
						candidatevalue[0] = i;
						candidatevalue[1] = value.getKey();
						candidatevalue[2] = value.getValue();
						keepintrack = consider(keepintrack, activefeature, candidatevalue);
					}
					//matches.add(currentmatch);
				}
				output += "Top ten matches for " + featurestrings[activefeature] + ":\n";
				int cellx;
				int celly;
				int cw;
				for(int i = 0; i < 10; i++)
				{
					cw = Utilities.imageSetMembers[(int)keepintrack[activefeature][i][0]].width / 8;
					cellx = (int)keepintrack[activefeature][i][1] % cw;
					celly = (int)keepintrack[activefeature][i][1] / cw;
					output += "Rank:\tImage:\tRegion:\tValue\n" + (i+1) + 
							"\timage" + (int)keepintrack[activefeature][i][0] + 
							//"\t" + (int)keepintrack[activefeature][i][1] + 
							"\t<" + cellx + ", " + celly + ">" +
							"\t" + keepintrack[activefeature][i][2] + "\n";
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		output += "task 8 complete.";
		return output;
	}
	
	public static String store_Dimensions(imageSetMember[] imgs)
	{
		try
		{
			Utilities.generateImageSetCSV(imgs);
		}catch(Exception e){e.printStackTrace();}
		return "Dimensions database generated.";
	}
	
	
	public static String targetimage_generateHistograms(imageSetMember target, int cspace)
	{
		try
		{
			MagickImage img = target.getLoadedImage(cspace).getInstance();
			MagickImage[] dummyarray = new MagickImage[1];
			dummyarray[0] = img;
			ArrayList<DWTFeature> dwtfeatures = DWTUtils.task6(dummyarray);
			ArrayList<GAmplitudeFeature> gamplitudefeatures = new ArrayList<GAmplitudeFeature>();
			ArrayList<GAngleFeature> ganglefeatures = new ArrayList<GAngleFeature>();
			ArrayList<DCTFeature> dctfeatures= DCTUtils.task3(dummyarray);
			List<ColorFeature> colorfeatures = MedianCutUtils.getFeatures(dummyarray);

			byte[] pixels;
			byte[] reds;
			byte[] greens;
			byte[] blues;
			GAmplitudeFeature[] outputs2;
			GAngleFeature[] outputs;
			pixels = new byte[img.getDimension().width * img.getDimension().height * 3];
			img.dispatchImage(0, 0, img.getDimension().width, img.getDimension().height, "RGB", pixels);
			reds = new byte[pixels.length / 3];
			greens = new byte[pixels.length / 3];
			blues = new byte[pixels.length / 3];
			for(int i = 0; i < reds.length; i++)
			{
				reds[i] = pixels[3*i];
				greens[i] = pixels[3*i + 1];
				blues[i] = pixels[3*i + 2];
			}
			SobelGradientFeatures2.getSobelAngleGradientMax(reds, img.getDimension().width, img.getDimension().height, 0);
			SobelGradientFeatures2.getSobelAngleGradientMax(greens, img.getDimension().width, img.getDimension().height, 1);
			SobelGradientFeatures2.getSobelAngleGradientMax(blues, img.getDimension().width, img.getDimension().height, 2);

			SobelGradientFeatures2.getSobelAmplitudeGradientMax(reds, img.getDimension().width, img.getDimension().height, 0);
			SobelGradientFeatures2.getSobelAmplitudeGradientMax(greens, img.getDimension().width, img.getDimension().height, 1);
			SobelGradientFeatures2.getSobelAmplitudeGradientMax(blues, img.getDimension().width, img.getDimension().height, 2);

			outputs = SobelGradientFeatures2.getSobelAngleHistogram(img, "targetimage", false);
			for(int k = 0; k < outputs.length; k++)
				ganglefeatures.add(outputs[k]);

			outputs2 = SobelGradientFeatures2.getSobelAmplitudeHistogram(img, "targetimage", false);
			for(int k = 0; k < outputs2.length; k++)
				gamplitudefeatures.add(outputs2[k]);
			String[] filenames = {"colordatatarget.csv", "dctdatatarget.csv", "gAngdatatarget.csv", "gAmpdatatarget.csv", "dwtdatatarget.csv"};
			String fn;

			fn = filenames[2];
			SobelGradientFeatures2.writeGAngletoCSV(ganglefeatures, fn);

			fn = filenames[3];
			SobelGradientFeatures2.writeGAmplitudetoCSV(gamplitudefeatures, fn);

			for(int i = 0; i < dctfeatures.size(); i++)
			{
				dctfeatures.get(i).setImageId("targetimage");
				dwtfeatures.get(i).setImageId("targetimage");
			}
			
			for(int i = 0; i < colorfeatures.size(); i++)
			{
				colorfeatures.get(i).setImageId("targetimage");
			}

			fn = filenames[1];
			DCTUtils.writeDCTtoCSV(dctfeatures, fn);

			fn = filenames[4];
			DWTUtils.writeDWTtoCSV(dwtfeatures, fn);
			
			fn = filenames[0];
			MedianCutUtils.writeColorsToCSV(colorfeatures, fn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "Target image histograms generated";
	}
	

	/**
	 * TODO Put here a description of what this method does.
	 *	This is similar to description in tast 2 funciton.
	 * 	copied task 2 description below(see task2 implementation)
	 *- completes task 2
	 * --- gets cellgrid3, the 1D array that has the celled images, ie cellgrid3 array has 64 images
	 * --- for cellgrid3[0], ie chooses the first image, image with imageid 0,
	 * ----- gets the 3 color histogram of the image, one for each color space RGB, YUV and HSV
	 * ----- each color histogram is in its respactive 3 X 16 array
	 * ---------For RGB, 3 for column 1 corrospands to RedCount, Gcount and BCount 
	 * ------------ assigns for celled image, 0 as imageid and <0,0> as cellcoord
	 * ------------ writes the imageID, cellcoord, colorspace and three 3 X 16 array (the histogram s) to db
	 * ------------ these are written in tab t2_colorHis in excel file
	 * ------------ using imageID 0, it assigns the imageId to B3, color space to colorSpace and array values to the bin values
	 * ---------FOR YUV do similar as RGB
	 * ---------FOR HSV do similar as RGB  
	 * --- For cellgrid3[1 to 63] do the same as cellgrid3[0]
	 *
	 * @param instance3
	 * @param _width
	 * @param _height
	 * @param cellgrid3
	 * @param xlDbFileName
	 * @return
	 */
	public static String add_t6_waveletHistogram(imageSetMember[] igs, int cspace) {
		// TODO Auto-generated method stub.
		MagickImage[] imgs;
		
		int valids = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
				valids++;
		}
		
		imgs = new MagickImage[valids];
		ArrayList<DWTFeature> features;
		int counter = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
			{
				imgs[counter] = igs[i].getLoadedImage(cspace).getInstance();
				counter++;
			}
		}
		features = DWTUtils.task6(imgs);
		DWTUtils.writeDWTtoCSV(features, "dwtdata.csv");
		return "completed Task 6";
	}


	/**
	 * TODO Put here a description of what this method does.
	 *	This is similar to description in tast 2 funciton.
	 * 	copied task 2 description below(see task2 implementation)
	 *- completes task 2
	 * --- gets cellgrid3, the 1D array that has the celled images, ie cellgrid3 array has 64 images
	 * --- for cellgrid3[0], ie chooses the first image, image with imageid 0,
	 * ----- gets the 3 color histogram of the image, one for each color space RGB, YUV and HSV
	 * ----- each color histogram is in its respactive 3 X 16 array
	 * ---------For RGB, 3 for column 1 corrospands to RedCount, Gcount and BCount 
	 * ------------ assigns for celled image, 0 as imageid and <0,0> as cellcoord
	 * ------------ writes the imageID, cellcoord, colorspace and three 3 X 16 array (the histogram s) to db
	 * ------------ these are written in tab t2_colorHis in excel file
	 * ------------ using imageID 0, it assigns the imageId to B3, color space to colorSpace and array values to the bin values
	 * ---------FOR YUV do similar as RGB
	 * ---------FOR HSV do similar as RGB  
	 * --- For cellgrid3[1 to 63] do the same as cellgrid3[0]
	 *
	 * @param instance3
	 * @param _width
	 * @param _height
	 * @param cellgrid3
	 * @param xlDbFileName
	 * @return
	 */
	public static String add_t5_amplitudeHistogram(imageSetMember[] igs, int cspace) {
		// TODO Auto-generated method stub.
		MagickImage[] imgs;
		ArrayList<GAmplitudeFeature> features = new ArrayList<GAmplitudeFeature>();
		int counter = 0;
		int valids = 0;

		try
		{
			for(int i = 0; i < igs.length; i++)
			{
				if(igs[i]!=null)
					valids++;
			}

			imgs = new MagickImage[valids];

			for(int i = 0; i < igs.length; i++)
			{
				if(igs[i]!=null)
				{
					imgs[counter] = igs[i].getLoadedImage(cspace).getInstance();
					counter++;
				}
			}

			GAmplitudeFeature[] outputs;
			byte[] pixels;
			byte[] reds;
			byte[] greens;
			byte[] blues;
			for(int k = 0; k < imgs.length; k++)
			{
				pixels = new byte[imgs[k].getDimension().width * imgs[k].getDimension().height * 3];
				imgs[k].dispatchImage(0, 0, imgs[k].getDimension().width, imgs[k].getDimension().height, "RGB", pixels);
				reds = new byte[pixels.length / 3];
				greens = new byte[pixels.length / 3];
				blues = new byte[pixels.length / 3];
				for(int i = 0; i < reds.length; i++)
				{
					reds[i] = pixels[3*i];
					greens[i] = pixels[3*i + 1];
					blues[i] = pixels[3*i + 2];
				}
				SobelGradientFeatures2.getSobelAmplitudeGradientMax(reds, imgs[k].getDimension().width, imgs[k].getDimension().height, 0);
				SobelGradientFeatures2.getSobelAmplitudeGradientMax(greens, imgs[k].getDimension().width, imgs[k].getDimension().height, 1);
				SobelGradientFeatures2.getSobelAmplitudeGradientMax(blues, imgs[k].getDimension().width, imgs[k].getDimension().height, 2);
			}
			for(int j = 0; j < imgs.length; j++)
			{
				outputs = SobelGradientFeatures2.getSobelAmplitudeHistogram(imgs[j], "image" + j, false);
				for(int k = 0; k < outputs.length; k++)
					features.add(outputs[k]);
			}
			//SobelGradientFeatures2.correctAngleBins(features);
			SobelGradientFeatures2.writeGAmplitudetoCSV(features, "gAmpdata.csv");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		return "completed Task 5";
	}


	/**
	 * TODO Put here a description of what this method does.
	 *	This is similar to description in tast 2 funciton.
	 * 	copied task 2 description below(see task2 implementation)
	 *- completes task 2
	 * --- gets cellgrid3, the 1D array that has the celled images, ie cellgrid3 array has 64 images
	 * --- for cellgrid3[0], ie chooses the first image, image with imageid 0,
	 * ----- gets the 3 color histogram of the image, one for each color space RGB, YUV and HSV
	 * ----- each color histogram is in its respactive 3 X 16 array
	 * ---------For RGB, 3 for column 1 corrospands to RedCount, Gcount and BCount 
	 * ------------ assigns for celled image, 0 as imageid and <0,0> as cellcoord
	 * ------------ writes the imageID, cellcoord, colorspace and three 3 X 16 array (the histogram s) to db
	 * ------------ these are written in tab t2_colorHis in excel file
	 * ------------ using imageID 0, it assigns the imageId to B3, color space to colorSpace and array values to the bin values
	 * ---------FOR YUV do similar as RGB
	 * ---------FOR HSV do similar as RGB  
	 * --- For cellgrid3[1 to 63] do the same as cellgrid3[0]
	 *
	 * @param instance3
	 * @param _width
	 * @param _height
	 * @param cellgrid3
	 * @param xlDbFileName
	 * @return
	 */
	public static String add_t4_angleHistogram(imageSetMember[] igs, int cspace) {
		// TODO Auto-generated method stub.
		
		MagickImage[] imgs;
		ArrayList<GAngleFeature> features = new ArrayList<GAngleFeature>();
		int counter = 0;
		int valids = 0;
		
		try
		{
			for(int i = 0; i < igs.length; i++)
			{
				if(igs[i]!=null)
					valids++;
			}

			imgs = new MagickImage[valids];

			for(int i = 0; i < igs.length; i++)
			{
				if(igs[i]!=null)
				{
					imgs[counter] = igs[i].getLoadedImage(cspace).getInstance();
					counter++;
				}
			}

			GAngleFeature[] outputs;
			byte[] pixels;
			byte[] reds;
			byte[] greens;
			byte[] blues;
			for(int k = 0; k < imgs.length; k++)
			{
				pixels = new byte[imgs[k].getDimension().width * imgs[k].getDimension().height * 3];
				imgs[k].dispatchImage(0, 0, imgs[k].getDimension().width, imgs[k].getDimension().height, "RGB", pixels);
				reds = new byte[pixels.length / 3];
				greens = new byte[pixels.length / 3];
				blues = new byte[pixels.length / 3];
				for(int i = 0; i < reds.length; i++)
				{
					reds[i] = pixels[3*i];
					greens[i] = pixels[3*i + 1];
					blues[i] = pixels[3*i + 2];
				}
				SobelGradientFeatures2.getSobelAngleGradientMax(reds, imgs[k].getDimension().width, imgs[k].getDimension().height, 0);
				SobelGradientFeatures2.getSobelAngleGradientMax(greens, imgs[k].getDimension().width, imgs[k].getDimension().height, 1);
				SobelGradientFeatures2.getSobelAngleGradientMax(blues, imgs[k].getDimension().width, imgs[k].getDimension().height, 2);
			}
			for(int j = 0; j < imgs.length; j++)
			{
				outputs = SobelGradientFeatures2.getSobelAngleHistogram(imgs[j], "image" + j, false);
				for(int k = 0; k < outputs.length; k++)
					features.add(outputs[k]);
			}
			//SobelGradientFeatures2.correctAngleBins(features);
			SobelGradientFeatures2.writeGAngletoCSV(features, "gAngdata.csv");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return "completed Task 4";
	}


	/**
	 * TODO Put here a description of what this method does.
	 *	This is similar to description in tast 2 funciton.
	 * 	copied task 2 description below (see task2 implementation)
	 *- completes task 2
	 * --- gets cellgrid3, the 1D array that has the celled images, ie cellgrid3 array has 64 images
	 * --- for cellgrid3[0], ie chooses the first image, image with imageid 0,
	 * ----- gets the 3 color histogram of the image, one for each color space RGB, YUV and HSV
	 * ----- each color histogram is in its respactive 3 X 16 array
	 * ---------For RGB, 3 for column 1 corrospands to RedCount, Gcount and BCount 
	 * ------------ assigns for celled image, 0 as imageid and <0,0> as cellcoord
	 * ------------ writes the imageID, cellcoord, colorspace and three 3 X 16 array (the histogram s) to db
	 * ------------ these are written in tab t2_colorHis in excel file
	 * ------------ using imageID 0, it assigns the imageId to B3, color space to colorSpace and array values to the bin values
	 * ---------FOR YUV do similar as RGB
	 * ---------FOR HSV do similar as RGB  
	 * --- For cellgrid3[1 to 63] do the same as cellgrid3[0] 
	 * @param instance3
	 * @param _width
	 * @param _height
	 * @param cellgrid3
	 * @param xlDbFileName
	 * @return
	 */
	public static String add_t3_frequencyHistogram(imageSetMember[] igs, int cspace) {
		
		MagickImage[] imgs;
		
		int valids = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
				valids++;
		}
		
		imgs = new MagickImage[valids];
		ArrayList<DCTFeature> features;
		int counter = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
			{
				imgs[counter] = igs[i].getLoadedImage(cspace).getInstance();
				counter++;
			}
		}
		features = DCTUtils.task3(imgs);
		DCTUtils.writeDCTtoCSV(features, "dctdata.csv");
		
		return "completed Task 3";
	}


	/**
	 * TODO Put here a description of what this method does.
	 *- completes task 2
	 * --- gets cellgrid3, the 1D array that has the celled images, ie cellgrid3 array has 64 images
	 * --- for cellgrid3[0], ie chooses the first image, image with imageid 0,
	 * ----- gets the 3 color histogram of the image, one for each color space RGB, YUV and HSV
	 * ----- each color histogram is in its respactive 3 X 16 array
	 * ---------For RGB, 3 for column 1 corrospands to RedCount, Gcount and BCount 
	 * ------------ assigns for celled image, 0 as imageid and <0,0> as cellcoord
	 * ------------ writes the imageID, cellcoord, colorspace and three 3 X 16 array (the histogram s) to db
	 * ------------ these are written in tab t2_colorHis in excel file
	 * ------------ using imageID 0, it assigns the imageId to B3, color space to colorSpace and array values to the bin values
	 * ---------FOR YUV do similar as RGB
	 * ---------FOR HSV do similar as RGB  
	 * --- For cellgrid3[1 to 63] do the same as cellgrid3[0] 
	 * @param instance3
	 * @param _width
	 * @param _height
	 * @param cellgrid3
	 * @param xlDbFileName
	 * @return
	 */
	private static String add_t2_colorHistogram(imageSetMember[] igs, int cspace) {
		// TODO Auto-generated method stub.
		//getHistogram(
		MagickImage[] imgs;
		
		int valids = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
				valids++;
		}
		
		imgs = new MagickImage[valids];
		List<ColorFeature> features;
		int counter = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
			{
				imgs[counter] = igs[i].getLoadedImage(cspace).getInstance();
				counter++;
			}
		}
		features = MedianCutUtils.getFeatures(imgs);
		MedianCutUtils.writeColorsToCSV(features, "colordata.csv");
		
		return "completed Task 2";
	}



	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param string
	 * @param magickImage
	 * @return
	 */
	private static void getHistogram_Task1(imageSetMember[] igs, int cspace) {
		// TODO Auto-generated method stub.
		byte[] allPixelColors;
		MagickImage[] imgs;
		
		int valids = 0;
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
				valids++;
		}
		
		imgs = new MagickImage[valids];
		int counter = 0;
		int sumlength = 0;
		
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
			{
				sumlength += igs[i].width * igs[i].height * 3;
			}
		}
		
		allPixelColors = new byte[sumlength];
		for(int i = 0; i < igs.length; i++)
		{
			if(igs[i]!=null)
			{
				for(int j = 0; j < igs[i].height * igs[i].width * 3; j++)
				{
					allPixelColors[counter] = igs[i].getLoadedImage(cspace).dispatch3()[j];
					counter++;
				}
			}
		}
		
		MedianCutUtils.getMedianCutSpec(allPixelColors);
	
	}


	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param tabstr
	 * @param tabnum
	 * @param img_ID
	 * @param cellCoord
	 * @param cs
	 * @param histogram
	 * @param h
	 * @param w
	 */
	private static void writeHistogramToDB(String tabStr, int tabNum,
			int img_ID, String cellCoord, String cs, int[][] histogram, int h, int w) {
		// TODO Auto-generated method stub.
		
		// this is the killer job.
		// write the abov to the xl file.
	
	try{
		//
		shw_db_tab0 = ourDB_write.getSheet(tabNum);
		//WritableCell cell = shw_db_tab0.getWritableCell(0,0);
		Date now = Calendar.getInstance().getTime();
		Label label = new Label(0,1,now.toString());
		Label label2 =  new Label(0,2, "4870");
	//	cell.
		shw_db_tab0.addCell(label);
		shw_db_tab0.addCell(label2);
		
		}
	catch (Exception ioe) {
		ioe.printStackTrace();
		}
	
		
	}



	/**
	 * TODO Put here a description of what this method does.
	 * - completes task 1
	 * --- gets instance3, the array that has the input file as an image
	 * --- gets the 3 color histogram specifications of the image, one for each color space RGV, YUV and HSV.
	 * --- each color histogram specification is in its respactive 2n X 16 array
	 * --- For RGB, 2n for column 1 corrospands to RedMax, Redmin, Gmax Gmin, Bmax and Bmin 
	 * ------ assigns 100 as image id for input image
	 * ------ writes the imageID, colorspac and three 2n X 16 array (the histogram spacs) to db
	 * ------ these are written in tab t1_hisSpec in excel file
	 * ------ it assigns the imageId to B3, color space to colorSpace and array values to the bin values 
	 * ----For YUV do similar as RGB
	 * ----For HSV do similar as RGB 

	 *
	 * @param instance3
	 * @param _width
	 * @param _height
	 * @param cellgrid3
	 * @param xlDbFileName
	 * @return
	 */
	private static String add_t1_histogramSpec(imageSetMember[] igs, int cspace) {
		// TODO Auto-generated method stub.
		
		int[][] hisSpecRGB = {{0,16,32,48,64,80,96,112,128,144,160,176,192,208,224,240},
				{15,31,47,63,79,95,111,127,143,159,175,191,207,223,239,255},
				{0,16,32,48,64,80,96,112,128,144,160,176,192,208,224,240},
				{15,31,47,63,79,95,111,127,143,159,175,191,207,223,239,255},
				{0,16,32,48,64,80,96,112,128,144,160,176,192,208,224,240},
				{15,31,47,63,79,95,111,127,143,159,175,191,207,223,239,255}
			};

		
		
		
		return "completed Task 1";
	}
	
	
	
	public static void main(){
		
		
		 int image_20 = 20;
		 int image_21 = 21;
		 int image_22 = 22;
		 int [][] specImg20 = {{1, 6, 11, 16, 21, 26, 31, 36, 41, 46},
				 			   {5,10, 15, 20, 25, 30, 35, 40, 45, 50}} ;
		 int [][] specImg21 = {{ 1, 11, 21, 31, 41},
	 			   			   {10, 20, 30, 40, 50}} ;
		 int [][] specImg22 = {{ 1, 21, 41, 61, 81},
	 			   			   {20, 40, 60, 80, 100}} ;
		 	 
			// DB_init(DB_FILENAME);
			// sh_t1_hisSpec_tab1.
			 //writeHistogramSpecToDBCell(MagickImage image, int imageId,int w, int h, int cs){
		 
		 
	}

}
