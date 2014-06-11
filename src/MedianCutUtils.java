

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import magick.MagickException;
import magick.MagickImage;
import au.com.bytecode.opencsv.CSVWriter;

public class MedianCutUtils {
 
	private static List<CColorInfo> specification;
	private static final String SPEC_FILENAME = "hist_spec.txt";

	public static void writeColorsToCSV(List<ColorFeature> features, String fileName) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(fileName));
	
			String[] rowofentries = new String[4];
			for(int i = 0; i < features.size(); i++) {
				//write one row.
				rowofentries[0] = features.get(i).getImageId();
				rowofentries[1] = features.get(i).getCellId() + "";
				rowofentries[2] = features.get(i).getBinId() + "";
				rowofentries[3] = features.get(i).getFrequency() + "";
				writer.writeNext(rowofentries);
			}

			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static List<ColorFeature> getFeatures(MagickImage[] images) {
		List<ColorFeature> features = new ArrayList<ColorFeature>();
		for (int i = 0; i < images.length; i++) {
			// get all possible cells along with indices
			MagickImage currentImage = images[i];
			int width = 0;
			int height = 0;
			byte[] allColors = null;
			try {
				width = currentImage.getDimension().width;
				height = currentImage.getDimension().height;
				allColors = new byte[width * height * 3];
				currentImage.dispatchImage(0, 0, width, height, "RGB", allColors);
			} catch (MagickException e) {
				throw new IllegalStateException("Error reading one of the images");
			}
			for (int h = 0; h < height/8; h++) {
				for (int w = 0; w < width/8; w++) {
					int cellId = h * (width/8) + w;
					String imageId = "image" + i;
					byte[] cellColors = new byte[8 * 8 * 3];
					for (int p = 0; p < 8; p++) {
						for (int q = 0; q < 8; q++) {
							cellColors[p * 24 +  q * 3] = allColors[(h * 8 + p) * 3 * width + (w * 8 + q) * 3];
							cellColors[p * 24 +  q * 3 + 1] = allColors[(h * 8 + p) * 3 * width + (w * 8 + q) * 3 + 1];
							cellColors[p * 24 +  q * 3 + 2] = allColors[(h * 8 + p) * 3 * width + (w * 8 + q) * 3 + 2];
						}
					}
					int[] frequencies = getFeatures(cellColors);
					for (int j = 0; j < frequencies.length; j++) {
						features.add(new ColorFeature(imageId, cellId, j, frequencies[j]));
					}
				}
			}
		}
		return features;
	}
	
	
	public static int[] getFeatures(byte[] allColors) {
		int[] frequencies = new int[16];
		for (int i = 0; i < allColors.length/3; i++) {
			CColorInfo currentColor = new CColorInfo(allColors[3*i], allColors[3*i+1], allColors[3*i+2]);
			int medianCuts = 4;
			int component = -1;

			int nextIndex = 0;
			for (int p = 0; p < medianCuts; p++) {
				int levelIndex = nextIndex - (int)Math.pow(2, p) + 1;
				int nextLevelIndex = -1;
				// round robin the components for every iterations
				component = (component + 1) % 3;
				if (specification.get(nextIndex).getValue(component) > currentColor.getValue(component)) {
					nextLevelIndex = levelIndex * 2;
				} else {
					nextLevelIndex = levelIndex * 2 + 1;
				}
				nextIndex = (int)Math.pow(2, p + 1) - 1 + nextLevelIndex;
			}
			int binIndex = nextIndex - (int)Math.pow(2, 4) + 1;
			frequencies[binIndex] = frequencies[binIndex] + 1;
		}
		return frequencies;
	}

	public static void writeHistogramSpec(List<CColorInfo> spec) {
			FileWriter fstream;
			try {
				fstream = new FileWriter(SPEC_FILENAME);
				BufferedWriter out = new BufferedWriter(fstream);
				for (int i = 0; i < 4; i++) {
					String level = "";
					for (int j = 0; j < (int)Math.pow(2, i); j++) {
						int padding = (int) Math.pow(2, i) - 1;
						level += spec.get(padding + j).getFormatterString() + " ";
					}
					out.write(level + "\n");
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static List<CColorInfo> getMedianCutSpec(byte[] allColors) {
		// our data structure to store the color clusters
		// also store the pixel location along with color info so we can restore em back
		List<List<CColorInfo>> colorClusters = new ArrayList<List<CColorInfo>>();

		// put the initial cluster in our clusters set
		List<CColorInfo> colors = new ArrayList<CColorInfo>();
		for (int i = 0; i < allColors.length/3; i++) {
			CColorInfo color = new CColorInfo(allColors[3*i], allColors[3*i + 1], allColors[3*i + 2]);
			colors.add(color);
		}
		colorClusters.add(colors);

		int medianCuts = 4;
		int component = -1;

		List<CColorInfo> medians = new ArrayList<CColorInfo>();

		for (int i = 0; i < medianCuts; i++) {
			// round robin the components for every iterations
			component = (component + 1) % 3;
			List<List<CColorInfo>> toCutSets = new ArrayList<List<CColorInfo>>();
			for (List<CColorInfo> colorList : colorClusters) {
				toCutSets.add(colorList);
			}
			colorClusters.clear();
			for (List<CColorInfo> colorList : toCutSets) {
				// sort the set based on component
				List<CColorInfo> sortedColors = sortColorSet(colorList, component);
				// split it into two sets at median
				int medianIndex = sortedColors.size()/2;
				medians.add(sortedColors.get(medianIndex));
				List<CColorInfo> firstHalf = new ArrayList<CColorInfo>();
				List<CColorInfo> secondHalf = new ArrayList<CColorInfo>();
				for (int j = 0; j < sortedColors.size(); j++) {
					CColorInfo pixelInfo = sortedColors.get(j);
					if (j <= medianIndex) {
						firstHalf.add(pixelInfo);
					} else {
						secondHalf.add(pixelInfo);
					}
				}
				// put them back into our original collection
				colorClusters.add(firstHalf);
				colorClusters.add(secondHalf);
			}
		}
		specification = medians;
		writeHistogramSpec(specification);
		return medians;
	}

	/**
	 * Sorts the given set of colors based on one of the three components.
	 * The component index is 0 - Red, 1 - Green, 2 - Blue
	 * 
	 * @param colorsList The color list; can be in any order
	 * @param component The component index
	 * @return The ordered list of colors
	 */
	public static List<CColorInfo> sortColorSet(List<CColorInfo> colorsList, final int component) {
		Collections.sort(colorsList, new Comparator<CColorInfo>() {
			public int compare(CColorInfo o1, CColorInfo o2) {
				long o1Component = -1;
				long o2Component = -1;
				switch (component) {
					case 0:
						o1Component = o1.getR();
						o2Component = o2.getR();
						break;
					case 1:
						o1Component = o1.getG();
						o2Component = o2.getG();
						break;
					case 2:
						o1Component = o1.getB();
						o2Component = o2.getB();
						break;
					default:
						throw new IllegalStateException("Cannot compare: component index not in [0, 1, 2]");
				}
				return (o1Component > o2Component ? -1 : (o1Component == o2Component ? 0 : 1));
			}
		});
		return colorsList;
	}

}
