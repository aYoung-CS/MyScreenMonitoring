package client;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import dbcon.User;
import protocol.Protocol;
import protocol.Result;

import javax.imageio.ImageIO;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Title Client
 * @Description �ͻ���
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
	public DataOutputStream dos;
	public DataInputStream dis;
	public Result result;


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
	 * socket���ӷ����
	 */
	public void connect(){
		try{
			socket = new Socket(serverIP, serverPort);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��¼
	 */
	public void login(){

	}

	/**
	 * ע��
	 */
	public void register(){

	}

	/**
	 * �˳�
	 * @throws IOException
	 */
	public void logout() throws IOException {
		//�������������Ϣ
		Protocol.send(Protocol.TYPE_LOGOUT, dos,new String("logout").getBytes());
		// �ر���Դ
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
	public void sendUser(int type,User user) throws IOException{
		byte[] data = SerializeData(user);
		try {
			switch (type){
				case 1:
					System.out.println("client register");
					Protocol.send(Protocol.TYPE_REGISTER,dos,data);
					break;
				case 2:
					System.out.println("client login");
					Protocol.send(Protocol.TYPE_LOGIN,dos,data);
					break;
				case 3:
					System.out.println("client logout");
					Protocol.send(Protocol.TYPE_LOGOUT,dos,data);
					break;
				case 4:
					System.out.println("client send image");
					Protocol.send(Protocol.TYPE_IMAGE,dos,data);
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
	public byte[] SerializeData(User user) throws IOException {
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
	public byte[] saveImage(BufferedImage buff) {
		if (buff == null){
			islive = false;
			System.out.println("��׽ͼƬΪ��");
			return null;
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buff, "png", baos);
			byte[] res = baos.toByteArray();
			baos.close();
			System.out.println("save file successfully");
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ϵͳ����
	 */
	public void showSystemTray() {

	}

	/**
	 * ��ȡ����˷�����Ϣ
	 * @return msg
	 */
	public String getMsg() throws UnsupportedEncodingException {
		result = Protocol.getResult(dis);
		return new String(result.getData(), "UTF-8");
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		final Client client = new Client();
		client.connect();

		User user = new User();
		user.setUsername("ayoung12");
		user.setPassword("ayoung1");



		client.sendUser(Protocol.TYPE_REGISTER, user);
		System.out.println(client.getMsg());

		client.sendUser(Protocol.TYPE_LOGIN,user);
		System.out.println(client.getMsg());

		client.sendUser(Protocol.TYPE_LOGOUT,user);
		System.out.println(client.getMsg());

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