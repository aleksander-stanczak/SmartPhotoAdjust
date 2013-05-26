package input;

public class InputGenerator {
	
	static public boolean[] getInputVector(){
		
		// gather info about image like contrast, histogram etc.
		boolean[] imageInfo = {false,false,false,false,false};
		
		InputNoiseCalc inc = new InputNoiseCalc();
		InputBlurredCalc ibc = new InputBlurredCalc();
		InputHueSaturationCalc ihsc = new InputHueSaturationCalc();
		InputColorBalanceCalc icbc = new InputColorBalanceCalc();
		InputBrightnessContrastCalc ibcc = new InputBrightnessContrastCalc();
		
		//inc.run(ip);
		imageInfo[0] = inc.isResult();
		
		//icbc.run(ip);
		imageInfo[1] = icbc.isResult();
		
		//ibcc.run(ip);
		imageInfo[2] = ibcc.isResult();
		
		//ihsc.run(ip);
		imageInfo[3] = ihsc.isResult();
		
		//ibc.run(ip);
		imageInfo[4] = ibc.isResult();
		
		return imageInfo;
	}

}
