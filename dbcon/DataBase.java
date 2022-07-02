package dbcon;

import java.sql.*;
import java.util.Vector;

public class DataBase {

    public static Connection c;

    public static Connection DatabaseInit()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:D:/t/identifier.sqlite");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println("数据库初始化失败");
        }
        System.out.println("Opened database successfully");
        return c;
    }

    public static boolean IsTableExist(Connection c)
    {
        Statement stmt = null;
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT 1 FROM USER;" );
            return true;
        }catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }

    public static Vector UserList(Connection c){
        Vector<User> v = new Vector<User>();

        try {
            c.setAutoCommit(false);
            Statement stmt = null;
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM USER;" );
            while ( rs.next() ) {
                User tmpuser = new User();
                String id = rs.getString("ID");
                String username = rs.getString("USERNAME");
                String ip = rs.getString("IP");
                String mac = rs.getString("MAC");
                tmpuser.Username=username;
                tmpuser.ClientIP=ip;
                tmpuser.ClientMac=mac;
                v.add(tmpuser);
//                System.out.println( "ID = " + id );
//                System.out.println( "USERNAME = " + username );
//                System.out.println( "IP = " + ip );
//                System.out.println( "MAC = " + mac );
//                System.out.println();
            }
            rs.close();
            stmt.close();
//            System.out.println(v.get(0).getUsername());
            return v;
        } catch ( Exception e ) {
            System.err.println( e);
            return null;
        }
    }



    public static void CreateTable()/*创建USER表*/
    {
        Statement stmt=null;
        try{
            stmt = c.createStatement();
            String sql = "CREATE TABLE USER " +
                    "(ID integer PRIMARY KEY autoincrement," +
                    " USERNAME           TEXT    NOT NULL, " +
                    " PASSWORD           TEXT    NOT NULL, " +
                    " IP                 TEXT    NOT NULL, " +
                    " MAC                TEXT    NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
        }catch (Exception e){
            System.out.println("Create Table Wrong");
            System.err.println(e);
        }
    }

    /**
     * 用户注册函数
     * @param Username 用户名
     * @param Password 密码
     * @param Ip IP
     * @param Mac MAC地址
     * @return 1:注册成功 0:注册错误 2:用户名重复
     * @throws SQLException
     */
    public static int Register(String Username, String Password, String Ip, String Mac) throws SQLException
    {
        Statement stmt=null;
        /*校验Username是否重复*/
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM USER WHERE USERNAME=\""+Username+"\";" );
        int rowCount = 0;
        while(rs.next()) {
            rowCount++;
        }
        if (rowCount>0)
            return 2;

        /*传参*/
        try {
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO USER (USERNAME,PASSWORD,IP,MAC) " +
                    "VALUES ("+"\""+Username+"\",\""+Password+"\",\""+Ip+"\",\""+Mac+ "\");";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            c.commit();
            return 1;
        } catch ( Exception e ) {
            System.err.println(e);
            return 0;
        }
    }


    /**
     * 用户登录函数
     * @return 登录成功返回1，失败返回0
     */
    public static int Login(String Username, String Password)
    {
        Statement stmt=null;
        try {
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM USER WHERE USERNAME=\""+Username+"\" AND PASSWORD=\""+Password+"\";" );
            int flag = 0;
            while(rs.next()) {
                flag++;
            }
            if(flag!=0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        } catch ( Exception e ) {
            System.err.println(e);
            return 0;
        }
    }

//    /**
//     * init
//     */
//    public DataBase(){
//        c = null;
//        Statement stmt = null;
//        try {
//            Class.forName("org.sqlite.JDBC");
//            c = DriverManager.getConnection("jdbc:sqlite:D:/t/identifier.sqlite");
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//        }
//        if(IsTableExist(c)==false){
//            CreateTable(c);
//        }
//    }

    public static void main( String args[] ) throws SQLException {
//        DatabaseInit();/*sqlite*/
//        Connection c = null;
//        Statement stmt = null;
//        /*连接到数据库*/
//        try {
//            Class.forName("org.sqlite.JDBC");
//            c = DriverManager.getConnection("jdbc:sqlite:D:/t/identifier.sqlite");
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//        }
//        System.out.println(IsTableExist(c));


//        int result=Register(c,"Tip5y","Test", "127.0.0.1", "MACBADS");
//        System.out.println(result);
    }
}