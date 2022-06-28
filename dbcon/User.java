package dbcon;

public class User {
    public String Username;
    public String Password;
    public String RePassword;
    public String ClientIP;
    public String ClientMac;
    public String severIP;
    public String SeverPort;

    public User(){

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
