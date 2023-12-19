package com.example.towerdefense;

public enum EnemyType {
    CIRCLE_ENEMY(1000, 100, "images/circle.png"),
    SQUARE_ENEMY(3000, 200, "images/square.jpg"),
    TRIANGLE_ENEMY(7500, 500, "images/triangle.png"),
    BOSS_ENEMY(15000, 5000, "images/fire.png");

    private final int health;
    private final int attackStrength;
    private final String imagePath;

    EnemyType(int health, int attackStrength, String imagePath) {
        this.health = health;
        this.attackStrength = attackStrength;
        this.imagePath = imagePath;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    public String getImagePath() {
        return imagePath;
    }
}
