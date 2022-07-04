package client;

import dbcon.User;
import net.coobird.thumbnailator.Thumbnails;
import protocol.Protocol;
import protocol.Result;
import server.ServerView;
import dbcon.AesEnc;
import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.rmi.server.RMISocketFactory;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import static client.ClientView.client0;
import static protocol.Protocol.*;

/**
 * @Title Client
 * @Description �ͻ���
 * @Version 1.0
 */

public class Client implements  Runnable{

	private static final String CLIENT_KEY_STORE_PASSWORD       = "client";
	private static final String CLIENT_TRUST_KEY_STORE_PASSWORD = "serverc";

	public static SSLSocket socket;
	private static AesEnc aesEnc = new AesEnc();
	Robot robot;
	static boolean islive = false;
	public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static User user;
	public static SSLContext ctx1;

	/**
	 * init
	 */
	public Client() throws NoSuchAlgorithmException {
		try{
			ctx1 = SSLContext.getInstance("SSL");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore tks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream("../data/key/client.keystore"), CLIENT_KEY_STORE_PASSWORD.toCharArray());
			tks.load(new FileInputStream("../data/key/tclient.keystore"), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

			kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
			tmf.init(tks);
			ctx1.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			robot = new Robot();
		} catch (AWTException | NoSuchAlgorithmException | FileNotFoundException | KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}



	/**
	 * ����MD5
	 * @param source
	 * @return
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f'};// �������ֽ�ת����16���Ʊ�ʾ���ַ�
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();// MD5 �ļ�������һ�� 128 λ�ĳ�������
			// ���ֽڱ�ʾ���� 16 ���ֽ�
			char str[] = new char[16 * 2];// ÿ���ֽ��� 16 ���Ʊ�ʾ�Ļ���ʹ�������ַ��� ���Ա�ʾ�� 16
			// ������Ҫ 32 ���ַ�
			int k = 0;// ��ʾת������ж�Ӧ���ַ�λ��
			for (int i = 0; i < 16; i++) {// �ӵ�һ���ֽڿ�ʼ���� MD5 ��ÿһ���ֽ�// ת���� 16
				// �����ַ���ת��
				byte byte0 = tmp[i];// ȡ�� i ���ֽ�
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];// ȡ�ֽ��и� 4 λ������ת��,// >>>
				// Ϊ�߼����ƣ�������λһ������
				str[k++] = hexDigits[byte0 & 0xf];// ȡ�ֽ��е� 4 λ������ת��

			}
			s = new String(str);// ����Ľ��ת��Ϊ�ַ���

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * �ж��Ƿ���IP
	 * @param ipString
	 * @return �Ƿ���true �񷵻�false
	 */
	public static boolean isCorrectIp(String ipString) {
		//1���ж��Ƿ���7-15λ֮�䣨0.0.0.0-255.255.255.255.255��
		if (ipString.length()<7||ipString.length()>15) {
			return false;
		}
		//2���ж��Ƿ�����С����ֳ��Ķ�
		String[] ipArray = ipString.split("\\.");
		if (ipArray.length != 4) {
			return false;
		}
		for (int i = 0; i < ipArray.length; i++) {
			//3���ж�ÿ���Ƿ�������
			try {
				int number = Integer.parseInt(ipArray[i]);
				//4.�ж�ÿ�������Ƿ���0-255֮��
				if (number <0||number>255) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �ж�port�Ƿ�淶
	 * @param Port
	 * @return �Ƿ���true �񷵻�false
	 */
	public static boolean isCorrectPort(String Port){
		if (Port.indexOf(".") > 0)
			return false;
		int port = Integer.parseInt(Port);
		if (port < 0 || port > 0xFFFF)
			return false;
		return true;
	}

	/**
	 * ��ȡclient�������������еĽ���
	 * @return
	 */
	public static String getRunningProcess() throws IOException {
		Process proc;
		proc = Runtime.getRuntime().exec("tasklist");
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String info = br.readLine();
		StringBuilder result = new StringBuilder();
		info = br.readLine();
		info = br.readLine();
		while(info != null){
			if(info.indexOf("    ") > 0) {
				info = info.substring(0, info.indexOf("    "));
				result.append(info);
			}
			info = br.readLine();
		}
		return result.toString();
	}

	/**
	 * socket���ӷ����
	 * @param user
	 * @return ����map���socket dos dis
	 */
	public static HashMap connect(User user) {
		HashMap con = new HashMap();
		try{
//			SSLsocket = new SSLSocket(user.ServerIP, Integer.parseInt(user.ServerPort));
			socket = (SSLSocket) ctx1.getSocketFactory().createSocket(user.ServerIP, Integer.parseInt(user.ServerPort));
			con.put("socket", socket);
			con.put("dos", new DataOutputStream(socket.getOutputStream()));
			con.put("dis", new DataInputStream(socket.getInputStream()));
			System.out.println("[+]connected");
			return con;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[+]Socket wrong");
			return null;
		}
	}

	/**
	 * ��¼
	 */
	public static void login(User user, DataOutputStream dos) throws Exception {
		sendUser(Protocol.TYPE_LOGIN, user, dos);
	}

	/**
	 * ע��
	 */
	public static void register(User user, DataOutputStream dos) throws Exception {
		sendUser(Protocol.TYPE_REGISTER, user, dos);
	}

	/**
	 * �˳�
	 * @throws IOException
	 */
	public static void logout(User user, DataOutputStream dos, DataInputStream dis) throws Exception {
		sendUser(Protocol.TYPE_LOGOUT, user, dos);
		try {
			if (dos != null)
				dos.close();
			if (socket != null)
				socket.close();
			if (dis != null)
				dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static double getAccuracy(long size) {
		double accuracy;
		if (size < 900) {
			accuracy = 0.85;
		} else if (size < 2047) {
			accuracy = 0.6;
		} else if (size < 3275) {
			accuracy = 0.44;
		} else {
			accuracy = 0.4;
		}
		return accuracy;
	}

	/**
	 * ��ȡ����mac��ַ
	 * @param inetAddress
	 * @return ����mac��ַ
	 */
	private static String getLocalMac(InetAddress inetAddress) {
		try {
			//��ȡ��������ȡ��ַ
			byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				//�ֽ�ת��Ϊ����
				int temp = mac[i] & 0xff;
				String str = Integer.toHexString(temp);
				if (str.length() == 1) {
					sb.append("0" + str);
				} else {
					sb.append(str);
				}
			}
			return sb.toString();
		} catch (Exception exception) {
		}
		return null;
	}

	/**
	 * ����
	 */
	public BufferedImage getScreenShot(){
		return robot.createScreenCapture(new Rectangle(0, 0, screenSize.width, screenSize.height));
	}

	/**
	 * ����
	 * @param type
	 * @param user
	 * @throws IOException
	 */
	public static void sendUser(int type, User user, DataOutputStream dos) throws Exception {
		byte[] data = SerializeData(user);
		Protocol.send(type, dos, aesEnc.encrypt(data));
	}



	/**
	 * ͼƬ���ݱ��浽user������
	 * @param buff
	 */
	public static byte[] saveImage(BufferedImage buff, long desFileSize) throws IOException {
		byte[] bs = null;
		if (buff == null){
			islive = false;
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(buff, "png", baos);
		byte[] imageBytes = baos.toByteArray();
		baos.close();
		if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
			return imageBytes;
		}
		long srcSize = imageBytes.length;
		double accuracy = getAccuracy(srcSize / 1024);
		try {
			while (imageBytes.length > desFileSize * 1024) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
				Thumbnails.of(inputStream)
						.scale(accuracy)
						.outputQuality(accuracy)
						.toOutputStream(outputStream);
				imageBytes = outputStream.toByteArray();
			}

			System.out.println("[+]ͼƬѹ�� : ͼƬԭ��С=" + srcSize / 1024 + "+kb | ѹ�����С=" + imageBytes.length / 1024 + "kb");
		} catch (Exception e) {
			System.out.println("[+]ͼƬѹ��ʧ��!" + e);
		}
		return imageBytes;
	}

	/**
	 * ��ȡ����˷�����Ϣ
	 * @return msg
	 */
	public static String getMsg(DataInputStream dis) throws Exception {
		Result result = Protocol.getResult(dis);
		return new String(aesEnc.decrypt(result.getData()), "UTF-8");
	}

	/**
	 * ����ͼƬ
	 */
	public void sendImage(){
		BufferedImage bufferedImage = client0.getScreenShot();
		try {
			user.RunningProcess = getRunningProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			user.imageData = client0.saveImage(bufferedImage,1000);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			client0.sendUser(Protocol.TYPE_IMAGE,user,ClientView.dos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(user.Frequency);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		System.out.println("[+]start run!!");
		sendImage();
		while(client0.islive){
			Result result = Protocol.getResult(ClientView.dis);
			if(result.getType() == Protocol.TYPE_IMAGE){
				System.out.println("[+]sending image!!!");
				try {
					String recvmsg = new String(aesEnc.decrypt(result.getData()), "UTF-8");
					System.out.println("[+]recvmsg is " + recvmsg);
					int fre = Integer.parseInt(recvmsg.substring(0, recvmsg.indexOf(";")));
					String IllegalProcess = recvmsg.substring(recvmsg.indexOf(";")+1);
					if(IllegalProcess.equals("null")) //���쳣
						System.out.println("[+]nothing weird");
					else { //���ں���������
						System.out.println(IllegalProcess);
						ClientView.IllegalProcess = IllegalProcess;
						ClientView.clientAlert();
					}
					if(fre != 0)
						user.Frequency = fre;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("[+]Frequency is : "+user.Frequency);

				sendImage();
			}
			else{
				System.out.println("[+]Wrong! stop the monitor");
				client0.islive = false;
			}
		}
		System.out.println("close thread");
	}

	public static void stop(){
		System.exit(1);
	}

	public static void main(String[] args) throws IOException {
		user = new User();
		user.ClientIP = InetAddress.getLocalHost().getHostAddress();
		user.ClientMac = getLocalMac(InetAddress.getLocalHost());
		System.out.println("[+]client IP:"+user.ClientIP);
		System.out.println("[+]client mac:"+user.ClientMac);
		ClientView.ClientView(args, user);
	}
}