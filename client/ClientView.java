package client;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;



//public String User;

public class ClientView extends Application {
//    public static void ClientView(String[] args){
//        Application.launch(args);
//    }
//    相关变量

    public String Username;
    public String Password;
    public String severIP;
    public String severPort;
    public String repwd;
    public String ClientIP;
    public String ClientMac;
    public String SeverIP;
    public String SeverPort;
    public int ClientFluent;
    public int type;



    @Override
    public void start(Stage Client) throws Exception {
        System.out.println("start...");


//CreateButton
//首页按钮
        Button button1 = new Button("  登录  ");
        button1.setLayoutX(350);
        button1.setLayoutY(280);

        Button button2 = new Button("  注册  ");
        button2.setLayoutX(350);
        button2.setLayoutY(320);
//登录按钮
        Button button3 = new Button("  确认登录  ");
        button3.setLayoutX(290);
        button3.setLayoutY(400);

        Button button4 = new Button("   取消   ");
        button4.setLayoutX(410);
        button4.setLayoutY(400);
//注册按钮
        Button button5 = new Button("  确认注册  ");
        button5.setLayoutX(290);
        button5.setLayoutY(500);

        Button button6 = new Button("    取消   ");
        button6.setLayoutX(410);
        button6.setLayoutY(500);
//客户端界面按钮
        Button button7 = new Button("    退出   ");
        button7.setLayoutX(300);
        button7.setLayoutY(400);

//CreateLabel
//登录标签
        Label label1 = new Label("用户名");
        label1.setLayoutX(250);
        label1.setLayoutY(200);

        Label label2 = new Label("密码");
        label2.setLayoutX(250);
        label2.setLayoutY(250);

        Label label3 = new Label("severIP");
        label3.setLayoutX(250);
        label3.setLayoutY(300);

        Label label4 = new Label("severPort");
        label4.setLayoutX(250);
        label4.setLayoutY(350);
//注册标签
        Label label5 = new Label("用户名");
        label5.setLayoutX(250);
        label5.setLayoutY(150);

        Label label6 = new Label("密码");
        label6.setLayoutX(250);
        label6.setLayoutY(200);

        Label label7 = new Label("repwd");
        label7.setLayoutX(250);
        label7.setLayoutY(250);

        Label label8 = new Label("ClientIP");
        label8.setLayoutX(250);
        label8.setLayoutY(300);

        Label label9 = new Label("ClientMac");
        label9.setLayoutX(250);
        label9.setLayoutY(350);

        Label label10 = new Label("SeverIP");
        label10.setLayoutX(250);
        label10.setLayoutY(400);

        Label label11 = new Label("SeverPort");
        label11.setLayoutX(250);
        label11.setLayoutY(450);

//客户端界面标签
        Label label12 = new Label("监控频率");
        label12.setLayoutX(250);
        label12.setLayoutY(300);

//CreateTextField
//登录文本框
        TextField textField1 = new TextField ();
        textField1.setLayoutX(340);
        textField1.setLayoutY(200);

        TextField textField2 = new TextField ();
        textField2.setLayoutX(340);
        textField2.setLayoutY(300);

        TextField textField3 = new TextField ();
        textField3.setLayoutX(340);
        textField3.setLayoutY(350);

        textField1.setOnAction((ActionEvent e) -> {
            Username = textField1.getText();
            System.out.println(Username);
        });
        textField2.setOnAction((ActionEvent e) -> {
            severIP = textField2.getText();
            System.out.println(severIP);
        });
        textField3.setOnAction((ActionEvent e) -> {
            severPort = textField2.getText();
            System.out.println(severPort);
        });

//注册文本框
        TextField textField4 = new TextField ();
        textField4.setLayoutX(340);
        textField4.setLayoutY(150);

        TextField textField5 = new TextField ();
        textField5.setLayoutX(340);
        textField5.setLayoutY(300);

        TextField textField6 = new TextField ();
        textField6.setLayoutX(340);
        textField6.setLayoutY(350);

        TextField textField7 = new TextField ();
        textField7.setLayoutX(340);
        textField7.setLayoutY(400);

        TextField textField8 = new TextField ();
        textField8.setLayoutX(340);
        textField8.setLayoutY(450);

        textField4.setOnAction((ActionEvent e) -> {
            Username = textField4.getText();
            System.out.println(Username);
        });
        textField5.setOnAction((ActionEvent e) -> {
            ClientIP = textField5.getText();
            System.out.println(ClientIP);
        });
        textField6.setOnAction((ActionEvent e) -> {
            ClientMac = textField6.getText();
            System.out.println(ClientMac);
        });
        textField7.setOnAction((ActionEvent e) -> {
            SeverIP = textField7.getText();
            System.out.println(SeverIP);
        });
        textField8.setOnAction((ActionEvent e) -> {
            SeverPort = textField8.getText();
            System.out.println(SeverPort);
        });

//客户端界面文本框
        TextField textField9 = new TextField ();
        textField9.setLayoutX(340);
        textField9.setLayoutY(300);

        textField9.setOnAction((ActionEvent e) -> {
            ClientFluent = Integer.parseInt(textField9.getText());
            System.out.println(ClientFluent);
        });


//CreatepasswordField
//登录密码框
        PasswordField passwordField1 = new PasswordField();
        passwordField1.setLayoutX(340);
        passwordField1.setLayoutY(250);

        passwordField1.setOnAction((ActionEvent e) -> {
            Password = passwordField1.getText();
            System.out.println(Password);
        });

//注册密码框
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setLayoutX(340);
        passwordField2.setLayoutY(200);

        PasswordField passwordField3 = new PasswordField();
        passwordField3.setLayoutX(340);
        passwordField3.setLayoutY(250);

        passwordField2.setOnAction((ActionEvent e) -> {
            Password = passwordField2.getText();
            System.out.println(Password);

        });

        passwordField3.setOnAction((ActionEvent e) -> {
            repwd = passwordField3.getText();
            System.out.println(repwd);
        });

//注册密码判断是否一致
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

//CreatePane
        AnchorPane pane1 = new AnchorPane();//首页布局
        AnchorPane pane2 = new AnchorPane();//登录界面布局
        AnchorPane pane3 = new AnchorPane();//注册界面布局
        AnchorPane pane4 = new AnchorPane();//客户端监控界面布局
        pane1.getChildren().addAll(button1,button2);
        pane2.getChildren().addAll(button3,button4,label1,label2,label3,label4,textField1,textField2,textField3,passwordField1);
        pane3.getChildren().addAll(button5,button6,label5,label6,label7,label8,label9,label10,label11,textField4,textField5,textField6,textField7,textField8,passwordField2,passwordField3,text1,text2);
        pane4.getChildren().addAll(button7,label12,textField9);
//CreateScene
        Scene ClientHome = new Scene(pane1,800,600);
        Scene SignIn = new Scene(pane2,800,600);
        Scene Register = new Scene(pane3,800,600);
        Scene ClientMonitor = new Scene(pane4,800,600);
        Client.setScene(ClientHome);

//按钮点击事件
        button1.setOnAction(event -> {
            Client.setScene(SignIn);
        });

        button2.setOnAction(event -> {
            Client.setScene(Register);
        });

        button3.setOnAction(event -> {
            Username = textField1.getText();
            severIP = textField2.getText();
            severPort = textField3.getText();
            Password = passwordField1.getText();
            type = 1;
//测试
            System.out.println(Username);
            System.out.println(severIP);
            System.out.println(severPort);
            System.out.println(Password);
            System.out.println(type);

            Client.setScene(ClientMonitor);
        });

        button4.setOnAction(event -> {
            Client.setScene(ClientHome);
        });

        button5.setOnAction(event -> {

            Username = textField4.getText();
            ClientIP = textField5.getText();
            ClientMac = textField6.getText();
            SeverIP = textField7.getText();
            SeverPort = textField8.getText();
            Password = passwordField2.getText();
            repwd = passwordField3.getText();
            type = 0;

            if(!Password.equals(repwd)) {
                text1.setText("密码不一致");
                text2.setText("密码不一致");
            }
//测试
            System.out.println(Username);
            System.out.println(ClientIP);
            System.out.println(ClientMac);
            System.out.println(SeverIP);
            System.out.println(SeverPort);
            System.out.println(Password);
            System.out.println(repwd);
            System.out.println(type);

        });

        button6.setOnAction(event -> {
            Client.setScene(ClientHome);
        });

        button7.setOnAction(event -> {
            Client.setScene(ClientHome);
        });



//        stage.setScene(Client.scene);
        Client.setTitle("远程桌面监控");
//        Client.setWidth(800);
//        Client.setHeight(600);
        Client.getIcons().add(new Image("file:img/1.jpeg"));
        Client.show();

    }




    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("init...");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("stop...");
    }
}
