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
            System.out.println("���ݿ��ʼ��ʧ��");
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

    public static void CreateTable(Connection c)/*����USER��*/
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
     * �û�ע�ắ��
     * @param c
     * @param Username �û���
     * @param Password ����
     * @param Ip IP
     * @param Mac MAC��ַ
     * @return 1:ע��ɹ� 0:ע����� 2:�û����ظ�
     * @throws SQLException
     */
    public static int Register(Connection c ,String Username, String Password, String Ip, String Mac) throws SQLException
    {
        Statement stmt=null;
        /*У��Username�Ƿ��ظ�*/
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM USER WHERE USERNAME=\""+Username+"\";" );
        int rowCount = 0;
        while(rs.next()) {
            rowCount++;
        }
        if (rowCount>0)
            return 2;

        /*����*/
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
     * �û���¼����
     * @param c
     * @param Username �û���
     * @param Password ����
     * @return ��¼�ɹ�����1��ʧ�ܷ���0
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
        /*���ӵ����ݿ�*/
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