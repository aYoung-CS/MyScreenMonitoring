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
 * @Description 客户端
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
	 * 判断是否是IP
	 * @param ipString
	 * @return 是返回true 否返回false
	 */
	public static boolean isCorrectIp(String ipString) {
		//1、判断是否是7-15位之间（0.0.0.0-255.255.255.255.255）
		if (ipString.length()<7||ipString.length()>15) {
			return false;
		}
		//2、判断是否能以小数点分成四段
		String[] ipArray = ipString.split("\\.");
		if (ipArray.length != 4) {
			return false;
		}
		for (int i = 0; i < ipArray.length; i++) {
			//3、判断每段是否都是数字
			try {
				int number = Integer.parseInt(ipArray[i]);
				//4.判断每段数字是否都在0-255之间
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
	 * 判断port是否规范
	 * @param Port
	 * @return 是返回true 否返回false
	 */
	public static boolean isCorrectPort(String Port){
		int port = Integer.parseInt(Port);
		if (port < 0 || port > 0xFFFF)
			return false;
		return true;
	}

	/**
	 * 获取client端所有正在运行的进程
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
	 * socket连接服务端
	 * @param user
	 * @return 返回map存放socket dos dis
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
		Protocol.send(type, dos, data);
	}



	/**
	 * 图片数据保存到user对象中
	 * @param buff
	 */
	public static byte[] saveImage(BufferedImage buff, long desFileSize) throws IOException {
		byte[] bs = null;
		if (buff == null){
			islive = false;
			System.out.println("捕捉图片为空");
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

			System.out.println("【图片压缩】  图片原大小=" + srcSize / 1024 + "+kb | 压缩后大小=" + imageBytes.length / 1024 + "kb");
		} catch (Exception e) {
			System.out.println("【图片压缩】msg=图片压缩失败!" + e);
		}
		return imageBytes;
	}

	/**
	 * 获取服务端返回消息
	 * @return msg
	 */
	public static String getMsg(DataInputStream dis) throws UnsupportedEncodingException {
		Result result = Protocol.getResult(dis);
		return new String(result.getData(), "UTF-8");
	}

	/**
	 * 发送图片
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
					if(IllegalProcess.equals("null")) //无异常
						System.out.println("114514");
					else //存在黑名单进程
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