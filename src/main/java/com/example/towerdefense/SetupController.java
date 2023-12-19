package com.example.towerdefense;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SetupController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<String> difficultyField;
    @FXML
    private Label alertLabel;

    private static final String[] DIFFICULTY_LEVELS = new String[]{"Easy", "Normal", "Hard"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        difficultyField.getItems().addAll(Arrays.asList(DIFFICULTY_LEVELS));
    }

    public boolean isValidName(String name) {
        if (name.isBlank()) {
            return false;
        }
        return true;
    }

    public boolean isValidDifficulty(String difficulty) {
        if (difficulty == null || (!difficulty.equals("Easy")
                                   && !difficulty.equals("Normal")
                                   && !difficulty.equals("Hard"))) {
            return false;
        }
        return true;
    }

    @FXML
    protected void startGame() throws IOException {
        if (!isValidName(nameField.getText())) {
            alertLabel.setText("Enter Your Name!");
            return;
        }
        if (!isValidDifficulty(difficultyField.getValue())) {
            alertLabel.setText("Select a Difficulty Level!");
            return;
        }

        Difficulty difficulty;
        if (difficultyField.getValue().equals("Easy")) {
            difficulty = Difficulty.EASY;
        } else if (difficultyField.getValue().equals("Normal")) {
            difficulty = Difficulty.NORMAL;
        } else if (difficultyField.getValue().equals("Hard")) {
            difficulty = Difficulty.HARD;
        } else {
            throw new RuntimeException("Unexpected difficulty: " + difficultyField.getValue());
        }

        Stage window = (Stage) nameField.getScene().getWindow();
        GameController gameController = new GameController(difficulty, nameField.getText());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-screen.fxml"));
        fxmlLoader.setController(gameController);
        Scene scene = new Scene(fxmlLoader.load());
        window.setScene(scene);
    }
}
