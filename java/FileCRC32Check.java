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
	private static HashMap<File, Object> fileMap = new HashMap<File, Object>();
	private static Iterator<File> iter;
	
	public static HashMap<String, Object> getResult(String filePath) throws IOException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// Recursive File Search
		searchFile(filePath);
		
		iter = fileMap.keySet().iterator();
		int nochange = 0, update = 0, delete = 0;
		
		while(iter.hasNext()) {
			File item = iter.next();
			long crcValue = getFileCRC32(item.getPath());
			
			StringBuilder sb = new StringBuilder();
			sb.append(item.getName() + " is ");
			
			if((long)fileMap.get(item) == crcValue) {
				sb.append("no change");
				nochange++;
			} else if((long)fileMap.get(item) != crcValue) {
				sb.append("update");
				update++;
			} else {
				sb.append("delete");
				delete++;
			}
				
			sb.append(" [ before : " + fileMap.get(item) + " ] [ after : " + crcValue + " ] ");
			//System.out.println(sb);
		}
		resultMap.put("nochange", nochange);
		resultMap.put("update", update);
		resultMap.put("delete", delete);
		
		return resultMap;
	}
	
	// Recursive File Search
	public static void searchFile(String filePath) throws IOException {
		File path = new File(filePath);
		File[] fList = path.listFiles();
		
		for(int i=0; i<fList.length; i++) {
			if(fList[i].isFile()) { // File
				long crcValue = getFileCRC32(fList[i].getPath());
				fileMap.put(fList[i], crcValue);
			} else if(fList[i].isDirectory()) { // Directory
				searchFile(fList[i].getPath());
			}
		}
	}
	
	// 파일 CRC 값
	public static long getFileCRC32(String filePath) throws IOException {
		long crcValue = -1;   
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
	        System.exit(-1);   
	    } catch (IOException e) {   
	    	e.printStackTrace();
	        System.exit(-1);   
	    } finally {
	        if(in != null) {
	            in.close();
	        }
	    }
		
		return crcValue;
	}
		
}
