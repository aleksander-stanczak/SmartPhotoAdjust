
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;

import output.OutputGammaFilter;
import output.OutputHistogramEqualizationFilter;
import output.OutputSaturationFilter;

import neural_networks.Perceptron;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.*;
import input.InputGenerator;

public class Smart_Photo_Adjust implements PlugInFilter {

	private ImagePlus raw_image;
	
	@Override
	public void run(ImageProcessor ip) {
		
		// variables
		boolean[] inputVector = null;
		boolean[] outputVector = null;
		
		/////
		// find inputVector - information about unprocessed image 
		
		inputVector = InputGenerator.getInputVector();
		
		/////
		
		/////
		// process by neural network and suggest adjustments (as outputVector)
		
		
		Perceptron p = new Perceptron();
		//outputVector = p.getNetworkOutput(inputVector);
		/////
		
		/////
		// adjust image
		
		// Brighting image with use of gamma correction
		/*OutputGammaFilter gf = new OutputGammaFilter();
		gf.setup("0", raw_image);
		gf.run(ip);*/
		/*OutputHistogramEqualizationFilter hef = new OutputHistogramEqualizationFilter();
		hef.setup(null, raw_image);
		hef.run(ip);*/
		OutputSaturationFilter sf = new OutputSaturationFilter();
		sf.setup(null, raw_image);
		sf.run(ip);
		
		
		/////
		
		//IJ.showMessage("Smart Photo Adjust Plugin","Automatic photo adjust conducted succesfully!");
		
	}


	@Override
	public int setup(String arg, ImagePlus imp) {
		
		raw_image = imp;
		
		return DOES_ALL;
	}
	
	


}
