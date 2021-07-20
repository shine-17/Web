package app;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Task implements Runnable {
	
	public Long ipToLong(String ip) {
		String[] ipArr = ip.split("\\.");
		long num = 0;
		
		for(int i=0; i<ipArr.length; i++) {
			int power = 3-i;
			num += Integer.parseInt(ipArr[i])%256 * Math.pow(256, power);
		}
		
		return num;
	}
	
	public static String intToIp(int i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}
	
	public synchronized void run() {
		InetAddress pingcheck = null;
		boolean isAlive = false;
		int count = 0;
		
		try {
			Thread.sleep(1000);
			
			//String endIp = br.readLine();
			String startIp = "172.30.1.1";
			String endIp = "172.30.1.10";
			Long ip1 = ipToLong(startIp);
			Long ip2 = ipToLong(endIp);
			
			System.out.println("startIp : "+ ip1);
			System.out.println("endIp : "+ intToIp(ip2.intValue()));
			
			long beforeTime = System.currentTimeMillis();
			for(long i=ip1; i<=ip2; i++) {
				Long l = i;
				pingcheck = InetAddress.getByName(intToIp(l.intValue()));
				isAlive = pingcheck.isReachable(1000);
				System.out.println("ip : "+pingcheck + "," + isAlive);
				System.out.println("ip : "+intToIp(l.intValue()) + " / Thread : "+Thread.currentThread().getName() + " / 성공여부 : "+isAlive);
				
				if(isAlive == true)
					count++;
			}
			long afterTime = System.currentTimeMillis();
			long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
			System.out.println("개수 : "+(ip2 - ip1));
			System.out.println("실행시간 : " + secDiffTime + " / 응답핑 수 : " + count);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class PingCheck  {
	public static void main(String[] args) throws IOException {
		BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1); 
//		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 1, TimeUnit.SECONDS, blockingQueue);
		
		Runnable task = new Task();
		Thread thread1 = new Thread(task);
		Thread thread2 = new Thread(task);
		Thread thread3 = new Thread(task);
		Thread thread4 = new Thread(task);
		Thread thread5 = new Thread(task);
		Thread thread6 = new Thread(task);
		Thread thread7 = new Thread(task);
		Thread thread8 = new Thread(task);
		Thread thread9 = new Thread(task);
		Thread thread10 = new Thread(task);
		
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
		thread6.start();
		thread7.start();
		thread8.start();
		thread9.start();
		thread10.start();
		
//		threadPoolExecutor.execute(task);
		
	}

}
