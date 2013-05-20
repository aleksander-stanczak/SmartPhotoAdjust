package app;

import neural_networks.Perceptron;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Upload test - Hello Mateo! ;)");

		System.out.println("App start");
		Perceptron p = new Perceptron();
		System.out.println("Weights before training: ");
		p.printMatrix(p.weights);
		p.deltaRule();
		System.out.println("Weights after training: ");
		p.printMatrix(p.weights);
		
		
		System.out.println("SmartPhotoAdjust closed");

	}

}
