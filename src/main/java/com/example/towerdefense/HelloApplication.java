package com.example.towerdefense;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tower Defense");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setWidth(1800);
        stage.setHeight(900);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}