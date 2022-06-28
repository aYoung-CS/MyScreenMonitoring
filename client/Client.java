package client;

import dbcon.User;
import protocol.Protocol;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

/**
 * @Title Client
 * @Description 客户端
 * @Version 1.0
 */

public class Client {

	Socket socket;
	Robot robot;
	private String serverIP = "192.168.0.4";
	private int serverPort = 33000;
	static boolean islive = true;
	public int loginType;
	public String Password;
	public String Username;
	public String repwd;
	public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	DataOutputStream dos = null;

	DataOutputStream dataOutputStream = null;

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

	public String setServerIP(){
		return serverIP;
	}
	public int setServerPort(){
		return serverPort;
	}

	/**
	 * socket连接服务端
	 */
	public void connect(){
		try{
			socket = new Socket(serverIP, serverPort);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登录
	 */
	public void login(){

	}

	/**
	 * 注册
	 */
	public void register(){

	}

	/**
	 * 退出
	 * @throws IOException
	 */
	public void logout() throws IOException {
		//向服务器发送消息
		Protocol.send(Protocol.TYPE_LOGOUT, dos,new String("logout").getBytes());
		// 关闭资源
		try {
			if (dos != null)
				dos.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void sendUser(int type,User user) throws IOException{
		byte[] data = SerializeData(user);
		try {
			switch (type){
				case 1:
					Protocol.send(Protocol.TYPE_REGISTER,dos,data);
				case 2:
					Protocol.send(Protocol.TYPE_LOGIN,dos,data);
				case 3:

			}
		}catch (Exception e){

		}
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public byte[] SerializeData(User user) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(user);
		byte[] bytes = bos.toByteArray();
		out.flush();
		return bytes;
	}


	/**
	 * 发送图片
	 * @param buff
	 */
	public void sendImage(BufferedImage buff) {
		if (buff == null)
			return;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buff, "png", baos);
			Protocol.send(Protocol.TYPE_IMAGE, dos, baos.toByteArray());
			baos.close();
			System.out.println("send file successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 系统托盘
	 */
	public void showSystemTray() {

	}

	public static void main(String[] args) throws IOException, InterruptedException {

		final Client client = new Client();
		client.connect();
		User user = new User();

		user.setUsername("kkfine");
		user.setPassword("kkfine");
		client.sendUser(Protocol.TYPE_LOGIN,user);

		/*
		client.load();// 登录

		client.showSystemTray();// 显示托盘
		while (client.isLive) {
			client.sendImage(client.getScreenShot());
			try {
				Thread.sleep(50);
			} catch (InterruptedException ev) {

			}
		}
		 */
		while(client.islive){
			BufferedImage bufferedImage = client.getScreenShot();
			client.sendImage(bufferedImage);
			Thread.sleep(50);
		}
	}
}