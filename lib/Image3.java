import java.awt.Rectangle;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.MontageInfo;
import magick.util.MagickWindow;

public class Image3 {
	
	private MagickImage instance;
	private MagickImage[] partition;
	
	public Image3(String s) {
		try {
			instance = new MagickImage(new ImageInfo(s));
		} catch (MagickException e) {
			instance = null;
			e.printStackTrace();
		}
	}
	
	public MagickImage getInstance() {
		return instance;
	}
	
	public MagickImage[] getPartition() {
		return partition;
	}
	
	/**
	 * dispatch an image into a series of bytes
	 * assumes order red, green, blue
	 */
	public byte[] dispatch(MagickImage i) {
		
		byte[] dispatched = null;
		
		try {
			int w = i.getDimension().width;
			int h = i.getDimension().height;
				
			dispatched = new byte[w * h * 3];
			instance.dispatchImage(0, 0, w, h, "RGB", dispatched);
		}
		catch(MagickException e) {
			e.printStackTrace();
		}
		
		return dispatched;
	}
	
	/**
	 * partition the instance image into n by n cells
	 * and cache the images in partition[]
	 */
	public void partition(int n) {
		try {
			partition = new MagickImage[n * n];
			
			float w = instance.getDimension().width / n;
			float h = instance.getDimension().height / n;
			
			int c = 0;
			
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					Rectangle r = new Rectangle((int) (w * j), (int) (h * i), (int) w, (int) h);
					partition[c] = instance.cropImage(r);
					c++;
				}
			}
		} catch (MagickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * convert a byte into its integer representation
	 */
	public int byteToInteger(byte b) {
		return (int) (b & 0xff);
	}
	
	public static void main(String[] args) throws MagickException {
		Image3 image = new Image3("Penguins.jpg");
		image.partition(4);

        MagickImage seqImage = new MagickImage(image.getPartition());
        MontageInfo montageInfo = new MontageInfo(new ImageInfo());
        montageInfo.setFileName("montage.jpg");
        montageInfo.setBorderWidth(1);
        MagickImage montage = seqImage.montageImages(montageInfo);
        montage.writeImage(new ImageInfo());
		
		MagickWindow window = new MagickWindow(new MagickImage(new ImageInfo("montage.jpg")));
		window.setTitle("");
		window.setVisible(true);
	}
}
