package Aux;

import java.io.File;

public class ImageEraser {
	
	public void eraseFoundImages(){
		File foundFolder = new File(getClass().getResource("/FoundObjects")
				.getPath());
		File[] foundListOfFiles = foundFolder.listFiles();

		    for (int i = 0; i < foundListOfFiles.length; i++) {
		      if (foundListOfFiles[i].isFile()) {
		    	  foundListOfFiles[i].delete();
		      }
		    }
	}
}
