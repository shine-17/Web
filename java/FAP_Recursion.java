package Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FileSearch {
	private static HashMap<File, Object> fileMap = new HashMap<File, Object>();

	public static void main(String[] args) throws IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String filePath = "C:\\fap\\";
		searchFile(filePath); //최초 전체파일 탐색 후 CRC값 저장
		
//		String filePath_1 = br.readLine();
		
		System.out.println("---------------------");
		
		Iterator<File> iter = fileMap.keySet().iterator();
		
		while(iter.hasNext()) {
			File item = iter.next();
//			System.out.println(item.getPath() + " : " + fileMap.get(item));
			
			long crcValue = getFileCRC32(item.getPath());
			
//			System.out.println(item.getPath());
			System.out.println(crcValue);
			
//			System.out.println(item.getName() + " : " + fileMap.get(item));
//			System.out.println(item.getName() + " : " + crcValue);
			
			if((long)fileMap.get(item) == crcValue) {
//				System.out.println(item.getName() + " : 이상없음");
			} else if((long)fileMap.get(item) != crcValue) {
//				System.out.println("수정");
			} else {
//				System.out.println("삭제");
			}
				
		}
		
		
		
	}
	
	// 재귀
	private static void searchFile(String filePath) throws IOException {
		File path = new File(filePath);
		File[] fList = path.listFiles();
		
		for(int i=0; i<fList.length; i++) {
			if(fList[i].isFile()) { // 상위 File
				long crcValue = getFileCRC32(fList[i].getPath());
				System.out.println(crcValue);
				fileMap.put(fList[i], crcValue);
			} else if(fList[i].isDirectory()) { // 디렉토리 하위 File
				searchFile(fList[i].getPath());
			}
		}
	}
	
	private static long getFileCRC32(String filePath) throws IOException {
		long crcValue = -1;   
	    CRC32 crc32 = new CRC32();   
	    CheckedInputStream in = null;
	    
	    try {
	    	in = new CheckedInputStream(new FileInputStream(filePath), crc32);
	    	
			while (in.read() != -1)
				crcValue = crc32.getValue();
		} catch (FileNotFoundException e) {   
	        System.err.println("CheckedIODemo: " + e);   
	        System.exit(-1);   
	    } catch (IOException e) {   
	        System.err.println("CheckedIODemo: " + e);   
	        System.exit(-1);   
	    } finally {
	        if(in != null) {
	            in.close();
	        }
	    }
		
		return crcValue;
	}

}
