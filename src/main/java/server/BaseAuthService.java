package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Класс для предоставления источника пользователей для чата.
 * Как хранилище данных используется СУБД SQLite.
 */
public class BaseAuthService implements IAuthService {
    private static final String DATABASE_NAME = "chat.db";

    private Connection connection;

    /**
     * Устанавливает соединение с базой и обрабатывает исключительную ситуацию.
     * С этого метода начинается использование класса.
     */
    @Override
    public void start() {
        try {
            this.connect(DATABASE_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Закрывает соединение с базой. Этим методом заканчивается использование класса.
     */
    @Override
    public void stop() {
        disconnect();
    }

    /**
     * Добавляет нового пользователя, если такого ранее не существовало.
     *
     * @param login
     * @param pass
     */
    @Override
    public String addLoginPass(String login, String pass) {
        int count = 0;

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE login = ? LIMIT 1");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                ps = connection.prepareStatement("INSERT INTO users (login, pass) VALUES (?, ?)");
                ps.setString(1, login);
                ps.setString(2, this.stringToMd5(pass));
                ps.execute();

                return login;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Удаляет указанного пользователя по логину.
     *
     * @param login
     */
    @Override
    public void deleteByLogin(String login) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE login = ?");
            ps.setString(1, login);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает список всех пользователей.
     */
    @Override
    public ArrayList<String> getUsersList() {
        ArrayList<String> users = new ArrayList<>();

        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT login FROM users");

            while(rs.next()) {
                users.add(rs.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Возвращает ник пользователя по его логину и паролю. Пока ником выступает сам логин.
     *
     * @param login
     * @param pass
     */
    @Override
    public String getNickByLoginPass(String login, String pass) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT login FROM users WHERE login = ? AND pass = ? LIMIT 1");
            ps.setString(1, login);
            ps.setString(2, this.stringToMd5(pass));
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Закрываем соединение.
     */
    private void disconnect() {
        try {
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Получения коннекта к базе. В случае, если она не существует, то создаём её
     * и создаём необходимую таблицу с нужной стрктурой.
     *
     * @param filename
     */
    private void connect(String filename) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        String url = "jdbc:sqlite:" + filename;

        this.connection = DriverManager.getConnection(url);

        if (this.connection != null) {
            this.connection.createStatement().execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id    INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL,"
                    + "login VARCHAR(255) UNIQUE,"
                    + "pass  TEXT)");
        }
    }

    /**
     * Функция для преобразования строки с помощью MD5.
     *
     * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     *
     * @param string
     */
    private String stringToMd5(String string) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());

            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();

            for (int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }
}
