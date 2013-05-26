package input;

import ij.*;
import ij.plugin.filter.*;

public class InputColorBalanceCalc implements PlugInFilter {
	ImagePlus imp;
	private boolean result = false;
	@SuppressWarnings("unused")
	private String argument;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		this.argument = arg;
		return DOES_ALL;
	}

	@Override
	public void run(ij.process.ImageProcessor arg0) {
		// TODO Auto-generated method stub
			
		result = calulateColorBalance(arg0);
				    
	    IJ.showMessage(Boolean.toString(result));
						
	}
	
	/**
	 * Calculate if Color Balance is needed
	 * 
	 * @param	ip		image that will be marked
	 * @return			if blurred image
	 */
	private boolean calulateColorBalance(ij.process.ImageProcessor ip) {
		
		boolean colorbalance = false;
				
		int w = ip.getWidth();
		int h = ip.getHeight();
		int[] rgb = new int[3];
		
		//calculate histogram values for RGB
		int []hist_r = new int[256];
		int []hist_g = new int[256];
		int []hist_b = new int[256];
		
		for (int i=0; i< 256; i++) {
			hist_r[i] = 0;
			hist_g[i] = 0;
			hist_b[i] = 0;
		}
		
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				ip.getPixel(x,y,rgb);
				hist_r[rgb[0]] += 1;
				hist_g[rgb[1]] += 1;
				hist_b[rgb[2]] += 1;
			}
		}
		
		//Get min and max values of current image histogram
		int max_hist_r = 0; 
		int max_hist_b = 0;
		int max_hist_g = 0;
		
		for (int i=0; i<255; i++) {
			if (max_hist_r <hist_r[i]) {
				max_hist_r = hist_r[i];
			}
			if (max_hist_b < hist_b[i]) {
				max_hist_b = hist_b[i];
			}
			if (max_hist_g < hist_g[i]) {
				max_hist_g = hist_g[i];
			}
		}
		
		
		//Calculate if we need to do color balance
		double significatn_hist_perc_r = 0;
		double significatn_hist_perc_g = 0;
		double significatn_hist_perc_b = 0;
		
		significatn_hist_perc_r = (double) max_hist_r/20;
		significatn_hist_perc_g = (double) max_hist_g/20;
		significatn_hist_perc_b = (double) max_hist_b/20;
		
		int values_less_then_signif_r = 0;
		int values_less_then_signif_g = 0;
		int values_less_then_signif_b = 0;
		
		for (int i=0; i<255; i++) {
			if (hist_r[i] <significatn_hist_perc_r) {
				values_less_then_signif_r += 1;
			}
			if (hist_g[i] <significatn_hist_perc_g) {
				values_less_then_signif_g += 1;
			}
			if (hist_b[i] <significatn_hist_perc_b) {
				values_less_then_signif_b += 1;
			}
		}
		
		int sum_values_less_then_signif = values_less_then_signif_g + values_less_then_signif_r + values_less_then_signif_b;
		
		if (sum_values_less_then_signif > 200) {
			colorbalance = true;
		}
		
		return colorbalance;
	
	}
	
	/**
	 * Return if there's need to change ColorBalance or not
	 * 
	 * @return
	 */
	public boolean isResult() {
		return result;
	}

}

