package Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.CRC32;

public class FileCRC32Check {
	
	public HashMap<String, Object> getResult(HashMap<File, Long> before_fileMap, HashMap<File, Long> after_fileMap) {
		Iterator<File> iter = before_fileMap.keySet().iterator();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		int nochange = 0, update = 0, delete = 0;
		
		while(iter.hasNext()) {
			File item = iter.next();
			Long before_crcValue = before_fileMap.get(item);
			Long after_crcValue = after_fileMap.get(item);
			
			if(after_crcValue == null) {
				delete++;
			} else if((long)before_crcValue == (long)after_crcValue) {
				nochange++;
			} else if((long)before_crcValue != (long)after_crcValue && (long)after_crcValue > 0) {
				update++;
			}
		}
		
		resultMap.put("nochange", nochange);
		resultMap.put("update", update);
		resultMap.put("delete", delete);
		
		return resultMap;
	}
	
	public HashMap<File, Long> getResult(String filePath) throws IOException {
		HashMap<File, Long> resultMap = new HashMap<File, Long>();
		
		// Recursive File Search
		return searchFile(filePath, resultMap);
	}
	
	// Recursive File Search
	public HashMap<File, Long> searchFile(String filePath, HashMap<File, Long> resultMap) throws IOException {
		File path = new File(filePath);
		File[] fList = path.listFiles();
		
		for(int i=0; i<fList.length; i++) {
			if(fList[i].isFile()) { // File
				long crcValue = getFileCRC32(fList[i].getPath());
				resultMap.put(fList[i], crcValue);
			} else if(fList[i].isDirectory()) { // Directory
				searchFile(fList[i].getPath(), resultMap);
			}
		}
		
		return resultMap;
	}
	
	// 파일 CRC32 값
	public static long getFileCRC32(String filePath) throws IOException {
		long crcValue = 0;   
	    CRC32 crc32 = new CRC32();   
	    BufferedInputStream in = null;
	    
	    try {
	    	in = new BufferedInputStream(new FileInputStream(filePath));
	    	byte buffer[] = new byte[32768];
	    	int length = 0;
	    	
	    	while ((length = in.read(buffer)) >= 0)
	    		crc32.update(buffer, 0, length);
	    	
			crcValue = crc32.getValue();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
	    } catch (IOException e) {   
	    	e.printStackTrace();
	    	return -2;
	    } finally {
	        if(in != null) {
	            in.close();
	        }
	    }
		
		return crcValue;
	}
		
}
