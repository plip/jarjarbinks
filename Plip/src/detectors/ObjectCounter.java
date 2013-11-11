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

public class ObjectCounter {
	
	private int quantity;

	public void count() {
		
		Mat image = Highgui.imread(getClass().getResource("/44.jpg")
				.getPath());

		Mat imageWithEdges = edgeDetector(image);

		Mat detectedObjects = findContours(imageWithEdges);
		
		imageDistanceTransformer(detectedObjects);	
		
		System.out.println(quantity);
	}

	public Mat findContours( Mat filteredImage ) {
		quantity = 0;
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfPoint2f approx = new MatOfPoint2f();
		MatOfPoint2f mMOP2f1 = new MatOfPoint2f();
		MatOfPoint mMOP = new MatOfPoint();
		Scalar s1 = new Scalar(255, 0, 0);
		int count = 0;

		Imgproc.findContours(filteredImage, contours, new Mat(),
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Highgui.imwrite("contours.jpg", filteredImage);

		for (int i = 0; i < contours.size(); i++) {

			contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);

			Imgproc.approxPolyDP(mMOP2f1, approx,
					Imgproc.arcLength(mMOP2f1, true) * 0.02, true);
			approx.convertTo(mMOP, CvType.CV_32S);

			if (Imgproc.contourArea(approx) > 40000
					&& Imgproc.contourArea(approx) < 600000) {

				System.out.println(Imgproc.contourArea(approx));
				Imgproc.drawContours(filteredImage, contours, i, s1, -1);
				
				Highgui.imwrite("contour" + i + ".jpg", filteredImage);

				quantity++;
			}
		}
		
		Highgui.imwrite("contoursfilled.jpg", filteredImage);
		
		return filteredImage;
	}

	public Mat edgeDetector( Mat image ) {
		Mat medianBlur = new Mat();
		Mat bilateral = new Mat();
		Mat cannyBilateral = new Mat();
		Mat binary = new Mat();

		// Filter Create binary image from source image
		Imgproc.cvtColor(image, binary, Imgproc.COLOR_BGR2RGB);
//		binary = image;
		//Ycbcr 
		Imgproc.medianBlur(binary, medianBlur, 7);

		Imgproc.bilateralFilter(medianBlur, bilateral, 5, 230, 5, 1);

		Imgproc.Canny(bilateral, cannyBilateral, 35 , 85);
		
		Imgproc.dilate(cannyBilateral, cannyBilateral, new Mat(), new Point(-1,
				-1), 3);

		Highgui.imwrite("CannybilateralFilter.jpg", cannyBilateral);
		Highgui.imwrite("Cannybilateral.jpg", bilateral);

		return cannyBilateral;
	}
	
	public void imageDistanceTransformer( Mat detectedObjects ){
		
		// transformation
		Mat transform = new Mat();
		Mat thres = new Mat();
		Mat normalize = new Mat();

		Imgproc.distanceTransform(detectedObjects, transform,
				Imgproc.CV_DIST_L1, 3);
		Highgui.imwrite("transform.jpg", transform);

		Core.normalize(transform, normalize, 0, 255, Core.NORM_MINMAX);
		Highgui.imwrite("normalize.jpg", normalize);

		Imgproc.threshold(normalize, thres, 64, 128, Imgproc.THRESH_BINARY);
		Highgui.imwrite("thres.jpg", thres);
		
		//findContours(thres);
		
		
	}

}
