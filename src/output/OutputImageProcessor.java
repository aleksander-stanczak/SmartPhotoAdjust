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
		
		// first bit despeckle or not (if noise)
		if ( processingParams[0] == 1 ){
			// despeckle
			OutputDespeckleFilter df = new OutputDespeckleFilter();
			df.setup("1", img);
			df.run(ip);
		}
		
		// second bit sharpen or not (if blurred)
		if ( processingParams[1] == 1 ){
			// despeckle
			OutputSharpenFilter sf = new OutputSharpenFilter();
			sf.setup("1", img);
			sf.run(ip);
		}
		
		// 3rd & 4th bit gamma filter correction 
		// {0 - no correction; 1 - increase gamma; 2 - decrease gamma}
		System.out.println(processingParams[2]+""+processingParams[3]);
	}

}
