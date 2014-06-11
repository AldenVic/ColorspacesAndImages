

public class CColorInfo {

	private int _r;
	private int _g;
	private int _b;

	public CColorInfo(int r, int g, int b) {
		_r = r;
		_g = g;
		_b = b;
	}

	public CColorInfo(byte r, byte g, byte b) {
		_r = r & 0xFF;
		_g = g & 0xFF;
		_b = b & 0xFF;
	}

	public int getR() {
		return _r;
	}

	public int getG() {
		return _g;
	}

	public int getB() {
		return _b;
	}

	public int getValue(int component) {
		switch (component) {
		case 0:
			return _r;
		case 1:
			return _g;
		case 2:
			return _b;
		default:
			throw new IllegalStateException("Component should be from [0, 1, 2] only.");
		}
	}

	@Override
	public String toString() {
		return "CColorInfo [_r=" + _r + ", _g=" + _g + ", _b=" + _b + "]";
	}

	public String getFormatterString() {
		return "[" + _r + ", " + _g + ", " + _b + "]";
	}

}
