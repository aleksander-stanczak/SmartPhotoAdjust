package input;

import ij.*;
import ij.plugin.filter.*;
import ij.WindowManager;

public class InputBrightnessContrastCalc implements PlugInFilter {
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
			
		result = calulateBrightnessContrast(arg0);
				    
	    IJ.showMessage(Boolean.toString(result));
						
	}
	
	/**
	 * Calculate if Color Brightness and Contrast need to by change
	 * 
	 * @param	ip		image that will be marked
	 * @return			if blurred image
	 */
	private boolean calulateBrightnessContrast(ij.process.ImageProcessor ip) {
		
		boolean brightnesscontrast = false;
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		//set image as current window
		ImagePlus bc_imp;
		bc_imp = WindowManager.getCurrentImage();
		
		//calculate histogram values
		int []hist = new int[256];
		
		for (int i=0; i<256; i++) {
			hist[i] = 0;
		}
		
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				int [] piksels = bc_imp.getPixel(x,y);
				int gray_value = (piksels[0]+piksels[1]+piksels[2])/3;
				hist[(int)gray_value] += 1;

			}
		}
		
		//find max value in histogram
		int max_hist_value = 0;
		
		for (int i=0; i<255; i++) {
			if (max_hist_value < hist[i]) {
				max_hist_value = hist[i];
			}
		}
		
		
		//calc 5 most highest or lowest frequencies
		int sum_first = 0;
		int sum_last = 0;
		
		for (int i=0; i<5; i++) {
			sum_first += hist[i];
			sum_last += hist[255-i];
		}
		
		//decide what to do
		int perc_of_max = max_hist_value/51;
		
		if ((sum_first <perc_of_max) ||(sum_last <perc_of_max)) {
			brightnesscontrast = true;
		}
		
	    Window_Closer Wind_Clow = new Window_Closer();
	    Wind_Clow.run(argument);
		
		return brightnesscontrast;
		
	}

	/**
	 * Return if there's need to change Brightness and Contrast or not
	 * 
	 * @return
	 */
	public boolean isResult() {
		return result;
	}
	
}

