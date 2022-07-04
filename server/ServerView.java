package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static dbcon.DataBase.DatabaseInit;

import  dbcon.DataBase;
import javafx.stage.WindowEvent;
import protocol.Protocol;

import javax.imageio.ImageIO;


public  class ServerView extends Application implements Runnable{

    //    相关变量
    public static int ServerFluent;
    public static String Blacklist = "";
    private TrayIcon trayIcon;
    public static Image img;
    public static ImageView imageView=new ImageView();
    public static String Username=null;
    public static GridPane grid=new GridPane();
    public static int i=0;
    public static int page=0;
    public static String username=null;
    public static String path = "src/image/" + Username;
    public static File f ;
    public static File result[] ;
    public static BufferedImage buff=null;

    public static Vector<dbcon.User> v=new Vector<dbcon.User>();
//    private DataBase db = new DataBase();
//    public dbcon.User user0;
//    public Result result0;
//    public int type0;

    //建立列表存放用户名
//static ArrayList<User> users = new ArrayList<>();
    static Vector<User> users = new Vector<>();

    @Override
    public void run() {
        Application.launch();
    }


    //    public static void ServerView(String[] args){
//
//        Application.launch(args);
//
//    }
    public static  TreeItem<User> root = new TreeItem<>(new User("   用户   ",""));


    //显示图像
    public static void setImg(Image image,String username){
        System.out.println(Username);
        if(Username == null){
            imageView.setImage(null);
            return;
        }
        if(Username.equals(username)){
            img=image;
            imageView.setImage(img);
        }else{
            imageView.setImage(null);
        }
    }

    //    修改状态
    public static void ChangeStatus(int Type,String user){
//登陆成功

        root.getChildren().clear();
        if(Type== Protocol.TYPE_LOGIN){
            i=0;
            while (i<users.size()){
                if(users.get(i).getName().equals(user)){
                    users.get(i).setStatus("在线");
                    System.out.println(users.get(i).getName()+":"+users.get(i).getStatus());
                }
                i++;
            }
            users.stream().forEach((user0) -> {
                root.getChildren().add(new TreeItem<>(user0));
            });
        }
//注册成功
        if(Type== Protocol.TYPE_REGISTER){
            users.add(new User(user,"离线"));
            root.getChildren().clear();
            users.stream().forEach((user0) -> {
                root.getChildren().add(new TreeItem<>(user0));
            });
        }
// 退出登录
        if(Type== Protocol.TYPE_LOGOUT){

            i=0;
            while (i<users.size()){
                if(users.get(i).getName().equals(user)){
                    users.get(i).setStatus("离线");
                    System.out.println(users.get(i).getName()+":"+users.get(i).getStatus());
                }
                i++;
            }
            setImg(null,user);

            users.stream().forEach((user0) -> {
                root.getChildren().add(new TreeItem<>(user0));
            });
        }


    }



    @Override
    public void start(Stage Server) throws Exception {
        System.out.println("start...");


        //Creating a tree table view
        TreeTableView<User> treeTableView = new TreeTableView<>(root);
        root.setExpanded(true);


        //初始化,显示用户和状态“离线”
//        users.add(new User("a","离线"));
//        Vector<dbcon.User> v = dbcon.DataBase.UserList(DatabaseInit());
        v= dbcon.DataBase.UserList(DatabaseInit());
        i=0;
        while(i<v.size()) {
            users.add(new User(v.get(i).getUsername(),"离线"));
            i++;
        }
        users.stream().forEach((user) -> {
            root.getChildren().add(new TreeItem<>(user));
        });

        //Creating the root element


        //Creating a column
        TreeTableColumn<User,String> ServerColumn = new TreeTableColumn<>("服务端");
        TreeTableColumn<User,String> StatusColumn = new TreeTableColumn<>("状态");
        ServerColumn.setPrefWidth(200);
        StatusColumn.setPrefWidth(100);

// Create Text
        Text text1 = new Text("");
        Font font1 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text1.setFont(font1);

// 树表点击事件
        treeTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                Node node = event.getPickResult().getIntersectedNode();
                if (node instanceof Text || (node instanceof TreeTableCell && ((TreeTableCell) node).getText() != null)) {
                    String name=treeTableView.getSelectionModel().getSelectedItem().getValue().getName();
                    v = dbcon.DataBase.UserList(DatabaseInit());
                    int i=0;
                    while(i<v.size()){
                        if(v.get(i).getUsername().equals(name)){
                            System.out.println(v.get(i).getClientIP());
                            System.out.println(v.get(i).getClientMac());
                            Username=v.get(i).getUsername();
                            text1.setText("  用户名："+v.get(i).getUsername()+"  IP："+v.get(i).getClientIP()+"  Mac："+v.get(i).getClientMac());
                            break;
                        }
                        i++;
                    }
//                    v.get(i).getUsername();
                    System.out.println(name);
                }
            }
        });

        //Defining cell content
        ServerColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<User, String> users) ->
                        new ReadOnlyStringWrapper(users.getValue().getValue().getName()));
        StatusColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<User, String> status) ->
                        new ReadOnlyStringWrapper(status.getValue().getValue().getStatus())
        );


        treeTableView.getColumns().setAll(ServerColumn,StatusColumn);
        treeTableView.setPrefWidth(302);
        treeTableView.setPrefHeight(700);
        treeTableView.setShowRoot(true);
//        sceneRoot.getChildren().add(treeTableView);

//布局
//        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 5));
        grid.add(treeTableView, 0, 0);
//        grid.add(text1, 1, 1);

        HBox hbox=new HBox();
        HBox hbox1=new HBox();
        hbox.setPadding(new Insets(0, 12, 10, 12)); //节点到边缘的距离
        hbox.setSpacing(20); //节点之间的间距
        hbox1.setPadding(new Insets(5, 12, 5, 12)); //节点到边缘的距离
        hbox1.setSpacing(20); //节点之间的间距


// 显示图片
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(700);
        imageView.setFitWidth(1200);
        grid.add(imageView, 1, 0);


//服务端修改监控频率
// 频率文本框
        TextField textField1 = new TextField ();
        grid.add(textField1, 0, 1);

        textField1.setOnAction((ActionEvent e) -> {
            ServerFluent = Integer.parseInt(textField1.getText());
            System.out.println(ServerFluent);
        });
// 频率按钮
        Button button1 = new Button("  修改频率  ");
        grid.add(button1, 0, 2);
//按钮点击事件
        button1.setOnAction(event -> {
            ServerFluent = Integer.parseInt(textField1.getText());
            System.out.println(ServerFluent);
        });
//打开图片文件夹
        Text text=new Text("");
        Button button2 = new Button("  查看历史图像  ");

        button2.setOnAction(event -> {
            // 路径
            path = "src/image/" + Username;
            f = new File(path);
// 路径不存在
            if (!f.exists()) {
                System.out.println(path + " not exists");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("没有该用户文件夹");
                alert.showAndWait();
                return;
            }else{
                result = f.listFiles();
                if(result.length==0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("尚未保存该用户图片");
                    alert.showAndWait();
                }else {
                    page = 0;
                    buff = null;
                    try {
                        buff = ImageIO.read(new FileInputStream(path + "/" + result[page].getName()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Image image = SwingFXUtils.toFXImage(buff, null);
                    text.setText(result[page].getName().replace(".png",""));
                    setImg(image, Username);
                }
            }

        });

        Button button3 = new Button("  上一张  ");
        button3.setOnAction(event -> {
            if(page<result.length&&page>=1){
                try {
                    page=page-1;
                    buff = ImageIO.read(new FileInputStream(path + "/" + result[page].getName()));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Image image = SwingFXUtils.toFXImage(buff, null);
                text.setText(result[page].getName().replace(".png",""));
                setImg(image, Username);
            }
        });
        Button button4 = new Button("  下一张  ");
        button4.setOnAction(event -> {
            if(page<result.length-1&&page>=0){

                try {
                    buff = ImageIO.read(new FileInputStream(path + "/" + result[++page].getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Image image = SwingFXUtils.toFXImage(buff, null);
                text.setText(result[page].getName().replace(".png",""));
                setImg(image, Username);
            }
        });

        Button button5 =new Button("关闭图片查看");
        button5.setOnAction(event -> {
            text.setText("");
            setImg(null,Username);
        });
//Port设置
        TextField textField3 = new TextField ();
        textField3.setPrefWidth(300);
        textField3.setPromptText("输入服务端端口号");
        textField3.setOnAction(event -> {
            server.Server.Port = Integer.parseInt(textField3.getText());
        });
        Button button7 =new Button("设置PORT");
        button7.setOnAction(event -> {
            server.Server.Port = Integer.parseInt(textField3.getText());
        });
        hbox1.getChildren().addAll(button7,textField3,text1,text);

//黑名单输入
        TextField textField2 = new TextField ();
        textField2.setPrefWidth(600);
        textField2.setPromptText("黑名单输入格式(用;隔开)：ccc;ddd");
        textField2.setOnAction(event -> {
            Blacklist = textField2.getText();
        });
        Button button6 =new Button("设置黑名单");
        button6.setOnAction(event -> {
            Blacklist = textField2.getText();
        });

        hbox.getChildren().addAll(button2, button3,button4,button5,button6,textField2);
        grid.add(hbox, 1, 2);
        grid.add(hbox1, 1, 1);
        // 托盘最小化
//  点关闭不关闭窗口
        enableTray(Server);
        Platform.setImplicitExit(false);
        Server.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                Server.hide();
            }
        });


//服务端场景
        Scene ServerBoard = new Scene(grid,1600,820);

        Server.setScene(ServerBoard);
        Server.setTitle("远程桌面监控服务端");
        Server.getIcons().add(new Image("img/1.jpeg"));
        Server.show();
    }

    public static class User {

        private SimpleStringProperty name;
        private SimpleStringProperty status;
        public SimpleStringProperty nameProperty() {
            if (name == null) {
                name = new SimpleStringProperty(this, "name");
            }
            return name;
        }
        public SimpleStringProperty statusProperty() {
            if (status == null) {
                status = new SimpleStringProperty(this, "离线");
            }
            return status;
        }
        private User(String name, String status) {
            this.name = new SimpleStringProperty(name);
            this.status = new SimpleStringProperty(status);
        }
        public String getName() {
            return name.get();
        }
        public void setName(String fName) {
            name.set(fName);
        }
        public String getStatus() {
            return status.get();
        }
        public void setStatus(String fName) {
            status.set(fName);
        }


    }
    private void enableTray(final Stage stage) {
        PopupMenu popupMenu = new PopupMenu();
        java.awt.MenuItem openItem = new java.awt.MenuItem("显示");
        java.awt.MenuItem hideItem = new java.awt.MenuItem("最小化");
        java.awt.MenuItem quitItem = new java.awt.MenuItem("退出");

        ActionListener acl = new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false

                if (item.getLabel().equals("退出")) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    Platform.exit();
                    return;
                }
                if (item.getLabel().equals("显示")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
                if (item.getLabel().equals("最小化")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.hide();
                        }
                    });
                }
            }
        };

        //双击事件方法
        MouseListener sj = new MouseListener() {
            public void mouseReleased(java.awt.event.MouseEvent e) {
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
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
            BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/img/2.gif"));
            trayIcon = new TrayIcon(image, "远程桌面监控服务端", popupMenu);
            trayIcon.setToolTip("远程桌面监控服务端");
            tray.add(trayIcon);
            trayIcon.addMouseListener(sj);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("init...");

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Server.stop();
        System.out.println("stop...");
    }
}