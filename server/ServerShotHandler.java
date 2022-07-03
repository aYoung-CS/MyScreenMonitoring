package server;

import dbcon.DataBase;
import dbcon.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import protocol.Protocol;
import protocol.Result;
import server.Server;
import server.ServerView;
import dbcon.DataBase;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static protocol.Protocol.DeserializeData;
import static protocol.Protocol.TYPE_IMAGE;

public class ServerShotHandler implements Runnable{
    private Socket socket;
    private DataInputStream dis=null;
    private DataOutputStream dos=null;
    private String key=null;
    private boolean isLive=true;

    public ServerShotHandler(){

    }

    public ServerShotHandler(Socket socket){
        this.socket=socket;
        try {
            this.dis=new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("ServerShotHandler constructor is wrong");
        }
    }

    @Override
    public void run() {
        System.out.println("ok");
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("dos/dis wrong");
        }
        while (true) {
            try {
                Result result = Protocol.getResult(dis);
                System.out.println(result + "-----");
                int type = result.getType();
                User user = DeserializeData(result.getData());
                if (type <= 2) {
                    System.out.println("yes");
                    System.out.println(user.getPassword());
                    System.out.println(user.getUsername());
                    System.out.println("???:" + result.getType());
                }

                int res;
                byte[] msg = null;
                System.out.println("type: " + type);

                if (type == Protocol.TYPE_REGISTER) {
                    res = DataBase.Register(user.getUsername(), user.getPassword(), user.getClientIP(), user.getClientMac());
                    switch(res){
                        case 0:
                            msg = "fail".getBytes(StandardCharsets.UTF_8);
                            break;
                        case 1:
                            msg = "success".getBytes(StandardCharsets.UTF_8);
//  修改状态，UI增加用户
                            ServerView.ChangeStatus(result.getType(),user.getUsername());
                            break;
                        case 2:
                            msg = "username repeat".getBytes(StandardCharsets.UTF_8);
                            break;
                        default:
                            break;
                    }
                    Protocol.send(Protocol.TYPE_LOGIN, dos, msg);
                    dos.flush();
                } else if (type == Protocol.TYPE_LOGIN) {
                    res = DataBase.Login(user.getUsername(), user.getPassword());
                    switch (res){
                        case 0:
                            msg = "fail".getBytes(StandardCharsets.UTF_8);
                            break;
                        case 1:
                            msg = "success".getBytes(StandardCharsets.UTF_8);
//   修改状态，用户为“在线”状态
                            ServerView.ChangeStatus(result.getType(),user.getUsername());
                            break;
                        default:
                            break;
                    }
                    Protocol.send(Protocol.TYPE_LOGIN, dos, msg);
                    dos.flush();
                } else if (type == Protocol.TYPE_LOGOUT) {
                    System.out.println("User " + user.getUsername() + " logout");
                    socket.close();
                    dos.close();
                    dis.close();
// 修改状态，用户为“离线”状态
                    ServerView.ChangeStatus(result.getType(),user.getUsername());
                    break;
                } else if (type == Protocol.TYPE_IMAGE) {
                    System.out.println("images");
                    ByteArrayInputStream bai=new ByteArrayInputStream(user.imageData);
                    System.out.println("images1");
                    BufferedImage buff= ImageIO.read(bai);
                    Image image = SwingFXUtils.toFXImage(buff, null);
                    ServerView.setImg(image,user.getUsername());
                    msg = (ServerView.ServerFluent+"").getBytes(StandardCharsets.UTF_8);
                    Protocol.send(Protocol.TYPE_IMAGE, dos, msg);
                    dos.flush();
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ServerShotHandler run is wrong");
            }
        }
    }
}
