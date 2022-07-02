package server;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static dbcon.DataBase.DatabaseInit;
import  dbcon.DataBase;

public  class ServerView extends Application implements Runnable {

    //    ��ر���
    public int ServerFluent;
//    private DataBase db = new DataBase();
//    public dbcon.User user0;
//    public Result result0;
//    public int type0;

    //�����б����û���
    ArrayList<User> users = new ArrayList<>();

    @Override
    public void run(){
        Application.launch();
    }


    public TreeItem<User> root = new TreeItem<>(new User("   �û�   ",""));

    @Override
    public void start(Stage Server) throws Exception {
        System.out.println("start...");

        //Creating a tree table view
        TreeTableView<User> treeTableView = new TreeTableView<>(root);
        root.setExpanded(true);


        //��ʼ��,��ʾ�û���״̬�����ߡ�
        Vector<dbcon.User> v = dbcon.DataBase.UserList(DatabaseInit());
        int i=0;
        String str=null;
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
                    int i=0;
                    while(i<v.size()){
                        if(v.get(i).getUsername().equals(name)){
                            System.out.println(v.get(i).getClientIP());
                            System.out.println(v.get(i).getClientMac());
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

//��ʾͼƬ,����


        treeTableView.getColumns().setAll(ServerColumn,StatusColumn);
        treeTableView.setPrefWidth(302);
        treeTableView.setPrefHeight(700);
        treeTableView.setShowRoot(true);
//        sceneRoot.getChildren().add(treeTableView);

//����
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 5));
        grid.add(treeTableView, 0, 0);
        grid.add(text1, 1, 1);


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

//����˳���
        Scene ServerBoard = new Scene(grid,1000,800);

        Server.setScene(ServerBoard);
        Server.setTitle("Զ�������ط����");
        Server.getIcons().add(new Image("file:img/1.jpeg"));
        Server.show();
    }

    public class User {

        private SimpleStringProperty name;
        private SimpleStringProperty status;
        public SimpleStringProperty nameProperty() {
            if (name == null) {
                name = new SimpleStringProperty(this, "name");
            }
            return name;
        }
        public SimpleStringProperty emailProperty() {
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