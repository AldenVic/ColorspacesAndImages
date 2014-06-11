public class DCTFeature {

	private String _imageId;
	private int _cellId;
	private int _channelId;
	private int _binId;
	private double _value;

	public DCTFeature() {
	}

	public DCTFeature(String imageId, int cellId, int channelId, int binId, double value) {
		_imageId = imageId;
		_cellId = cellId;
		_channelId = channelId;
		_binId = binId;
		_value = value;
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

	public int getChannelId() {
		return _channelId;
	}

	public void setChannelId(int channelId) {
		_channelId = channelId;
	}

	public int getBinId() {
		return _binId;
	}

	public void setBinId(int binId) {
		_binId = binId;
	}

	public double getValue() {
		return _value;
	}

	public void setValue(double value) {
		_value = value;
	}

}
