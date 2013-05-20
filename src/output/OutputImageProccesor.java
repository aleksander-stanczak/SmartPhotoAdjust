package output;

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import ij.plugin.FFT;
import ij.plugin.Histogram;

public class OutputImageProccesor implements PlugInFilter {
	private ImagePlus imp;
	private String argument;
	private int w,h;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		this.argument = arg;
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		w = ip.getWidth();
		h = ip.getHeight();
		ip.invert();
		imp.updateAndDraw();
		IJ.wait(1500);
		ip.invert();
		imp.updateAndDraw();
		getFFTMatix(argument);
	}

	private void getFFTMatix(String arg) {
		FFT picture_fft_matrix = new FFT();
		picture_fft_matrix.run(arg);
		Histogram picture_fft_histogram = new Histogram();
		picture_fft_histogram.run(arg);
	}

	@Override
	public void run(ij.process.ImageProcessor arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
