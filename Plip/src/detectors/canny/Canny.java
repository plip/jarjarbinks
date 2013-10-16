package detectors.canny;

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
	
			
			Mat image = Highgui.imread(getClass().getResource("/IMG_0368.jpg").getPath());
			Mat binary = new Mat();
			
			// Create binary image from source image
			
			Imgproc.medianBlur(image, image, 9);
			
//			Highgui.imwrite( "nogreen.jpg" , image);
			Mat canny = new Mat();
			binary= image;
//	  		Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2GRAY);
//	  		Imgproc.bilateralFilter(binary, binary ,5, 5, 3, 2);
	  		Highgui.imwrite( "bw.jpg" , binary);
	  		Imgproc.Canny(binary, canny, 22, 44);
	  		
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
//					Highgui.imwrite("contour"+i+".jpg", canny);
				}
			}
	  		
	  		List<MatOfPoint> dcountours = new ArrayList<MatOfPoint>();
	  		Imgproc.distanceTransform(canny, canny, Imgproc.CV_DIST_L2, 3);
	  		Highgui.imwrite( "dist.jpg" , canny);
	  		Imgproc.threshold(canny, canny, 64, 128, Imgproc.THRESH_BINARY);
	  		Highgui.imwrite( "thres.jpg" , canny);
//	  		Imgproc.findContours(canny, dcountours, new Mat(),
//					Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	  		int realcount=0;
	  		
//	  		}
//	  		Highgui.imwrite("bigcontour.jpg", canny);
	  		System.out.println(count);
}
	
}
