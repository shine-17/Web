package app;

import java.io.IOException;

public class PingCheck  {
	// String -> Long
	public static Long ipToLong(String ip) {
		String[] ipArr = ip.split("\\.");
		long num = 0;
		
		for(int i=0; i<ipArr.length; i++) {
			int power = 3-i;
			num += Integer.parseInt(ipArr[i])%256 * Math.pow(256, power);
		}
		return num;
	}
	// long -> String
	public static String intToIp(long i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}
		
	public static void main(String[] args) throws IOException {
		String startIp = "172.30.1.1";
		String endIp = "172.30.1.100";
		Long start = ipToLong(startIp);
		Long end = ipToLong(endIp);
		long ipLength = end - start+1;
		
		try {
			MyThread.pingCount = 0;
			MyThread.count = 0;
			int threadCount = 20;
			
			MyThread[] threads = new MyThread[threadCount];
			
			long beforeTime = System.currentTimeMillis();
			
			for(int i=0; i<threads.length; i++) {
				long a = 0;
				long b = 0;
				
				if(ipLength % threadCount == 0 ) {
					a = start+i*(ipLength/threadCount);
					b = end-(threadCount-(i+1))*(ipLength/threadCount);
				} else {
					a = start+i*(ipLength/threadCount);
					b = a + (ipLength/threadCount-1);
					
					if(i == threads.length-1)
						b = end;
				}
				threads[i] = new MyThread(a, b);
				
				threads[i].start();
			}
			for(int i=0; i<threads.length; i++) {
				threads[i].join();
			}
			long afterTime = System.currentTimeMillis();
			long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
			System.out.println("응답핑 : " + MyThread.pingCount + " / 연산시간 : "+secDiffTime + "ms");
			System.out.println("연산개수 : "+MyThread.count);
		} catch (Exception e) {
			
		}
		
	}

}
