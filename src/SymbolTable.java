import java.util.ArrayList;
import java.io.*;

public class SymbolTable {
	
	private ArrayList<Entry> contents;
	
	public SymbolTable()
	{
		contents = new ArrayList<Entry>();
	}
	
	public void addEntry(int s)
	{
		contents.add(new Entry(s));
	}
	
	public Entry getEntry(int s)
	{
		for(int i = 0; i < contents.size(); i++)
			if(contents.get(i).getSymbol() == s)
				return contents.get(i);
		return null;		
	}
	
	public Entry getEntry(String c)
	{
		for(int i = 0; i < contents.size(); i++)
			if(contents.get(i).getCode().equals(c))
				return contents.get(i);
		return null;
	}
	
	public void writeOut(byte[] data, int colorspace)
	{
		try
		{
			BinaryOut out;
			if(colorspace == 0)
				out = new BinaryOut(Utilities.OUT_OBJECT_RGB_FILENAME);
			else
				out = new BinaryOut(Utilities.OUT_OBJECT_YBR_FILENAME);
			//BufferedWriter out = new BufferedWriter(fstream);
			String code;
		  for(int i = 0; i < data.length; i++)
		  {
			  code = getEntry((short) ((short) data[i] & 0xff)).getCode();
			  //out.write(code);
			  for(int j = 0; j < code.length(); j++)
			  {
				  if (code.charAt(j) == '0') out.write(false); else out.write(true);
			  }
		  }
		 /// out.close();
		  out.flush();
		}
		catch(Exception ex)
		{
			
		}
	}
	
	public byte[] readIn(int colorspace)
	{
		byte[] out;
		//String getitall;
		String tryword = "";
		Entry search;
		try
		{
			ArrayList<Byte> buffer = new ArrayList<Byte>();
			//FileReader fstream = new FileReader("out.bin");
			//BufferedReader in = new BufferedReader(fstream);
			BinaryIn in;
			if(colorspace == 0)
				in = new BinaryIn(Utilities.OUT_OBJECT_RGB_FILENAME);
			else
				in = new BinaryIn(Utilities.OUT_OBJECT_YBR_FILENAME);
			//getitall = in.readLine();
			while(!in.isEmpty())
			{
				tryword += in.readBoolean() ? '1' : '0';
				search = getEntry(tryword);
				if(search != null)
				{
					tryword = "";
					buffer.add(new Byte((byte) search.getSymbol()));
				}
			}
			out = new byte[buffer.size()];
			for(int i = 0; i < out.length; i++)
				out[i] = buffer.get(i);
		}
		catch(Exception ex)
		{
			return null;
		}
		return out;
	}
	
	public void updateEntry(int s, char c)
	{
		for(int i = 0; i < contents.size(); i++)
			if(contents.get(i).getSymbol() == s)
				contents.get(i).appendToCode(c);
	}
	
	public void initTable(int[][] freqdist)
	{
		for(int q = 0; q < freqdist.length; q++)
		{
			this.addEntry(freqdist[q][0]);
		}
	}
	
	//SHANNON FANO CODE START//
	
	public void buildTableViaSF(byte[] data)
	{
		int low = 0;
		int [][] freqlist = frequencySort(getFrequencies(data));
		int high = freqlist.length - 1;
		initTable(freqlist);
		shannonFano(freqlist, low, high);
		low = 9001;
	}

	public void shannonFano(int[][] frequencyDistribution, int lowrange, int highrange)
	{
		if(lowrange == highrange) return;
		int totalval = 0;
		int runningtotal = 0;
		int midwaypivot = -1;
		
		for(int g = lowrange; g <= highrange; g++)
		{
			totalval += frequencyDistribution[g][1];
		}
		
		int midway = totalval / 2;
		for(int i = lowrange; i <= highrange; i++)
		{
			if(i == highrange && midwaypivot == -1)
				midwaypivot = highrange;
			if(midwaypivot == -1)
			{
				runningtotal += frequencyDistribution[i][1];
				if(runningtotal >= midway) midwaypivot = i+1;
				updateEntry(frequencyDistribution[i][0], '0');
			}
			else
			{
				updateEntry(frequencyDistribution[i][0], '1');
			}
		}
		shannonFano(frequencyDistribution, lowrange, midwaypivot-1);
		shannonFano(frequencyDistribution, midwaypivot, highrange);
	}
	
	public int[][] frequencySort(int[][] unsorted)
	{
		int n = unsorted.length;
		int[][] out = unsorted;
		for (int i = 1; i < n; i++){
			  int j = i;
			  int B = out[i][1];
			  int Q = out[i][0];
			  while ((j > 0) && (out[j-1][1] > B)){
			  out[j][0] = out[j-1][0];
			  out[j][1] = out[j-1][1];
			  j--;
			  }
			  out[j][1] = B;
			  out[j][0] = Q;
		}
		return out;
	}
	
	public int[][] getFrequencies(byte[] data)
	{
		int range = 256;
		int[] setup = new int[range];
		int converted;
		int totalsignificant = 0;
		
		for(int i = 0; i < range; i++)
		{
			setup[i] = 0;
		}
		
		for(int i = 0; i < data.length; i++)
		{
			converted = (short) ((short) data[i] & 0xff);
			setup[converted]++;
			if(setup[converted] == 1)
				totalsignificant++;
		}
		
		int[][] output = new int[totalsignificant][2];
		int j = 0;
		
		for(int i = 0; i < setup.length; i++)
		{
			if(setup[i] != 0)
			{
				output[j][0] = i;
				output[j][1] = setup[i];
				j++;
			}
		}
		
		return output;
	}
	
	//SHANNON FANO CODE END//
	
	
	
}
