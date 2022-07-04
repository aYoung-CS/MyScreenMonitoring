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

    //    ��ر���
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

    //�����б����û���
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
    public static  TreeItem<User> root = new TreeItem<>(new User("   �û�   ",""));


    //��ʾͼ��
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

    //    �޸�״̬
    public static void ChangeStatus(int Type,String user){
//��½�ɹ�

        root.getChildren().clear();
        if(Type== Protocol.TYPE_LOGIN){
            i=0;
            while (i<users.size()){
                if(users.get(i).getName().equals(user)){
                    users.get(i).setStatus("����");
                    System.out.println(users.get(i).getName()+":"+users.get(i).getStatus());
                }
                i++;
            }
            users.stream().forEach((user0) -> {
                root.getChildren().add(new TreeItem<>(user0));
            });
        }
//ע��ɹ�
        if(Type== Protocol.TYPE_REGISTER){
            users.add(new User(user,"����"));
            root.getChildren().clear();
            users.stream().forEach((user0) -> {
                root.getChildren().add(new TreeItem<>(user0));
            });
        }
// �˳���¼
        if(Type== Protocol.TYPE_LOGOUT){

            i=0;
            while (i<users.size()){
                if(users.get(i).getName().equals(user)){
                    users.get(i).setStatus("����");
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


        //��ʼ��,��ʾ�û���״̬�����ߡ�
//        users.add(new User("a","����"));
//        Vector<dbcon.User> v = dbcon.DataBase.UserList(DatabaseInit());
        v= dbcon.DataBase.UserList(DatabaseInit());
        i=0;
        while(i<v.size()) {
            users.add(new User(v.get(i).getUsername(),"����"));
            i++;
        }
        users.stream().forEach((user) -> {
            root.getChildren().add(new TreeItem<>(user));
        });

        //Creating the root element


        //Creating a column
        TreeTableColumn<User,String> ServerColumn = new TreeTableColumn<>("�����");
        TreeTableColumn<User,String> StatusColumn = new TreeTableColumn<>("״̬");
        ServerColumn.setPrefWidth(200);
        StatusColumn.setPrefWidth(100);

// Create Text
        Text text1 = new Text("");
        Font font1 = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text1.setFont(font1);

// �������¼�
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
                            text1.setText("  �û�����"+v.get(i).getUsername()+"  IP��"+v.get(i).getClientIP()+"  Mac��"+v.get(i).getClientMac());
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

//����
//        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 5));
        grid.add(treeTableView, 0, 0);
//        grid.add(text1, 1, 1);

        HBox hbox=new HBox();
        HBox hbox1=new HBox();
        hbox.setPadding(new Insets(0, 12, 10, 12)); //�ڵ㵽��Ե�ľ���
        hbox.setSpacing(20); //�ڵ�֮��ļ��
        hbox1.setPadding(new Insets(5, 12, 5, 12)); //�ڵ㵽��Ե�ľ���
        hbox1.setSpacing(20); //�ڵ�֮��ļ��


// ��ʾͼƬ
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(700);
        imageView.setFitWidth(1200);
        grid.add(imageView, 1, 0);


//������޸ļ��Ƶ��
// Ƶ���ı���
        TextField textField1 = new TextField ();
        grid.add(textField1, 0, 1);

        textField1.setOnAction((ActionEvent e) -> {
            ServerFluent = Integer.parseInt(textField1.getText());
            System.out.println(ServerFluent);
        });
// Ƶ�ʰ�ť
        Button button1 = new Button("  �޸�Ƶ��  ");
        grid.add(button1, 0, 2);
//��ť����¼�
        button1.setOnAction(event -> {
            ServerFluent = Integer.parseInt(textField1.getText());
            System.out.println(ServerFluent);
        });
//��ͼƬ�ļ���
        Text text=new Text("");
        Button button2 = new Button("  �鿴��ʷͼ��  ");

        button2.setOnAction(event -> {
            // ·��
            path = "src/image/" + Username;
            f = new File(path);
// ·��������
            if (!f.exists()) {
                System.out.println(path + " not exists");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("û�и��û��ļ���");
                alert.showAndWait();
                return;
            }else{
                result = f.listFiles();
                if(result.length==0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("��δ������û�ͼƬ");
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

        Button button3 = new Button("  ��һ��  ");
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
        Button button4 = new Button("  ��һ��  ");
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

        Button button5 =new Button("�ر�ͼƬ�鿴");
        button5.setOnAction(event -> {
            text.setText("");
            setImg(null,Username);
        });
//Port����
        TextField textField3 = new TextField ();
        textField3.setPrefWidth(300);
        textField3.setPromptText("�������˶˿ں�");
        textField3.setOnAction(event -> {
            server.Server.Port = Integer.parseInt(textField3.getText());
        });
        Button button7 =new Button("����PORT");
        button7.setOnAction(event -> {
            server.Server.Port = Integer.parseInt(textField3.getText());
        });
        hbox1.getChildren().addAll(button7,textField3,text1,text);

//����������
        TextField textField2 = new TextField ();
        textField2.setPrefWidth(600);
        textField2.setPromptText("�����������ʽ(��;����)��ccc;ddd");
        textField2.setOnAction(event -> {
            Blacklist = textField2.getText();
        });
        Button button6 =new Button("���ú�����");
        button6.setOnAction(event -> {
            Blacklist = textField2.getText();
        });

        hbox.getChildren().addAll(button2, button3,button4,button5,button6,textField2);
        grid.add(hbox, 1, 2);
        grid.add(hbox1, 1, 1);
        // ������С��
//  ��رղ��رմ���
        enableTray(Server);
        Platform.setImplicitExit(false);
        Server.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                Server.hide();
            }
        });


//����˳���
        Scene ServerBoard = new Scene(grid,1600,820);

        Server.setScene(ServerBoard);
        Server.setTitle("Զ�������ط����");
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
                status = new SimpleStringProperty(this, "����");
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
        java.awt.MenuItem openItem = new java.awt.MenuItem("��ʾ");
        java.awt.MenuItem hideItem = new java.awt.MenuItem("��С��");
        java.awt.MenuItem quitItem = new java.awt.MenuItem("�˳�");

        ActionListener acl = new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();
                Platform.setImplicitExit(false); //���ʹ����ʾ����������false

                if (item.getLabel().equals("�˳�")) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    Platform.exit();
                    return;
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
            public void mouseReleased(java.awt.event.MouseEvent e) {
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
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
            BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/img/2.gif"));
            trayIcon = new TrayIcon(image, "Զ�������ط����", popupMenu);
            trayIcon.setToolTip("Զ�������ط����");
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