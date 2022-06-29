package server;

import dbcon.DataBase;
import dbcon.User;
import protocol.Protocol;
import src.myutil.Result;
import src.server.Server;
import src.server.View;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerShotHandler implements Runnable{
    private Socket socket;
    private DataInputStream dis=null;
    private String key=null;
    private boolean isLive=true;
    private DataOutputStream dos = null ;
    InputStream in ;


    public ServerShotHandler(Socket socket){
        this.socket=socket;
        try {
            this.dis=new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
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
        while(isLive){
            System.out.println("run accept");
            Result result = null;
            result = src.myutil.Protocol.getResult(dis);
            System.out.println("result is :"  + result);

            if(result!=null){
                ShotHandler(result);
            }else {
                try {
                    dis.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void ShotHandler(Result r){
        try {
            Result result = r;
            User user = DeserializeData(result.getData());
            dos = new DataOutputStream(socket.getOutputStream());

            if (result.getType() == Protocol.TYPE_REGISTER){
                DataBase.Register(user.getUsername(),user.getPassword(),user.getClientIP(),user.getClientMac());

            }else if(result.getType() == Protocol.TYPE_LOGIN){

                System.out.println("result.getType :" +result.getType());
                int res = DataBase.Login(user.getUsername(),user.getPassword());
                byte[] msg = null;
                if(res == 1){
                    msg = "login success".getBytes(StandardCharsets.UTF_8);
                }else {
                    msg = "login fail".getBytes(StandardCharsets.UTF_8);
                }

                Protocol.send(Protocol.TYPE_LOGIN,dos,msg);
//                socket.close();

            }else if(result.getType() == Protocol.TYPE_LOGOUT){

                System.out.println("User " + user.getUsername() + "logout");

            }else if(result.getType() == Protocol.TYPE_IMAGE){
//                System.out.println("images");
                ByteArrayInputStream bai=new ByteArrayInputStream(user.imageData);
                BufferedImage buff= ImageIO.read(bai);
                server.ServerView.centerPanel.setBufferedImage(buff);//Œ™∆¡ƒªº‡øÿ ”Õº…Ë÷√BufferedImage
                server.ServerView.centerPanel.repaint();
                isLive=false;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("ServerShotHandler run is wrong");
        }
    }
}
