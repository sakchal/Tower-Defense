package com.example.towerdefense;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameOverController implements Initializable {

    @FXML
    private Button restartBtn;
    @FXML
    private Label header;
    @FXML
    private VBox stats;

    private boolean victory;
    private final int moneySpent;
    private final int enemiesKilled;
    private final int upgradesPurchased;

    public GameOverController(boolean victory, int moneySpent, int enemiesKilled,
                              int upgradesPurchased) {
        this.victory = victory;
        this.moneySpent = moneySpent;
        this.enemiesKilled = enemiesKilled;
        this.upgradesPurchased = upgradesPurchased;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (victory) {
            header.setText("Congrats! You Won!");
        } else {
            header.setText("Game Over!\nDefeated . . .");
        }
        stats.getChildren().add(new Label("Money Spent: " + moneySpent));
        stats.getChildren().add(new Label("Enemies Killed: " + enemiesKilled));
        stats.getChildren().add(new Label("Tower Upgrades: " + upgradesPurchased));

        restartBtn.setOnAction(event -> {
            Stage window = (Stage) restartBtn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("setup-view.fxml"));
            try {
                window.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                System.out.println("Cannot load new screen.");
                e.printStackTrace();
            }
        });
    }
}
