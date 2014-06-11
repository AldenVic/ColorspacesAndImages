

public class CTriplet {

	private final int _a;
	private final int _b;
	private final int _c;

	private final int _aLostBits;
	private final int _bLostBits;
	private final int _cLostBits;

	public CTriplet(int a, int b, int c) {
		_aLostBits = a & 0x00FFFFFF;
		_bLostBits = b & 0x00FFFFFF;
		_cLostBits = c & 0x00FFFFFF;
		_a = a >>> 24;
		_b = b >>> 24;
		_c = c >>> 24;
	}

	public int getA() {
		return _a << 24 | _aLostBits;
	}

	public int getB() {
		return _b << 24 | _bLostBits;
	}

	public int getC() {
		return _c << 24 | _cLostBits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _a;
		result = prime * result + _b;
		result = prime * result + _c;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CTriplet other = (CTriplet) obj;
		if (_a != other._a)
			return false;
		if (_b != other._b)
			return false;
		if (_c != other._c)
			return false;
		return true;
	}

}
