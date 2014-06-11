
public class CPixelInfoDouble {

	private final int _row;
	private final int _column;

	private final double _red;
	private final double _green;
	private final double _blue;

	public CPixelInfoDouble(int row, int column, double red, double green,
			double blue) {
		_row = row;
		_column = column;
		_red = red;
		_green = green;
		_blue = blue;
	}

	public int getRow() {
		return _row;
	}

	public int getColumn() {
		return _column;
	}

	public double getRed() {
		return _red;
	}

	public double getGreen() {
		return _green;
	}

	public double getBlue() {
		return _blue;
	}

}
