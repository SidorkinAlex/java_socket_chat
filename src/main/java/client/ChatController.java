package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatController {

    @FXML private TextArea taChat;
    @FXML private TextArea taUserList;
    @FXML private TextField tfMessage;
    @FXML private Label lNick;

    public void logoutButtonAction() {
        this.showLogin();

        ClientConnection.logout();
    }

    public void deleteButtonAction() {
        this.showLogin();

        ClientConnection.delete();
    }

    public void showLogin() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource("./views/LoginView.fxml"));
            Parent window = null;
            try {
                window = (BorderPane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = Main.getStage();

            stage.setResizable(false);
            stage.setScene(new Scene(window));
            stage.setMinWidth(300);
            stage.setMinHeight(140);
            stage.setWidth(300);
            stage.setHeight(140);
            stage.centerOnScreen();
        });
    }

    public void sendButtonAction() {
        ClientConnection.sendMessage(this.tfMessage.getText());
        this.tfMessage.clear();
    }

    public void addMessage(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        this.taChat.appendText(dtf.format(now) + " : " + message + "\n");
        this.taChat.selectPositionCaret(this.taChat.getLength());
    }

    public void setNickLabel(String nick) {
        this.lNick.setText("Чат: " + nick);
    }

    public void updateUserList(String[] users) {
        taUserList.setText("");

        for (int i = 0; i < users.length; i++) {
            taUserList.appendText(users[i] + "\n");
        }
    }
}
