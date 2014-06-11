import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class BlockMatching {

	public static final int N = 10;
	private static final int CELL_SIZE = 8;
	private static final int GRID_SIZE = 3;

	public static Map<Integer, Double> getTopNSimilar(List<Feature> targetGrid, List<Feature> features, int widht, int height) {
		int asd = 1;
		List<List<Feature>> generateCandidates = exGenerateCandidates(features, widht, height);
		Map<Double, List<Feature>> topNClosest = findTopNClosest(generateCandidates, targetGrid, N);
		Map<Integer, Double> cellIndexSimilarityMap = new HashMap<Integer, Double>();
		for (Double similarity : topNClosest.keySet()) {
			int cellIndex = topNClosest.get(similarity).get(0).getCellIndex();
			cellIndexSimilarityMap.put(cellIndex, similarity);
		}
		return cellIndexSimilarityMap;
	}

	public static List<List<Feature>> exGenerateCandidates(List<Feature> features, int width, int height) {
		width /= CELL_SIZE;
		height /= CELL_SIZE;
		List<List<Feature>> generatedCandidates = new ArrayList<List<Feature>>();
		for (int j = 0; j <= height - GRID_SIZE; j++) {
			for (int i = 0; i <= width - GRID_SIZE; i++) {
				List<Feature> gridFeatures = new ArrayList<Feature>();
				for (int p = j; p < j + 3; p++) {
					for (int q = i; q < i + 3; q++) {
						gridFeatures.add(features.get(p * width + q));
					}
				}
				generatedCandidates.add(gridFeatures);
			}
		}
		return generatedCandidates;
	}

	public static Map<Double, List<Feature>> findTopNClosest(List<List<Feature>> imageRegions, List<Feature> targetRegion, int n) {
		if (n > imageRegions.size()) {
			throw new IllegalStateException("You can't get more than existing regions");
		}
		SortedMap<Double, List<Feature>> similarityMap = new  TreeMap<Double, List<Feature>>();
		for (List<Feature> grid : imageRegions) {
			similarityMap.put(getGridSimilarity(targetRegion, grid), grid);
		}
		Map<Double, List<Feature>> topN = new HashMap<Double, List<Feature>>();
		for (int i = 0; i < n; i++) {
			for (Double similarityValue : similarityMap.keySet()) {
				topN.put(similarityValue, similarityMap.get(similarityValue));
			}
		}
		return topN;
	}

	public static double getGridSimilarity(List<Feature> grid1, List<Feature> grid2) {
		if (grid1.size() != grid2.size()) {
			throw new IllegalStateException("The lists are not of equal size");
		}
		int size = grid1.size();
		double similarity = 0;
		for (int i = 0; i < size; i++) {
			similarity += getHistogramSimilarity(grid1.get(i), grid2.get(i));
		}
		return similarity/size;
	}

	public static double getHistogramSimilarity(Feature h1, Feature h2) {
		if (h1.getValues().length != h2.getValues().length) {
			throw new IllegalStateException("The histograms do not have equal size");
		}
		int size = h1.getValues().length;
		double similarity = 0;
		for (int i = 0; i < size; i++) {
			double min = h1.getValues()[i] + .0001;
			double max = h2.getValues()[i] + .0001;
			if (Math.abs(h1.getValues()[i]) >= Math.abs(h2.getValues()[i])) {
				min = h2.getValues()[i] + .0001;
				max = h1.getValues()[i] + .0001;
			}
			if(min==0.0)
				throw new IllegalStateException("Division by zero.");
			similarity += min/max;
			if(similarity/size > 2)
				throw new IllegalStateException("invalid similarity. similarity = " + similarity + " size = " + size + " result = " + similarity/size);
		}
		return similarity/size;
	}

}
