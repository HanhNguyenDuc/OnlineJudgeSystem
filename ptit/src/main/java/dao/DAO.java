package dao;

import java.sql.Connection;
import java.sql.DriverManager;


public class DAO {
    protected static java.sql.Timestamp convertUtilToSql(java.util.Date uDate) {
        java.sql.Timestamp sDate = new java.sql.Timestamp(uDate.getTime());
        return sDate;
    }
    public static Connection con;
    
    public DAO(){
        if (con == null){
            String dbUrl = "jdbc:mysql://localhost:3306/judge_db?autoReconnect=true&useSSL=false";
            String dbClass = "com.mysql.jdbc.Driver";

            try {
                    Class.forName(dbClass);
                    con = DriverManager.getConnection (dbUrl, "phpmyadmin", "Duchanhctn9_");
            }catch(Exception e) {
                    e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        DAO dao = new DAO();
        
        
    }
}