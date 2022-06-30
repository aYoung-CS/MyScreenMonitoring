package server;

import dbcon.DataBase;
import dbcon.User;
import protocol.Protocol;
import protocol.Result;
import server.Server;
import server.ServerView;
import dbcon.DataBase;

import javax.xml.crypto.Data;
import java.awt.dnd.DropTarget;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
    public User DeserializeData(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bin);
        User user = (User)oin.readObject();
        oin.close();
        bin.close();
        return user;
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
                            System.out.println("login fail");
                            break;
                        case 1:
                            msg = "success".getBytes(StandardCharsets.UTF_8);
                            break;
                        default:
                            break;
                    }
                    Protocol.send(Protocol.TYPE_LOGIN, dos, msg);
                    dos.flush();
                } else if (type == Protocol.TYPE_LOGOUT) {
                    System.out.println("User " + user.getUsername() + " logout");
                    msg = "logout".getBytes(StandardCharsets.UTF_8);
                    Protocol.send(Protocol.TYPE_LOGOUT, dos, msg);
                    socket.close();
                    dos.close();
                    dis.close();
                    break;
                } else if (type == Protocol.TYPE_IMAGE) {

                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ServerShotHandler run is wrong");
            }
        }
    }
}
