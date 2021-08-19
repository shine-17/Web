Thread safe하지 않은 클래스들, 스레드 작업 로직의 구조문제, 일부만 동기화한 문제, 동적으로도 구현


package app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
	public static String longToIp(long i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}
	
	static int pingCount;
	static int count;
		
	public static void main(String[] args) throws IOException {
		String startIp = "172.30.1.1";
		String endIp = "172.30.1.100";
		Long start = ipToLong(startIp);
		Long end = ipToLong(endIp);
		
		try {
			int threadCount = 20;
			
			Stack<Long> ipStack = new Stack<>();
			
			for(Long i=start; i<=end; i++) {
				ipStack.push(i);
			}

			ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			long beforeTime = System.currentTimeMillis();
			
			while(!ipStack.isEmpty()) {
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
//						ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
						
						String threadName = Thread.currentThread().getName();
						boolean isAlive = false;
						String ip = null;
						
						if(!ipStack.isEmpty()) {
							System.out.println("pop");
							
							synchronized (ipStack) {
								ip = longToIp(ipStack.pop());
							}
							
							try {
								InetAddress pingCheck = InetAddress.getByName(ip);
								isAlive = pingCheck.isReachable(1000);
								count++;
								
								if(isAlive == true)
									pingCount++;
								
								System.out.println("IP :"+ip + " Thread : "+threadName + " ping : "+isAlive);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				executorService.execute(runnable);
				
				//System.out.println("ipStack size : " +ipStack.size());
			}
			
			//스레드풀 종료
	        executorService.shutdown();
			
			long afterTime = System.currentTimeMillis();
			long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
			System.out.println("연산개수 : "+ count + " / 응답핑 : " + pingCount + " / 연산시간 : "+secDiffTime + "ms");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
