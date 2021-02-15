package db;

import java.sql.*;

public class Db {
    final private String username = "username";
    final private String password = "root";
    final private String url = "jdbc:oracle:thin@0.0.0.0:1521:orcl";

    private Connection con;

    public static Db getInstance() throws SQLException {
        Db db = new Db();
        try {
            db.con = DriverManager.getConnection(db.url, db.username, db.password);
        } catch (Exception e) {
            throw new SQLException("Connection problem. Check password, username or url");
        }

        return db;
    }

    public ResultSet execute(String q) throws SQLException {
        try {
            Statement st = this.con.createStatement();
            return st.executeQuery(q);
        } catch (Exception e) {
            throw new SQLException("Cannot process the query");
        }
    }

}
