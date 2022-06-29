package client;

import dbcon.User;
import javafx.application.Application;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import protocol.Protocol;
import server.ServerShotHandler;
import src.myutil.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * @Title Client
 * @Description �ͻ���
 * @Version 1.0
 */

public class Client {

	private static Socket socket;
	Robot robot;
	private String serverIP = "192.168.47.1";
	private int serverPort = 33000;
	static boolean islive = true;
	public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static DataOutputStream dos = null;
	private static BufferedReader bufferedReader = null;
	private static BufferedReader bufferedReader_Server = null;
	private static BufferedWriter bufferedWriter = null;
	private static DataInputStream din = null;
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
	public static void sendUser(int type, User user) throws IOException{
		byte[] data = SerializeData(user);
		try {
			switch (type){
				case 1:
					Protocol.send(Protocol.TYPE_REGISTER,dos,data);
					break;
				case 2:
					Protocol.send(Protocol.TYPE_LOGIN,dos,data);
					break;
				case 3:
					Protocol.send(Protocol.TYPE_LOGOUT,dos,data);
					break;
				case 4:
					Protocol.send(Protocol.TYPE_IMAGE,dos,data);
					break;
				default:
					break;
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
	public static byte[] SerializeData(User user) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(user);
		byte[] bytes = bos.toByteArray();
		out.flush();
		return bytes;
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
	 * ͼƬ���ݱ��浽user������
	 * @param buff
	 */
	public byte[] saveImage(BufferedImage buff,long desFileSize) throws IOException {
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
	 * ϵͳ����
	 */
	public void showSystemTray() {

	}

	public static String GetMsg() throws IOException {
		din = new DataInputStream(socket.getInputStream());
		System.out.println(din);
		Result result = Protocol.getResult(din);
		return new String(result.getData(),StandardCharsets.UTF_8);
	}

	public static void main(String[] args) throws Exception {

		final Client client = new Client();
		client.connect();
		ClientView.ClientView(args,dos,socket);

		User user = new User();
		user.setUsername("kkfine");
		user.setPassword("kkfine");

		client.sendUser(Protocol.TYPE_LOGIN,user);
		String res2 = GetMsg();
		System.out.println(res2);

		/*
		client.load();// ��¼
		client.showSystemTray();// ��ʾ����
		*/

		while(client.islive){
			BufferedImage bufferedImage = client.getScreenShot();
			user.imageData = client.saveImage(bufferedImage,100);
			client.sendUser(Protocol.TYPE_IMAGE,user);
			Thread.sleep(50);
			System.exit(1);;
		}
	}
}