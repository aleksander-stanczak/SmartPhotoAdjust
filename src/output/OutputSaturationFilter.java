package output;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.filter.PlugInFilter;

public class OutputSaturationFilter implements PlugInFilter{
	
	ImagePlus imp;
	private  final static int OPAQUE = 0xFF000000;
	private static final float INCREASE_SATURATION = 1.2f;
	private static final float DECREASE_SATURATION = 0.8f;
	
	private float saturation = 1;
	private int brightness = 0;
	private float contrast = 1;
	private float hueChange = 0;
	
	private MemoryImageSource memoryImageSource;
	//private ImageRegion imageRegion = null;

	@Override
	public void run(ij.process.ImageProcessor ip) {
		
		
		Image image = imp.getImage();

		int width = imp.getWidth();
		int height = imp.getHeight();

		int maskSize = width*height;

		float xs_xoff = (float)(width/2.  + 0.5);
		float ys_yoff = (float)(height/2. + 0.5);

		PixelGrabber pg = null;

		int[] pixels = new int[width*height];
		int[] pixelsOrig = new int[width*height];
		
		pixelsOrig = (int[]) ip.getPixels();
		
		///
		
		float d255 = 1/255f;
		float d6 = 1/6f;
		float d3 = 1/3f;
		float d2 = 1/2f;

		for (int i=0; i<pixels.length; i++){
			int c = pixelsOrig[i];

			if ( (c & OPAQUE) == OPAQUE) {

				int r = ((c >> 16) & 255);
				int g = ((c >>  8) & 255);
				int b = ((c      ) & 255);

				if (contrast != 1) {
					r = (int) (contrast*(r-127) + 127);
					g = (int) (contrast*(g-127) + 127);
					b = (int) (contrast*(b-127) + 127);

					if (r > 255) r = 255;
					if (r <   0) r = 0;
					if (g > 255) g = 255;
					if (g <   0) g = 0;
					if (b > 255) b = 255;
					if (b <   0) b = 0;
				}

				if (brightness != 0) {
					r = (int) (r + brightness);
					g = (int) (g + brightness);
					b = (int) (b + brightness);

					if (r > 255) r = 255;
					if (r <   0) r = 0;
					if (g > 255) g = 255;
					if (g <   0) g = 0;
					if (b > 255) b = 255;
					if (b <   0) b = 0;
				}

				if (hueChange != 0 || saturation != 1) {
					float var_Min;    //Min. value of RGB
					float var_Max;    //Max. value of RGB
					float del_Max;             //Delta RGB value

					float rf = r * d255;
					float gf = g * d255;
					float bf = b * d255;

					if (rf > gf) { var_Min = gf; var_Max = rf; }
					else { var_Min = rf; var_Max = gf; }
					if (bf > var_Max) var_Max = bf;
					if (bf < var_Min) var_Min = bf;

					del_Max = var_Max - var_Min; 

					float H = 0, S, L;
					L = ( var_Max + var_Min ) * d2;

					if ( del_Max == 0 ) { H = 0; S = 0; }
					else {                                   //Chromatic data..{
						if ( L < 0.5 ) 
							S = del_Max / ( var_Max + var_Min );
						else           
							S = del_Max / ( 2 - var_Max - var_Min );

						if ( rf == var_Max ) {
							float del_G = ( ( ( var_Max - gf ) * d6 ) + ( del_Max * d2 ) ) / del_Max;
							float del_B = ( ( ( var_Max - bf ) * d6 ) + ( del_Max * d2 ) ) / del_Max;
							H = del_B - del_G;
						}
						else if ( gf == var_Max ) {
							float del_R = ( ( ( var_Max - rf ) * d6 ) + ( del_Max * d2 ) ) / del_Max;
							float del_B = ( ( ( var_Max - bf ) * d6 ) + ( del_Max * d2 ) ) / del_Max;
							H = d3 + del_R - del_B;
						}
						else if ( bf == var_Max ) {
							float del_R = ( ( ( var_Max - rf ) * d6 ) + ( del_Max * d2 ) ) / del_Max;
							float del_G = ( ( ( var_Max - gf ) * d6 ) + ( del_Max * d2 ) ) / del_Max;
							H = ( 2 * d3 ) + del_G - del_R;
						}
					}

					H += hueChange;

					if ( H < 0 ) H += 1;
					if ( H > 1 ) H -= 1;

					if ( S == 0 ) {                       //HSL values = From 0 to 1
						r = (int) (L * 255);         //RGB results = From 0 to 255
						g = (int) (L * 255);
						b = (int) (L * 255);
					}
					else {
						S*= saturation;

						float var_2;
						if ( L < 0.5 ) 
							var_2 = L * ( 1 + S );
						else           
							var_2 = ( L + S ) - ( S * L );

						float var_1 = 2 * L - var_2;

						r = (int) (255 * Hue_2_RGB( var_1, var_2, H + d3 ) );
						g = (int) (255 * Hue_2_RGB( var_1, var_2, H )      );
						b = (int) (255 * Hue_2_RGB( var_1, var_2, H - d3 ) );
					}
				}
				///////////////////

				if (r > 255) r = 255;
				if (r <   0) r = 0;
				if (g > 255) g = 255;
				if (g <   0) g = 0;
				if (b > 255) b = 255;
				if (b <   0) b = 0;

				c = OPAQUE | (r <<16) | (g << 8) | b;
			}
			pixels[i] = c;		
		}
		
		
//		memoryImageSource = new MemoryImageSource(width, height, pixels, 0, width);
//
//		image = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
//		
		ip.setPixels(pixels);
		imp.updateAndDraw();
		
		/*imp.setImage(image);
		imp.show();
		imp.updateAndDraw();*/
		
		/*IJ.showMessage("Debug: "+image);
		
		ImagePlus imp = new ImagePlus("New image",image);
		imp.setProcessor(ip);
		imp.
		imp.updateAndDraw();*/
		
		/*ImagePlus new_img = new ImagePlus("New image",image);
		new_img.setProcessor(ip);
		new_img.updateAndDraw();*/
		
		//imageRegion.setImage(image);
		
		
		
		
		
		/////////////////////////////
/*		
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		int [] h_hist = new int [360];
		
		for (int i=0; i<360; i++) {
			h_hist[i] = 0;
		}
		
		//set image as current window
				ImagePlus hs_imp = imp;
						
				for (int x=0; x<w; x++) {
					for (int y=0; y<h; y++) {
						int [] piksels = hs_imp.getPixel(x,y);
							
						double [] hsv_piksels = RGBtoHSV(piksels[0],piksels[1],piksels[2]);
						
						//if value null set 0
						int h_piksel = (int) hsv_piksels[0];
						
						if (h_piksel <0) {
							h_piksel =0;
						}
						
						h_hist[h_piksel] += 1;
					}
				}
		
		*/
		
		//imp.updateAndDraw();
		if ( saturation != 1 )
			IJ.showMessage("Saturation update by "+saturation+" conducted succesfully!");
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		
		switch (Integer.valueOf(arg)) {
		case 0:
			saturation = DECREASE_SATURATION;
			break;
		case 2:
			saturation = INCREASE_SATURATION;
			break;
		default:
			saturation = 1;
			break;
		}
		
		this.imp = imp;
		return DOES_ALL;
	}
	
	float Hue_2_RGB(float v1, float v2, float vH ) {            //Function Hue_2_RGB
		if ( vH < 0 ) 
			vH += 1;
		if ( vH > 1 ) 
			vH -= 1;
		if ( ( 6 * vH ) < 1 ) 
			return ( v1 + ( v2 - v1 ) * 6 * vH );
		if ( ( 2 * vH ) < 1 ) 
			return v2;
		if ( ( 3 * vH ) < 2 ) 
			return ( v1 + ( v2 - v1 ) * ( ( 2 / 3f ) - vH ) * 6 );
		return ( v1 );
	}

}
