/*
 * phase two
 * group six
 * 10/17/12
 * 
 * assumes r,g,b,a for consistency
 */

import java.io.File;
import javax.swing.*;


import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.util.MagickWindow;

public class Image {

	/*
	 * global constants
	 */	
	private MagickImage instance;
	private byte[] dispatched;
	private int w;
	private int h;
	private int csI;
	private String csS = null;	
	
	public Image() {
		instance = null;
	}

	/*
	 * constructor
	 * parameter file directory
	 */
	public Image(String str) {
		
		try {
			instance = new MagickImage(new ImageInfo(str));
			w = instance.getDimension().width;
			h = instance.getDimension().height;
			dispatched = null;
		} 
		catch (MagickException e) {
			instance = null;
			System.err.println("unable to load \"" + str + "\"");
		}
	}
	
	public byte[] getDispatched(){
		return dispatched;
	
	}
	public void setDispatched3(byte[] a){
			dispatched = a;
	}
	
 public Image(int w1, int h1, byte[] a) {

		
		String imgTitle = null;
		try {MagickImage b = new MagickImage();
			b.constituteImage(w1, h1, "RGB", a);
			b.setFileName("lastInputFile.jpg");
			imgTitle = b.getFileName();
			instance = b;
			dispatched = a;	
			w = instance.getDimension().width;
			h = instance.getDimension().height;
			
			
		} 
		catch (MagickException e) {
			instance = null;
			System.err.println("unable to set magicImage instatnce for image Title: " + imgTitle);
		}
	}
	
	public int getW(){
		return w;
	}
	public int getH(){
		return h;
	}
	public int get_csI(){
		return csI;
	}
	public String get_csS(){
		return csS;
	}

	

		
	public void setW(int a){
		w=a;
	}
	public void setH(int a){
		h = a;
	}
	public void setcsI(int a){
		csI=a;
	}
	public void setcsS(String a){
		csS = a;
	}
	

	
	
	
	
	
	/*
	 * dispatch the image into a series of bytes
	 * assumes order red, green, blue, alpha/opacity
	 */
	public byte[] dispatch() {
		
		if(dispatched == null) {
			try {
				if(instance != null) {
					dispatched = new byte[w * h * 4];
					instance.dispatchImage(0, 0, w, h, "RGBA", dispatched);
				}
			}
			catch(MagickException e) {
				e.printStackTrace();
			}
		}
		
		return dispatched;
	}
	
	/*
	 * dispatch the image into a series of bytes
	 * assumes order red, green, blue, alpha/opacity
	 */
	public byte[] dispatch3() {
		
		if(dispatched == null) {
			try {
				if(instance != null) {
					dispatched = new byte[w * h * 3];
					instance.dispatchImage(0, 0, w, h, "RGB", dispatched);
				}
			}
			catch(MagickException e) {
				e.printStackTrace();
			}
		}
		
		return dispatched;
	}
	
	
	
	/*
	 * return the original image instance
	 */
	public MagickImage getInstance() {
		return instance;
	}
	public MagickImage getconstImage() {
		return instance;
	}
	
	/*
	   public MagickImage getInstance_rgb() {
		return instance_rgb;
	}
	
		public MagickImage getInstance_yuv() {
		return instance_yuv;
	}
		public MagickImage getInstance_hsv() {
		return instance_hsv;
	}
	
	*/
	
	/*
	 * convert a byte into its integer representation
	 */
	public int byteToInteger(byte b) {
		return (short) ((short) b & 0xff);
	}
	
	/*
	 * displays an image in a separate window
	 * TODO: just a dummy function till we write our
	 * actual image decoder/display
	 */
	public void display(MagickImage i) {
		MagickWindow window = new MagickWindow(i);
		window.setSize(w + 49, h + 42);
		
		try {
			window.setTitle(i.getFileName());
		} 
		catch (MagickException e) {}
		
		window.setVisible(true);
	}
	
	public static void display20(MagickImage i) {
		MagickWindow window = new MagickWindow(i);
		try {
		window.setSize(i.getDimension().width + 49, i.getDimension().height + 42);
		

			window.setTitle(i.getFileName());
		} 
		catch (MagickException e) {}
		
		window.setVisible(true);
	}
	
	/*
	 * displays an image in a separate window
	 * featuring independent closing
	 * 
	 * 	 */
	public void displayTask(MagickImage i, String s) {
		
        
      
		// setup structures to display image
		String path = null;
		ImageIcon ii = null;
		JLabel label = null;
		JScrollPane jsp = null;
		
		//MagickWindow window = new MagickWindow(i);
		//window.setSize(w + 49, h + 42);
		
		//get image path location
		try {
			//window.setTitle(i.getFileName());
			path = i.getFileName();
	
		} 
		catch (MagickException e) {}
		
		
		// create and set up independent the window
        JFrame ind_frame = new JFrame(s + ",     location: " + path);         
        ind_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		
		//window.setVisible(true);		
		if (path != null) {
			ii = new ImageIcon(path);
			label = new JLabel(ii);
			jsp = new JScrollPane(label);
			ind_frame.getContentPane().add(jsp);	
			ind_frame.getContentPane().add(jsp);	
		}
		else 
		{}
	

        // display the window.
        ind_frame.setSize(w + 60 ,h + 60);
        ind_frame.setResizable(true);
        ind_frame.setVisible(true);
	
		
		
	}
	
	/*
	 * returns a series of bytes pertaining to
	 * certain layer(s), e.g. getLayer(0) for red component,
	 * getLayer(1) for blue component, and so on.
	 */
	public byte[] getLayer(int n) {
		
		if(dispatched == null)
			dispatch();
		
		int length = dispatched.length;
		byte[] r = new byte[length / 4];
		
		for(int i = n; i < length; i += 4) {
			r[i / 4] = dispatched[i];
			}
		
		return r;
	}
	
	/*
	 * converts ycbcr to rgb
	 */
	public double[] rgb(double y, double cb, double cr) {
		
		double r = y + 1.402 * (cr - 128);
		double g = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128);
		double b = y + 1.772 * (cb - 128);
		
		double[] rgb = {r, g, b};
		
		return rgb;
	}
	
	/*
	 * converts rgb to ycbcr
	 * y is luminance
	 * cb is blue difference
	 * cr is red difference
	 */
	public double[] ycbcr(double r, double  g, double b) {
		
		double y = (0.299 * r) + (0.587 * g) + (0.114 * b);
		double cb = 128 - (0.168736 * r) - (0.331264 * g) + (0.5 * b);
		double cr = 128 + (0.5 * r) - (0.418688 * g) - (0.081312 * b);
		
		double[] ycbcr = {y, cb, cr};
		
		return ycbcr;
	}
	
	/*
	 * converts rgb to yuv
	 * y is luminance
	 * u is blue - y
	 * v is red - y
	 */
	
	public double[] yuv(double r, double  g, double b) {
		
		double y = (0.299 * r) + (0.587 * g) + (0.114 * b);
		double u = 128 - (0.299 * r) - (0.587 * g) + (0.886 * b);
		double v = 128 + (0.701 * r) - (0.587 * g) - (0.114 * b);
		
		double[] yuv = {y, u, v};
		


		
		
		return yuv;
		
		
	}
	
	/*
	 * converts rgb to hsl may not work
	 * h is hue
	 * s is sensation
	 * v is value
	 */
/*	
	public double[] hsl(double R, double  G, double B) {
		//int minS = (_s1 <= _s2 && _s1 <= _s3) ? _s1 : ((_s2 <= _s1 && _s2 <= _s3) ? _s2 : _s3);
		//double	var_Min = (var_R <= var_G && var_R <= var_B) ? var_R : ((var_G <= var_R && var_B <= var_B) ? var_G : var_B);
			
			
		double del_R, del_G, del_B;
		
		double	 var_R = ( R / 255 );   //RGB from 0 to 255
		double	 var_G = ( G / 255 );
		double	 var_B = ( B / 255 );

		double	 var_Min = Math.min( var_R,  Math.min(var_G, var_B));    //Min. value of RGB
		double	 var_Max = Math.max( var_R,  Math.max(var_G, var_B));    //Min. value of RGB
		double	 del_Max = var_Max - var_Min ;            //Delta RGB value
		double H,S,L;
		L = ( var_Max + var_Min ) / 2;

				 if ( del_Max == 0 )                     //This is a gray, no chroma...
				 {
				    H = 0 ;                               //HSL results from 0 to 1
				    S = 0;
				 }
				 else                                    //Chromatic data...
				 {
				    if ( L < 0.5 )
				    	S = del_Max / ( var_Max + var_Min );
				    else 
				    	S = del_Max / ( 2 - var_Max - var_Min );
				    
				    
				    del_R = ( ( ( var_Max - var_R ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
				    del_G = ( ( ( var_Max - var_G ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
				    del_B = ( ( ( var_Max - var_B ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
				   

				    if      ( var_R == var_Max )
				    	H = del_B - del_G;
				    else if ( var_G == var_Max )
				    	H = ( 1 / 3 ) + del_R - del_B;
				    else 
				    	H = ( 2 / 3 ) + del_G - del_R;
				    
				    
				    if ( H < 0 ) H += 1;
				    if ( H > 1 ) H -= 1;
				 }
		
				double[] hsl =  {H, S, L};
		
		return hsl;		
		
	}
	
	*/

	public double[] hsv1 (double r1, double g1, double b1){		
		// copy from http://axonflux.com/handy-rgb-to-hsl-and-rgb-to-hsv-color-model-c
		// and http://www.cs.rit.edu/~ncs/color/t_convert.html
		
		double r, g, b;
	    r = r1/255;
	    g = g1/255;
	    b = b1/255;
	    double max = Math.max(r, Math.max(g, b));
	    double min = Math.min(r,Math.min(g, b));
	    double h,s,v;
	    	v = max;
	    double d = max - min;
	    if (max == 0)
	    	{s = 0;}
	    else
	    	{s = d/ max;
	    	h = -1;
	    	double[] hsv = {h,s,v};
	    	return hsv;
	    	}
	    
	    
	    if(max == min){
	        h = 0; // achromatic
	    	}
	    else{
	    	if (max == r)
	    		{h = (g - b) / d ;}
	    	else if (max == g)
	            {h = (b - r) / d + 2;}
	    	else
	            {h = (r - g) / d + 4;}
	       
	    	h = h /6;
	        }

	    double[] hsv = {h,s,v};
	    return hsv;
		
	    }



	public double[] hsv2 (double r1, double g1, double b1){		
		// copy from http://axonflux.com/handy-rgb-to-hsl-and-rgb-to-hsv-color-model-c
		// and http://www.cs.rit.edu/~ncs/color/t_convert.html
		
		double r, g, b;
	    r = r1/255;
	    g = g1/255;
	    b = b1/255;
	    double max = Math.max(r, Math.max(g, b));
	    double min = Math.min(r,Math.min(g, b));
	    double h,s,v;
	    	v = max;
	    double d = max - min;
	    
	    if (max == 0)
	    	{s = 0;
	    	h = -1;
	    	double[] hsv = {h,s,v};
	    	return hsv;
	    	}
	    else
	    	{s = d/ max;	

	    	}
	    
	    
	    if(max == min){
	       h = 0; // achromatic
	    	}
	    else{
	    	if (max == r)
	    		{h = 0 + (g - b) / d ;}
	    	else if (max == g)
	            {h = ((b - r) / d) + 2;}
	    	else
	            {h = ((r - g) / d) + 4;}
	       
	    	h = h * 60;
	    }
	      if (h < 0)
	    	  	h = h + 360;

	    double[] hsv = {h,s,v};
	    return hsv;
		
	    }


	
	public double[] hsv (double r, double g, double b){		
		// references
		//from http://axonflux.com/handy-rgb-to-hsl-and-rgb-to-hsv-color-model-c
		// and http://www.cs.rit.edu/~ncs/color/t_convert.html
		//http://www.gamedev.net/topic/18309-rgb-and-hsv-colors---the-formula///
		
		double max = Math.max(r, Math.max(g, b));
	    double min = Math.min(r,Math.min(g, b));
	    double h,s,v;
	    	v = max;
	    double d = max - min;
	    
	    if (max == 0)
	    	{s = 0;
	    	h = -1 ;
	    	double[] hsv = {h,s,v};
	    	return hsv;
	    	}
	    else
	    	{s = d/ max;	

	    	}
	    
	    
	    if(max == min){
	        h = 0; // achromatic
	    	}
	    else{
	    	if (max == r)
	    		{h = 0 + (g - b) / d ;}
	    	else if (max == g)
	            {h = ((b - r) / d) + 2;}
	    	else
	            {h = ((r - g) / d) + 4;}
	    	}
	    
	    	h = h * 60;
	      if (h < 0)
	    	  	h = h + 360;

	    double[] hsv = {h,s,v};
	    return hsv;
		
	    }


	
	
	public int[] iycbcr(int r, int  g, int b) {
		
		int y = (int)((0.299 * r) + (0.587 * g) + (0.114 * b));
		int cb = (int)(128 - (0.168736 * r) - (0.331264 * g) + (0.5 * b));
		int cr = (int)(128 + (0.5 * r) - (0.418688 * g) - (0.081312 * b));
		
		int[] ycbcr = {y, cb, cr};
		
		return ycbcr;
	}
	
	public int[] irgb(int y, int cb, int cr) {
		
		int r = (int)(y + 1.402 * (cr - 128));
		int g = (int)(y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128));
		int b = (int)(y + 1.772 * (cb - 128));
		
		int[] rgb = {r, g, b};
		
		return rgb;
	}
	
	public static double[] bytetodouble(byte[] data)
	{
		double[] output = new double[data.length];
		for(int i = 0; i < data.length; i++)
		{
			output[i] = (double)((short)data[i] & 0xff);
		}
		return output;
	}
	
	public static byte[] doubletobyte(double[] data)
	{
		byte[] output = new byte[data.length];
		for(int i = 0; i < data.length; i++)
		{
			output[i] = (byte)(data[i]);
		}
		return output;
	}
	
	public static int[] doubletoint(double[] data)
	{
		int[] output = new int[data.length];
		for(int i = 0; i < data.length; i++)
		{
			output[i] = (int)(data[i]);
			if(data[i] < 1)
				output[i] = 1;
			else if(data[i] > 254)
				output[i] = 254;
		}
		return output;
	}
	
	public static double[] inttodouble(int[] data)
	{
		double[] output = new double[data.length];
		for(int i = 0; i < data.length; i++)
		{
			output[i] = (double)(data[i]);
			if(data[i] < 1)
				output[i] = 1;
			else if(data[i] > 254)
				output[i] = 254;
		}	
		return output;
	}
	
	public static int[] bytetoint(byte[] data)
	{
		int[] output = new int[data.length];
		for(int i = 0; i < data.length; i++)
		{
			output[i] = (short)((short) data[i] & 0xff);
		}
		return output;
	}
	
	public static byte[] inttobyte(int[] data)
	{
		byte[] output = new byte[data.length];
		for(int i = 0; i < data.length; i++)
		{
			output[i] = (byte)(data[i]);
		}
		return output;
	}
	
	/*
	 *  Uniformly quantizes the data using N bins, given by the user.
	 *  Decision boundaries with corresponding reconstruction values are first formed.
	 *  Then the lookup table of reconstruction values is used to quantize each value.
	 *  Uses mid-rise quantization.
	 */
	public byte[] quantize(byte[] data, int bins)
	{
		int max = 256;
		int increments;
		if(max < bins)
		{
			increments = 1;
		}
		else
			increments = max / bins;
		byte[] out = new byte[data.length];
		int[] lut = new int[bins + 1];
		int frombyte, lookupindex;
		
		//Set up the look up table by inserting reconstruction values.
		for(int i = 0; i < bins + 1; i++)
		{
			lut[i] = i * increments;
		}
		
		//Quantize the data
		for(int j = 0; j < data.length; j++)
		{
			frombyte = (short) ((short) data[j] & 0xff);
			lookupindex = frombyte / increments;
			if(lookupindex >= lut.length)
				lookupindex = lut.length - 1;
			out[j] = (byte)(lut[lookupindex]);
		}	
		
		//return output
		return out;
	}
	
	public byte[] quantizeYBR(byte[] data, int bins)
	{
		double max = 1.0;
		double increments;
		if(bins > 100)
		{
			increments = 0.1;
		}
		else
			increments = max / bins;
		byte[] out = new byte[data.length];
		double[] lut = new double[bins + 1];
		int lookupindex;
		double frombyte;
		
		//Set up the look up table by inserting reconstruction values.
		for(int i = 0; i < bins + 1; i++)
		{
			lut[i] = i * increments;
		}
		
		//Quantize the data
		for(int j = 0; j < data.length; j++)
		{
			frombyte = (double)  (data[j] & 0xff);
			lookupindex = (int)(frombyte / increments);
			if(lookupindex >= lut.length)
				lookupindex = lut.length - 1;
			out[j] = (byte)(lut[lookupindex]);
		}	
		
		//return output
		return out;
	}
	
	public byte[] quantizeError(byte[] data, int bins)
	{
		int max = 256;
		double increments;
		//int closest;
		byte fvalue;
		if(max < bins)
		{
			increments = 1;
		}
		else
			increments = (max * 1.0) / bins;
		byte[] out = new byte[data.length];
		int[] lut = new int[bins + 1];
		int frombyte, lookupindex;
		
		//Set up the look up table by inserting reconstruction values.
		for(int i = 0; i < bins + 1; i++)
		{
			lut[i] = (int)(i * increments);
		}
		
		//Quantize the data
		for(int j = 0; j < data.length; j++)
		{
		
			frombyte = (int) (data[j] & 0xff);

			lookupindex = (int)(frombyte / increments);
			if(lookupindex >= lut.length)
				lookupindex = lut.length - 1;
			if(lookupindex < 0)
				fvalue = (byte)(-lut[-lookupindex]);
			else
				fvalue = (byte)(lut[lookupindex]);
			out[j] = fvalue;
		}	
		
		//return output
		return out;
	}
	
	public byte[] quantizeErrorYBR(byte[] data, int bins)
	{
		double max = 1.0;
		double increments;
		//int closest;
		byte fvalue;
		if(bins > 100)
		{
			increments = 0.1;
		}
		else
			increments = max / bins;
		byte[] out = new byte[data.length];
		double[] lut = new double[bins + 1];
		int lookupindex;
		double frombyte;
		
		//Set up the look up table by inserting reconstruction values.
		for(int i = 0; i < bins + 1; i++)
		{
			lut[i] = i * increments;
		}
		
		//Quantize the data
		for(int j = 0; j < data.length; j++)
		{
		
			frombyte = (double) (data[j] & 0xff);

			lookupindex = (int)(frombyte / increments);
			if(lookupindex >= lut.length)
				lookupindex = lut.length - 1;
			if(lookupindex < 0)
				fvalue = (byte)(-lut[-lookupindex]);
			else
				fvalue = (byte)(lut[lookupindex]);
			out[j] = fvalue;
		}	
		
		//return output
		return out;
	}
	
	
	public byte[] reduceResolution(byte[] data, int s)
	{
		byte[] out = data;
		
		for(int x = 1; x < w - 1; x++)
		{
			for(int y = 1; y < h - 1; y++)
			{
				if(x % s == 0)
					out[(w * y) + x] = out[(w * y) + x + 1];
				else if((x + (s/2)) % s == 0)
					out[(w * y) + x] = out[(w * (y + 1)) + x];
			}
		}
		return out;
	}
	
	
	//changed the signature of Khai's function
	public static int stbyteToInteger(byte b) {
		return (short) ((short) b & 0xff);
	}


	public static int[] correctedPixels(byte [] pixels)
	{
		int [] corrected_pixels = new int[pixels.length];
		for(int i = 0 ; i < pixels.length ; i++)
			corrected_pixels[i] = stbyteToInteger(pixels[i]);
		return corrected_pixels;
	}


	/*
	 * Predictive Coding
	 */
	public static int[] applyPredictiveCoding(int option, int[] channel, int width)
	{
		int [] pc_errors = new int[channel.length];
		int A = 0, B = 0, C = 0;
		for(int i = 0 ; i < pc_errors.length ; i++)
		{
			if( i % width == 0 || i < width)
			{
				pc_errors[i] = channel[i];
				continue;
			}
			if(option != 1)
			{
				A = channel[i - 1];
				B = channel[i - width];
				C = channel[i - width - 1];
			}	
			switch(option)
			{
			case 1: 
				return channel;
				//	break;
			case 2:
				pc_errors[i] = (channel[i] - A);
				break;
			case 3:
				pc_errors[i] = (channel[i] - B);
				break;

			case 4:
				pc_errors[i] = (channel[i] - C);
				break;
			case 5:
				pc_errors[i] = (channel[i] - (int)((A + B + C) / 3));
				break;
			case 6:
				pc_errors[i] = (channel[i] - (A + B - C));
				break;
			case 7:
				pc_errors[i] = (channel[i] - (int)((A + B) /2));
				break;
			case 8:
				if( (B - C > 0) && (A - C > 0) )
					pc_errors[i] = channel[i] - (int) (C + Math.sqrt(Math.pow(B-C, 2) + Math.pow(A-C, 2))) ;	
				else if( (B - C < 0) && (A - C < 0) )
					pc_errors[i] = channel[i] - (int) (C + Math.sqrt(Math.pow(B-C, 2) + Math.pow(A-C, 2))) ;	
				else 
					pc_errors[i] = channel[i] - (int)((A + B) /2);	
				break;
			default:
				//wrong input
				break;
			}
		}
		return pc_errors;
	}
	public static int[] removePredictiveCoding(int option, int[] pc_errors, int width)
	{
		int [] channel = new int[pc_errors.length];
		int A = 0, B = 0, C = 0;
		for(int i = 0 ; i < channel.length ; i++)
		{
			if( i % width == 0 || i < width)
			{
				channel[i] = pc_errors[i];
				continue;
			}
			if(option != 1)
			{
				A = channel[i - 1];
				B = channel[i - width];
				C = channel[i - width - 1];
			}	
			switch(option)
			{
			case 1: 
				return pc_errors;
				//	break;
			case 2:
				channel[i] = (pc_errors[i] + A);
				break;
			case 3:
				channel[i] = (pc_errors[i] + B);
				break;

			case 4:
				channel[i] = (pc_errors[i] + C);
				break;
			case 5://correct from here
				channel[i] = (pc_errors[i] + ((A + B + C) / 3));
				break;
			case 6:
				channel[i] = (pc_errors[i] + (A + B - C));
				break;
			case 7:
				channel[i] = (pc_errors[i] + ((A + B) /2));
				break;
			case 8:
				if( (B - C > 0) && (A - C > 0) )
					channel[i] = pc_errors[i] + (int) (C + Math.sqrt(Math.pow(B-C, 2) + Math.pow(A-C, 2))) ;	
				else if( (B - C < 0) && (A - C < 0) )
					channel[i] = pc_errors[i] + (int) (C + Math.sqrt(Math.pow(B-C, 2) + Math.pow(A-C, 2))) ;	
				else 
					channel[i] = pc_errors[i] + ((A + B) /2);	
				break;
			default:
				//wrong input
				break;
			}
		}
		return channel;
	}
	
	public static double getDistortion(byte[] original, byte[] decompressed)
	{
		if(original.length == 0)
			return -1;
		double count = 0.0;
		double total = original.length;
		for(int i=0; i < original.length; i++)
		{
			if(i < decompressed.length && i < original.length && original[i]==decompressed[i])
			{
				count++;
			}
		}
		return count / total;
	}
	
	public static double getSize(String fn)
	{
		File file = new File(fn);
		return file.length();
	}
	
	/*
	 * main, test
	 */
	public static void main(String[] args) throws MagickException {
		
		Image test = new Image("Jellyfish.jpg");
		
		test.dispatch();
		
		//CONVERT TO YCBCR
		
		byte[] red = test.getLayer(0);
		byte[] blue = test.getLayer(2);
		byte[] green = test.getLayer(1);
		
		byte[] initialimage = new byte[red.length * 3];
		
		for(int i = 0; i < red.length; i++)
		{
			initialimage[3*i] = red[i];
			initialimage[3*i+1] = green[i];
			initialimage[3*i+2] = blue[i];
		}		
		
		int s = 2;
		
		red = ResolutionReduction.reduceResolution(test.dispatched, test.w, test.h, s, 0);
		green = ResolutionReduction.reduceResolution(test.dispatched, test.w, test.h, s, 1);
		blue = ResolutionReduction.reduceResolution(test.dispatched, test.w, test.h, s, 2);
		
		test.w = test.w / s;
		test.h = test.h / s;
		
		byte[] newreds = new byte[red.length * 3];
		byte[] newgreens = new byte[green.length * 3];
		byte[] newblues = new byte[blue.length * 3];
		
		int q;
		
		//red = test.quantize(red, 10);
		//blue = test.quantize(blue, 10);
		//green = test.quantize(green, 10);
		
		//int[] int_red = correctedPixels(red);
		//int[] pcr = applyPredictiveCoding(2,int_red,test.w);
		
		//int[] int_green = correctedPixels(green);
		//int[] pcg = applyPredictiveCoding(2,int_green,test.w);
		
		//int[] int_blue = correctedPixels(blue);
		//int[] pcb = applyPredictiveCoding(2,int_blue,test.w);
		
		//for(int i = 0; i < int_red.length; i++)
		//{
		//	red[i] = (byte)pcr[i];
		//	green[i] = (byte)pcg[i];
		//	blue[i] = (byte)pcb[i];
		//}
		
		//red = test.quantizeError(red, 255);
		//green = test.quantizeError(green, 255);
		//blue = test.quantizeError(blue,  255);
		
		byte[] combined = new byte[red.length * 3];
		
		for(int i = 0; i < red.length; i++)
		{
			combined[3*i] = red[i];
			combined[3*i+1] = green[i];
			combined[3*i+2] = blue[i];
		}
		
		//combined = test.quantizeError(combined, 10);
		
		SymbolTable st = new SymbolTable();
		st.buildTableViaSF(combined);
		st.writeOut(combined, 0);
		//byte[] otherblue = blue;
		//byte[] newotherblues = new byte[otherblue.length * 3];
		combined = st.readIn(0);
		
		for(int i = 0; i < red.length; i++)
		{
			red[i] = combined[3* i];
			green[i] = combined[3* i + 1];
			blue[i] = combined[3* i + 2];
		}
		
		//int_red = correctedPixels(red);
		//pcr = removePredictiveCoding(2,int_red,test.w);
		
		//int_green = correctedPixels(green);
		//pcg = removePredictiveCoding(2,int_green,test.w);
		
		//int_blue = correctedPixels(blue);
		//pcb = removePredictiveCoding(2,int_blue,test.w);
		
		//for(int i = 0; i < int_red.length; i++)
		//{
		//	red[i] = (byte)pcr[i];
		//	green[i] = (byte)pcg[i];
		//	blue[i] = (byte)pcb[i];
		//}
		
		//for(int i = 0; i < red.length; i++)
		//{
		//	combined[3*i] = red[i];
		//	combined[3*i+1] = green[i];
		//	combined[3*i+2] = blue[i];
		//}
		
		for(int i = 0; i < red.length; i++) {
			//System.out.println(test.byteToInteger(red[i]));
			q = 3 * i;
			newreds[q] = (byte) (red[i]);
			newreds[q+1] = (byte) 0;
			newreds[q+2] = (byte) 0;
			
			newgreens[q] = (byte) 0;
			newgreens[q+1] = (byte) green[i];
			newgreens[q+2] = (byte) 0;
			
			newblues[q] = (byte) 0;
			newblues[q+1] = (byte) 0;
			newblues[q+2] = (byte) blue[i];
			
			//newotherblues[q] = (byte) 0;
			//newotherblues[q+1] = (byte) 0;
			//newotherblues[q+2] = (byte) otherblue[i];
		}		
		
		// new image
		MagickImage image = new MagickImage();
		
		image.constituteImage(test.w, test.h, "RGB", newreds);
		test.display(image);
		
		image.constituteImage(test.w, test.h, "RGB", newgreens);
		test.display(image);
		
		image.constituteImage(test.w, test.h, "RGB", newblues);
		test.display(image);
		
		image.constituteImage(test.w, test.h, "RGB", combined);
		test.display(image);
		
		//System.out.println("Size: " + getSize("out.RGB") + " bytes");
		//System.out.println("SNR: " + getDistortion(initialimage, combined));
		
		/*
		image.constituteImage(test.w, test.h, "R", red);
		test.display(image);
		
		image.constituteImage(test.w, test.h, "G", green);
		test.display(image);
		
		image.constituteImage(test.w, test.h, "B", blue);
		test.display(image);
		*/
	}
	
	
}