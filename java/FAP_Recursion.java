package Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FileSearch {
	private static HashMap<File, Object> fileMap = new HashMap<File, Object>();
	private static BufferedReader br;
	private static Iterator<File> iter;

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));
		
		long before = System.currentTimeMillis();
//		String filePath = "C:\\fap\\";
		String filePath = "C:\\BMJ\\webfonts";
		searchFile(filePath); //최초 전체파일 탐색 후 CRC값 저장
		
//		String filePath_1 = br.readLine();
		
//		changeFileData();
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
			System.out.println(sb);
		}
		
		System.out.println("------------------------------");
		System.out.println("no change : " + nochange);
		System.out.println("update : " + update);
		System.out.println("delete : " + delete);
		System.out.println("------------------------------");
		
		System.out.println("sec : " + (System.currentTimeMillis() - before));
	}
	
	// 재귀
	private static void searchFile(String filePath) throws IOException {
		File path = new File(filePath);
		File[] fList = path.listFiles();
		
		for(int i=0; i<fList.length; i++) {
			if(fList[i].isFile()) { // 상위 File
				long crcValue = getFileCRC32(fList[i].getPath());
				fileMap.put(fList[i], crcValue);
			} else if(fList[i].isDirectory()) { // 디렉토리 하위 File
				searchFile(fList[i].getPath());
			}
		}
	}
	
	// 파일 CRC 값
	private static long getFileCRC32(String filePath) throws IOException {
		long crcValue = -1;   
	    CRC32 crc32 = new CRC32();   
	    CheckedInputStream in = null;
	    
	    try {
	    	in = new CheckedInputStream(new FileInputStream(filePath), crc32);
	    	
			while (in.read() != -1);
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
	
	private static void changeFileData() throws IOException {
		ArrayList<String> randomList = new ArrayList<String>();
		iter = fileMap.keySet().iterator();
		
		while(iter.hasNext()) {
			randomList.add(iter.next().getPath());
		}
		
		for(int i=0; i<3; i++) {
			int random = (int) ((Math.random() * 120) / 10);
			String path = randomList.get(random);
			
			br = Files.newBufferedReader(Paths.get(path));
			String line = br.readLine();
			System.out.println(path + " : " + line);
			
			if(line == null) {
				line = "0";
			} else if(Integer.parseInt(line) < 100) {
				line = String.valueOf(Integer.parseInt(line) + 100);
			} else {
				line = String.valueOf(Integer.parseInt(line) - 100);
			}
			
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(path),Charset.forName("UTF-8"));
			bw.write(line);
			bw.close();
		}
		
	}

}
