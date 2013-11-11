import detectors.ObjectCounter;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.loadLibrary("opencv_java246");

		ObjectCounter canny =new ObjectCounter();
		canny.count();
	}

}
