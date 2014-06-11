import java.util.HashMap;
import java.util.Map;


public class ResolutionReduction {

	/**
	 * Reduce resolution of a given color channel by 1/s for a given parameter
	 * s. Instead of skipping pixels randomly, this algorithm finds least
	 * preferred pixels and skip them. It does so, by splitting the entire 2D
	 * color channel into s x s grids and finds the most repeated color value
	 * within this grid and uses this color value in the final reduced output
	 * channel, skipping all the less relevant pixels. Another way to do the
	 * reduction is to pick the average of the grid instead of mode color.
	 * 
	 * For this program we assume the color channel is passed as a single
	 * flattened array compliant to ImageMagick's, RGBA dispatchImage. The
	 * reduced channel, after reduction is also returned in the same format.
	 * 
	 * Note: If either widht or height is not a multiple of number s, the method
	 * will ignore all the subsequent rows/columns after the perfect multiple
	 * closest to width/height.
	 * 
	 * @param colorChannel The flattened RGBA byte array of the color channel
	 * @param width The width of the 2D color channel
	 * @param height The height of the 2D color channel
	 * @param s The reduction ratio
	 * @param pos The index of the actual channel value within the 4-size window (eg: 0 for R)
	 * @return The reduced color channel in flattened byte array format (single component based on pos)
	 */
	public static byte[] reduceResolution(byte[] colorChannel, int width, int height, int s, int pos) {
		int newWidth = width / s;
		int newHeight = height / s;

		byte[] reducedChannel = new byte[newWidth * newHeight];

		// iterate over each position of reduced array
		// extract the corresponding sxs grid from the original array
		// apply mode or avg to it and use it in the reduced array
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				// the sxs grid corresponding to this cell of reduced array
				byte[] grid = createGrid(colorChannel, width, height, s, i, j);
				reducedChannel[(j * newWidth) + i] = getMode(grid, s, pos);
				// reducedChannel[(j * newWidth) + i] = getAverage(grid, s,
				// pos);
			}
		}

		return reducedChannel;
	}

	/**
	 * Given a flattened RGBA color channel and factor s and positions i,j in
	 * the reduced array, this function will create the sxs grid again in
	 * flattened RGBA format.
	 * 
	 * @param colorChannel The flattened RGBA color channel
	 * @param width The widht of the color channel
	 * @param height The height of the color channel
	 * @param s The reduction factor s
	 * @param i The col position
	 * @param j The row position
	 * @return The s x s grid as flattened RGBA byte array
	 */
	private static byte[] createGrid(byte[] colorChannel, int width, int height, int s, int i, int j) {
		byte[] grid = new byte[s * s * 4];
		for (int k = 0; k < s; k++) { // width
			for (int l = 0; l < s; l++) { // height
				grid[(l * s * 4) + k * 4 + 0] = colorChannel[((j * s + l) * width * 4) + (i * s + k) * 4 + 0];
				grid[(l * s * 4) + k * 4 + 1] = colorChannel[((j * s + l) * width * 4) + (i * s + k) * 4 + 1];
				grid[(l * s * 4) + k * 4 + 2] = colorChannel[((j * s + l) * width * 4) + (i * s + k) * 4 + 2];
				grid[(l * s * 4) + k * 4 + 3] = colorChannel[((j * s + l) * width * 4) + (i * s + k) * 4 + 3];
			}
		}
		return grid;
	}

	/**
	 * Given a s x s grid as flattened RGBA byte array and the index position,
	 * finds the color component values (corresponding to pos) which is most
	 * repeated. All the byte values are converted to unsinged before performing
	 * any operations.
	 * 
	 * @param grid The s x s grid as flattened RGBA byte array
	 * @param s The reduction factor s
	 * @param pos The index of the actual channel value within the 4-size window (eg: 0 for R)
	 * @return The color component value which is repeated the most
	 */
	private static byte getMode(byte[] grid, int s, int pos) {
		// construct frequency map
		Map<Integer, Integer> frequencyMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < grid.length / 4; i++) {
			// make the byte value unsigned
			int value = 0xFF & grid[i * 4 + pos];

			if (frequencyMap.get(value) == null) {
				frequencyMap.put(value, 1);
			} else {
				int newCount = frequencyMap.get(value) + 1;
				frequencyMap.put(value, newCount);
			}
		}
		// find the maximum of it
		int maxFrequency = -1;
		int maxValue = -1;
		for (Integer value : frequencyMap.keySet()) {
			if (frequencyMap.get(value) > maxFrequency) {
				maxFrequency = frequencyMap.get(value);
				maxValue = value;
			}
		}
		return (byte) maxValue;
	}

	/**
	 * Given a s x s grid as flattened RGBA byte array and the index position,
	 * finds the average of color component values (corresponding to pos). All
	 * the byte values are converted to unsinged before performing any
	 * operations.
	 * 
	 * @param grid The s x s grid as flattened RGBA byte array
	 * @param s The reduction factor s
	 * @param pos The index of the actual channel value within the 4-size window (eg: 0 for R)
	 * @return The average color component value
	 */
	private static byte getAverage(byte[] grid, int s, int pos) {
		int sum = 0;
		for (int i = 0; i < grid.length / 4; i++) {
			// make the byte value unsigned before summing
			sum += 0xFF & grid[i * 4 + pos];
		}
		int avg = sum / grid.length;
		return (byte) avg;
	}

}
