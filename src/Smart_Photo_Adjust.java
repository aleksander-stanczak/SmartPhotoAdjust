
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;

import output.OutputGammaFilter;
import output.OutputHistogramEqualizationFilter;
import output.OutputImageProcessor;
import output.OutputSaturationFilter;

import neural_networks.Perceptron;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.*;
import input.InputGenerator;
import ij.io.Opener;
import ij.process.ImageProcessor;

public class Smart_Photo_Adjust implements PlugInFilter {

	private ImagePlus raw_image;
	
	@Override
	public void run(ImageProcessor ip) {
		
		// variables
		int[] inputVector = null;
		int[] outputVector = null;
		
		/////
		// find inputVector - information about unprocessed image 
		
		/*Opener opener = new Opener();  
		String imageFilePath = "C:\\Users\\Itryi\\Mateusz\\Studia\\mgr_semestr3\\CPOO\\Projekt\\rozmazane\\zdj_cie0108.jpeg";
		ImagePlus imp = opener.openImage(imageFilePath);
		ImageProcessor ip1 = imp.getProcessor();
		
		inputVector = InputGenerator.getInputVector(ip1);
		
		System.out.println("HERE");*/
		/////
		
		/////
		// process by neural network and suggest adjustments (as outputVector)
		
		// OPTION 1: default perceptron, learned from exemplary data inside it
		//Perceptron p = new Perceptron();
		//p.deltaRule();
		//--------------
		
		int[][] inputTrainingVector = null;
		int[][] outputTrainingVector = null;
		// OPTION 2: teach perceptron from outer class
		Perceptron p = new Perceptron(inputTrainingVector, outputTrainingVector);
		p.deltaRule();
		// -------------
		
		// use network to calculate output operations
		outputVector = p.getNetworkOutput(inputVector);
		
		/////
		// adjust image according to neural network
		
		
		// test output filter corrections
		// bits:		1/1/1/11/11									= 7 bits of output data
		// operations:	despeckle/sharpen/eq. hist./gamma/satur.	= 5 operations
		// exemplary output vectors (normally should be outputVector):
		//int[] processingParams = {1,1,1,1,1,1,1};
		int[] processingParams = {0,1,0,0,0,1,1};
		
		// processing image basing on output data
		OutputImageProcessor outputProcessor = new OutputImageProcessor(raw_image,ip);
		outputProcessor.processImage(processingParams);
		
		
		IJ.showMessage("Smart Photo Adjust Plugin","Automatic photo adjust conducted succesfully!");
		
	}


	@Override
	public int setup(String arg, ImagePlus imp) {
		
		raw_image = imp;
		
		return DOES_ALL;
	}
	
	


}
