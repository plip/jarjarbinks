package recongizers;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class SurfDetector {

	public List<Mat> foundImagesDescriptors = new ArrayList<Mat>();
	public List<Mat> productImagesDescriptors = new ArrayList<Mat>();
	public List<String> foundImageNames = new ArrayList<String>();
	public List<String> productImagesNames = new ArrayList<String>();
	public DescriptorMatcher matcher;

	public SurfDetector() {
		super();
	}

	public SurfDetector(int matcher) {
		super();
		this.matcher = DescriptorMatcher.create(matcher);
		this.matcher.train();
	}

	public void detect(String imageRes, String sceneRes) {

		Mat img_object = Highgui.imread(getClass().getResource("/" + imageRes)
				.getPath());
		Mat img_scene = Highgui.imread(getClass().getResource("/" + sceneRes)
				.getPath());
		Imgproc.cvtColor(img_object, img_object, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(img_scene, img_scene, Imgproc.COLOR_BGR2GRAY);
		// -- Step 1: Detect the keypoints using SURF Detector

		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);

		MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();

		detector.detect(img_object, keypoints_object);
		detector.detect(img_scene, keypoints_scene);

		// -- Step 2: Calculate descriptors (feature vectors)
		DescriptorExtractor extractor = DescriptorExtractor
				.create(DescriptorExtractor.BRISK);

		Mat descriptors_object = new Mat();

		Mat descriptors_scene = new Mat();
		extractor.compute(img_object, keypoints_object, descriptors_object);
		extractor.compute(img_scene, keypoints_scene, descriptors_scene);

		// -- Step 3: Matching descriptor vectors using FLANN matcher
		DescriptorMatcher matcher = DescriptorMatcher
				.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		MatOfDMatch matches = new MatOfDMatch();

		matcher.match(descriptors_object, descriptors_scene, matches);

		double max_dist = 0;
		double min_dist = 100;
		// matches = matchesList.get(0);
		// -- Quick calculation of max and min distances between keypoints

		double avg = 0;
		double[] distances = new double[descriptors_object.rows()];
		for (int i = 0; i < descriptors_object.rows(); i++) {
			DMatch[] dmatches = matches.toArray();
			double dist = dmatches[i].distance;
			distances[i] = dist;
			if (dist < min_dist) {
				min_dist = dist;
				// System.out.println("-- Min dist : %f \n"+ min_dist );
			}
			if (dist > max_dist) {
				max_dist = dist;
				// System.out.println("-- Max dist : %f \n" + max_dist );
			}
		}

		System.out.println("-- Max dist : %f \n" + max_dist);
		System.out.println("-- Min dist : %f \n" + min_dist);

		// -- Draw only "good" matches (i.e. whose distance is less than
		// 3*min_dist )
		MatOfDMatch good_matches = new MatOfDMatch();

		for (int i = 0; i < descriptors_object.rows(); i++) {
			DMatch[] dmatches = matches.toArray();
			double dist = dmatches[i].distance;
			if (dist < 2 * min_dist || dist < 70) {
				good_matches.push_back(matches.row(i));
				avg += dist;
			}
		}
		avg = avg / (good_matches.rows());

		System.out.println("-- AVG : %f \n" + avg);
		System.out.println("-- #Matches : \n" + good_matches.rows());
		Mat img_matches = new Mat();
		MatOfByte bytes = new MatOfByte();
		Features2d.drawMatches(img_object, keypoints_object, img_scene,
				keypoints_scene, good_matches, img_matches, Scalar.all(-1),
				Scalar.all(-1), bytes, Features2d.NOT_DRAW_SINGLE_POINTS);

		// -- Localize the object
		MatOfPoint2f obj = new MatOfPoint2f();
		MatOfPoint2f scene = new MatOfPoint2f();
		;
		Point[] obj_good_points = new Point[good_matches.toArray().length];
		Point[] scene_good_points = new Point[good_matches.toArray().length];
		for (int i = 0; i < good_matches.toArray().length; i++) {
			DMatch[] good_dmatches = good_matches.toArray();
			KeyPoint[] keypoints_object_array = keypoints_object.toArray();
			KeyPoint[] keypoints_scene_array = keypoints_scene.toArray();

			// -- Get the keypoints from the good matches
			obj_good_points[i] = (keypoints_object_array[good_dmatches[i].queryIdx].pt);
			scene_good_points[i] = (keypoints_scene_array[good_dmatches[i].trainIdx].pt);
		}
		obj.fromArray(obj_good_points);
		try {
			Mat h = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 5.0);

			// -- Get the corners from the image_1 ( the object to be "detected"
			// )
			MatOfPoint2f obj_corners = new MatOfPoint2f();
			Point[] point_obj_corners = new Point[4];
			point_obj_corners[0] = new Point(0, 0);
			point_obj_corners[1] = new Point(img_object.cols(), 0);
			point_obj_corners[2] = new Point(img_object.cols(),
					img_object.rows());
			point_obj_corners[3] = new Point(0, img_object.rows());
			obj_corners.fromArray(point_obj_corners);
			// std::vector<Point2f> scene_corners(4);
			MatOfPoint2f scene_corners = new MatOfPoint2f();
			// Point[] point_scene_corners = new Point[4];
			// for(int i=0; i < 4; i++){
			// point_scene_corners[i] = new Point();
			// }
			// scene_corners.fromArray(point_scene_corners);
			Core.perspectiveTransform(obj_corners, scene_corners, h);

			// -- Draw lines between the corners (the mapped object in the scene
			// - image_2 )
			Point point1 = new Point(scene_corners.toArray()[0].x
					+ img_object.cols(), scene_corners.toArray()[0].y);
			Point point2 = new Point(scene_corners.toArray()[1].x
					+ img_object.cols(), scene_corners.toArray()[1].y);
			Point point3 = new Point(scene_corners.toArray()[2].x
					+ img_object.cols(), scene_corners.toArray()[2].y);
			Point point4 = new Point(scene_corners.toArray()[3].x
					+ img_object.cols(), scene_corners.toArray()[3].y);

			Point[][] pointsArray = new Point[1][4];
			Point[] points = { point1, point2, point3, point4 };
			pointsArray[0] = points;
			MatOfPoint pppoints = new MatOfPoint();
			pppoints.fromArray(points);
			List<MatOfPoint> list = new ArrayList<MatOfPoint>();
			list.add(pppoints);
			Imgproc.drawContours(img_matches, list, -1, new Scalar(0, 255, 0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// -- Show detected matches
		String filename = "objectDetected.jpg";

		Highgui.imwrite(filename, img_matches);

	}

	public double minDist(String imageRes, String sceneRes) {
		// Mat img_object = Highgui.imread(getClass().getResource("/" +
		// imageRes)
		// .getPath());
		// Mat img_scene = Highgui.imread(getClass().getResource("/" + sceneRes)
		// .getPath());
		// System.out.println(imageRes);
		// System.out.println(sceneRes);
		Mat img_object_src = Highgui.imread(imageRes);
		Mat img_scene_src = Highgui.imread(sceneRes);
		Mat img_object = new Mat();
		Mat img_scene = new Mat();
		Imgproc.cvtColor(img_object_src, img_object, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(img_scene_src, img_scene, Imgproc.COLOR_BGR2GRAY);
		// -- Step 1: Detect the keypoints using SURF Detector
		// Imgproc.GaussianBlur(img_scene, img_scene, new Size (5,5), 2.2, 2);
		// Imgproc.GaussianBlur(img_object, img_object, new Size (5,5), 2.2, 2);
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);

		MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();

		detector.detect(img_object, keypoints_object);
		detector.detect(img_scene, keypoints_scene);

		// -- Step 2: Calculate descriptors (feature vectors)
		DescriptorExtractor extractor = DescriptorExtractor
				.create(DescriptorExtractor.BRISK);

		Mat descriptors_object = new Mat();
		Mat descriptors_scene = new Mat();
		extractor.compute(img_object, keypoints_object, descriptors_object);
		extractor.compute(img_scene, keypoints_scene, descriptors_scene);

		// -- Step 3: Matching descriptor vectors using FLANN matcher

		DescriptorMatcher matcher = DescriptorMatcher
				.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		MatOfDMatch matches = new MatOfDMatch();

		matcher.match(descriptors_object, descriptors_scene, matches);

		double max_dist = 0;
		double min_dist = 100;
		// matches = matchesList.get(0);
		// -- Quick calculation of max and min distances between keypoints

		double avg = 0;
		double[] distances = new double[descriptors_object.rows()];
		for (int i = 0; i < descriptors_object.rows(); i++) {
			DMatch[] dmatches = matches.toArray();
			double dist = dmatches[i].distance;
			distances[i] = dist;
			if (dist < min_dist) {
				min_dist = dist;
			}
			if (dist > max_dist) {
				max_dist = dist;
			}
		}
		return min_dist;
	}

	public void recognize() {
		// File foundFolder = new File(getClass().getResource("/FoundObjects")
		// .getPath());
		// File productImageFolder = new
		// File(getClass().getResource("/ProductImages")
		// .getPath());
		// File[] foundListOfFiles = foundFolder.listFiles();
		// File[] productImageListOfFiles = productImageFolder.listFiles();

		computeDescriptors();
		for (int i = 0; i < foundImagesDescriptors.size(); i++) {

			for (int j = 0; j < productImagesDescriptors.size(); j++) {

				double dist = minDist(foundImagesDescriptors.get(i),
						productImagesDescriptors.get(j));
				if (dist < 50) {
					System.out.println(foundImageNames.get(i) + "-"
							+ productImagesNames.get(j) + "- Dist:" + dist);
				}
			}

		}
	}

	/* Calculate minDist between to image descriptors */

	public double minDist(Mat objectDescriptor, Mat productDescriptor) {

		double min_dist = -100;
		if (objectDescriptor != null && productDescriptor != null) {

			DescriptorMatcher matcher = this.matcher;
			if (matcher == null) {
				this.matcher = DescriptorMatcher
						.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
				this.matcher.train();
				matcher = this.matcher;
			}
			MatOfDMatch matches = new MatOfDMatch();

			matcher.match(objectDescriptor, productDescriptor, matches);

			double max_dist = 0;
			min_dist = 100;
			// matches = matchesList.get(0);
			// -- Quick calculation of max and min distances between keypoints

			double[] distances = new double[objectDescriptor.rows()];
			for (int i = 0; i < objectDescriptor.rows(); i++) {
				DMatch[] dmatches = matches.toArray();
				double dist = dmatches[i].distance;
				distances[i] = dist;
				if (dist < min_dist) {
					min_dist = dist;
				}
				if (dist > max_dist) {
					max_dist = dist;
				}
			}
		}
		return min_dist;
	}

	/* initialize detector get All image Descriptors */

	public void computeDescriptors() {
		File foundFolder = new File(getClass().getResource("/FoundObjects")
				.getPath());
		File productImageFolder = new File(getClass().getResource(
				"/ProductImages").getPath());
		File[] foundListOfFiles = foundFolder.listFiles();
		File[] productImageListOfFiles = productImageFolder.listFiles();

		for (int i = 0; i < foundListOfFiles.length; i++) {
			if (foundListOfFiles[i].isFile()) {
				String imagePath = foundListOfFiles[i].getPath();
				Mat objectImage = Highgui.imread(imagePath);
				Mat descriptors = extractImageDescriptor(objectImage);
				foundImagesDescriptors.add(descriptors);
				foundImageNames.add(foundListOfFiles[i].getName());
			}
		}
		for (int i = 0; i < productImageListOfFiles.length; i++) {
			if (productImageListOfFiles[i].isFile()
					&& !((productImageListOfFiles[i].getName())
							.equals(".DS_Store"))) {
				String imagePath = productImageListOfFiles[i].getPath();
				Mat productImage = Highgui.imread(imagePath);
				Mat descriptors = extractImageDescriptor(productImage);
				productImagesDescriptors.add(descriptors);
				productImagesNames.add(productImageListOfFiles[i].getName());
			}
		}
	}

	/* extract Image Descripto */

	public Mat extractImageDescriptor(Mat image) {
		Mat descriptors = new Mat();
		if (image != null) {
			Mat greyscale_image = new Mat();
			Imgproc.cvtColor(image, greyscale_image, Imgproc.COLOR_BGR2GRAY);
			MatOfKeyPoint keypoints = new MatOfKeyPoint();
			FeatureDetector detector = FeatureDetector
					.create(FeatureDetector.ORB);
			detector.detect(greyscale_image, keypoints);
			DescriptorExtractor extractor = DescriptorExtractor
					.create(DescriptorExtractor.BRISK);
			extractor.compute(greyscale_image, keypoints, descriptors);
		}
		return descriptors;
	}

}
