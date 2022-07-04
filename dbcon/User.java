package dbcon;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

public class User implements Serializable {
    public String Username;
    public String Password;
    public String RePassword;
    public String ClientIP;
    public String ClientMac;
    public String ServerIP;
    public String ServerPort;
    public int Frequency;
    public byte[] imageData;
    public String RunningProcess;
    public User(){

    }
    public String getClientIP() {
        return ClientIP;
    }

    public void setClientIP(String clientIP) {
        ClientIP = clientIP;
    }

    public String getServerIP() {
        return ServerIP;
    }

    public String getClientMac() {
        return ClientMac;
    }

    public String getPassword() {
        return Password;
    }

    public String getServerPort() {
        return ServerPort;
    }

    public String getUsername() {
        return Username;
    }

    public void setClientMac(String clientMac) {
        ClientMac = clientMac;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean CheckRePassword(String rePassword) {
        return RePassword == this.Password;
    }

    public void setServerIP(String serverIP) {
        ServerIP = serverIP;
    }


    public void setServerPort(String serverPort) {
        ServerPort = serverPort;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getRunningProcess() {
        return RunningProcess;
    }
}
