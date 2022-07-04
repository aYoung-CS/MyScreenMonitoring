package client;

import dbcon.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import static client.Client.*;


//public String User;
public class ClientView extends Application {
    public static void ClientView(String[] args){
        Application.launch(args);
    }
    //    ��ر���
    public String Username;
    public String Password;
    public String repwd;
    public String ServerIP;
    public String ServerPort;
    public int type;
    private TrayIcon trayIcon;
    private static Socket socket;
    public static DataOutputStream dos = null;
    public static DataInputStream dis = null;
    public static boolean status=false;
    private int signA=0;//����
    private int signB=0;
    private int signC=0;
    public static User user;
    public static Client client0;

    static {
        try {
            client0 = new Client();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String IllegalProcess = null;
    public static Alert alert;
    public static void ClientView(String[] args, User user1){
        user = user1;
        Application.launch(args);
    }

    public static void clientAlert() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alert.setTitle("�쳣����");
                alert.setHeaderText("�������к��������������̹ر�");
                alert.setContentText(IllegalProcess);
                if(!alert.isShowing())
                    alert.showAndWait();
            }
        });
    }

    @Override
    public void start(Stage Client) throws Exception {


        System.out.println("start...");
        alert = new Alert(Alert.AlertType.WARNING);

//CreateButton
//��ҳ��ť
        Button button1 = new Button("  ��¼  ");
        button1.setLayoutX(350);
        button1.setLayoutY(280);

        Button button2 = new Button("  ע��  ");
        button2.setLayoutX(350);
        button2.setLayoutY(320);
//��¼��ť
        Button button3 = new Button("  ȷ�ϵ�¼  ");
        button3.setLayoutX(290);
        button3.setLayoutY(400);

        Button button4 = new Button("   ȡ��   ");
        button4.setLayoutX(410);
        button4.setLayoutY(400);
//ע�ᰴť
        Button button5 = new Button("  ȷ��ע��  ");
        button5.setLayoutX(290);
        button5.setLayoutY(400);

        Button button6 = new Button("    ȡ��   ");
        button6.setLayoutX(410);
        button6.setLayoutY(400);
//�ͻ��˽��水ť
        Button button7 = new Button("    �˳�   ");
        button7.setLayoutX(300);
        button7.setLayoutY(400);

        Button button8 = new Button("    ��ʼ���/�޸�Ƶ��    ");
        button8.setLayoutX(400);
        button8.setLayoutY(400);

//CreateLabel
//��¼��ǩ
        Label label1 = new Label("�û���");
        label1.setLayoutX(250);
        label1.setLayoutY(200);

        Label label2 = new Label("����");
        label2.setLayoutX(250);
        label2.setLayoutY(250);

        Label label3 = new Label("serverIP");
        label3.setLayoutX(250);
        label3.setLayoutY(300);

        Label label4 = new Label("serverPort");
        label4.setLayoutX(250);
        label4.setLayoutY(350);
//ע���ǩ
        Label label5 = new Label("�û���");
        label5.setLayoutX(250);
        label5.setLayoutY(150);

        Label label6 = new Label("����");
        label6.setLayoutX(250);
        label6.setLayoutY(200);

        Label label7 = new Label("repwd");
        label7.setLayoutX(250);
        label7.setLayoutY(250);

        Label label8 = new Label("ServerIP");
        label8.setLayoutX(250);
        label8.setLayoutY(300);

        Label label9 = new Label("ServerPort");
        label9.setLayoutX(250);
        label9.setLayoutY(350);

//�ͻ��˽����ǩ
        Label label12 = new Label("���Ƶ��");
        label12.setLayoutX(250);
        label12.setLayoutY(300);

//CreateTextField
//��¼�ı���
        TextField textField1 = new TextField ();
        textField1.setLayoutX(340);
        textField1.setLayoutY(200);

        TextField textField2 = new TextField ();
        textField2.setLayoutX(340);
        textField2.setLayoutY(300);

        TextField textField3 = new TextField ();
        textField3.setLayoutX(340);
        textField3.setLayoutY(350);

//ע���ı���
        TextField textField4 = new TextField ();
        textField4.setLayoutX(340);
        textField4.setLayoutY(150);

        TextField textField5 = new TextField ();
        textField5.setLayoutX(340);
        textField5.setLayoutY(300);

        TextField textField6 = new TextField ();
        textField6.setLayoutX(340);
        textField6.setLayoutY(350);

//�ͻ��˽����ı���
        TextField textField9 = new TextField ();
        textField9.setLayoutX(340);
        textField9.setLayoutY(300);

        textField9.setOnAction((ActionEvent e) -> {
            user.Frequency = Integer.parseInt(textField9.getText());
            System.out.println(user.Frequency);
        });


//CreatepasswordField
//��¼�����
        PasswordField passwordField1 = new PasswordField();
        passwordField1.setLayoutX(340);
        passwordField1.setLayoutY(250);

        passwordField1.setOnAction((ActionEvent e) -> {
            Password = passwordField1.getText();
            System.out.println(Password);
        });

//ע�������
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setLayoutX(340);
        passwordField2.setLayoutY(200);

        PasswordField passwordField3 = new PasswordField();
        passwordField3.setLayoutX(340);
        passwordField3.setLayoutY(250);

// CreateText
// ��¼Text
        Text text8 = new Text("");
        Font font8 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text8.setFont(font8);
        text8.setLayoutX(550);
        text8.setLayoutY(220);
        text8.setFill(Color.BROWN);
        Text text9 = new Text("");
        Font font9 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text9.setFont(font9);
        text9.setLayoutX(550);
        text9.setLayoutY(270);
        text9.setFill(Color.BROWN);
        Text text10 = new Text("");
        Font font10 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text10.setFont(font10);
        text10.setLayoutX(550);
        text10.setLayoutY(320);
        text10.setFill(Color.BROWN);
        Text text11 = new Text("");
        Font font11 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text11.setFont(font11);
        text11.setLayoutX(550);
        text11.setLayoutY(370);
        text11.setFill(Color.BROWN);

//�ͻ��˼��Text
        Text text12 = new Text("");
        Font font12 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text12.setFont(font12);
        text12.setLayoutX(550);
        text12.setLayoutY(320);
        text12.setFill(Color.BROWN);

//ע�������ж��Ƿ�һ��
        Text text1 = new Text("");
        Font font1 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text1.setFont(font1);
        text1.setLayoutX(550);
        text1.setLayoutY(220);
        text1.setFill(Color.BROWN);
        Text text2 = new Text("");
        Font font2 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text2.setFont(font2);
        text2.setLayoutX(550);
        text2.setLayoutY(270);
        text2.setFill(Color.BROWN);
//ע��Text
        Text text3 = new Text("");
        Font font3 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text3.setFont(font3);
        text3.setLayoutX(550);
        text3.setLayoutY(170);
        text3.setFill(Color.BROWN);
        Text text4 = new Text("");
        Font font4 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text4.setFont(font4);
        text4.setLayoutX(550);
        text4.setLayoutY(320);
        text4.setFill(Color.BROWN);
        Text text5 = new Text("");
        Font font5 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text5.setFont(font5);
        text5.setLayoutX(550);
        text5.setLayoutY(370);
        text5.setFill(Color.BROWN);


// ������С��
//  ��رղ��رմ���
        enableTray(Client);
        Platform.setImplicitExit(false);
        Client.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                Client.hide();
            }
        });


//CreatePane
        AnchorPane pane1 = new AnchorPane();//��ҳ����
        AnchorPane pane2 = new AnchorPane();//��¼���沼��
        AnchorPane pane3 = new AnchorPane();//ע����沼��
        AnchorPane pane4 = new AnchorPane();//�ͻ��˼�ؽ��沼��
        pane1.getChildren().addAll(button1,button2);
        pane2.getChildren().addAll(button3,button4,label1,label2,label3,label4,textField1,textField2,textField3,passwordField1,
                text8,text9,text10,text11);
        pane3.getChildren().addAll(button5,button6,label5,label6,label7,label8,label9,
                textField4,textField5,textField6,passwordField2,passwordField3,
                text1,text2,text3,text4,text5);
        pane4.getChildren().addAll(button7,button8,label12,textField9,text12);
//CreateScene
        Scene ClientHome = new Scene(pane1,800,600);
        Scene SignIn = new Scene(pane2,800,600);
        Scene Register = new Scene(pane3,800,600);
        Scene ClientMonitor = new Scene(pane4,800,600);
        Client.setScene(ClientHome);

//��ť����¼�
        button1.setOnAction(event -> {
            Client.setScene(SignIn);
        });

        button2.setOnAction(event -> {
            Client.setScene(Register);
        });

        button3.setOnAction(event -> {
            text8.setText("");
            text9.setText("");
            text10.setText("");
            text11.setText("");
            if(textField1.getText().equals("")||textField1.getText()==null){
                text8.setText("�û�������Ϊ��");
            }else{
                signA++;
            }
            if(passwordField1.getText().equals("")||passwordField1.getText()==null){
                text9.setText("���벻��Ϊ��");
            }else{
                signA++;
            }
            if(textField2.getText().equals("")||textField2.getText()==null){
                text10.setText("ServerIP����Ϊ��");
            }
            else if(!isCorrectIp(textField2.getText())){
                text10.setText("����IP��ʽ����");
            }else{
                signA++;
            }
            if(textField3.getText().equals("")||textField3.getText()==null){
                text11.setText("ServerPort����Ϊ��");
            }
            else if(!isCorrectPort(textField3.getText())){
                text11.setText("����PORT����");
            }else{
                signA++;
            }
            if(signA ==4){
                Username = textField1.getText();
                ServerIP = textField2.getText();
                ServerPort = textField3.getText();
                Password = passwordField1.getText();
                type = 1;
                signA++;
            }
            if(signA == 5) {

                user.setUsername(Username);
                Password = client.Client.getMD5(Password.getBytes(StandardCharsets.UTF_8));
                user.setPassword(Password);
                user.setServerIP(ServerIP);
                user.setServerPort(ServerPort);
                HashMap con = client.Client.connect(user);
                if (con == null) {
                    System.out.println("����������쳣������IP�Ͷ˿�");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("����������쳣������IP�Ͷ˿�");
                    alert.showAndWait();
                } else {
                    socket = (Socket) con.get("socket");
                    dos = (DataOutputStream) con.get("dos");
                    dis = (DataInputStream) con.get("dis");

                    try {
                        client.Client.login(user, dos);
                        String res = client.Client.getMsg(dis);
                        System.out.println(res);
                        if (res.equals("fail")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Login Fail");
                            alert.showAndWait();
                            Client.setScene(ClientHome);
                        } else {
                            status = true;
                            Client.setScene(ClientMonitor);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            l:
            signA = 0;
// ����ı�������
            textField1.clear();
            textField2.clear();
            textField3.clear();
            passwordField1.clear();

//����
            System.out.println(Username);
            System.out.println(ServerIP);
            System.out.println(ServerPort);
            System.out.println(Password);
            System.out.println(type);
        });

        button4.setOnAction(event -> {
            Client.setScene(ClientHome);
        });

        button5.setOnAction(event -> {
            text1.setText("");
            text2.setText("");
            text3.setText("");
            text4.setText("");
            text5.setText("");

            if(textField4.getText().equals("")||textField4.getText()==null){
                text3.setText("�û�������Ϊ��");
            }else{
                signB++;
            }
            if(passwordField2.getText().equals("")||passwordField2.getText()==null){
                text1.setText("���벻��Ϊ��");
            }else{
                signB++;
            }
            if(passwordField3.getText().equals("")||passwordField3.getText()==null){
                text2.setText("���벻��Ϊ��");
            }else {
                signB++;
            }
            if(!passwordField2.getText().equals(passwordField3.getText())) {
                text1.setText("���벻һ��");
                text2.setText("���벻һ��");
            }else{
                signB++;
            }
            if(textField5.getText().equals("")||textField5.getText()==null){
                text4.setText("ServerIP����Ϊ��");
            }
            else if(!isCorrectIp(textField5.getText())){
                text4.setText("����IP��ʽ����");
            }else{
                signB++;
            }
            if(textField6.getText().equals("")||textField6.getText()==null){
                text5.setText("ServerPort����Ϊ��");
            }
            else if(!isCorrectPort(textField6.getText())){
                text5.setText("����PORT����");
            }else{
                signB++;
            }
            if(signB == 6){
                Username = textField4.getText();
                ServerIP = textField5.getText();
                ServerPort = textField6.getText();
                Password = passwordField2.getText();
                repwd = passwordField3.getText();
                type = 0;
                signB++;
            }
            if(signB == 7) {
                user.setUsername(Username);
                Password = client.Client.getMD5(Password.getBytes(StandardCharsets.UTF_8));
                user.setPassword(Password);
                user.setServerIP(ServerIP);
                user.setServerPort(ServerPort);
                HashMap con = client.Client.connect(user);
                if (con == null) {
                    System.out.println("error");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("����������쳣������IP�Ͷ˿�");
                    alert.showAndWait();
                }
                else {
                    socket = (Socket) con.get("socket");
                    dos = (DataOutputStream) con.get("dos");
                    dis = (DataInputStream) con.get("dis");
                    System.out.println(user.getUsername());
                    try {
                        client.Client.register(user, dos);
                        String res = client.Client.getMsg(dis);
                        System.out.println(res);
                        if (res.equals("fail")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Register Fail");
                            alert.showAndWait();
                        } else if (res.equals("username repeat")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Username Repeat");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Register Success");
                            alert.showAndWait();
                            Client.setScene(ClientHome);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            signB = 0;
//����ı�������
            textField4.clear();
            textField5.clear();
            textField6.clear();
            passwordField2.clear();
            passwordField3.clear();
//����
            System.out.println(Username);
            System.out.println(ServerIP);
            System.out.println(ServerPort);
            System.out.println(Password);
            System.out.println(repwd);
            System.out.println(type);

        });

        button6.setOnAction(event -> {
            Client.setScene(ClientHome);
        });

        button7.setOnAction(event -> {

            try {
                client.Client.logout(user, dos, dis);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Logout");
                alert.showAndWait();
                status=false;
                client0.islive=false;
                Client.setScene(ClientMonitor);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Client.setScene(ClientHome);
        });

        button8.setOnAction(event -> {
            text12.setText("");

            if(textField9.getText().equals("")||textField9.getText()==null){
                text12.setText("Ƶ�ʲ���Ϊ��");
            }else{
                signC++;
            }
            if(signC == 1){

                user.Frequency = Integer.parseInt(textField9.getText());
                type = 2;
                signC++;
            }
            if(signC == 2){

                if(!islive){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("��ʼ���");
                    alert.showAndWait();
                    islive=true;
                    try {
                        new Thread( new Client()).start();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }else{
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle(null);
                    alert1.setHeaderText(null);
                    alert1.setContentText("�޸�Ƶ�ʳɹ�");
                    alert1.showAndWait();
                }
                type = 2;
            }
            signC=0;
            textField9.clear();
//����
            System.out.println(user.Frequency);

        });

//        stage.setScene(Client.scene);
        Client.setTitle("Զ�������ؿͻ���");
        Client.getIcons().add(new Image("img/1.jpeg"));
        Client.show();

    }

    private void enableTray(final Stage stage) {
        PopupMenu popupMenu = new PopupMenu();
        java.awt.MenuItem openItem = new java.awt.MenuItem("��ʾ");
        java.awt.MenuItem hideItem = new java.awt.MenuItem("��С��");
        java.awt.MenuItem quitItem = new java.awt.MenuItem("�˳�");

        ActionListener acl = new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();
                Platform.setImplicitExit(false); //���ʹ����ʾ����������false

                if (item.getLabel().equals("�˳�")) {
                    if(status){
//                        stage.show();
                        try {
                            Client.logout(user, dos, dis);
                            status=false;
                            Client.stop();
                            Platform.exit();
                            return;
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }else{
                        Client.stop();
                        Platform.exit();
                        return;
                    }

                }
                if (item.getLabel().equals("��ʾ")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
                if (item.getLabel().equals("��С��")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.hide();
                        }
                    });
                }

            }

        };

        //˫���¼�����
        MouseListener sj = new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }
            public void mousePressed(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseClicked(MouseEvent e) {
                Platform.setImplicitExit(false); //���ʹ����ʾ����������false
                if (e.getClickCount() == 2) {
                    if (stage.isShowing()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stage.hide();
                            }
                        });
                    }else{
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stage.show();
                            }
                        });
                    }
                }
            }
        };

        openItem.addActionListener(acl);
        quitItem.addActionListener(acl);
        hideItem.addActionListener(acl);

        popupMenu.add(openItem);
        popupMenu.add(hideItem);
        popupMenu.add(quitItem);

        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/img/1.gif"));
            trayIcon = new TrayIcon(image, "Զ�������ؿͻ���", popupMenu);
            trayIcon.setToolTip("Զ�������ؿͻ���");
            tray.add(trayIcon);
            trayIcon.addMouseListener(sj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("[+]ServerView init...");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("[+]ServerView stop...");
    }
}
