package output;

import ij.*;
import ij.plugin.filter.*;
import ij.plugin.FFT;
import ij.WindowManager;


public class OutputImageProccesor implements PlugInFilter {
	@SuppressWarnings("unused")
	private ImagePlus imp;
	private String argument;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		this.argument = arg;
		return DOES_ALL;
	}

	@Override
	public void run(ij.process.ImageProcessor ip) {
		
		double perc_value = getFFTMatix(argument);
	}
	
/**
 * Calculate FFT matrix and calculate noise ratio
 * 
 * @param	arg		name of image
 * @return			low frequency percentage
 */
	
	private double getFFTMatix(String arg) {
		
		//create and run FFT
		FFT picture_fft_matrix = new FFT();
		picture_fft_matrix.run(arg);
		
		//set FFT window as current image
		ImagePlus fft_imp;
		fft_imp = WindowManager.getCurrentImage();
		
		//get FFT image height&width
		int w = fft_imp.getWidth();
  		int h = fft_imp.getHeight(); 
  		
  		//histogram calc
  		int[] D = new int[256];

		for (int y=0; y<w-1; y++)
  		{
     		for (int x=0; x<h-1; x++)
    		 {		
     				int [] p = new int[2];
        			p = fft_imp.getPixel(y,x);
        			D[p[0]]=D[p[0]]+1;
     		} 
  		}
     
    	int[] Hi=new int[256];
     	Hi[0]=D[0];
     	
     	for(int i=1;i<256;i++)
    	{
        		Hi[i]=Hi[i-1]+D[i];
    	}
     	
     	//end histogram calc
     	
     	int max_D = D[0];	
     		
     	for (int i=1; i<256; i++) {
     		
     		if (max_D<D[i]) {
     			max_D = D[i];
     		}
     	}
     	
     	// calc low frequency percentage
     	double perc_content  = (double) Hi[1]/max_D;
     	
     	Window_Closer Wind_Clow = new Window_Closer();
     	Wind_Clow.run(argument);
     	
     	return perc_content;
	}

	
	
}
