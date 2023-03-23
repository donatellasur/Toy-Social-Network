package repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {
    private String url="jdbc:postgresql://localhost:5432/academic";
    private String user="postgres";
    private String password="postgres";

    public JDBCUtils() {
        loadDBCredentials();
    }

    public Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,user,password);
        }catch (SQLException ex){
            System.out.println("Error getting connection" + ex);
        }
        return (Connection) connection;
    }

    private void loadDBCredentials() {
        url = "jdbc:postgresql://localhost:5432/academic";
        user = "postgres";
        password = "postgres";
    }
}
