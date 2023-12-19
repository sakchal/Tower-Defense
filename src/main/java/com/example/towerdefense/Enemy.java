package com.example.towerdefense;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Enemy extends Group {
    public static final int IMAGE_SIZE = 50;
    public static final int HEALTH_BAR_WIDTH = 50;
    public static final int HEALTH_BAR_HEIGHT = 5;

    private int health;
    private boolean underAttack;
    private final int maxHealth;
    private final int attackStrength;
    private final String imagePath;

    //Display Components
    private final ImageView image;
    private final Rectangle imageBounds;
    private final Rectangle healthBar;

    public Enemy(EnemyType enemyType) {
        image = new ImageView(new Image(getClass().getResourceAsStream(enemyType.getImagePath())));
        image.setFitWidth(IMAGE_SIZE);
        image.setFitHeight(IMAGE_SIZE);

        imageBounds = new Rectangle(IMAGE_SIZE, IMAGE_SIZE, Color.TRANSPARENT);

        healthBar = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.GREEN);
        healthBar.setArcWidth(HEALTH_BAR_HEIGHT);
        healthBar.setArcHeight(HEALTH_BAR_HEIGHT);
        healthBar.setY(image.getY() + image.getFitHeight() + 5);

        this.getChildren().add(imageBounds);
        this.getChildren().add(image);
        this.getChildren().add(healthBar);

        this.health = enemyType.getHealth();
        this.maxHealth = enemyType.getHealth();
        this.underAttack = false;
        this.attackStrength = enemyType.getAttackStrength();
        this.imagePath = enemyType.getImagePath();
    }

    public boolean isUnderAttack() {
        return underAttack;
    }

    public void setUnderAttack(boolean underAttack) {
        this.underAttack = underAttack;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health = Math.max(health - damage, 0);
        healthBar.setWidth(((double) health / maxHealth) * HEALTH_BAR_WIDTH);
        healthBar.setX((image.getFitWidth() - healthBar.getWidth()) / 2.0);
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getCenterX() {
        return image.getX() + image.getFitWidth() / 2 + getTranslateX();
    }

    public double getCenterY() {
        return image.getY() + image.getFitHeight() / 2 + getTranslateY();
    }

    public void handleFlashAnimation(int frameCounter) {
        healthBar.setFill(underAttack ? Color.RED : Color.GREEN);
        if (underAttack && frameCounter % 10 <= 2) {
            image.setVisible(false);
        } else {
            image.setVisible(true);
        }
    }

    public boolean isImageVisible() {
        return image.isVisible();
    }
}
