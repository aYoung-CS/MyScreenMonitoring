package server;

import dbcon.DataBase;
import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;;

public class Server {

	public static Map<String, SSLSocket> client=new HashMap<String,SSLSocket>();
	public static boolean serverLive=true;
	public static volatile int Port = 0;
	public static String SelfAddress;
	public static String HostName;
	private static final String SERVER_KEY_STORE_PASSWORD       = "server";
	private static final String SERVER_TRUST_KEY_STORE_PASSWORD = "clients";
	public static SSLServerSocket  ServerSocket;
	public static void stop(){
		System.exit(1);
	}
	public static void init() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
		try {
			SSLContext ctx = SSLContext.getInstance("SSL");

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore tks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream("key/server.keystore"), SERVER_KEY_STORE_PASSWORD.toCharArray());
			tks.load(new FileInputStream("key/tserver.keystore"), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());

			kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());
			tmf.init(tks);

			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			ServerSocket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(Port);
			ServerSocket.setNeedClientAuth(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		DataBase.DatabaseInit();
		if(!DataBase.IsTableExist()){
			DataBase.CreateTable();
		};
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
			System.out.println("[+]start listening on " + SelfAddress + ":" + Port);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			new Thread( new ServerView()).start();
			while(Port == 0){
				continue;
			}
			init();
			System.out.println("[+]start listening on " + SelfAddress + ":" + Port);
			while(serverLive){
				SSLSocket socket = (SSLSocket) ServerSocket.accept();
				new Thread(new ServerShotHandler(socket)).start();
			}
		} catch (IOException | UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
	}
}
