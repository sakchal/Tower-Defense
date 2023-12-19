package com.example.towerdefense;

public enum TowerType {
    SNIPER_TOWER(5, 200, 50, 75, "Sniper Tower", "images/sniper_tower.png"),
    GRENADE_TOWER(20, 150, 100, 100, "Grenade Tower", "images/grenade_tower.png"),
    MISSILE_TOWER(75, 150, 200, 100, "Missile Tower", "images/missile_tower.png");

    private final int attackStrength;
    private final int range;
    private final int baseCost;
    private final int upgradeCost;
    private final String name;
    private final String imagePath;

    TowerType(int attackStrength, int range, int baseCost, int upgradeCost, String name,
              String imagePath) {
        this.attackStrength = attackStrength;
        this.range = range;
        this.baseCost = baseCost;
        this.name = name;
        this.upgradeCost = upgradeCost;
        this.imagePath = imagePath;
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    public int getRange() {
        return range;
    }

    public int getBaseCost() {
        return baseCost;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }
}
