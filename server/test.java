package server;

import dbcon.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class test {

    public static void main(String[] args) throws Exception {


        User user = new User();
        user.setClientIP("127.0.0.1");
        user.setPassword("12345678");
        user.setClientIP("192.168.47.1");
        user.setSeverPort("33000");
        user.setClientMac("ssssss");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(user);
        byte[] UserByte = bos.toByteArray();
        System.out.println(UserByte);

        ByteArrayInputStream bis = new ByteArrayInputStream(UserByte);
        ObjectInputStream in = new ObjectInputStream(bis);
        User user2 = (User) in.readObject();

        System.out.println(user2.Password);

    }
}
