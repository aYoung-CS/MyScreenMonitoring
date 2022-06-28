package dbcon;

import java.sql.*;

public class Database {
    public static Connection DatabaseInit()
    {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
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

    public static void CreateTable(Connection c)/*创建USER表*/
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
     * @param c
     * @param Username 用户名
     * @param Password 密码
     * @param Ip IP
     * @param Mac MAC地址
     * @return 1:注册成功 0:注册错误 2:用户名重复
     * @throws SQLException
     */
    public static int Register(Connection c ,String Username, String Password, String Ip, String Mac) throws SQLException
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
     * @param c
     * @param Username 用户名
     * @param Password 密码
     * @return 登录成功返回1，失败返回0
     */
    private static int Login(Connection c,String Username,String Password)
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

    public static void main( String args[] ) throws SQLException {
//        DatabaseInit();/*sqlite*/
        Connection c = null;
        Statement stmt = null;
        /*连接到数据库*/
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
//        System.out.println(IsTableExist(c));
        if(IsTableExist(c)==false){
            CreateTable(c);
        }
        int result=Login(c,"Tip5y","Test");
        System.out.println(result);
    }
}