import org.opencv.features2d.DescriptorMatcher;

import recongizers.OrbBriskDetector;
import Aux.ImageEraser;
import detectors.ObjectCounter;


public class Main {

	public static void main(String[] args) {
		
		System.loadLibrary("opencv_java246");
		ImageEraser imageEraser = new ImageEraser();
		imageEraser.eraseFoundImages();
		ObjectCounter canny =new ObjectCounter();
	
		canny.count("scenes/1.jpg");

		OrbBriskDetector orbBriskDetector = new OrbBriskDetector(DescriptorMatcher.BRUTEFORCE_HAMMING);
		orbBriskDetector.recognize();
	}
	
	

}
