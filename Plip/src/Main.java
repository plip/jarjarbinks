

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import detectors.Canny;

public class Main {

	/**
	 * @param args
	 */
	public void run(){
		
/*		Mat image = Highgui.imread(getClass().getResource("/prueba2.jpg").getPath());
		Mat binary = new Mat();
		
		// Create binary image from source image
		Imgproc.medianBlur(image, image, 9);
		
//		Highgui.imwrite( "nogreen.jpg" , image);
		Mat canny = new Mat();
  		Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2GRAY);
  		Highgui.imwrite( "bw.jpg" , binary);
//  		Imgproc.Canny(binary, canny, 20, 30);
  		Highgui.imwrite( "canny.jpg" , canny);
  		
  		Imgproc.threshold(binary, binary,185,255,Imgproc.THRESH_BINARY);	
  		Highgui.imwrite( "binary.jpg" , binary);
  		Mat fg = new Mat(); 
  	    Imgproc.erode(binary, fg, new Mat(), new Point(-1,-1), 2);
  	    Highgui.imwrite( "erode.jpg" , binary);
  		// Threshold to obtain the peaks 
  		// This will be the markers for the foreground objects
  		 Mat bg = new Mat(); 	
  		 Imgproc.dilate(binary, bg, new Mat(), new Point(-1,-1),5 );
  		 Highgui.imwrite( "dilate.jpg" , binary);
  		 Imgproc.threshold(bg, bg,1,128.,Imgproc.THRESH_BINARY_INV);
  		 
  		 Mat markers= new Mat(binary.size(), CvType.CV_8U, new Scalar(0));
  		 Core.add(bg,fg,markers);
  		
  	  //  WatershedSegmenter segmenter=new WatershedSegmenter();
  	   // segmenter.setMarkers(markers);
  	    
  	//    Mat result = segmenter.process(image);
  	 */
	}
	
	public static void main(String[] args) {
		System.loadLibrary("opencv_java246");
//		Main detector = new Main();
//		detector.run();	
		Canny canny = new Canny();
		canny.run();
	}
}
