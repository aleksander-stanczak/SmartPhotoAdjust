package output;

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;

import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.*;

public class OutputSharpenFilter implements PlugInFilter {
	
	ImagePlus imp;

	@Override
	public void run(ij.process.ImageProcessor ip) {
		
		ip.sharpen();
		imp.updateAndDraw();
		IJ.showMessage("Sharpen conducted succesfully!");
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		
		this.imp = imp;
		return DOES_ALL;
	}
	
	


}
