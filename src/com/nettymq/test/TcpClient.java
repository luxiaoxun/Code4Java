package com.nettymq.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Tcp client for echo server.
 * This client also receive message forwarded by netty MQ server
 */
public class TcpClient {
	
	private final static String serverString = "127.0.0.1";
	private final static int servPort = 18866;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		// Create socket that is connected to server on specified port
	    Socket socket = new Socket(serverString, servPort);
	    System.out.println("Connected to server...send echo string (quit to end)");
	    
	    final InputStream in = socket.getInputStream();
	    OutputStream out = socket.getOutputStream();
	    
	    BufferedReader inFromUser=new BufferedReader(new InputStreamReader(System.in));
	    
	    new Thread() {
			public void run() {
				while(true){
					byte[] readBytes =new byte[1024];
					int ret=0;
					try {
						ret = in.read(readBytes);
					} catch (IOException e) {
						break;
					}
					if(ret==-1){
						break;
					}
					String retString = new String(readBytes);
					System.out.println("Received : "+retString);
				}
			}
		}.start();
	    
	    while (true) {
	    	String msg =inFromUser.readLine();
	    	if(msg.equals("quit")){
	    		break;
	    	}
	    	out.write(msg.getBytes());
		}

	    socket.close();  // Close the socket and its streams
	}
}
