package output;

import ij.*;
import ij.plugin.filter.*;
import ij.WindowManager;

public class OutputBlurredProccessor implements PlugInFilter {
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
			
		result = calculateConvolvedImage(arg0);
				    
	    IJ.showMessage(Boolean.toString(result));
						
	}
	
	/**
	 * Calculate Blurred matrix and calculate blurred ratio
	 * 
	 * @param	arg0		image that will be marked
	 * @return			if blurred image
	 */
	private boolean calculateConvolvedImage(ij.process.ImageProcessor arg0) {
		
		//Convolution image with define kernel
		float[] kernel = {0,-1,0,-1,4,-1,0,-1,0};
		boolean blurred = false;
		arg0.convolve(kernel,3,3);
		
		//set blurred window as current image
		ImagePlus blurre_imp;
		blurre_imp = WindowManager.getCurrentImage();
		
		//get blurred image height&width
		int w = blurre_imp.getWidth();
  		int h = blurre_imp.getHeight();
  		
  		int piksel_ammount = w*h;
  		int piksel_count =0;
  		int max = 0;
  		double mean = 0;
  		
  		for (int x=0; x< w; x++) {
  			for (int y=0; y<h; y++) {
  				int [] piksels = new int [4];
  				int piksel;
  				
  				piksels = blurre_imp.getPixel(x,y);
  				piksel = piksels[0];
  				piksel_count += piksel;
  				
  				if (max < piksel) {
  					max = piksel;
  				}
  			}
  		}
  		
  		mean = (double) piksel_count/piksel_ammount;
  		
		if (mean < 1.75) {
			blurred = true; 
		}
		else if (max <200) {
			blurred = true;
		}
		
	    Window_Closer Wind_Clow = new Window_Closer();
	    Wind_Clow.run(argument);
		
		return blurred;
		
	}
	
	/**
	 * Return if there's blurred or not
	 * @return
	 */
	public boolean isResult() {
		return result;
	}

	
}
