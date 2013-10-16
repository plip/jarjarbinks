package detectors;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class WatershedSegmenter {

private Mat markers;

	public WatershedSegmenter(){
		super();
		this.markers=new Mat();
	}
		
	public void setMarkers( Mat markerImage ){
		markerImage.convertTo(markers, CvType.CV_32S);	        
	}

	public Mat process(Mat image) {
		Imgproc.watershed(image, markers);
		markers.convertTo(markers, CvType.CV_8U);
		return markers;
	}
}
