package output;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class OutputImageProcessor {
	
	private ImagePlus img;
	private ImageProcessor ip;
	
	
	public OutputImageProcessor(ImagePlus img, ImageProcessor ip) {
		super();
		this.img = img;
		this.ip = ip;
	}


	public void processImage(int[] processingParams){
		
		// 1st bit despeckle or not (if noise)
		if ( processingParams[0] == 1 ){
			// despeckle
			OutputDespeckleFilter df = new OutputDespeckleFilter();
			df.setup("1", img);
			df.run(ip);
		}
		
		// 2nd bit sharpen or not (if blurred)
		if ( processingParams[1] == 1 ){
			// despeckle
			OutputSharpenFilter sf = new OutputSharpenFilter();
			sf.setup("1", img);
			sf.run(ip);
		}
		
		// 3rd bit equalize histogram or not (if something in contrast or lighting is wrong)
		if ( processingParams[2] == 1 ){
			// equalize histogram
			OutputHistogramEqualizationFilter hef = new OutputHistogramEqualizationFilter();
			hef.setup("1", img);
			hef.run(ip);
		}
		
		// 4th & 5th bit gamma filter correction 
		// {1 - no correction; 0 - decrease gamma; 2 - increase gamma}
		if ( processingParams[3]+processingParams[4] != 1 ){
			// despeckle
			OutputGammaFilter gf = new OutputGammaFilter();
			gf.setup(String.valueOf(processingParams[3]+processingParams[4]), img);
			gf.run(ip);
		
		}
		
		// 6th & 7th bit saturation correction
		// {1 - no correction; 0 - decrease saturation; 2 - increase saturation}
		if ( processingParams[5]+processingParams[6] != 1 ){
			// correct gamma
			OutputSaturationFilter sf = new OutputSaturationFilter();
			sf.setup(String.valueOf(processingParams[5]+processingParams[6]), img);
			sf.run(ip);
		
		}
	}

}
