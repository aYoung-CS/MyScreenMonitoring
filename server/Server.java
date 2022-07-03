package server;


import dbcon.DataBase;
import server.ServerView;

import javax.swing.text.View;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class Server {

	public static Map<String,Socket> client=new HashMap<String,Socket>(); //已连接client集合
	//	public static View view= new View();
	public static String curKey=null;
	public static boolean serverLive=true;
	public static int port = 33000;
	public static String SelfAddress;
	public static String HostName;

	public static void main(String[] args) {
		DataBase.DatabaseInit();
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HostName = ia.getHostName();
		SelfAddress = ia.getHostAddress();

		try {
			System.out.println("[+]start listening on " + SelfAddress + ":" + port);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			new Thread( new ServerView()).start();
			while(serverLive){
				Socket socket = serverSocket.accept();
				new Thread(new ServerShotHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
