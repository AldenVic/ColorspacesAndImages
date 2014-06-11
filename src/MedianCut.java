

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;
import magick.util.MagickWindow;

public class MedianCut {

	public static void main(String[] args) {
		MedianCut m = new MedianCut();
		String fileName = "/home/joker_hs/Tulips.jpg";
		//"/home/joker_hs/Work/mis/blue_gradient.jpg";
		try {
			ImageInfo imageInfo = new ImageInfo(fileName);
			MagickImage image = new MagickImage(imageInfo);
			int colorInstances = 2;
			System.out.println("Dimensions: " + image.getDimension().width + " x " + image.getDimension().height);
			System.out.println("Original color instances: " + m.getColorInstances(image));

			int width = image.getDimension().width;
			int height = image.getDimension().height;
			int[] pixels = new int[width * height * 4];
			image.dispatchImage(0, 0, width, height, "RGBA", pixels);

			/*for (int i = 0; i < pixels.length; i += 4) {
				double[] lab = Utilities.rgbToLAB(pixels[i], pixels[i + 1], pixels[i + 2]);
				pixels[i] = lab[0];
				pixels[i + 1] = lab[1];
				pixels[i + 2] = lab[2];
			}/**/

			MagickImage newImage = m.getImageFromArray(image,
					m.medianCut(image, colorInstances, pixels));
			
			//MagickImage newImage = m.reduceColorInstances(image, colorInstances);
			newImage.setFileName("/home/joker_hs/test.jpg");
			newImage.writeImage(new ImageInfo());
			System.out.println("Reduced color instances: " + m.getColorInstances(newImage));
			MagickWindow window = new MagickWindow(new MagickImage(new ImageInfo("/home/joker_hs/test.jpg")));
            window.setTitle("Compressed Image");
            window.setSize(800, 600);
            window.setVisible(true);/**/
		} catch (MagickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a MagickImage counts the number of color instances (8-bit precision)
	 * 
	 * @param image The image
	 * @return The count
	 * @throws MagickException The exception
	 */
	public int getColorInstances(MagickImage image) throws MagickException {
		int[] pixels = new int[image.getDimension().width * image.getDimension().height * 4];
		image.dispatchImage(0, 0, image.getDimension().width, image.getDimension().height, "RGBA", pixels);
		return getColorInstances(pixels, 4);
	}

	/**
	 * Counts the color instances from a given array of values, typically the
	 * output of dispatchImage.
	 * 
	 * @param pixels The array of pixels containing color values
	 * @param windowWidth The no. of spaces each pixel takes to represent its color
	 * @return The unique colors count
	 */
	public int getColorInstances(int[] pixels, int windowWidth) {
		Set<CTriplet> colors = new HashSet<CTriplet>();
		for (int i = 0; i < pixels.length; i = i + windowWidth) {
			CTriplet triplet = new CTriplet(pixels[i], pixels[i + 1], pixels[i + 2]);
			colors.add(triplet);
		}
		return colors.size();
	}

	/**
	 * Given a MagickImage reduces the number of color instances using the Median-cut algorithm.
	 * Because of the nature of the median cut algorithm, the reduced number of colors are the
	 * closest power of 2.
	 * 
	 * @param image The image
	 * @param colorInstances Required number of color instances.
	 * @return
	 */
	public MagickImage reduceColorInstances(MagickImage image, int colorInstances) throws MagickException {
		int width = image.getDimension().width;
		int height = image.getDimension().height;

		// gather colors from the original image
		int[] pixels = new int[width * height * 4];
		image.dispatchImage(0, 0, width, height, "RGBA", pixels);

		return getImageFromArray(image, medianCut(image, colorInstances, pixels));
	}

	/**
	 * Given a MagickImage and a flattened set of its colors in whatever color space
	 * this function will cluster the colors using median cut algorithm and replaces
	 * the colors of all the pixels in the cluster with their average colors and returns
	 * a flattened list back which is also in the same color space. The final reduced number
	 * of colors is dependent of two factors, first, we run the median cut algorithm for until
	 * we reach the cluster count equal to the power of two that's closest to given color
	 * instances, second, if the averages of these individual clusters turn out to be equal, then
	 * the actual number of color instances in the reduced image, will differ with the number
	 * of clusters created by the median-cut algorithm
	 * 
	 * @param image The image
	 * @param colorInstances The number of color instances required
	 * @param pixels The flattened array containing colors in any color space
	 * @return The reduced set of colors as a flattened array
	 * @throws MagickException The exception
	 */
	public int[] medianCut(MagickImage image, int colorInstances,
			int[] pixels) throws MagickException {
		// check for obvious errors
		if (getColorInstances(image) < colorInstances) {
			throw new IllegalStateException("colorInstances count cannot be greater" +
					" than the total number of colors");
		}

		int width = image.getDimension().width;
		int height = image.getDimension().height;

		// our data structure to store the color clusters
		// also store the pixel location along with color info so we can restore em back
		Set<Set<CPixelInfo>> colorClusters = new HashSet<Set<CPixelInfo>>();

		// put the initial cluster in our clusters set
		Set<CPixelInfo> colors = new HashSet<CPixelInfo>();
		for(int i = 0; i < height; i++) {
			for (int j = 0; j< width; j++) {
				int index = (i * width * 4)  + (j * 4);
				colors.add(new CPixelInfo(i, j, pixels[index], pixels[index + 1], pixels[index + 2]));
			}
		}
		colorClusters.add(colors);

		int medianCuts = (int) Math.ceil(Math.log(colorInstances)/Math.log(2));
		int component = -1;

		for (int i = 0; i < medianCuts; i++) {
			// round robin the components for every iterations
			component = (component + 1) % 3;
			List<Set<CPixelInfo>> toCutSets = new ArrayList<Set<CPixelInfo>>();
			for (Set<CPixelInfo> set : colorClusters) {
				toCutSets.add(set);
			}
			colorClusters.clear();
			for (Set<CPixelInfo> set : toCutSets) {
				// sort the set based on component
				List<CPixelInfo> sortedColors = sortColorSet(set, component);
				// split it into two sets at median
				int medianIndex = sortedColors.size()/2;
				List<CPixelInfo> firstHalf = new ArrayList<CPixelInfo>();
				List<CPixelInfo> secondHalf = new ArrayList<CPixelInfo>();
				for (int j = 0; j < sortedColors.size(); j++) {
					CPixelInfo pixelInfo = sortedColors.get(j);
					if (j <= medianIndex) {
						firstHalf.add(pixelInfo);
					} else {
						secondHalf.add(pixelInfo);
					}
				}
				// put them back into our original collection
				colorClusters.add(new HashSet<CPixelInfo>(firstHalf));
				colorClusters.add(new HashSet<CPixelInfo>(secondHalf));
			}
		}
		return flattenClusters(colorClusters, image);
	}

	/**
	 * Constructs a MagickImage from given set of color clusters by replacing colors of all the 
	 * pixels in the cluster by their average color.
	 *  
	 * @param colorClusters The set of color clusters
	 * @param oldImage The old image, to copy the dimension information
	 * @return The reconstructed new MagickImage
	 * @throws MagickException The exception
	 */
	public MagickImage getImageFromClusters(Set<Set<CPixelInfo>> colorClusters,
			MagickImage oldImage) throws MagickException {
		int[] pixels = flattenClusters(colorClusters, oldImage);
		return getImageFromArray(oldImage, pixels);
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
	public int[] flattenClusters(Set<Set<CPixelInfo>> colorClusters,
			MagickImage oldImage) throws MagickException {
		int[] pixels = new int[oldImage.getDimension().width * oldImage.getDimension().height * 4];
		for (Set<CPixelInfo> colorCluster : colorClusters) {
			CPixelInfo averageColor = getModeColor(colorCluster);
			for (CPixelInfo pixel : colorCluster) {
				int oldOpacity = oldImage.getOnePixel(pixel.getRow(), pixel.getColumn()).getOpacity();
				int index = (pixel.getRow() * oldImage.getDimension().width * 4)  + (pixel.getColumn() * 4);
				pixels[index++] = averageColor.getRedInt();
				pixels[index++] = averageColor.getGreenInt();
				pixels[index++] = averageColor.getBlueInt();
				pixels[index] = oldOpacity;
			}
		}
	//	System.out.println((pixels[0]>>>24) + ", " + (pixels[1]>>>24) + ", " + (pixels[2]>>>24));
		return pixels;
	}

	/**
	 * Creates a new MagickImage from a given set of flattened RGBA values
	 * 
	 * @param image The old image to copy the dimensions
	 * @param pixels The pixel values as a flattened array
	 * @return The new MagickImage object
	 * @throws MagickException
	 */
	public MagickImage getImageFromArray(MagickImage image, int[] pixels) throws MagickException {
		int width = image.getDimension().width;
		int height = image.getDimension().height;

		MagickImage newImage = new MagickImage();
		newImage.constituteImage(width, height, "RGBA", pixels);
		return newImage;
	}

	/**
	 * Given a set of colors returns the color that is most repeated
	 * 
	 * @param colors The set of colors
	 * @return The most repeated color
	 */
	public CPixelInfo getModeColor(Set<CPixelInfo> colors) {
		Map<CTriplet, Integer> colorSet = new HashMap<CTriplet, Integer>();
		for (CPixelInfo color : colors) {
			Integer count = colorSet.get(new CTriplet(color.getRedInt(), color.getGreenInt(), color.getBlueInt()));
			if (count == null) {
				colorSet.put(new CTriplet(color.getRedInt(), color.getGreenInt(), color.getBlueInt()), 1);
			} else {
				colorSet.put(new CTriplet(color.getRedInt(), color.getGreenInt(), color.getBlueInt()), count + 1);
			}
		}
		CTriplet max = null;
		for (CTriplet triplet : colorSet.keySet()) {
			if (max == null) {
				max = triplet;
			}
			if (colorSet.get(triplet) > colorSet.get(max)) {
				max = triplet;
			}
		}
		return new CPixelInfo(-1, -1, max.getA(), max.getB(), max.getC());
	}

	/**
	 * Given a set of colors returns the average of all those colors
	 *  
	 * @param colors The set of colors
	 * @return The average color instance
	 */
	public CPixelInfo getAverageColor(Set<CPixelInfo> colors) {
		double firstComponent = 0;
		double secondComponent = 0;
		double thirdComponent = 0;

		for (CPixelInfo color : colors) {
			firstComponent += color.getRed();
			secondComponent += color.getGreen();
			thirdComponent += color.getBlue();
		}

		firstComponent /= colors.size();
		secondComponent /= colors.size();
		thirdComponent /= colors.size();
		CPixelInfo averageColor = new CPixelInfo(-1, -1,
				new PixelPacket((int)firstComponent, (int)secondComponent, (int)thirdComponent, 0));
		return averageColor;
	}

	/**
	 * Sorts the given set of colors based on one of the three components.
	 * The component index is 0 - Red, 1 - Green, 2 - Blue
	 * 
	 * @param colors The color set
	 * @param component The component index
	 * @return The ordered list of colors
	 */
	public List<CPixelInfo> sortColorSet(Set<CPixelInfo> colors, final int component) {
		List<CPixelInfo> colorsList = new ArrayList<CPixelInfo>();
		for (CPixelInfo pixelPacket : colors) {
			colorsList.add(pixelPacket);
		}
		Collections.sort(colorsList, new Comparator<CPixelInfo>() {
			public int compare(CPixelInfo o1, CPixelInfo o2) {
				long o1Component = -1;
				long o2Component = -1;
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
