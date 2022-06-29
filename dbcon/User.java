package dbcon;

import java.io.Serializable;

public class User implements Serializable {
    public String Username;
    public String Password;
    public String RePassword;
    public String ClientIP;
    public String ClientMac;
    public String severIP;
    public String SeverPort;
    public byte[] imageData;
    public String ClientMonitor;
    public String Status;
    public User(){

    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatus() {
        return Status;
    }

    public void setClientMonitor(String clientMonitor) {
        ClientMonitor = clientMonitor;
    }

    public String getClientMonitor() {
        return ClientMonitor;
    }

    public String getClientIP() {
        return ClientIP;
    }

    public void setClientIP(String clientIP) {
        ClientIP = clientIP;
    }

    public String getSeverIP() {
        return severIP;
    }

    public String getClientMac() {
        return ClientMac;
    }

    public String getPassword() {
        return Password;
    }

    public String getSeverPort() {
        return SeverPort;
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

    public void setSeverIP(String severIP) {
        this.severIP = severIP;
    }


    public void setSeverPort(String severPort) {
        SeverPort = severPort;
    }

    public void setUsername(String username) {
        Username = username;
    }
}