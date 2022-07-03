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
import javax.naming.directory.SearchControls;
import javax.xml.crypto.Data;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.beans.beancontext.BeanContextServiceAvailableEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        System.out.println("ok");
        File file2=new File("src/AlertList.txt");
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
                    // 存图
                    File file=new File("src/image/"+user.getUsername());
                    if(!file.exists()){//如果文件夹不存在
                        file.mkdir();//创建文件夹
                    }
                    SimpleDateFormat si=new SimpleDateFormat("yy-MM-dd-hh-mm-ss");
///获得当前系统时间  年-月-日 时：分：秒
                    String time=si.format(new Date());
//将时间拼接在文件名上即可
                    File file1=new File("src/image/"+user.getUsername()+"/"+time+".png");
                    ImageIO.write(buff,"png",file1);
                    Image image = SwingFXUtils.toFXImage(buff, null);
                    ServerView.setImg(image,user.getUsername());
                    String IllegalProcess = checkProcess(user.getRunningProcess());
                    BufferedWriter writer = new BufferedWriter(new FileWriter("src/AlertList.txt", true));
                    if(IllegalProcess != null) {
                        writer.append(time).append(":").append(user.getUsername()).append(" ").append(IllegalProcess);
                        writer.append("\r\n");
                        writer.close();
                    }
                    msg = (ServerView.ServerFluent+";"+IllegalProcess).getBytes(StandardCharsets.UTF_8);
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
