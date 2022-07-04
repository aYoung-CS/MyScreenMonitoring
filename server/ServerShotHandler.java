package server;

import dbcon.AesEnc;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static protocol.Protocol.DeserializeData;
import static protocol.Protocol.TYPE_IMAGE;

public class ServerShotHandler implements Runnable{
    private Socket socket;
    private DataInputStream dis=null;
    private DataOutputStream dos=null;
    private AesEnc aesEnc = new AesEnc();

    public ServerShotHandler(Socket socket){
        this.socket=socket;
        try {
            this.dis=new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("ServerShotHandler constructor is wrong");
        }
    }
    /**
     * 检查黑名单进程
     * @param RunningProcess
     * @return
     */
    public String checkProcess(String RunningProcess){
        String blacklist = "";
        if(ServerView.Blacklist.endsWith(";"))
            blacklist = ServerView.Blacklist;
        else
            blacklist = ServerView.Blacklist+";";
        StringBuilder IllegalProcess = new StringBuilder();
        while(blacklist.indexOf(";") > 0){
            String p1 = blacklist.substring(0, blacklist.indexOf(";"));
            if(RunningProcess.indexOf(p1) > 0){
                System.out.println("Alert!!!"+p1);
                IllegalProcess.append(p1).append(";");
            }
            blacklist = blacklist.substring(blacklist.indexOf(";")+1);
        }
        if(IllegalProcess.length() > 0)
            return IllegalProcess.substring(0, IllegalProcess.length()-1);
        else
            return null;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[+]dos/dis wrong");
        }
        while (true) {
            try {
                Result result = Protocol.getResult(dis);
//                System.out.println(result + "-----");
                int type = result.getType();
                User user = DeserializeData(aesEnc.decrypt(result.getData()));
                int res;
                byte[] msg = null;
                System.out.println("[+]·服务端解密数据后的type为: " + type);

                if (type == Protocol.TYPE_REGISTER) {
                    res = DataBase.Register(user.getUsername(), user.getPassword(), user.getClientIP(), user.getClientMac());
                    switch(res){
                        case 0:
                            msg = "fail".getBytes(StandardCharsets.UTF_8);
                            break;
                        case 1:
                            msg = "success".getBytes(StandardCharsets.UTF_8);

                            ServerView.ChangeStatus(result.getType(),user.getUsername());
                            break;
                        case 2:
                            msg = "username repeat".getBytes(StandardCharsets.UTF_8);
                            break;
                        default:
                            break;
                    }
                    Protocol.send(Protocol.TYPE_LOGIN, dos, aesEnc.encrypt(msg));
                    dos.flush();
                } else if (type == Protocol.TYPE_LOGIN) {
                    res = DataBase.Login(user.getUsername(), user.getPassword());
                    switch (res){
                        case 0:
                            msg = "fail".getBytes(StandardCharsets.UTF_8);
                            break;
                        case 1:
                            msg = "success".getBytes(StandardCharsets.UTF_8);

                            ServerView.ChangeStatus(result.getType(),user.getUsername());
                            break;
                        default:
                            break;
                    }
                    Protocol.send(Protocol.TYPE_LOGIN, dos, aesEnc.encrypt(msg));
                    dos.flush();
                } else if (type == Protocol.TYPE_LOGOUT) {
                    System.out.println("[+]用户名为" + user.getUsername() + "登出");
                    socket.close();
                    dos.close();
                    dis.close();

                    ServerView.ChangeStatus(result.getType(),user.getUsername());
                    break;
                } else if (type == Protocol.TYPE_IMAGE) {
                    System.out.println("[+]user.imageData length is " + user.imageData.length);
                    ByteArrayInputStream bai=new ByteArrayInputStream(user.imageData);
                    BufferedImage buff= ImageIO.read(bai);
                    // 存图
                    File file=new File("D://RDMS/data/image/"+user.getUsername());
                    if(!file.exists()){//如果文件夹不存在
                        file.mkdir();//创建文件夹
                    }
                    SimpleDateFormat si=new SimpleDateFormat("yy-MM-dd-hh-mm-ss");
///获得当前系统时间  年-月-日 时：分：秒
                    String time=si.format(new Date());
//将时间拼接在文件名上即可
                    File file1=new File("D://RDMS/data/image/"+user.getUsername()+"/"+time+".png");
                    ImageIO.write(buff,"png",file1);
                    Image image = SwingFXUtils.toFXImage(buff, null);
                    ServerView.setImg(image,user.getUsername());
                    String IllegalProcess = checkProcess(user.getRunningProcess());
                    BufferedWriter writer = new BufferedWriter(new FileWriter("D://RDMS/data/AlertList.txt", true));
                    if(IllegalProcess != null) {
                        writer.append(time).append(":").append(user.getUsername()).append(" ").append(IllegalProcess);
                        writer.append("\r\n");
                        writer.close();
                    }
                    msg = (ServerView.ServerFluent+";"+IllegalProcess).getBytes(StandardCharsets.UTF_8);
                    System.out.println("[+]msg is " + new String(aesEnc.encrypt(msg)));
                    Protocol.send(Protocol.TYPE_IMAGE, dos, aesEnc.encrypt(msg));
                    dos.flush();
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("[+]ServerShotHandler run is wrong");
            }
        }
    }
}
