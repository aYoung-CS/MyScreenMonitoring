package protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;


public class Protocol {
    public static int TYPE_REGISTER=1; // ��ʾ�ͻ���ע��
    public static int TYPE_LOGIN=2;    // ��ʾ�ͻ��˵�¼
    public static int TYPE_LOGOUT=3;  // ��ʾ�ǳ�
    public static int TYPE_IMAGE=4;   // ��ʾ���͵�����ĻͼƬ����
    public static int TYPE_USER=5;    // ��ʾ����Э���� �����ֶι���user�û�����Ϣ�ı�־λ��ֵΪuser������л�����
    public static int TYPE_RESULT=6;  //��ʾ����Э���� �����ֶι��ڷ���˶����û���½ע�����Ӧ��־λ

    public static void send(int type, DataOutputStream dos,byte[] data) throws IOException {
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
//            System.out.println("type is " + type);
            int totalLen=dis.readInt();
//            System.out.println("totalLen is " + type);
            byte[] bytes=new byte[totalLen-4-1];
//            System.out.println(bytes.length);
            dis.readFully(bytes);
            return new Result(type&0xFF,totalLen,bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("getResult is wrong");
        }
        return null;
    }

}