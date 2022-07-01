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
 * @Description 客户端
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
	 * socket连接服务端
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
	 * 登录
	 */
	public static void login(User user, DataOutputStream dos) throws IOException {
		sendUser(Protocol.TYPE_LOGIN, user, dos);
	}

	/**
	 * 注册
	 */
	public static void register(User user, DataOutputStream dos) throws IOException {
		sendUser(Protocol.TYPE_REGISTER, user, dos);
	}

	/**
	 * 退出
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
	 * 获取本地mac地址
	 * @param inetAddress
	 * @return 本地mac地址
	 */
	private static String getLocalMac(InetAddress inetAddress) {
		try {
			//获取网卡，获取地址
			byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				//字节转换为整数
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
	 * 截屏
	 */
	public BufferedImage getScreenShot(){
		return robot.createScreenCapture(new Rectangle(0, 0, screenSize.width, screenSize.height));
	}

	/**
	 * 发送
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
	 * 序列化
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
	 * 图片数据保存到user对象中
	 * @param buff
	 */
//	public byte[] saveImage(BufferedImage buff,long desFileSize) throws IOException {
//		byte[] bs = null;
//		if (buff == null){
//			islive = false;
//			System.out.println("捕捉图片为空");
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
//			System.out.println("【图片压缩】  图片原大小=" + srcSize / 1024 + "+kb | 压缩后大小=" + imageBytes.length / 1024 + "kb");
//		} catch (Exception e) {
//			System.out.println("【图片压缩】msg=图片压缩失败!" + e);
//		}
//		return imageBytes;
//	}

	/**
	 * 系统托盘
	 */
	public void showSystemTray() {

	}

	/**
	 * 获取服务端返回消息
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
		client.load();// 登录

		client.showSystemTray();// 显示托盘
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