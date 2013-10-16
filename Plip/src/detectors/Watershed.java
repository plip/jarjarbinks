package detectors;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Watershed {

	/**
	 * @param args
	 */
	public void run(){
		
		Mat image = Highgui.imread(getClass().getResource("/Scene.jpg").getPath());
		Mat binary = new Mat();
		 
		// Create binary image from source image
  		Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2GRAY);
  		Imgproc.threshold(binary, binary,185,255,Imgproc.THRESH_BINARY);	

  		Mat fg = new Mat(); 
  	    Imgproc.erode(binary, fg, new Mat(), new Point(-1,-1), 2);
  		
  		// Threshold to obtain the peaks 
  		// This will be the markers for the foreground objects
  		 Mat bg = new Mat(); 	
  		 Imgproc.dilate(binary, bg, new Mat(), new Point(-1,-1),5 );
  		 Imgproc.threshold(bg, bg,1,128.,Imgproc.THRESH_BINARY_INV);

  		Mat markers= new Mat(binary.size(), CvType.CV_8U, new Scalar(0));
  		Core.add(bg,fg,markers);
  		
  	    WatershedSegmenter segmenter=new WatershedSegmenter();
  	    segmenter.setMarkers(markers);
  	    
  	    Mat result = segmenter.process(image);
  	    result.convertTo(result,CvType.CV_8U);

  	    String filename = "objectDetected.jpg";
		Highgui.imwrite( filename , result);

	}

}
