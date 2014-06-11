/*
 * LZW
 * 10/20/2012
 */

import java.util.*;

public class LZW {

	private static int clength = 8;
	private static int bsize = 0;

	/*
	 * compression
	 */
	public static List<Integer> compress(byte[] pixels) {

		int size = (int) Math.pow(2, clength);

		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		List<Integer> output = new ArrayList<Integer>();

		for (int i = 0; i < size; i++)
			dictionary.put(String.valueOf((char) i), i);

		String temp = new String();

		for (byte b : pixels) {

			char c = (char) (b & (0xff));

			String entry = temp + c;

			if (dictionary.containsKey(entry)) {
				temp = entry;
			} else {
				output.add(dictionary.get(temp));

				dictionary.put(entry, size++);
				temp = String.valueOf(c);
			}
		}

		if (!temp.equals(null))
			output.add(dictionary.get(temp));

		return output;
	}
	
	public static int getBitlength(List<Integer> dict)
	{
		int output = 8;
		if(dict == null || dict.size() == 0)
			return -1;
		int max = dict.get(0);
		for(int z = 0; z < dict.size(); z++)
		{
			if(dict.get(z) > max)
				max = dict.get(z);
		}
		output = 1;
		while(Math.pow(2, output) < max)
			output++;
		return output;
	}
	
	public static String[] convertToStream(List<Integer> dict, int bitlength)
	{
		int current;
		String temp;
		String othertemp;
		String[] output = new String[dict.size()];
		for(int i = 0; i < dict.size(); i++)
		{
			current = dict.get(i);
			temp = "";
			for(int j = 0; j < bitlength; j++)
			{
				temp = temp.concat("0");
			}
			for(int k = bitlength - 1; k >= 0; k--)
			{
				if(Math.pow(2, k) <= current)
				{
					temp = temp.substring(0, bitlength - 1 - k) + "1" + temp.substring(bitlength - 1 - k + 1);
					current -= (int)Math.pow(2, k);
				}
			}
			output[i] = temp;
		}
		return output;
	}
	
	public static List<Integer> convertToInts(String[] dict)
	{
		List<Integer> output = new ArrayList<Integer>();
		int temp;
		int bitsize = dict[0].length();
		for(int i = 0; i < dict.length; i++)
		{
			temp = 0;
			for(int j = 0; j < bitsize; j++)
			{
				if(dict[i].charAt(bitsize - 1 - j) == '1')
				{
					temp += (int)Math.pow(2, j);
				}
			}
			output.add(temp);
		}
		return output;
	}

	public static void writeOut(byte[] pixels, int length, int colorspace) {
		clength = length;
		List<Integer> compressed = compress(pixels);
		bsize = getBitlength(compressed);
		String[] output = convertToStream(compressed, bsize);
		
		try {
			BinaryOut out;
			if(colorspace == 0)
				out = new BinaryOut(Utilities.OUT_OBJECT_RGB_FILENAME);
			else
				out = new BinaryOut(Utilities.OUT_OBJECT_YBR_FILENAME);
			// BufferedWriter out = new BufferedWriter(fstream);
			//for (int i = 0; i < compressed.size(); i++) {
			//	out.write(compressed.get(i));
			//}
			for(int i = 0; i < output.length; i++)
			{
				for(int j = 0; j < bsize; j++)
					if(output[i].charAt(j) == '1')
						out.write(true);
					else
						out.write(false);
			}
			// / out.close();
			out.flush();
		} catch (Exception ex) {

		}
	}

	public static byte[] readIn(int colorspace) {
		byte[] out;
		String temp;
		// String getitall;
		try {
			List<Integer> buffer;
			ArrayList<String> sbuffer = new ArrayList<String>();
			String [] outs;
			// FileReader fstream = new FileReader("out.bin");
			// BufferedReader in = new BufferedReader(fstream);
			BinaryIn in;
			if(colorspace == 0)
				in = new BinaryIn(Utilities.OUT_OBJECT_RGB_FILENAME);
			else
				in = new BinaryIn(Utilities.OUT_OBJECT_YBR_FILENAME);
			// getitall = in.readLine();
			while (!in.isEmpty()) {
				temp = "";
			//	buffer.add(in.readInt());
				for(int i = 0; i < bsize; i++)
				{
					if(!in.isEmpty() && in.readBoolean() == true)
						temp = temp.concat("1");
					else
						temp = temp.concat("0");				
				}
				sbuffer.add(temp);
			}
			outs = new String[sbuffer.size()];
			for(int i = 0; i < outs.length; i++)
			{
				outs[i] = sbuffer.get(i);
			}
			buffer = convertToInts(outs);
			out = new byte[buffer.size()];
			out = decompress(buffer);
		} catch (Exception ex) {
			return null;
		}
		return out;
	}

	/*
	 * decompression
	 */
	public static byte[] decompress(List<Integer> compressed) {

		int size = (int) Math.pow(2, clength);

		Map<Integer, String> dictionary = new HashMap<Integer, String>();

		for (int i = 0; i < size; i++)
			dictionary.put(i, String.valueOf((char) i));

		String temp = String.valueOf((char) (int) compressed.remove(0));

		List<Byte> out = new ArrayList<Byte>();
		out.add((byte) temp.charAt(0));

		for (int k : compressed) {

			String entry = null;

			if (dictionary.containsKey(k))
				entry = dictionary.get(k);
			else if (k == size)
				entry = temp + temp.charAt(0);

			decodeEntry(out, entry);

			dictionary.put(size++, temp + entry.charAt(0));

			temp = entry;
		}

		// redundant copy
		Byte[] r = new Byte[out.size()];
		out.toArray(r);

		byte[] b = new byte[r.length];

		for (int i = 0; i < b.length; i++) {
			b[i] = r[i];
		}

		return b;
	}

	private static void decodeEntry(List<Byte> l, String s) {
		for (char c : s.toCharArray()) {
			l.add((byte) c);
		}
	}

	public static void main(String[] args) {

		byte[] b = new byte[256 * 8];

		for (int i = 0; i < b.length; i++)
			b[i] = (byte) i;

		List<Integer> compressed = compress(b);
		// System.out.println(compressed);

		byte[] decompressed = decompress(compressed);

		/*
		 * for(int i = 0; i < decompressed.length; i++) {
		 * System.out.print((short) ((short) decompressed[i] & 0xff) + ", "); }
		 */

		System.out.println("original entries: " + b.length
				+ "\ncompressed entries: " + compressed.size()
				+ " \ndecompressed entries : " + decompressed.length);
	}
}