package protocol;

import dbcon.User;

import java.io.*;
import java.util.Base64;


public class Protocol {
    public static int TYPE_REGISTER=1; // 表示客户端注册
    public static int TYPE_LOGIN=2;    // 表示客户端登录
    public static int TYPE_LOGOUT=3;  // 表示登出
    public static int TYPE_IMAGE=4;   // 表示发送的是屏幕图片数据
    public static int TYPE_MODIFYFRE=5;    // 表示传输协议中 修改监控频率
    public static int TYPE_RESULT=6;  //表示传输协议中 数据字段关于服务端对于用户登陆注册的响应标志位

    /**
     * 反序列化
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static User DeserializeData(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bin);
        User user = (User)oin.readObject();
        oin.close();
        bin.close();
        return user;
    }

    /**
     * 序列化
     * @param user
     * @return
     * @throws IOException
     */
    public static byte[] SerializeData(User user) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(user);
        byte[] bytes = bos.toByteArray();
        out.flush();
        return bytes;
    }

    public static void send(int type, DataOutputStream dos, byte[] data) throws IOException {
//        System.out.println(data.length);
        int TotalLen = 1+4+data.length;
//        System.out.println(TotalLen);
        try {
            dos.writeByte(type);
            dos.writeInt(TotalLen);
            dos.write(data);
            dos.flush();
        } catch (Exception e) {
            System.exit(0);
        }
    }

    public static Result getResult(DataInputStream dis){

        try {
            byte type = dis.readByte();
            System.out.println("type is " + type);
            int totalLen=dis.readInt();
            System.out.println("totalLen is " + totalLen);
            byte[] bytes=new byte[totalLen-4-1];
            System.out.println(bytes.length);
            dis.readFully(bytes);
            return new Result(type&0xFF,totalLen,bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("getResult is wrong");
        }
        return null;
    }

}