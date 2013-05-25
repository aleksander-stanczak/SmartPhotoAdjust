package input;

public class InputGenerator {
	
	static public boolean[] getInputVector(){
		
		// gather info about image like contrast, histogram etc.
		boolean[] imageInfo = {false,false,false,false};
		
		InputNoiseCalc inc = new InputNoiseCalc();
		
		//inc.run(ip);
		//imageInfo[0] = inc.isResult();
		
		return imageInfo;
	}

}
