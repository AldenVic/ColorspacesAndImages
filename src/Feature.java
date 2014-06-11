
public class Feature {

	private double[] _values;
	private String _imageId;
	private int _cellIndex;

	public Feature() {
	}

	public Feature(double[] values, String imageId, int cellIndex) {
		_values = values;
		_imageId = imageId;
		_cellIndex = cellIndex;
	}

	public double[] getValues() {
		return _values;
	}

	public String getImageId() {
		return _imageId;
	}

	public int getCellIndex() {
		return _cellIndex;
	}

}
