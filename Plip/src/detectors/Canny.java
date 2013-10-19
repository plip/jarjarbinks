package detectors;

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


public class Canny {

	public void run(){
		
			Mat image = Highgui.imread(getClass().getResource("/16.jpg").getPath());
			Mat medianBlur = new Mat();
			Mat bilateral = new Mat();
			Mat cannyMedianBlur = new Mat();
			Mat cannyBilateral = new Mat();
			Mat binary= new Mat();
		
			
			
			//Filter  Create binary image from source image
			Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2GRAY);	  
			Imgproc.medianBlur(binary, medianBlur, 7);
			
			Imgproc.bilateralFilter(medianBlur, bilateral ,5, 230, 5,1);
			Imgproc.erode(bilateral, bilateral, new Mat(), new Point(-1,-1), 2);
			
			
			// Create binary image from source image
//	  		Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2GRAY);
//	  		Imgproc.threshold(binary, binary,185,255,Imgproc.THRESH_BINARY);	
	  		
	  		Imgproc.Canny(medianBlur, cannyMedianBlur, 22, 44);
	  		Imgproc.Canny(bilateral, cannyBilateral, 22, 44);
	  		
	  		
			Imgproc.dilate(cannyBilateral, cannyBilateral, new Mat(), new Point(-1,-1),5 );
	  		
	  	
			Highgui.imwrite( "bilateral.jpg" , cannyBilateral);
			Highgui.imwrite( "cannyBilateral4.jpg" , cannyBilateral);
			Highgui.imwrite( "cannyMedianBlur.jpg" , cannyMedianBlur);

	  		//FindContours
	  		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			MatOfPoint2f approx = new MatOfPoint2f();
			MatOfPoint2f mMOP2f1 = new MatOfPoint2f();
			MatOfPoint mMOP = new MatOfPoint();
			Scalar s1 = new Scalar(255,0,0);
			int count = 0;
	  		Imgproc.findContours(cannyBilateral, contours, new Mat(),
					Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	  		System.out.println(contours.size());
	  		
	  		//Imgproc.drawContours(canny, contours, -1, s1, -1);
	  		
	  		Highgui.imwrite( "contours.jpg" , cannyBilateral);
	  		Mat bigcountours = new Mat();
	  		for (int i = 0; i < contours.size(); i++) {
				contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
				Imgproc.approxPolyDP(mMOP2f1, approx,
						Imgproc.arcLength(mMOP2f1, true) * 0.02, true);
				approx.convertTo(mMOP, CvType.CV_32S);
//				System.out.println(Imgproc.contourArea(approx));
				if(Imgproc.contourArea(approx) > 70000 && Imgproc.contourArea(approx) < 5000000){
					count++;
//					System.out.println(Imgproc.contourArea(approx));
					Imgproc.drawContours(cannyBilateral, contours, i, s1, -1);
					Highgui.imwrite("contour"+i+".jpg", cannyBilateral);
				}
			}
	  		
//transformation
	  		
//	  		List<MatOfPoint> dcountours = new ArrayList<MatOfPoint>();
//	  		Imgproc.distanceTransform(canny, canny, Imgproc.CV_DIST_L2, 3);
//	  		Highgui.imwrite( "dist.jpg" , canny);
//	  		Imgproc.threshold(canny, canny, 64, 128, Imgproc.THRESH_BINARY);
//	  		Highgui.imwrite( "thres.jpg" , canny);
//	  		Imgproc.findContours(canny, dcountours, new Mat(),
//					Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	  		int realcount=0;
	  		
//	  		}
//	  		Highgui.imwrite("bigcontour.jpg", canny);
  		System.out.println(count);
}
	
}
