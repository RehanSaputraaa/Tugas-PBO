package pertanian;

import java.sql.*;
import javax.swing.JOptionPane;

public class dbConnect {
    public static Connection connectDB() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pertanian", "root", "");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
