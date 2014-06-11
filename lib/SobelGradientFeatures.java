import java.io.FileWriter;
import java.util.ArrayList;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import au.com.bytecode.opencsv.CSVWriter;

public class SobelGradientFeatures
{

static double maxSobelAmplitude[] = {0,0,0};
	static double maxSobelAngle[] = {0,0,0};
	
	
	public static double[] getSobelAmplitudeGradients(byte [] pixels, int w, int h, int channel, boolean task8) throws MagickException
	{
		//Amplitude intensities
		double[] new_intensities = new double[w * h];
		
		//Get intensities
		for(int i = w + 1; i < pixels.length - w - 1; i++)
		{
			//Surrounding pixel matrix of the pixel under consideration
			byte[][] pixel_matrix = new byte[3][3];
			pixel_matrix[0][0] = pixels[ i - w - 1];
			pixel_matrix[0][1] = pixels[ i - w];
			pixel_matrix[0][2] = pixels[ i - w + 1];
			pixel_matrix[1][0] = pixels[ i - 1];
			pixel_matrix[1][1] = pixels[ i ];
			pixel_matrix[1][2] = pixels[ i + 1];
			pixel_matrix[2][0] = pixels[ i + w - 1];
			pixel_matrix[2][1] = pixels[ i + w ];
			pixel_matrix[2][2] = pixels[ i + w - 1];
			
			//Gx and Gy
			int x = pixel_matrix[0][0] + 2 * pixel_matrix[0][1] + pixel_matrix[0][2] - pixel_matrix[2][0] - 2 * pixel_matrix[2][1] - pixel_matrix[2][2];
			int y = pixel_matrix[0][2] + 2 * pixel_matrix[1][2] + pixel_matrix[2][2] - pixel_matrix[0][0] - 2 * pixel_matrix[1][0] - pixel_matrix[2][0];
			
			//Amplitude
			new_intensities[i] = Math.sqrt((x * x) + (y * y));	
			
			//Store largest amplitude value
			if(!task8)
			if(maxSobelAmplitude[channel] < new_intensities[i])	maxSobelAmplitude[channel] = new_intensities[i];
		
		}
		
		return new_intensities;
	}
	
	public static double[] getSobelAngleGradients(byte [] pixels, int w, int h, int channel, boolean task8) throws MagickException
	{
		//Angles
		double[] angles = new double[w * h];
		
		//Get Angles
		for(int i = w + 1; i < pixels.length - w - 1; i++)
		{
			//Surrounding pixel matrix of the pixel under consideration
			byte[][] pixel_matrix = new byte[3][3];
			pixel_matrix[0][0] = pixels[ i - w - 1];
			pixel_matrix[0][1] = pixels[ i - w];
			pixel_matrix[0][2] = pixels[ i - w + 1];
			pixel_matrix[1][0] = pixels[ i - 1];
			pixel_matrix[1][1] = pixels[ i ];
			pixel_matrix[1][2] = pixels[ i + 1];
			pixel_matrix[2][0] = pixels[ i + w - 1];
			pixel_matrix[2][1] = pixels[ i + w ];
			pixel_matrix[2][2] = pixels[ i + w - 1];
			
			//Gx and Gy
			int x = pixel_matrix[0][0] + 2 * pixel_matrix[0][1] + pixel_matrix[0][2] - pixel_matrix[2][0] - 2 * pixel_matrix[2][1] - pixel_matrix[2][2];
			int y = pixel_matrix[0][2] + 2 * pixel_matrix[1][2] + pixel_matrix[2][2] - pixel_matrix[0][0] - 2 * pixel_matrix[1][0] - pixel_matrix[2][0];
			
			//Angle
			angles[i] = Math.atan2(x,y);
			
			//Store largest angle value
//			if(!task8)
//				if(maxSobelAngle[channel] < angles[i])	maxSobelAngle[channel] = angles[i];
		
		}
		
		return angles;
	}
	
	public static void getSobelAngleGradientMax(byte [] pixels, int w, int h, int channel) throws MagickException
	{
		//Angles
		double[] angles = new double[w * h];
		
		//Get Angles
		for(int i = w + 1; i < pixels.length - w - 1; i++)
		{
			//Surrounding pixel matrix of the pixel under consideration
			byte[][] pixel_matrix = new byte[3][3];
			pixel_matrix[0][0] = pixels[ i - w - 1];
			pixel_matrix[0][1] = pixels[ i - w];
			pixel_matrix[0][2] = pixels[ i - w + 1];
			pixel_matrix[1][0] = pixels[ i - 1];
			pixel_matrix[1][1] = pixels[ i ];
			pixel_matrix[1][2] = pixels[ i + 1];
			pixel_matrix[2][0] = pixels[ i + w - 1];
			pixel_matrix[2][1] = pixels[ i + w ];
			pixel_matrix[2][2] = pixels[ i + w - 1];
			
			//Gx and Gy
			int x = pixel_matrix[0][0] + 2 * pixel_matrix[0][1] + pixel_matrix[0][2] - pixel_matrix[2][0] - 2 * pixel_matrix[2][1] - pixel_matrix[2][2];
			int y = pixel_matrix[0][2] + 2 * pixel_matrix[1][2] + pixel_matrix[2][2] - pixel_matrix[0][0] - 2 * pixel_matrix[1][0] - pixel_matrix[2][0];
			
			//Angle
			angles[i] = Math.atan2(x,y);
			
			//Store largest angle value
			if(maxSobelAngle[channel] < angles[i])	maxSobelAngle[channel] = angles[i];
		
		}
		
	}
	
	//Determine the bin that the amplitude value belongs to
	public static int getAmplitudeBin(double value, int channel)
	{
		
		double step = maxSobelAmplitude[channel] / 16;
		
		int bin = 1;
		
		while(bin <= 16)
		{
			if(value < step * bin)	break;
			bin++;
		}
		
		return (bin-1);
		
	}
	
	public static GAmplitudeFeature[] getSobelAmplitudeHistogram(MagickImage newImage, String image_id, boolean task8) throws MagickException
	{
	
		//List storing the Amplitude features temporarily
		ArrayList<GAmplitudeFeature> l = new ArrayList<GAmplitudeFeature>();

		//MagickImage newImage = new MagickImage(info);
		int h = newImage.getDimension().height;
		int w = newImage.getDimension().width;

		//Individual channel amplitude storage
		double[][] amplitudes = new double[3][h * w];

		//Original pixels of the image
		byte[] pixels = new byte[w * h * 4];

		//Get the image pixels
		newImage.dispatchImage(0, 0, w, h, "RGBA", pixels);

		//Separate the channels
		byte[] channel1 = new byte[h * w];
		byte[] channel2 = new byte[h * w];
		byte[] channel3 = new byte[h * w];

		for (int j = 0; j < h * w; j++)
		{
			channel1[j] = pixels[4 * j];
			channel2[j] = pixels[4 * j + 1];
			channel3[j] = pixels[4 * j + 2];
		}

		//Get the Amplitudes for each channel
		amplitudes[0] = getSobelAmplitudeGradients(channel1, w, h, 0, task8);

		amplitudes[1] = getSobelAmplitudeGradients(channel2, w, h, 1, task8);

		amplitudes[2] = getSobelAmplitudeGradients(channel3, w, h, 2, task8);

		//Get max height and width to crop the image
		int w_max, h_max;
		w_max = w / 8;
		w_max *= 8;
		h_max = h / 8;
		h_max *= 8;

		//Store values in GAmplitudeFeature object
		for (int j = 0; j < h_max; j++)
			for (int k = 0; k < w_max; k++)
			{

				GAmplitudeFeature g1 = new GAmplitudeFeature();
				GAmplitudeFeature g2 = new GAmplitudeFeature();
				GAmplitudeFeature g3 = new GAmplitudeFeature();

				g1.setImageId(image_id);
				g2.setImageId(image_id);
				g3.setImageId(image_id);

				g1.setCellId((j / 8) * (w / 8) + (k / 8));
				g2.setCellId((j / 8) * (w / 8) + (k / 8));
				g3.setCellId((j / 8) * (w / 8) + (k / 8));

				g1.setChannelId(0);
				g2.setChannelId(1);
				g3.setChannelId(2);

				g1.setBinId(getAmplitudeBin(amplitudes[0][j * w + k], 0));
				g2.setBinId(getAmplitudeBin(amplitudes[1][j * w + k], 1));
				g3.setBinId(getAmplitudeBin(amplitudes[2][j * w + k], 2));

				g1.setValue(amplitudes[0][j * w + k]);
				g2.setValue(amplitudes[1][j * w + k]);
				g3.setValue(amplitudes[2][j * w + k]);

				l.add(g1);
				l.add(g2);
				l.add(g3);

			}
		
		//Return object array
		GAmplitudeFeature[] amplitude_features = new GAmplitudeFeature[l.size()];

		//Filling return object array
		for (int i = 0; i < l.size(); i++)
		{
			amplitude_features[i] = l.get(i);
		}

		return amplitude_features;

	}
	
	//Determine the bin that the angle value belongs to
	public static int getAngleBin(double value, int channel)
	{
		
		double step = maxSobelAngle[channel] / 16;
		
		int bin = 1;
		
		while(bin <= 16)
		{
			if(value < step * bin)	break;
			bin++;
		}
		
		return (bin-1);
		
	}
	
	public static GAngleFeature[] getSobelAngleHistogram(MagickImage newImage, String image_id, boolean task8) throws MagickException
	{
	
		//List storing the Angle features temporarily
		ArrayList<GAngleFeature> l = new ArrayList<GAngleFeature>();

		//MagickImage newImage = new MagickImage(info);
		int h = newImage.getDimension().height;
		int w = newImage.getDimension().width;

		//Individual channel angle storage
		double[][] angles = new double[3][h * w];

		//Original image pixels
		byte[] pixels = new byte[w * h * 4];

		newImage.dispatchImage(0, 0, w, h, "RGBA", pixels);

		//Separate the channels
		byte[] channel1 = new byte[h * w];
		byte[] channel2 = new byte[h * w];
		byte[] channel3 = new byte[h * w];

		for (int j = 0; j < h * w; j++)
		{
			channel1[j] = pixels[4 * j];
			channel2[j] = pixels[4 * j + 1];
			channel3[j] = pixels[4 * j + 2];
		}

		//Get Angles
		angles[0] = getSobelAngleGradients(channel1, w, h, 0, task8);

		angles[1] = getSobelAngleGradients(channel2, w, h, 1, task8);

		angles[2] = getSobelAngleGradients(channel3, w, h, 2, task8);

		//Get max height and width to crop the image
		int w_max, h_max;
		w_max = w / 8;
		w_max *= 8;
		h_max = h / 8;
		h_max *= 8;

		//Store values in GAngleFeature object
		for (int j = 0; j < h_max; j++)
			for (int k = 0; k < w_max; k++)
			{

				GAngleFeature g1 = new GAngleFeature();
				GAngleFeature g2 = new GAngleFeature();
				GAngleFeature g3 = new GAngleFeature();

				g1.setImageId(image_id);
				g2.setImageId(image_id);
				g3.setImageId(image_id);

				g1.setCellId((j / 8) * (w / 8) + (k / 8));
				g2.setCellId((j / 8) * (w / 8) + (k / 8));
				g3.setCellId((j / 8) * (w / 8) + (k / 8));

				g1.setChannelId(0);
				g2.setChannelId(1);
				g3.setChannelId(2);

				g1.setBinId(getAngleBin(angles[0][j * w + k], 0));
				g2.setBinId(getAngleBin(angles[1][j * w + k], 1));
				g3.setBinId(getAngleBin(angles[2][j * w + k], 2));

				g1.setValue(angles[0][j * w + k]);
				g2.setValue(angles[1][j * w + k]);
				g3.setValue(angles[2][j * w + k]);

				l.add(g1);
				l.add(g2);
				l.add(g3);

			}

		//Return object array
		GAngleFeature[] angle_features = new GAngleFeature[l.size()];

		//Fill return object array
		for (int i = 0; i < l.size(); i++)
		{
			angle_features[i] = l.get(i);
		}

		return angle_features;

	}
	
	//Correct the amplitude bins after all images' amplitude features are extracted
	public static void correctAmplitudeBins(ArrayList<GAmplitudeFeature> g)
	{
		
		for( int i = 0 ; i < g.size() ; i++)
			g.get(i).setBinId(getAmplitudeBin(g.get(i).getValue(),g.get(i).getChannelId()));
				
	}
	
	//Correct the angle bins after all images' angle features are extracted
	public static void correctAngleBins(ArrayList<GAngleFeature> g)
	{
		
		for( int i = 0 ; i < g.size() ; i++)
			g.get(i).setBinId(getAngleBin(g.get(i).getValue(),g.get(i).getChannelId()));
				
	}
	
	
		public static void writeGAmplitudetoCSV(ArrayList<GAmplitudeFeature> features)
	{
		try
		{
			CSVWriter writer = new CSVWriter(new FileWriter("gAmpdata.csv"));
			// convert each entry vector into an array of strings, then write it
			// out
			String[] rowofentries = new String[5];
			for (int i = 0; i < features.size(); i++)
			{
				rowofentries[0] = features.get(i).getImageId();
				rowofentries[1] = features.get(i).getCellId() + "";
				rowofentries[2] = features.get(i).getChannelId() + "";
				rowofentries[3] = features.get(i).getBinId() + "";
				rowofentries[4] = features.get(i).getValue() + "";
				// write one row.
				writer.writeNext(rowofentries);
			}

			writer.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeGAngletoCSV(ArrayList<GAngleFeature> features)
	{
		try
		{
			CSVWriter writer = new CSVWriter(new FileWriter("gAngdata.csv"));
			// convert each entry vector into an array of strings, then write it
			// out
			String[] rowofentries = new String[5];
			for (int i = 0; i < features.size(); i++)
			{
				rowofentries[0] = features.get(i).getImageId();
				rowofentries[1] = features.get(i).getCellId() + "";
				rowofentries[2] = features.get(i).getChannelId() + "";
				rowofentries[3] = features.get(i).getBinId() + "";
				rowofentries[4] = features.get(i).getValue() + "";
				// write one row.
				writer.writeNext(rowofentries);
			}

			writer.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
