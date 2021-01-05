package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class LoginController {
    @FXML private TextField tfLogin;
    @FXML private PasswordField tfPass;
    @FXML private Label lResponse;

    public ChatController chatController;
    public Scene scene;

    private static LoginController instance;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }

    public void showResponse(String message) {
        Platform.runLater(() -> {
            this.lResponse.setText(message);
            this.lResponse.setTextFill(Color.web("#ff7e7e"));
        });
    }

    public void loginButtonAction() throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource("./views/ChatView.fxml"));
        Parent window = (BorderPane) fmxlLoader.load();
        this.chatController = fmxlLoader.<ChatController>getController();

        ClientConnection clientConnection = new ClientConnection(tfLogin.getText(), tfPass.getText(), this.chatController);
        Thread x = new Thread(clientConnection);
        x.start();

        this.scene = new Scene(window);
    }

    public void registerButtonAction() throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource("./views/ChatView.fxml"));
        Parent window = (BorderPane) fmxlLoader.load();
        this.chatController = fmxlLoader.<ChatController>getController();

        ClientConnection clientConnection = new ClientConnection(tfLogin.getText(), tfPass.getText(), this.chatController);
        clientConnection.setRegister(true);
        Thread x = new Thread(clientConnection);
        x.start();

        this.scene = new Scene(window);
    }

    public void showScene() throws IOException {
        Platform.runLater(() -> {
            Stage stage = (Stage) tfLogin.getScene().getWindow();
            stage.setResizable(true);
            stage.setWidth(500);
            stage.setHeight(400);

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(this.scene);
            stage.setMinWidth(300);
            stage.setMinHeight(300);
            stage.centerOnScreen();
        });
    }
}
