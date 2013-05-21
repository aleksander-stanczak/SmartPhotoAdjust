package app;

import neural_networks.Perceptron;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("SmartPhotoAdjust loaded 2");
		
		
		Perceptron p = new Perceptron();
		System.out.println("Weights before training: ");
		p.printMatrix(p.weights);
		p.deltaRule();
		System.out.println("Weights after training: ");
		p.printMatrix(p.weights);
		
		
		System.out.println("SmartPhotoAdjust closed");

	}

}
