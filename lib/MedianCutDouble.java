import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import magick.ColorspaceType;
import magick.MagickException;
import magick.MagickImage;


public class MedianCutDouble {

	public static void main(String[] args) {
		int a = 255;
		int b = a << 24;
		System.out.println(b);
	}

	private static final MedianCut INSTANCE = new MedianCut();

	public MagickImage medianCut(MagickImage image, int colorInstances,
			int colorspace) throws MagickException {
		// check for obvious errors
		if (INSTANCE.getColorInstances(image) < colorInstances) {
			throw new IllegalStateException("colorInstances count cannot be greater" +
					" than the total number of colors");
		}

		int width = image.getDimension().width;
		int height = image.getDimension().height;

		int[] rgbPixels = new int[width * height * 4];
		image.dispatchImage(0, 0, width, height, "RGBA", rgbPixels);

		double[] xyzPixels = transformFromRGB(rgbPixels, colorspace);
		
		return INSTANCE.getImageFromArray(image,
				transformToRGB(medianCut(image, colorInstances, xyzPixels), colorspace));
	}

	public double[] medianCut(MagickImage image, int colorInstances,
			double[] pixels) throws MagickException {
		// check for obvious errors
		if (INSTANCE.getColorInstances(image) < colorInstances) {
			throw new IllegalStateException("colorInstances count cannot be greater" +
					" than the total number of colors");
		}

		int width = image.getDimension().width;
		int height = image.getDimension().height;

		// our data structure to store the color clusters
		// also store the pixel location along with color info so we can restore 'em back
		Set<Set<CPixelInfoDouble>> colorClusters = new HashSet<Set<CPixelInfoDouble>>();

		// put the initial cluster in our clusters set
		Set<CPixelInfoDouble> colors = new HashSet<CPixelInfoDouble>();
		for(int i = 0; i < height; i++) {
			for (int j = 0; j< width; j++) {
				int index = (i * width * 4)  + (j * 4);
				colors.add(new CPixelInfoDouble(i, j, pixels[index], pixels[index + 1], pixels[index + 2]));
			}
		}
		colorClusters.add(colors);

		int medianCuts = (int) Math.ceil(Math.log(colorInstances)/Math.log(2));
		int component = -1;

		for (int i = 0; i < medianCuts; i++) {
			// round robin the components for every iterations
			component = (component + 1) % 3;
			List<Set<CPixelInfoDouble>> toCutSets = new ArrayList<Set<CPixelInfoDouble>>();
			for (Set<CPixelInfoDouble> set : colorClusters) {
				toCutSets.add(set);
			}
			colorClusters.clear();
			for (Set<CPixelInfoDouble> set : toCutSets) {
				// sort the set based on component
				List<CPixelInfoDouble> sortedColors = sortColorSetDoubles(set, component);
				// split it into two sets at median
				int medianIndex = sortedColors.size()/2;
				List<CPixelInfoDouble> firstHalf = new ArrayList<CPixelInfoDouble>();
				List<CPixelInfoDouble> secondHalf = new ArrayList<CPixelInfoDouble>();
				for (int j = 0; j < sortedColors.size(); j++) {
					CPixelInfoDouble pixelInfo = sortedColors.get(j);
					if (j <= medianIndex) {
						firstHalf.add(pixelInfo);
					} else {
						secondHalf.add(pixelInfo);
					}
				}
				// put them back into our original collection
				colorClusters.add(new HashSet<CPixelInfoDouble>(firstHalf));
				colorClusters.add(new HashSet<CPixelInfoDouble>(secondHalf));
			}
		}
		return flattenClustersDoubles(colorClusters, image);
	}

	/**
	 * Given a set of color clusters replaces each of the clusters by their average color and
	 * flattens the cluster into a #constituteImage friendly array
	 * 
	 * @param colorClusters The set of color clusters
	 * @param oldImage The old image, to copy the dimension information
	 * @return The flattened array
	 * @throws MagickException The exception
	 */
	private double[] flattenClustersDoubles(Set<Set<CPixelInfoDouble>> colorClusters,
			MagickImage oldImage) throws MagickException {
		double[] pixels = new double[oldImage.getDimension().width * oldImage.getDimension().height * 4];
		for (Set<CPixelInfoDouble> colorCluster : colorClusters) {
			CPixelInfoDouble averageColor = getModeColorDouble(colorCluster);
			for (CPixelInfoDouble pixel : colorCluster) {
				int oldOpacity = oldImage.getOnePixel(pixel.getRow(), pixel.getColumn()).getOpacity();
				int index = (pixel.getRow() * oldImage.getDimension().width * 4)  + (pixel.getColumn() * 4);
				pixels[index++] = averageColor.getRed();
				pixels[index++] = averageColor.getGreen();
				pixels[index++] = averageColor.getBlue();
				pixels[index] = oldOpacity;
			}
		}
		return pixels;
	}

	/**
	 * Given a set of colors returns the color that is most repeated
	 * 
	 * @param colors The set of colors
	 * @return The most repeated color
	 */
	private CPixelInfoDouble getModeColorDouble(
			Set<CPixelInfoDouble> colors) {
		// TODO Auto-generated method stub
		Map<CTripletDouble, Integer> colorSet = new HashMap<CTripletDouble, Integer>();
		for (CPixelInfoDouble color : colors) {
			Integer count = colorSet.get(new CTripletDouble(color.getRed(), color.getGreen(), color.getBlue()));
			if (count == null) {
				colorSet.put(new CTripletDouble(color.getRed(), color.getGreen(), color.getBlue()), 1);
			} else {
				colorSet.put(new CTripletDouble(color.getRed(), color.getGreen(), color.getBlue()), count + 1);
			}
		}
		CTripletDouble max = null;
		for (CTripletDouble triplet : colorSet.keySet()) {
			if (max == null) {
				max = triplet;
			}
			if (colorSet.get(triplet) > colorSet.get(max)) {
				max = triplet;
			}
		}
		return new CPixelInfoDouble(-1, -1, max.getA(), max.getB(), max.getC());
	}

	public double[] transformFromRGB(int[] pixels, int colorspace) {
		double[] transformedPixels = new double[pixels.length];
		for (int i = 0; i < pixels.length; i += 4) {
			pixels[i] = pixels[i] >>> 16;
			pixels[i + 1] = pixels[i + 1] >>> 16;
			pixels[i + 2] = pixels[i + 2] >>> 16;
			transformedPixels[i+3] = pixels[i+3];
			double[] transformedPixel;
			switch (colorspace) {
			case ColorspaceType.XYZColorspace:
				transformedPixel = Utilities_01.rgbToXYZ(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.LABColorspace:
				transformedPixel = Utilities_01.rgbToLAB(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.YUVColorspace:
				transformedPixel = Utilities_01.rgbToYUV(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.YCbCrColorspace:
				transformedPixel = Utilities_01.rgbToYCbCr(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.YIQColorspace:
				transformedPixel = Utilities_01.rgbToYIQ(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.HSLColorspace:
				transformedPixel = Utilities_01.rgbToHSL(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			default:
				transformedPixel = new double[3];
				break;
			}
			transformedPixels[i] = transformedPixel[0];
			transformedPixels[i+1] = transformedPixel[1];
			transformedPixels[i+2] = transformedPixel[2];
		}
		return transformedPixels;
	}

	public int[] transformToRGB(double[] pixels, int colorspace) {
		int[] transformedPixels = new int[pixels.length];
		for (int i = 0; i < pixels.length; i += 4) {
			transformedPixels[i+3] = (int)pixels[i+3];
			double[] transformedPixel = new double[3];
			switch (colorspace) {
			case ColorspaceType.XYZColorspace:
				transformedPixel = Utilities_01.xyzToRGB(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.LABColorspace:
				transformedPixel = Utilities_01.labToRGB(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.YUVColorspace:
				//transformedPixel = Utilities_01.y(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.YCbCrColorspace:
				//transformedPixel = Utilities_01.y(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.YIQColorspace:
				//transformedPixel = Utilities_01.rgbToYIQ(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			case ColorspaceType.HSLColorspace:
				transformedPixel = Utilities_01.hslToRGB(pixels[i], pixels[i + 1], pixels[i + 2]);
				break;
			default:
				break;
			}
			transformedPixels[i] = (int) transformedPixel[0];
			transformedPixels[i+1] = (int) transformedPixel[1];
			transformedPixels[i+2] = (int) transformedPixel[2];
			transformedPixels[i] = transformedPixels[i] << 16;
			transformedPixels[i+1] = transformedPixels[i+1] << 16;
			transformedPixels[i+2] = transformedPixels[i+2] << 16;
		}
		return transformedPixels;
	}


	/**
	 * Sorts the given set of colors based on one of the three components.
	 * The component index is 0 - Red, 1 - Green, 2 - Blue
	 * This function is to be used, when the components have double values.
	 * 
	 * @param colors The color set
	 * @param component The component index
	 * @return The ordered list of colors
	 */
	public List<CPixelInfoDouble> sortColorSetDoubles(Set<CPixelInfoDouble> colors, final int component) {
		List<CPixelInfoDouble> colorsList = new ArrayList<CPixelInfoDouble>();
		for (CPixelInfoDouble pixelPacket : colors) {
			colorsList.add(pixelPacket);
		}
		Collections.sort(colorsList, new Comparator<CPixelInfoDouble>() {
			public int compare(CPixelInfoDouble o1, CPixelInfoDouble o2) {
				double o1Component = -1;
				double o2Component = -1;
				switch (component) {
					case 0:
						o1Component = o1.getRed();
						o2Component = o2.getRed();
						break;
					case 1:
						o1Component = o1.getGreen();
						o2Component = o2.getGreen();
						break;
					case 2:
						o1Component = o1.getBlue();
						o2Component = o2.getBlue();
						break;
					default:
						throw new IllegalStateException("Component index not in [0, 1, 2]");
				}
				return (o1Component > o2Component ? -1 :(o1Component == o2Component ? 0 : 1));
			}
		});
		return colorsList;
	}

}
