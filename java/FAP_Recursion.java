package Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FileSearch {
	private static CheckedInputStream in = null;
	private static HashMap<File, Object> fileMap = new HashMap<File, Object>();
	private static CRC32 crc32 = new CRC32();

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String filePath = "C:\\fap\\";
		searchFile(filePath); //최초 전체파일 탐색 후 CRC값 저장
		
		String filePath_1 = br.readLine();
		
		Iterator<File> iter = fileMap.keySet().iterator();
		while(iter.hasNext()) {
			File item = iter.next();
//			System.out.println(item.getPath() + " : " + fileMap.get(item));
			
			in = new CheckedInputStream(new FileInputStream(item.getPath()), crc32);
			long crcValue = -1;
			while (in.read() != -1)
			crcValue = crc32.getValue();
			
			System.out.println(item.getName() + " : " + fileMap.get(item));
			System.out.println(item.getName() + " : " + crcValue);
			
			if((long)fileMap.get(item) == crcValue) {
				System.out.println(item.getName() + " : 이상없음");
			} else if((long)fileMap.get(item) != crcValue) {
				System.out.println("수정");
			} else {
				System.out.println("삭제");
			}
				
		}
		
		
		
	}
	
	// 재귀
	private static void searchFile(String filePath) throws IOException {
		File path = new File(filePath);
		File[] fList = path.listFiles();
		
		for(int i=0; i<fList.length; i++) {
			
			if(fList[i].isFile()) { // 상위 File
				in = new CheckedInputStream(new FileInputStream(fList[i].getPath()), crc32);
				long crcValue = -1;
				while (in.read() != -1);
				crcValue = crc32.getValue();
				
				fileMap.put(fList[i], crcValue);
			} else if(fList[i].isDirectory()) { // 디렉토리 하위 File
				searchFile(fList[i].getPath());
			}
		}
	}

}
