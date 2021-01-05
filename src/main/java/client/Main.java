package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("./views/LoginView.fxml"));
        Image image = new Image(getClass().getClassLoader().getResourceAsStream("./images/chat.png"));

        primaryStage.setTitle("Сокет-чат");
        primaryStage.getIcons().add(image);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 300, 140));
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return stage;
    }
}
