package client;

import dbcon.User;
import net.coobird.thumbnailator.Thumbnails;
import protocol.Protocol;
import protocol.Result;
import server.ServerView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import static client.ClientView.client0;
import static protocol.Protocol.*;

/**
 * @Title Client
 * @Description �ͻ���
 * @Version 1.0
 */

public class Client implements  Runnable{

	public static Socket socket;
	Robot robot;
	static boolean islive = false;
	public int loginType;
	public String Password;
	public String Username;
	public String repwd;
	public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static User user;
//	public static Result result;


	/**
	 * init
	 */
	public Client(){
		try{
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
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
			socket = new Socket(user.ServerIP, Integer.parseInt(user.ServerPort));
			con.put("socket", socket);
			con.put("dos", new DataOutputStream(socket.getOutputStream()));
			con.put("dis", new DataInputStream(socket.getInputStream()));
			System.out.println("connected");
			return con;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Socket wrong");
			return null;
		}
	}

	/**
	 * ��¼
	 */
	public static void login(User user, DataOutputStream dos) throws IOException {
		sendUser(Protocol.TYPE_LOGIN, user, dos);
	}

	/**
	 * ע��
	 */
	public static void register(User user, DataOutputStream dos) throws IOException {
		sendUser(Protocol.TYPE_REGISTER, user, dos);
	}

	/**
	 * �˳�
	 * @throws IOException
	 */
	public static void logout(User user, DataOutputStream dos, DataInputStream dis) throws IOException {
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
	public static void sendUser(int type, User user, DataOutputStream dos) throws IOException{
		byte[] data = SerializeData(user);
		Protocol.send(type, dos, data);
	}



	/**
	 * ͼƬ���ݱ��浽user������
	 * @param buff
	 */
	public static byte[] saveImage(BufferedImage buff, long desFileSize) throws IOException {
		byte[] bs = null;
		if (buff == null){
			islive = false;
			System.out.println("��׽ͼƬΪ��");
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

			System.out.println("��ͼƬѹ����  ͼƬԭ��С=" + srcSize / 1024 + "+kb | ѹ�����С=" + imageBytes.length / 1024 + "kb");
		} catch (Exception e) {
			System.out.println("��ͼƬѹ����msg=ͼƬѹ��ʧ��!" + e);
		}
		return imageBytes;
	}

	/**
	 * ��ȡ����˷�����Ϣ
	 * @return msg
	 */
	public static String getMsg(DataInputStream dis) throws UnsupportedEncodingException {
		Result result = Protocol.getResult(dis);
		return new String(result.getData(), "UTF-8");
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
		}

		try {
			Thread.sleep(user.Frequency);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		System.out.println("start run!!");
		sendImage();
		while(client0.islive){
			Result result = Protocol.getResult(ClientView.dis);
			if(result.getType() == Protocol.TYPE_IMAGE){
				System.out.println("sending image!!!");
				try {
					String recvmsg = new String(result.getData(), "UTF-8");
					int fre = Integer.parseInt(recvmsg.substring(0, recvmsg.indexOf(";")));
					String IllegalProcess = recvmsg.substring(recvmsg.indexOf(";")+1);
					if(IllegalProcess.equals("null")) //���쳣
						System.out.println("114514");
					else //���ں���������
						System.out.println(IllegalProcess);
					if(fre != 0)
						user.Frequency = fre;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.out.println("Frequency: "+user.Frequency);
				sendImage();
			}
			else{
				System.out.println("Wrong! stop the monitor");
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
		System.out.println("IP:"+user.ClientIP);
		System.out.println("mac:"+user.ClientMac);
		ClientView.ClientView(args, user);
	}
}