/**
 * Entry is a particular value in a bitstream representation.
 * Since we need variable size for our encoding, we use entry
 * to represent each code.
 *
 */
public class Entry {

	private int _symbol;
	private String _code;

	public Entry(int symbol, String code) {
		_symbol = symbol;
		_code = code;
	}

	public Entry(int symbol) {
		_symbol = symbol;
		_code = "";
	}

	public int getSymbol() {
		return _symbol;
	}

	public String getCode() {
		return _code;
	}

	public void appendToCode(char c) {
		_code += c;
	}

	@Override
	public String toString() {
		return "Entry [_symbol=" + _symbol + ", _code=" + _code + "]";
	}

}