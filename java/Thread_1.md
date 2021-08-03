```
package app;

import java.net.InetAddress;

public class MyThread extends Thread {
	public static long pingCount;
	private long start;
	private long end;
	public static long count;
	
	public MyThread(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
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
	
	@Override
	public void run() {
		try {
			boolean isAlive = false;
			
			for(long i=start; i<=end; i++) {
				Long l = i;
				InetAddress pingcheck = InetAddress.getByName(intToIp(l.intValue()));
				isAlive = pingcheck.isReachable(1000);
				System.out.println("ip : "+intToIp(l.intValue()) + " / Thread : "+Thread.currentThread().getName() + " / 성공여부 : "+isAlive);
				count++;
				
				if(isAlive == true)
					pingCount++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
}
```
