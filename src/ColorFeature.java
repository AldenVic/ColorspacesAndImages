
public class ColorFeature {

	private String _imageId;
	private int _cellId;
	private int _binId;
	private int _frequency;

	public ColorFeature() {
	}

	public ColorFeature(String imageId, int cellId, int binId, int frequency) {
		_imageId = imageId;
		_cellId = cellId;
		_binId = binId;
		_frequency = frequency;
	}

	public String getImageId() {
		return _imageId;
	}

	public void setImageId(String imageId) {
		_imageId = imageId;
	}

	public int getCellId() {
		return _cellId;
	}

	public void setCellId(int cellId) {
		_cellId = cellId;
	}

	public int getBinId() {
		return _binId;
	}

	public void setBinId(int binId) {
		_binId = binId;
	}

	public int getFrequency() {
		return _frequency;
	}

	public void setFrequency(int frequency) {
		_frequency = frequency;
	}

}
