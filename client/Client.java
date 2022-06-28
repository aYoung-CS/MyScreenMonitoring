package project.client;

import project.myutil.Protocol;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ��װ���ض˵ķ���
 * 
 * @author Administrator
 *
 */
public class Client {

	Socket socket;

	DataOutputStream dos = null;
	Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
	int width = (int) screensize.getWidth();
	int height = (int) screensize.getHeight();
	Robot robot;
	static boolean isLive = true;
	JButton button;

	public Client() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ӷ�����
	 */
	public void conn(String address, int port) {
		try {
			socket = new Socket(address, port);
			dos = new DataOutputStream(socket.getOutputStream());
			// dos.writeUTF("client");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ��Ļ��ͼ������
	 * 
	 * @return
	 */
	public BufferedImage getScreenShot() {
		BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
		return bfImage;
	}

	public void load() {
		byte[] bytes = "client".getBytes();
		Protocol.send(Protocol.TYPE_LOAD, bytes, dos);
	}

	public void sendImage(BufferedImage buff) {
		if (buff == null)
			return;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buff, "png", baos);
			Protocol.send(Protocol.TYPE_IMAGE, baos.toByteArray(), dos);
			baos.close();
			System.out.println("send file successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// �رտͻ��ˣ��ͷŵ���Դ
	public void close() {
		//�������������Ϣ
		Protocol.send(Protocol.TYPE_LOGOUT, new String("logout").getBytes(), dos);
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
	 * ͼƬ����
	 * 
	 * @param bfImage
	 * @param scale
	 * @return
	 */
	public BufferedImage scale(BufferedImage bfImage, double scale) {
		// ��ͼѹ��
		int width = bfImage.getWidth();
		int height = bfImage.getHeight();
		Image image = bfImage.getScaledInstance((int) (width * scale), (int) (height * scale), Image.SCALE_DEFAULT);
		BufferedImage tag = new BufferedImage((int) (width * scale), (int) (height * scale),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = tag.createGraphics();
		g.drawImage(image, 0, 0, null); // ������С���ͼ
		g.dispose();
		return tag;
	}

	/**
	 * ��ʾϵͳ����
	 */
	public void showSystemTray() {
		Image image = Toolkit.getDefaultToolkit().getImage("img/icon.png");
		final TrayIcon trayIcon = new TrayIcon(image);// ��������ͼ��
		trayIcon.setToolTip("��Ļ���ϵͳ\r\n�ͻ���");// ������ʾ����
		final SystemTray systemTray = SystemTray.getSystemTray();// ���ϵͳ���̶���

		final PopupMenu popupMenu = new PopupMenu(); // ���������˵�
		MenuItem item = new MenuItem("�˳�");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isLive = false;
				close();
			}
		});
		popupMenu.add(item);
		trayIcon.setPopupMenu(popupMenu);// Ϊ����ͼ��ӵ����˵�
		try {
			systemTray.add(trayIcon);// Ϊϵͳ���̼�����ͼ��
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		final Client client = new Client();
		client.showSystemTray();// ��ʾ����
		client.conn("192.168.0.4",33000);
		client.load();// ��¼
		client.showSystemTray();// ��ʾ����
		while (client.isLive) {
			client.sendImage(client.getScreenShot());
			try {
				Thread.sleep(50);
			} catch (InterruptedException ev) {
			}
		}
	}
}
