import java.util.ArrayList;
import java.util.List;

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
	
			
			Mat image = Highgui.imread(getClass().getResource("/IMG_0370.jpg").getPath());
			Mat binary = new Mat();
			binary=image;
			// Create binary image from source image
			Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2GRAY);
	  		Highgui.imwrite( "bw.jpg" , binary);
	  		Imgproc.threshold(binary, binary, 498, 500, Imgproc.THRESH_TOZERO_INV);
	  		Imgproc.medianBlur(binary, binary, 11);
	  		Imgproc.equalizeHist(binary, binary);
	  		Highgui.imwrite( "equ.jpg" , binary);
	  		
	  		Highgui.imwrite( "t.jpg" , binary);
			
			
//			Highgui.imwrite( "nogreen.jpg" , image);
			Mat canny = new Mat();
//			binary= image;
	  		
//	  		Imgproc.bilateralFilter(binary, binary ,5, 5, 3, 2);
	  		
	  		Imgproc.Canny(binary, canny, 25, 50);
	  		
	  		Highgui.imwrite( "canny.jpg" , canny);
	  		Imgproc.dilate(canny, canny, new Mat(), new Point(-1,-1), 6);
	  		Highgui.imwrite( "cannye.jpg" , canny);
	  		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			MatOfPoint2f approx = new MatOfPoint2f();
			MatOfPoint2f mMOP2f1 = new MatOfPoint2f();
			MatOfPoint mMOP = new MatOfPoint();
			Scalar s1 = new Scalar(255,0,0);
			int count = 0;
	  		Imgproc.findContours(canny, contours, new Mat(),
					Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	  		System.out.println(contours.size());
//	  		Imgproc.drawContours(canny, contours, -1, s1, -1);
	  		
//	  		Highgui.imwrite( "contours.jpg" , canny);
	  		Mat bigcountours = new Mat();
	  		for (int i = 0; i < contours.size(); i++) {
				contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
				Imgproc.approxPolyDP(mMOP2f1, approx,
						Imgproc.arcLength(mMOP2f1, true) * 0.02, true);
				approx.convertTo(mMOP, CvType.CV_32S);
//				System.out.println(Imgproc.contourArea(approx));
				if(Imgproc.contourArea(approx) > 100000 && Imgproc.contourArea(approx) < 5000000){
					count++;
//					System.out.println(Imgproc.contourArea(approx));
					Imgproc.drawContours(canny, contours, i, s1, -1);
//				 	Highgui.imwrite("contour"+i+".jpg", canny);
				}
			}
	  		Highgui.imwrite("contours.jpg", canny);
	  		List<MatOfPoint> dcontours = new ArrayList<MatOfPoint>();
	  		Imgproc.distanceTransform(canny, canny, Imgproc.CV_DIST_L1, 3);
	  		
	  		Highgui.imwrite( "dist.jpg" , canny);
	  		Imgproc.threshold(canny, canny, 64, 128, Imgproc.THRESH_BINARY);
	  		Highgui.imwrite( "thres.jpg" , canny);
//			
	  		Mat other = new Mat();
	  		canny.convertTo(other, CvType.CV_8UC1);
	  		
	  		int realcount=0;
	  		Imgproc.findContours(other, dcontours, new Mat(),
					Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	  		Imgproc.drawContours(other, dcontours,-1, s1);
	  		Highgui.imwrite( "other.jpg" , other);
//	  		}
//	  		Highgui.imwrite("bigcontour.jpg", canny);
	  		System.out.println(count);
	  		System.out.println(dcontours.size());
}
	
}
