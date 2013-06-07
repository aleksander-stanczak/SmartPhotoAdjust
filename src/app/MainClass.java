package app;

import output.OutputImageProcessor;
import neural_networks.Perceptron;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("SmartPhotoAdjust loaded");
		
		
		/*Perceptron p = new Perceptron();
		System.out.println("Weights before training: ");
		p.printMatrix(p.weights);
		p.deltaRule();
		System.out.println("Weights after training: ");
		p.printMatrix(p.weights);*/
		
		OutputImageProcessor outputProcessor = new OutputImageProcessor(null, null);
		outputProcessor.processImage(new int[] {0,1,0,1});
		
		System.out.println("SmartPhotoAdjust closed");

	}

}
