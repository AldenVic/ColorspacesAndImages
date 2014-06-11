import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.MontageInfo;
import magick.util.MagickWindow;


/**
 * TODO Put here a description of what this class does.
 *
 * @author Ramon.
 *         Created Nov 30, 2012.
 */
	
	public  class imageSetMember {
		
			
		// Loaded image information
		private int myIndex;
		
		private String[] imageLocations = {null, null, null};
		private String imageLocation= null;
		//private String imageLocation_rgb = "rgb_index_";
		//private String imageLocation_yuv = "yuv_index_";
		//private String imageLocation_hsv = "hsv_index_";
			
		private Image[] loadedImages = {null, null, null};
		private Image loadedImage = null;
		//private Image loadedImage_rgb = null;	
		//private Image loadedImage_yuv = null;
		//private Image loadedImage_hsv = null;
		
		private byte[] dispatched = null;
		private byte[] dispatched_rgb = null;	
		private byte[] dispatched_yuv = null;
		private byte[] dispatched_hsv = null;
		
		public int width;
		public int height;
		private int width_rgb;
		private int height_rgb;
		private int width_yuv;
		private int height_yuv;
		private int width_hsv;
		private int height_hsv;
		

		private byte[] red;
		private byte[] green ;
		private byte[] blue ;
		
		private byte[] red_r; 
		private byte[] green_g;
		private byte[] blue_b;

		private byte[] red_y; 
		private byte[] green_u;
		private byte[] blue_v;
	
		private byte[] red_h; 
		private byte[] green_s;
		private byte[] blue_vv;
		
		
		public void newImage(String a){
			myIndex = Utilities.totalMembers;
			imageLocation = a;				
			imageLocations[0] = "rgb_index_"+ myIndex + ".jpg";
			imageLocations[1] = "yuv_index_"+ myIndex + ".jpg";
			imageLocations[2] = "hsv_index_"+ myIndex + ".jpg";	

			

			loadedImage = new Image(imageLocation);
			//loadedImages[0] = new Image(a);
			//loadedImages[1] = new Image(a);
			//loadedImages[2] = new Image(a);
			
			loadedImage.dispatch();
			//dispatched_rgb = loadedImage.dispatch3();
			
			 
			//loadedImages[0].dispatch3();
			//loadedImages[1].dispatch3(); 
			//loadedImages[2].dispatch3(); 
			
			
			width = loadedImage.getW();
			height = loadedImage.getH();
			
			loadedImage.setcsI(-1);
			loadedImage.setcsS("default");	
			
			red = loadedImage.getLayer(0);
			green = loadedImage.getLayer(1);
			blue = loadedImage.getLayer(2);		
		
			dispatched = new byte[width * height * 3];
			for (int i = 0; i < red.length; i++) {
				dispatched[3 * i] = red[i];
				dispatched[3 * i + 1] = green[i];
				dispatched[3 * i + 2] = blue[i];
			}

			
			byte[] extended_yuv = new byte [dispatched.length];
			byte[] extended_hsv = new byte [dispatched.length];
			for (int i = 0; i < dispatched.length/3; i++) {
				double[] currentconv_yuv = loadedImage.yuv(
						dispatched[3 * i] & 0xFF,
						dispatched[3 * i + 1] & 0xFF,
						dispatched[3 * i + 2] & 0xFF);
				
				extended_yuv[3 * i] = (byte) currentconv_yuv[0];
				extended_yuv[3 * i + 1] = (byte) currentconv_yuv[1];
				extended_yuv[3 * i + 2] = (byte) currentconv_yuv[2];	

				double[] currentconv_hsv = loadedImage.hsv(
						dispatched[3 * i] & 0xFF,
						dispatched[3 * i + 1] & 0xFF,
						dispatched[3 * i + 2] & 0xFF);				
				extended_hsv[3 * i] = (byte) currentconv_hsv[0];
				extended_hsv[3 * i + 1] = (byte) currentconv_hsv[1];
				extended_hsv[3 * i + 2] = (byte) currentconv_hsv[2];	
			}
		
			dispatched_rgb = dispatched;
			dispatched_yuv =  extended_yuv;
			dispatched_hsv = extended_hsv;
			

			loadedImages[0] = new Image(width, height, dispatched_rgb);
			loadedImages[1] = new Image(width, height, dispatched_yuv);
			loadedImages[2] = new Image(width, height, dispatched_hsv);
			
			
			loadedImages[0].setcsI(0);
			loadedImages[0].setcsS("rgb");
			loadedImages[1].setcsI(1);
			loadedImages[1].setcsS("yuv");
			loadedImages[2].setcsI(2);
			loadedImages[2].setcsS("hsv"); 


			width_rgb = loadedImages[0].getW();
			height_rgb = loadedImages[0].getH();
			width_yuv = loadedImages[1].getW();
			height_yuv = loadedImages[1].getH();
			width_hsv = loadedImages[2].getW();
			height_hsv = loadedImages[2].getH();
			
			try{
						
				
				//1st written image
				MagickImage image_o = new MagickImage();
				image_o.constituteImage(width, height, "RGB", dispatched);
				image_o.setFileName("lastInputFile.jpg"); // assigns value to file name variable
							//loadedImage.display(image_o);
					// above displays file from memory, close this window will close all windows
					// so block out and use below, you need to write to memory
					// to prevent using up memory, use the same file name to write
					//and then display using a differnt display funciton
				//image_o.writeImage(new ImageInfo());//writes file to disk at "lastInputFile.jpg"
				//UtilFuncs.displayImage2(image_o,"lastInputFile.jpg",imageLocation);
					// display file from disk, if many images this will not take up 
					// much space as image is written over using file loction "lastInputFile.jpg"
					// the string variable in the displayImage2 function will not be writtent to
					// it will be used for window title purposes.
							
				// see comments in 1st written image undestand commmend rows
				MagickImage image_rgb = new MagickImage();
				image_rgb.constituteImage(width, height, "RGB", dispatched_rgb);
				image_rgb.setFileName("lastInputFile.jpg"); // assigns value to file name variable
								//loadedImages[0].display(image_rgb); 
				//image_rgb.writeImage(new ImageInfo()); 
				//UtilFuncs.displayImage2(image_o,"lastInputFile0.jpg",imageLocations[0]);	
				
					
				// see comments in 1st written image undestand commmend rows
				MagickImage image_yuv = new MagickImage();
				image_yuv.constituteImage(width, height, "RGB", dispatched_yuv);
				image_yuv.setFileName("lastInputFile.jpg");			
								//loadedImages[1].display(image_yuv); 	
				//image_yuv.writeImage(new ImageInfo());  
				//UtilFuncs.displayImage2(image_o,"lastInputFile1.jpg",imageLocations[1]);
				
				// see comments in 1st written image undestand commmend rows
				MagickImage image_hsv = new MagickImage();
				image_hsv.constituteImage(width, height, "RGB", dispatched_hsv);
				image_hsv.setFileName("lastInputFile.jpg");
									//loadedImages[2].display(image_hsv); 
				//image_hsv.writeImage(new ImageInfo()); 
				//UtilFuncs.displayImage2(image_o,"lastInputFile2.jpg",imageLocations[2]);
				
				
				}
			catch (MagickException e)
				{	e.printStackTrace();	} 
			
				
		/*	
				
			
			try{
				MagickImage image_yuv = new MagickImage();
				image_yuv.constituteImage(width_yuv, height_yuv, "RGB", decoded_yuv);
				image_yuv.setFileName(imageLocation_yuv_en);
				image_yuv.writeImage(new ImageInfo());
				UtilFuncs.displayImage(image_yuv, imageLocation_yuv_en);
				
				MagickImage image_hsl = new MagickImage();
				image_hsl.constituteImage(width_yuv, height_yuv, "RGB", decoded_yuv);
				image_yuv.setFileName(imageLocation_yuv_en);
				image_yuv.writeImage(new ImageInfo());
				UtilFuncs.displayImage(image_yuv, imageLocation_yuv_en);
				

				UtilFuncs.displayImage2(image_yuv, imageLocation_rgb_en, " " + myIndex);	
				UtilFuncs.displayImage2(image_hsl, imageLocation_rgb_en, " " + myIndex);					
				
				}
			catch (MagickException e)
				{	e.printStackTrace();	} 
			
			*/
	
			
			//decode 
			
			/*
			loadedImage = new Image(imageLocation);
			
			
			byte[] decoded_yuv = UtilFuncs.getByteArray(UtilFuncs.readFromFile(imageLocation_yuv_en, 256, 256, 256));
			byte[] decoded_hsv = UtilFuncs.getByteArray(UtilFuncs.readFromFile(imageLocation_hsv_en, 256, 256, 256));
						
			
			try{
				//MagickImage image_rgb = new MagickImage();
				//image_rgb.constituteImage(width_rgb, height_rgb, "RGB", dispatched_rgb);
				//image_rgb.setFileName(imageLocation_rgb_en);
				//image_rgb.writeImage(new ImageInfo());
				
				MagickImage image_yuv = new MagickImage();
				image_yuv.constituteImage(width_yuv, height_yuv, "RGB", decoded_yuv);
				image_yuv.setFileName(imageLocation_yuv_en);
				image_yuv.writeImage(new ImageInfo());
				UtilFuncs.displayImage(image_yuv, imageLocation_yuv_en); 
				
				MagickImage image_hsv = new MagickImage();
				image_hsv.constituteImage(width_hsv, height_hsv, "RGB", dispatched_hsv);
				image_hsv.setFileName(imageLocation_hsv_en);
				image_hsv.writeImage(new ImageInfo());
				
				
				// use for porject 2
				//UtilFuncs.displayImage2(image_rgb, imageLocation_rgb_en, " " + myIndex);	
				//UtilFuncs.displayImage2(image_yuv, imageLocation_yuv_en, " " + myIndex); 
				//UtilFuncs.displayImage2(image_hsv, imageLocation_hsv_en, " " + myIndex);
				
				}
			catch (MagickException e)
				{	e.printStackTrace();	} 
			
			 */
			
		}
		
		
		public	int getMyIndex(){
				return myIndex;
			}
		public void	setMyIndex( int a){
			 myIndex =a;
		}
		public Image getLoadedImage(int i){
			if (( i >=0) && (i <=2))
				return loadedImages[i];
			return loadedImages[0];
			
		}
		
		public String getImageLocation(int a){
			return imageLocations[a];
		}
		
		
			public  void set_dispatched_arrays(){
				//imageLocation = imLocation;
				//loadedImage = new Image(imLocation);
				


		}
	}	
	
	

