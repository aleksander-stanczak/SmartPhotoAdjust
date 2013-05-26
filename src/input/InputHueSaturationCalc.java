package input;

import ij.*;
import ij.plugin.filter.*;

public class InputHueSaturationCalc implements PlugInFilter {
	ImagePlus imp;
	private boolean result = false;
	private String argument;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		this.argument = arg;
		return DOES_ALL;
	}

	@Override
	public void run(ij.process.ImageProcessor arg0) {
		// TODO Auto-generated method stub
			
		result = calulateHueSaturation(arg0);
				    
	    IJ.showMessage(Boolean.toString(result));
						
	}
	
	/**
	 * Calculate if Color Hue and Saturation need to by change
	 * 
	 * @param	ip		image that will be marked
	 * @return			if hue and saturation need to be change
	 */
	private boolean calulateHueSaturation(ij.process.ImageProcessor ip) {
		
		boolean huesaturation = false;
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		int [] h_hist = new int [360];
		
		for (int i=0; i<360; i++) {
			h_hist[i] = 0;
		}
		
		//set image as current window
		ImagePlus hs_imp;
		hs_imp = WindowManager.getCurrentImage();
				
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
		
		//get max value for histogram
		int h_hist_max =0;
		
		for (int i=0; i<360; i++) {
			if (h_hist_max <h_hist[i]) {
				h_hist_max = h_hist[i];
			}
		}
				
		//calc 5 most highest or lowest frequencies
		int sum_first = 0;
		int sum_last = 0;
		
		for (int i=0; i<5; i++) {
			sum_first += h_hist[i];
			sum_last += h_hist[359-i];
		}
				
		//decide what to do
		int perc_of_max = h_hist_max/7;
		
		if ((sum_first <perc_of_max) ||(sum_last <perc_of_max)) {
			huesaturation = true;
		}
		
	    Window_Closer Wind_Clow = new Window_Closer();
	    Wind_Clow.run(argument);
		
		return huesaturation;
	}
	
	/**
	 * Change RGB pixel value to HSV value
	 * 
	 * @param	r		r value of image
	 * @param	g		g value of image
	 * @param	b		b value of image
	 */
	
	private static double[] RGBtoHSV(double r, double g, double b){

	    double h, s, v;
	    double min, max, delta;

	    min = Math.min(Math.min(r, g), b);
	    max = Math.max(Math.max(r, g), b);

	    // V
	    v = max;

	    delta = max - min;

	    // S
	     if( max != 0 )
	        s = delta / max;
	     else {
	        s = 0;
	        h = -1;
	        return new double[]{h,s,v};
	     }

	    // H
	     if( r == max )
	        h = ( g - b ) / delta; // between yellow & magenta
	     else if( g == max )
	        h = 2 + ( b - r ) / delta; // between cyan & yellow
	     else
	        h = 4 + ( r - g ) / delta; // between magenta & cyan

	     h *= 60;    // degrees

	     if( h < 0 )
	        h += 360;
	
	    return new double[]{h,s,v};
	}
	  
	
	/**
	 * Return if there's need to change Hue and Saturation or not
	 * 
	 * @return
	 */
	public boolean isResult() {
		return result;
	}
}

