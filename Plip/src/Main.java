import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import detectors.ObjectCounter;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.loadLibrary("opencv_java246");

		ObjectCounter canny =new ObjectCounter();
		
		canny.count();

//		SurfDetector surfDetector = new SurfDetector();
//		surfDetector.detect("Lanzopral.jpg","_Lanzopral.jpg");
	}

}
