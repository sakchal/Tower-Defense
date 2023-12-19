package com.example.towerdefense;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Tower extends Group {
    public static final int IMAGE_SIZE = 50;
    public static final int MAX_LEVEL = 3;

    //Display Components
    private final ImageView image;
    private final Rectangle imageBounds;
    private final Circle circle;
    private Label label;
    private Button upgradeBtn;

    //Tower Properties
    private int level;
    private int attackStrength;
    private int range;
    private final int upgradeCost;
    private final String imagePath;

    public Tower(TowerType towerType) {
        this.attackStrength = towerType.getAttackStrength();
        this.imagePath = towerType.getImagePath();
        this.range = towerType.getRange();
        this.upgradeCost = towerType.getUpgradeCost();
        this.level = 1;

        image = new ImageView(new Image(getClass().getResourceAsStream(towerType.getImagePath())));
        image.setFitWidth(IMAGE_SIZE);
        image.setFitHeight(IMAGE_SIZE);

        imageBounds = new Rectangle(IMAGE_SIZE, IMAGE_SIZE, Color.TRANSPARENT);

        circle = new Circle(this.range, Color.TRANSPARENT);
        circle.setStroke(Color.DARKGRAY);
        circle.setStrokeWidth(2);

        this.getChildren().add(imageBounds);
        this.getChildren().add(image);
        this.getChildren().add(circle);
    }

    public void initializeControls() {
        label = new Label(level + "");
        upgradeBtn = new Button("Upgrade $(" + getUpgradeCost() + ")");
        this.getChildren().add(label);
        this.getChildren().add(upgradeBtn);
        this.setCenterX(this.getCenterX());
        this.setCenterY(this.getCenterY());
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    public int getRange() {
        return range;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public String upgrade() {
        if (level == MAX_LEVEL) {
            return null;
        }
        level++;
        attackStrength += 50;
        range += 25;
        circle.setRadius(range);

        if (upgradeBtn != null && label != null) {
            label.setText(level + "");
            if (level >= MAX_LEVEL) {
                upgradeBtn.setDisable(true);
                upgradeBtn.setVisible(false);
                label.setText(level + " (Max)");
            }
        }
        return "Upgraded Tower to Level " + level + "! Attack +50. Range +25";
    }

    public void setCenterX(double x) {
        image.setX(x - image.getFitWidth() / 2);
        imageBounds.setX(image.getX());
        circle.setCenterX(getCenterX());
        if (upgradeBtn != null && label != null) {
            upgradeBtn.setLayoutX(x - upgradeBtn.getWidth() / 2.0);
            label.setLayoutX(x - image.getFitWidth() / 2 - 10);
        }
    }

    public void setCenterY(double y) {
        image.setY(y - image.getFitHeight() / 2);
        imageBounds.setY(image.getY());
        circle.setCenterY(getCenterY());
        if (upgradeBtn != null && label != null) {
            upgradeBtn.setLayoutY(y + image.getFitHeight() / 2 + 5);
            label.setLayoutY(y - image.getFitHeight() / 2 - 20);
        }
    }

    public double getCenterX() {
        return image.getX() + image.getFitWidth() / 2;
    }

    public double getCenterY() {
        return image.getY() + image.getFitHeight() / 2;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Rectangle getImageBounds() {
        return imageBounds;
    }

    public Button getUpgradeBtn() {
        return upgradeBtn;
    }

    public void setTranslucent(boolean translucent) {
        this.setOpacity(translucent ? 0.25 : 1);
    }

    public boolean isInRange(Enemy enemy) {
        double deltaX = Math.abs(this.getCenterX() - enemy.getCenterX());
        double deltaY = Math.abs(this.getCenterY() - enemy.getCenterY());

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= range;
    }

    public static int getCost(TowerType towerType, Difficulty difficulty) {
        int difficultyMultiplier;
        switch (difficulty) {
        case EASY:
            difficultyMultiplier = 1;
            break;
        case NORMAL:
            difficultyMultiplier = 2;
            break;
        case HARD:
            difficultyMultiplier = 3;
            break;
        default:
            throw new IllegalStateException("Unexpected difficulty value: " + difficulty);
        }

        return towerType.getBaseCost() * difficultyMultiplier;
    }
}
