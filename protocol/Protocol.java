package protocol;

import src.myutil.Result;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;


public class Protocol {
    public static int TYPE_REGISTER=1; // 表示客户端注册
    public static int TYPE_LOGIN=2;    // 表示客户端登录
    public static int TYPE_LOGOUT=3;  // 表示登出
    public static int TYPE_IMAGE=4;   // 表示发送的是屏幕图片数据
    public static int TYPE_RESULT=6;  //表示传输协议中 数据字段关于服务端对于用户登陆注册的响应标志位

    public static void send(int type, DataOutputStream dos,byte[] data) throws IOException {
//        System.out.println(data.length);
        System.out.println("send");
        int TotalLen = 1+4+data.length;
//        System.out.println(TotalLen);
        try {
            System.out.println("type:" + type );
            System.out.println("DataOutputStream object " + dos);

            dos.writeByte(type);
            dos.writeInt(TotalLen);

            System.out.println("TotalLen:" + TotalLen);
            dos.write(data);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("send is wrong");
        }
    }


    public static Result getResult(DataInputStream dis){

        try {
            byte type = dis.readByte();
//            System.out.println("type is " + type);

            int totalLen=dis.readInt();
//            System.out.println("totalLen is " + totalLen);
            byte[] bytes=new byte[totalLen-4-1];

            System.out.println(bytes.length);
            dis.readFully(bytes);
            return new Result(type&0xFF,totalLen,bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("getResult is wrong");
        }
        return null;
    }
}