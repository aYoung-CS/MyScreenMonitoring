package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.*;

import static dbcon.DataBase.DatabaseInit;

import  dbcon.DataBase;
import javafx.stage.WindowEvent;
import protocol.Protocol;

import javax.imageio.ImageIO;


public  class ServerView extends Application implements Runnable{

    //    相关变量
    public static int ServerFluent;
    private TrayIcon trayIcon;
    public static Image img;
    public static ImageView imageView=new ImageView();
    public static String Username=null;
    public static GridPane grid=new GridPane();
    public static int i=0;
    public static int Type=0;
    public static String username=null;
    public static String Blacklist = "cccc.exe;dddd.exe";
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


//        BufferedImage buff= new BufferedImage();
//        File file =new File("E:\\Java\\demo3\\img\\1.png");
//        ImageIO.write(buff,"png",file);


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
        grid.add(text1, 1, 1);

        HBox hbox=new HBox();


//显示图片,暂无

//        FileInputStream input = new FileInputStream("E:\\Java\\demo3\\img\\1.png");
//        Image image = new Image(input);
//        ImageView imageView=new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(700);
        imageView.setFitWidth(1200);
        grid.add(imageView, 1, 0);
//        grid.add(imageView1, 1, 0);

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
        Scene ServerBoard = new Scene(grid,1600,800);

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
