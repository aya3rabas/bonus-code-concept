package parkwise.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {

        String dbPath = java.nio.file.Paths
                .get("db", "ParkWiseEX31.accdb")
                .toAbsolutePath()
                .toString();

        String url = "jdbc:ucanaccess://" + dbPath;

        return DriverManager.getConnection(url);
    }
}