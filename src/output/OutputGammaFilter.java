package output;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;

public class OutputGammaFilter implements PlugInFilter {
	
	ImagePlus imp;
	private final double BRIGHTEN_GAMMA = 0.8;
	private final double BASE_GAMMA = 1;
	private final double DARKEN_GAMMA = 1.2;
	private double gamma_factor;

	@Override
	public void run(ij.process.ImageProcessor ip) {
		
		ip.gamma(gamma_factor);
		imp.updateAndDraw();
		IJ.showMessage("Gamma update conducted succesfully!");
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		
		switch (Integer.valueOf(arg)) {
		case 0:
			gamma_factor = BRIGHTEN_GAMMA;
			break;
		case 2:
			gamma_factor = DARKEN_GAMMA;
			break;
		default:
			gamma_factor = BASE_GAMMA;
			break;
		}
		
		this.imp = imp;
		return DOES_ALL;
	}
	
}
