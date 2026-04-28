import java.sql.*;
import utils.MyDatabase;

public class TestDB {
    public static void main(String[] args) throws Exception {
        try {
            Connection c = MyDatabase.getInstance().getConnection();
            Statement s = c.createStatement();
            
            // List tables
            ResultSet rsTables = c.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
            System.out.println("--- TABLES ---");
            while(rsTables.next()) {
                System.out.println(rsTables.getString("TABLE_NAME"));
            }
            
            System.out.println("\n--- Querying 'utilisateur' ---");
            try {
                ResultSet rs = s.executeQuery("SELECT * FROM utilisateur LIMIT 5");
                ResultSetMetaData meta = rs.getMetaData();
                int numCol = meta.getColumnCount();
                while(rs.next()) {
                    for(int i=1; i<=numCol; i++) {
                        System.out.print(meta.getColumnName(i) + "=" + rs.getString(i) + " | ");
                    }
                    System.out.println();
                }
            } catch(Exception e) {
                System.out.println("Error querying utilisateur: " + e.getMessage());
            }

            System.out.println("\n--- Querying 'user' ---");
            try {
                ResultSet rs = s.executeQuery("SELECT * FROM user LIMIT 5");
                ResultSetMetaData meta = rs.getMetaData();
                int numCol = meta.getColumnCount();
                while(rs.next()) {
                    for(int i=1; i<=numCol; i++) {
                        System.out.print(meta.getColumnName(i) + "=" + rs.getString(i) + " | ");
                    }
                    System.out.println();
                }
            } catch(Exception e) {
                System.out.println("Error querying user: " + e.getMessage());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
