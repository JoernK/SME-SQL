import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnector {

    private DBConnector(){

    }

    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:C:/ExampleDB.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
