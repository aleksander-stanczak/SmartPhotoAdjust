package neural_networks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;


public class Perceptron {
	
	final double MSE = 0.01;
	final double LEARNING_FACTOR = 0.01;
	final int MAX_ITERATIONS = (int) 10e6;
	
	public double final_mse=0;
	
	public double[][] weights;
	
	int[][] patterns = { 
		    { 0, 0, 0, 0 }, 
		    { 0, 0, 0, 1 }, 
		    { 0, 0, 1, 0 },
		    { 0, 0, 1, 1 }, 
		    { 0, 1, 0, 0 }, 
		    { 0, 1, 0, 1 }, 
		    { 0, 1, 1, 0 },
		    { 0, 1, 1, 1 }, 
		    { 1, 0, 0, 0 }, 
		    { 1, 0, 0, 1 } };
	
	int[][] teachingOutput = { 
		    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
		    { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
		    { 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 }, 
		    { 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
		    { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 }, 
		    { 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },
		    { 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 }, 
		    { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
		    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 } };
	
	
	int numberOfInputNeurons = patterns[0].length;
	int numberOfOutputNeurons = teachingOutput[0].length;
	
	int numberOfPatterns = patterns.length;
	
	public Perceptron() {

		System.out.println(numberOfInputNeurons);
		System.out.println(numberOfOutputNeurons);
		System.out.println(numberOfPatterns);
		
	    weights = new double[numberOfInputNeurons][numberOfOutputNeurons];
	    
	}
	
	public Perceptron(int[][] inputData, int[][] outputData) {

	    weights = new double[numberOfInputNeurons][numberOfOutputNeurons];
	    
	    patterns = inputData;
	    teachingOutput = outputData;
	    
	    numberOfPatterns = patterns.length;

	}
	
	
	public void deltaRule() {
		int iteratons = 0;
		double mse_error=Double.MAX_VALUE;
		double learningFactor = LEARNING_FACTOR;
		
		double delta = Double.MAX_VALUE;
		double old_mse = 0;
		
				
		while (mse_error > MSE && /*delta > Double.MIN_VALUE*10 &&*/ iteratons < MAX_ITERATIONS) {
			
			old_mse = mse_error;
			
			iteratons++;
						
			// over patterns
			for (int i = 0; i < numberOfPatterns; i++) {
				
				int[] output = setOutputValues(i);
				
				// over output
				for (int j = 0; j < numberOfOutputNeurons; j++) {
					
				    	// over input
				        for (int k = 0; k < numberOfInputNeurons; k++) {
			        	
				            weights[k][j] = weights[k][j] + learningFactor
				                    * patterns[i][k]
				                    * (teachingOutput[i][j] - output[j]);
				        }
				}
				
			 }
			
			// calculate Mean Square Error between network and training data set
			mse_error = calculateMSE();
			delta = Math.abs(mse_error-old_mse);
						
		}
		
		final_mse = mse_error;
		System.out.println("MSE: "+mse_error);
		System.out.println("No. of learning cycles: "+iteratons);
	
	}
	
	
	double calculateMSE(){
		
		double mse_error = 0;
		
		for (int i = 0; i < numberOfPatterns; i++){
			int[] output = setOutputValues(i);
			
			mse_error += /*Math.sqrt(*/(output[0] - teachingOutput[i][0])*(output[0] - teachingOutput[i][0])/*)*/;

		}
		mse_error /= numberOfPatterns;
		
		return mse_error;
	}
	
	int[] setOutputValues(int patternNo) {
		
		double bias = 0.7;
		int[] result = new int[numberOfOutputNeurons];
		int[] toImpress = patterns[patternNo];
		
		double net=0;
		
		for (int j = 0; j < result.length; j++) {
		    net = 0;
		    for (int i = 0; i < toImpress.length; i++)
		        net += weights[i][j] * toImpress[i];
		    if (net > bias)
		        result[j] = 1;
		    else
		        result[j] = 0;
		}
		//net = net*(1/(1+Math.exp(net)));
		
		return result;
	}
	
	public int[] getNetworkOutput(final int[] inputVector) {
		
		double bias = 0.7;
		int[] output = new int[numberOfOutputNeurons];
		int[] toImpress = inputVector;
		
		double net=0;
		
		for (int j = 0; j < output.length; j++) {
		    net = 0;
		    for (int i = 0; i < toImpress.length; i++)
		        net += weights[i][j] * toImpress[i];
		    if (net > bias)
		        output[j] = 1;
		    else
		        output[j] = 0;
		}
		//net = net*(1/(1+Math.exp(net)));
		
		return output;
	}
	
	public void printMatrix(double[][] matrix) {
		
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		        NumberFormat f = NumberFormat.getInstance();
		        if (f instanceof DecimalFormat) {
		            DecimalFormat decimalFormat = ((DecimalFormat) f);
		            decimalFormat.setMaximumFractionDigits(2);
		            decimalFormat.setMinimumFractionDigits(2);
		            System.out.print("(" + f.format(matrix[i][j]) + ")");
		        }
		    }
		    System.out.println();
		}
	}
	
	public static int returnInt(){
		return 666;
	}
	
	public static void main(String[] args) {
		
		Perceptron p = new Perceptron();
		System.out.println("Weights before training: ");
		p.printMatrix(p.weights);
		p.deltaRule();
		System.out.println("Weights after training: ");
		p.printMatrix(p.weights);
		
	}
	
}
