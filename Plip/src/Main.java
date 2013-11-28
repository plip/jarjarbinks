import java.io.File;

import org.opencv.features2d.DescriptorMatcher;

import Aux.ImageEraser;

import recongizers.SurfDetector;
import detectors.ObjectCounter;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.loadLibrary("opencv_java246");
		ImageEraser imageEraser = new ImageEraser();
		imageEraser.eraseFoundImages();
		ObjectCounter canny =new ObjectCounter();
	
		canny.count("55.jpg");

		SurfDetector surfDetector = new SurfDetector(DescriptorMatcher.BRUTEFORCE_HAMMING);
		surfDetector.recognize();
	}
	
	

}
