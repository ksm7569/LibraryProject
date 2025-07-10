package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
            String user = "hr";                     // ← 너의 Oracle 사용자명
            String password = "hr";                 // ← 너의 비번

            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("DB 연결 오류");
            e.printStackTrace();
        }
        return conn;
    }
}