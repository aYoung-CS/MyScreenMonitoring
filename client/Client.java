package client;

import dbcon.User;
import protocol.Protocol;
import protocol.Result;

import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.HashMap;

/**
 * @Title Client
 * @Description �ͻ���
 * @Version 1.0
 */

public class Client {

	public static Socket socket;
	Robot robot;
	static boolean islive = true;
	public int loginType;
	public String Password;
	public String Username;
	public String repwd;
	public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
	 * socket���ӷ����
	 * @param user
	 * @return
	 */
	public static HashMap connect(User user){
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
			System.exit(1);
		}
		return null;
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
		try {
			switch (type){
				case 1:
					System.out.println("client register");
					Protocol.send(Protocol.TYPE_REGISTER,dos, data);
					break;
				case 2:
					System.out.println("client login");
					Protocol.send(Protocol.TYPE_LOGIN, dos, data);
					break;
				case 3:
					System.out.println("client logout");
					Protocol.send(Protocol.TYPE_LOGOUT, dos, data);
					break;
				case 4:
					System.out.println("client send image");
					Protocol.send(Protocol.TYPE_IMAGE, dos, data);
					break;
				default:
					break;
			}
		}catch (Exception e){
			System.out.println("error in senduser");
		}
	}

	/**
	 * ���л�
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public static byte[] SerializeData(User user) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(user);
		byte[] bytes = bos.toByteArray();
		out.flush();
		return bytes;
	}

	/**
	 * ͼƬ���ݱ��浽user������
	 * @param buff
	 */
//	public byte[] saveImage(BufferedImage buff,long desFileSize) throws IOException {
//		byte[] bs = null;
//		if (buff == null){
//			islive = false;
//			System.out.println("��׽ͼƬΪ��");
//			return null;
//		}
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ImageIO.write(buff, "png", baos);
//		byte[] imageBytes = baos.toByteArray();
//		baos.close();
//		if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
//			return imageBytes;
//		}
//		long srcSize = imageBytes.length;
//		double accuracy = getAccuracy(srcSize / 1024);
//		try {
//			while (imageBytes.length > desFileSize * 1024) {
//				ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
//				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
//				Thumbnails.of(inputStream)
//						.scale(accuracy)
//						.outputQuality(accuracy)
//						.toOutputStream(outputStream);
//				imageBytes = outputStream.toByteArray();
//			}
//
//			System.out.println("��ͼƬѹ����  ͼƬԭ��С=" + srcSize / 1024 + "+kb | ѹ�����С=" + imageBytes.length / 1024 + "kb");
//		} catch (Exception e) {
//			System.out.println("��ͼƬѹ����msg=ͼƬѹ��ʧ��!" + e);
//		}
//		return imageBytes;
//	}

	/**
	 * ϵͳ����
	 */
	public void showSystemTray() {

	}

	/**
	 * ��ȡ����˷�����Ϣ
	 * @return msg
	 */
	public static String getMsg(DataInputStream dis) throws UnsupportedEncodingException {
		Result result = Protocol.getResult(dis);
		return new String(result.getData(), "UTF-8");
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		User user = new User();
		user.ClientIP = InetAddress.getLocalHost().getHostAddress();
		user.ClientMac = getLocalMac(InetAddress.getLocalHost());
		System.out.println("IP:"+user.ClientIP);
		System.out.println("mac:"+user.ClientMac);
		ClientView.ClientView(args, user);

		/*
		client.load();// ��¼

		client.showSystemTray();// ��ʾ����
		*/
//		while(client.islive){
//			System.out.println("1");
//			BufferedImage bufferedImage = client.getScreenShot();
//			user.imageData = client.saveImage(bufferedImage);
//			client.sendUser(Protocol.TYPE_IMAGE,user);
//			Thread.sleep(50);
//			System.exit(1);;
//		}
	}
}