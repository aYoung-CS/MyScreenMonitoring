package server;

import dbcon.User;
import protocol.Protocol;
import protocol.Result;
import server.Server;
import server.ServerView;

import java.io.*;
import java.net.Socket;

public class ServerShotHandler implements Runnable{
    private Socket socket;
    private DataInputStream dis=null;
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
        try {
            System.out.println("ok");
            Result result = Protocol.getResult(dis);
            System.out.println(result+"-----");
//            System.out.println(result.getType());
            int type = result.getType();
            if (type <= 2){
                System.out.println("yes");
                User user = DeserializeData(result.getData());
                System.out.println(user.getPassword());
                System.out.println(user.getUsername());
            }
            else if(result.getType() == Protocol.TYPE_LOGOUT){

            }
            else if(result.getType() == Protocol.TYPE_IMAGE){

            }
        }catch (Exception e){
            System.out.println("ServerShotHandler run is wrong");
        }
    }
}
