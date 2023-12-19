package com.example.towerdefense;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GameController implements Initializable {
    @FXML
    private Label moneyLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label healthLabel;
    @FXML
    private GridPane towerShop;
    @FXML
    private Label shopAlertLabel;
    @FXML
    private AnchorPane map;
    @FXML
    private Polyline path;
    @FXML
    private Group monument;
    @FXML
    private Button combatBtn;

    //Map Level Design
    public static final int PATH_STROKE_WIDTH = 50;
    public static final Double[] PATH_COORDS = {
        PATH_STROKE_WIDTH / 2.0, 275.0,
        250.0, 275.0,
        350.0, 175.0,
        550.0, 375.0,
        650.0, 275.0,
        850.0, 275.0,
        850.0, 100.0,
        1050.0, 100.0,
        1250.0, 400.0,
        1450.0, 400.0
    };

    private final LinkedList<LinkedList<EnemyType>> enemyWaves = new LinkedList<>(Arrays.asList(
        new LinkedList<>(Arrays.asList(EnemyType.CIRCLE_ENEMY, EnemyType.CIRCLE_ENEMY,
            EnemyType.CIRCLE_ENEMY, EnemyType.CIRCLE_ENEMY, EnemyType.CIRCLE_ENEMY,
            EnemyType.SQUARE_ENEMY, EnemyType.SQUARE_ENEMY)),
        new LinkedList<>(Arrays.asList(EnemyType.SQUARE_ENEMY, EnemyType.SQUARE_ENEMY,
            EnemyType.SQUARE_ENEMY, EnemyType.CIRCLE_ENEMY, EnemyType.CIRCLE_ENEMY,
            EnemyType.CIRCLE_ENEMY, EnemyType.SQUARE_ENEMY)),
        new LinkedList<>(Arrays.asList(EnemyType.TRIANGLE_ENEMY, EnemyType.TRIANGLE_ENEMY,
            EnemyType.TRIANGLE_ENEMY, EnemyType.CIRCLE_ENEMY, EnemyType.SQUARE_ENEMY,
            EnemyType.TRIANGLE_ENEMY)),
        new LinkedList<>(Arrays.asList(EnemyType.SQUARE_ENEMY, EnemyType.CIRCLE_ENEMY,
            EnemyType.TRIANGLE_ENEMY, EnemyType.TRIANGLE_ENEMY, EnemyType.TRIANGLE_ENEMY,
            EnemyType.BOSS_ENEMY))
    ));

    //Player attributes
    private final String name;
    private int money;
    private int health;

    //Map attributes
    private final Difficulty difficulty;
    private Tower purchasedTower;
    private final List<Tower> placedTowers;
    private final List<Enemy> placedEnemies;

    //Statistics
    private int moneySpent;
    private int enemiesKilled;
    private int upgradesPurchased;

    public GameController(Difficulty difficulty, String name) {
        this.difficulty = difficulty;
        this.name = name;
        this.moneySpent = 0;
        this.enemiesKilled = 0;
        this.upgradesPurchased = 0;

        switch (difficulty) {
        case EASY:
            this.health = 3000;
            this.money = 3000;
            break;
        case NORMAL:
            this.health = 2000;
            this.money = 2000;
            break;
        case HARD:
            this.health = 1000;
            this.money = 1000;
            break;
        default:
            throw new IllegalStateException("Unexpected difficulty value: " + difficulty);
        }

        purchasedTower = null;
        placedTowers = new ArrayList<>();
        placedEnemies = new ArrayList<>();

        //Redundant but needed for unit tests to work
        path = new Polyline();
        path.setStrokeWidth(PATH_STROKE_WIDTH);
        path.getPoints().addAll(PATH_COORDS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set label values
        nameLabel.setText("Name: " + this.name);
        setMoneyAndLabel(this.money);
        setHealthAndLabel(this.health);

        //Set path & monument location
        path.setStrokeWidth(PATH_STROKE_WIDTH);
        path.getPoints().addAll(PATH_COORDS);
        monument.setLayoutX(PATH_COORDS[PATH_COORDS.length - 2]);
        monument.setLayoutY(PATH_COORDS[PATH_COORDS.length - 1] - 50);

        //Generate TowerShop
        int column = 0;
        for (TowerType t : TowerType.values()) {
            Label towerLabel = new Label(t.getName() + " ($" + Tower.getCost(t, difficulty) + ")");
            towerLabel.setFont(new Font("Times New Roman", 18.0));
            towerLabel.setPadding(new Insets(15));

            ImageView towerImage = new ImageView(
                new Image(getClass().getResourceAsStream(t.getImagePath()))
            );
            towerImage.setPreserveRatio(true);
            towerImage.setFitHeight(75);
            Button towerButton = new Button(null, towerImage);
            towerButton.setOnAction(event -> buyTower(t));
            towerShop.add(towerLabel, column, 0);
            towerShop.add(towerButton, column, 1);
            column++;
        }

        //Setup startCombat button
        combatBtn.setOnAction(event -> {
            gameLoop.start();
        });

        //Setup MOUSE_PRESSED && MOUSE_MOVED event handlers for tower purchasing
        map.addEventHandler(MouseEvent.MOUSE_PRESSED, this::placeTower);
        map.addEventHandler(MouseEvent.MOUSE_MOVED, this::hoverPurchasedTower);
    }

    private final AnimationTimer gameLoop = new AnimationTimer() {
        private int frameCounter = 0;
        private int enemyWaveDelay = -1;

        @Override
        public void handle(long l) {
            //Spawn Enemies
            if (enemyWaves.size() > 0) {
                if (!enemyWaves.peek().isEmpty()) {
                    if (frameCounter % 30 == 0) {
                        placeEnemy(enemyWaves.peek().remove());
                    }
                } else {
                    if (placedEnemies.isEmpty()) {
                        if (enemyWaveDelay == 0) {
                            enemyWaves.remove();
                            enemyWaveDelay = -1;
                        } else if (enemyWaveDelay == -1) {
                            enemyWaveDelay = 300;
                        } else {
                            enemyWaveDelay--;
                        }
                    }
                }
            }

            //Enemy Animations
            for (Enemy enemy: placedEnemies) {
                enemy.handleFlashAnimation(frameCounter);
                enemy.setUnderAttack(false);
            }

            //Tower Attack
            for (Tower tower : placedTowers) {
                Iterator<Enemy> iterator = placedEnemies.iterator();
                while (iterator.hasNext()) {
                    Enemy enemy = iterator.next();
                    if (tower.isInRange(enemy)) {
                        enemy.setUnderAttack(true);
                        if (frameCounter % 10 == 0) {
                            enemy.takeDamage(tower.getAttackStrength());
                            if (enemy.getHealth() <= 0) {
                                map.getChildren().remove(enemy);
                                iterator.remove();
                                setMoneyAndLabel(money + 50);
                                enemiesKilled++;
                            }
                        }
                    }
                }
            }

            //Check Monument Health
            if (getHealth() <= 0) {
                loadGameOverScreen(false);
            }

            //Check Victory
            if (placedEnemies.isEmpty() && enemyWaves.isEmpty()) {
                loadGameOverScreen(true);
            }

            //Increment Counter
            frameCounter++;
        }
    };

    public void buyTower(TowerType towerType) {
        if (purchasedTower != null) {
            setShopAlert(false, "Place your purchased tower before buying a new tower!");
            return;
        }

        int cost = Tower.getCost(towerType, difficulty);

        if (cost > this.money) {
            setShopAlert(false, "Cannot buy tower. Need " + (cost - this.money) + " more funds.");
            return;
        }

        setMoneyAndLabel(this.money - cost);
        purchasedTower = new Tower(towerType);
        purchasedTower.initializeControls();
        map.getChildren().add(purchasedTower);
        purchasedTower.setVisible(false);
        setShopAlert(true, "Bought a tower! Click on map to place tower.");
    }

    private void hoverPurchasedTower(MouseEvent mouseEvent) {
        if (purchasedTower != null) {
            purchasedTower.setCenterX(mouseEvent.getX());
            purchasedTower.setCenterY(mouseEvent.getY());
            purchasedTower.setVisible(isOnMap(purchasedTower));
            purchasedTower.setTranslucent(!isValidTowerLocation(purchasedTower));
        }
    }

    private void placeTower(MouseEvent mouseEvent) {
        if (purchasedTower != null) {
            purchasedTower.setCenterX(mouseEvent.getX());
            purchasedTower.setCenterY(mouseEvent.getY());

            if (isValidTowerLocation(purchasedTower)) {
                purchasedTower.setVisible(true);
                purchasedTower.setTranslucent(false);
                final Tower tempTower = purchasedTower;
                purchasedTower.getUpgradeBtn().setOnAction(event -> {
                    String res = upgradeTower(tempTower);
                    if (res == null) {
                        setShopAlert(false, "Don't have enough money to upgrade tower!");
                    } else {
                        setShopAlert(true, res);
                    }
                    setMoneyAndLabel(this.money);
                });
                placedTowers.add(purchasedTower);
                purchasedTower = null;
                setShopAlert(true, "Select a tower to purchase");
            } else {
                setShopAlert(false, "You can't place towers on the path!");
            }
        }
    }

    public String upgradeTower(Tower tower) {
        if (tower.getUpgradeCost() > this.money) {
            return null;
        }

        String res = tower.upgrade();

        if (res != null) {
            setMoney(this.money - tower.getUpgradeCost());
            upgradesPurchased++;
        }

        return res;
    }

    private void placeEnemy(EnemyType enemyType) {
        Enemy enemy = new Enemy(enemyType);
        enemy.setTranslateX(-Double.MAX_VALUE);
        enemy.setTranslateY(-Double.MAX_VALUE);
        placedEnemies.add(enemy);
        map.getChildren().add(enemy);
        PathTransition pt = generatePathTransition(enemy);
        pt.setOnFinished(event -> {
            if (placedEnemies.contains(enemy)) {
                setHealthAndLabel(getHealth() - enemy.getAttackStrength());
                map.getChildren().remove(enemy);
                placedEnemies.remove(enemy);
            }
        });
        pt.play();
    }

    public PathTransition generatePathTransition(Enemy enemy) {
        PathTransition pt = new PathTransition(Duration.seconds(10), path, enemy);
        pt.setInterpolator(Interpolator.LINEAR);
        return pt;
    }

    public boolean isValidTowerLocation(Tower tower) {
        Shape pathIntersect = Shape.intersect(path, tower.getImageBounds());
        return pathIntersect.getBoundsInLocal().getWidth() == -1 && isOnMap(tower);
    }

    public boolean isOnMap(Tower tower) {
        Rectangle towerBounds = tower.getImageBounds();
        return towerBounds.getX() >= 0
            && towerBounds.getY() >= 0
            && towerBounds.getX() + towerBounds.getWidth() < 1600
            && towerBounds.getY() + towerBounds.getHeight() < 550;
    }

    public void setShopAlert(boolean success, String message) {
        shopAlertLabel.setTextFill(success ? Color.GREEN : Color.RED);
        shopAlertLabel.setText(message);
    }

    private void loadGameOverScreen(boolean victory) {
        gameLoop.stop();
        Stage window = (Stage) moneyLabel.getScene().getWindow();
        GameOverController gameOverController = new GameOverController(victory,
            moneySpent, enemiesKilled, upgradesPurchased);
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("game-over-screen.fxml"));
        fxmlLoader.setController(gameOverController);
        try {
            window.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMoneyAndLabel(int money) {
        setMoney(money);
        moneyLabel.setText("Money: $" + this.money);
    }

    private void setHealthAndLabel(int health) {
        setHealth(health);
        healthLabel.setText("Health: " + this.health);
    }

    public void setMoney(int money) {
        if (money < this.money) {
            if (this.money - money < 0) {
                moneySpent += this.money;
            } else {
                moneySpent += this.money - money;
            }
        }
        this.money = Math.max(money, 0);
    }

    public int getMoney() {
        return money;
    }

    public void setHealth(int health) {
        this.health = Math.max(health, 0);
    }

    public int getHealth() {
        return health;
    }

    public LinkedList<LinkedList<EnemyType>> getEnemyWaves() {
        return enemyWaves;
    }

    public int getUpgradesPurchased() {
        return upgradesPurchased;
    }

    public int getMoneySpent() {
        return moneySpent;
    }
}
