import java.sql.*;

public class Main {
    static Connection connection;
    static Statement stmt;
    static PreparedStatement ps;

    public static void main(String[] args) {
        try {
            connect();

            deleteAllEx();
            batchEx();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

    }

    public static void createTableEx() throws SQLException {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS students (\n" +
                "    id    INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                  UNIQUE\n" +
                "                  NOT NULL,\n" +
                "    name  TEXT,\n" +
                "    score BIGINT\n" +
                ");\n");
    }

    public static void dropTableEx() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS students");
    }

    public static void rollbackEx() throws SQLException {
        // connection.setAutoCommit(false);
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('bob1', 20)");
        Savepoint spl = connection.setSavepoint(); // Это на автомате вырубает автокомит, а значит в конце нужно доставить коммит в любом случае
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('bob2', 30)");
        connection.rollback(spl);
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('bob3', 260)");
        connection.setAutoCommit(true);
    }

    public static void batchEx() throws SQLException {
        connection.setAutoCommit(false);
        long t = System.currentTimeMillis();
        for (int i = 0; i < 200000; i++) {
            ps.setString(1, "bob" + i);
            ps.setInt(2, (i * 5) % 100);

            // ps.executeUpdate();
            // А можно кидать в батч, который потом разом закинуть
            ps.addBatch();

        }
        ps.executeBatch();
        connection.setAutoCommit(true);
        System.out.println("time: " + (System.currentTimeMillis() - t));
    }

    public static void transactionEx() throws SQLException {
        connection.setAutoCommit(false);

        long t = System.currentTimeMillis();
        for (int i = 0; i < 200000; i++) {
            stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('bob" + i + "', " + (i * 5) % 100 + ")");
        }
        connection.commit();
        // connection.setAutoCommit(true); // Закомитит изменения и включит обратно автокомит, когда при каждом запросе он лезет в базу
        System.out.println("time: " + (System.currentTimeMillis() - t));
    }

    public static void deleteAllEx() throws SQLException {
        stmt.executeUpdate("DELETE FROM students");
    }

    public static void deleteEx() throws SQLException {
        stmt.executeUpdate("DELETE FROM students WHERE score = 55");
    }

    public static void updateEx() throws SQLException {
        stmt.executeUpdate("UPDATE students SET score = 55 WHERE name = 'Боб1'");
    }

    public static void insertEx() throws SQLException {
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Боб1', 100)");
    }

    public static void selectEx() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT id, name FROM students WHERE score > 30");

        while (rs.next()) {
            System.out.println(rs.getInt(1) + " " + rs.getString(2));
        }
    }

    public static void connect() throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection("jdbc:sqlite:database.db");

        stmt = connection.createStatement();

        ps = connection.prepareStatement("INSERT INTO students (name, score) VALUES (?, ?);");
    }

    public static void disconnect() {
        try {
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
