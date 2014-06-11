

import magick.PixelPacket;

public class CPixelInfo {

	private final int _row;
	private final int _column;

	private final long _red;
	private final long _green;
	private final long _blue;

	public CPixelInfo(int row, int column, PixelPacket pixelPacket) {
		_row = row;
		_column = column;
		_red = pixelPacket.getRed();
		_green = pixelPacket.getGreen();
		_blue = pixelPacket.getBlue();
	}

	public CPixelInfo(int row, int column, long red, long green, long blue) {
		_row = row;
		_column = column;
		_red = red;
		_green = green;
		_blue = blue;
	}

	public CPixelInfo(int row, int column, int red, int green, int blue) {
		_row = row;
		_column = column;
		_red = red & 0xFFFFFFFFL;
		_green = green & 0xFFFFFFFFL;
		_blue = blue & 0xFFFFFFFFL;
	}
	
	public CPixelInfo(int row, int column, double red, double green, double blue) {
		_row = row;
		_column = column;
		_red = (long)red;
		_green = (long)green;
		_blue = (long)blue;
	}
	
	public CPixelInfo(int row, int column, byte red, byte green, byte blue) {
	_row = row;
	_column = column;
	_red = red & 0xFF;
	_green = green & 0xFF;
	_blue = blue & 0xFF;
	}
	public int getRow() {
		return _row;
	}

	public int getColumn() {
		return _column;
	}

	public long getRed() {
		return _red;
	}

	public long getGreen() {
		return _green;
	}

	public long getBlue() {
		return _blue;
	}

	public int getRedInt() {
		return (int) _red;
	}

	public int getGreenInt() {
		return (int) _green;
	}

	public int getBlueInt() {
		return (int) _blue;
	}

	@Override
	public String toString() {
		return "CPixelInfo [_row=" + _row + ", _column=" + _column + ", _red="
				+ _red + ", _green=" + _green + ", _blue=" + _blue + "]";
	}

}
