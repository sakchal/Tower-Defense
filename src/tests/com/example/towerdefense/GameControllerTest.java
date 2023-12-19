package com.example.towerdefense;

import javafx.animation.PathTransition;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;


public class GameControllerTest {
    @Test
    /*
     * (M2) Tests if player has different starting money amounts for each difficulty level.
     */
    public void playerMoneyVariationTest() throws IOException {
        HashSet<Integer> existingValues = new HashSet<>();
        for (Difficulty d : Difficulty.values()) {
            GameController controller = new GameController(d, "test");
            assertTrue(existingValues.add(controller.getMoney()));
        }
    }

    @Test
    /*
     * (M2) Tests if monument has different starting money amounts for each difficulty level.
     */
    public void monumentHealthVariationTest() throws IOException {
        HashSet<Integer> existingValues = new HashSet<>();
        for (Difficulty d : Difficulty.values()) {
            GameController controller = new GameController(d, "test");
            assertTrue(existingValues.add(controller.getHealth()));
        }
    }

    @Test
    /*
     * (M3) Tests if possible tower locations are correctly identified as valid or invalid.
     * Invalid tower locations are out of the maps' bounds & on the enemy path.
     */
    public void towerInvalidLocationTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        Tower tower = new Tower(TowerType.MISSILE_TOWER);

        tower.setCenterX(-20);
        tower.setCenterY(-20);
        assertFalse(controller.isValidTowerLocation(tower));

        tower.setCenterX(GameController.PATH_COORDS[0]);
        tower.setCenterY(GameController.PATH_COORDS[1]);
        assertFalse(controller.isValidTowerLocation(tower));

        tower.setCenterX(Tower.IMAGE_SIZE / 2.0);
        tower.setCenterY(Tower.IMAGE_SIZE / 2.0);
        assertTrue(controller.isValidTowerLocation(tower));
    }

    @Test
    /*
     * (M4) Tests whether the monument's health decreases after taking damage
     */
    public void monumentTakeDamageTest() {
        Enemy newEnemy = new Enemy(EnemyType.CIRCLE_ENEMY);
        GameController controller = new GameController(Difficulty.EASY, "test");
        while (controller.getHealth() > 0) {
            controller.setHealth(controller.getHealth() - newEnemy.getAttackStrength());
        }
        assertEquals(controller.getHealth(), 0);
    }

    @Test
    /*
     * (M4) Tests whether the damage dealt to the monument by each different enemy type is varied
     */
    public void monumentAttackVariationTest() {
        HashSet<Integer> existingValues = new HashSet<>();
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            GameController controller = new GameController(Difficulty.EASY, "test");
            controller.setHealth(controller.getHealth() - enemy.getAttackStrength());
            assertTrue(existingValues.add(controller.getHealth()));
        }
    }

    @Test
    /*
     * (M4) Tests if monuments health is prevented from being negative
     */
    public void monumentNegativeHealthTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        controller.setHealth(-20);
        assertEquals(controller.getHealth(), 0);
    }

    @Test
    /*
     * (M4) Tests if enemy stops at monument's location
     */
    public void enemyStopTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        Enemy enemy = new Enemy(EnemyType.CIRCLE_ENEMY);
        PathTransition pt = controller.generatePathTransition(enemy);
        pt.play();
        pt.pause();
        pt.jumpTo(pt.getDuration());
        double monumentX = GameController.PATH_COORDS[GameController.PATH_COORDS.length - 2];
        assertEquals(enemy.getCenterX(), monumentX);
    }

    @Test
    /*
     * (M6) Tests if the boss enemy appears spawns last and only once in the enemy queue.
     */
    public void bossEnemySpawnTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        LinkedList<LinkedList<EnemyType>> enemyWaves = controller.getEnemyWaves();
        assertTrue(enemyWaves.size() > 0);
        for (int i = 0; i < enemyWaves.size() - 1; i++) {
            assertFalse(enemyWaves.get(i).contains(EnemyType.BOSS_ENEMY));
        }
        assertEquals(enemyWaves.getLast().indexOf(EnemyType.BOSS_ENEMY),
            enemyWaves.getLast().size() - 1);
    }

    @Test
    /*
     * (M6) Tests if all enemy types are present in the enemy queue.
     */
    public void allEnemySpawnTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        LinkedList<LinkedList<EnemyType>> enemyWaves = controller.getEnemyWaves();
        HashSet<EnemyType> existingValues = new HashSet<>();
        for (LinkedList<EnemyType> enemyWave: enemyWaves) {
            existingValues.addAll(enemyWave);
        }
        assertEquals(existingValues.size(), EnemyType.values().length);
    }

    @Test
    /*
     * (M6) Tests if money is correctly spent & towers are upgraded when player has sufficient money
     */
    public void upgradeTowerSufficientMoneyTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        Tower tower = new Tower(TowerType.MISSILE_TOWER);
        controller.setMoney(tower.getUpgradeCost());
        int initialRange = tower.getRange();
        int initialAttack = tower.getAttackStrength();

        controller.upgradeTower(tower);

        assertTrue(initialRange < tower.getRange() || initialAttack < tower.getAttackStrength());
        assertEquals(controller.getMoney(), 0);
    }

    @Test
    /*
     * (M6) Tests if money isn't spent & towers aren't upgraded when player has insufficient money
     */
    public void upgradeTowerInsufficientMoneyTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        Tower tower = new Tower(TowerType.MISSILE_TOWER);
        controller.setMoney(0);
        int initialRange = tower.getRange();
        int initialAttack = tower.getAttackStrength();

        controller.upgradeTower(tower);

        assertEquals(initialRange, tower.getRange());
        assertEquals(initialAttack, tower.getAttackStrength());
        assertEquals(controller.getMoney(), 0);
    }

    @Test
    /*
     * (M6) Tests if upgrades purchased count is correctly updated when towers are upgraded
     */
    public void upgradesPurchasedCountTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        Tower tower1 = new Tower(TowerType.SNIPER_TOWER);
        Tower tower2 = new Tower(TowerType.GRENADE_TOWER);

        assertEquals(controller.getUpgradesPurchased(), 0);
        controller.upgradeTower(tower1);
        controller.upgradeTower(tower1);
        controller.upgradeTower(tower2);
        assertEquals(controller.getUpgradesPurchased(), 3);
    }

    @Test
    /*
     * (M6) Tests if money spent count is correctly updated when money is spent
     */
    public void moneySpentCountTest() {
        GameController controller = new GameController(Difficulty.EASY, "test");
        int initialMoney = controller.getMoney();

        assertEquals(0, controller.getMoneySpent());
        controller.setMoney(controller.getMoney() - 100);
        assertEquals(100, controller.getMoneySpent());
        controller.setMoney(Integer.MIN_VALUE);
        assertEquals(initialMoney, controller.getMoneySpent());
    }

    @Test
    /*
     * (M6) Tests if the enemy boss's attack strength is high enough to always destroy the monument
     */
    public void bossAttackStrengthTest() {
        Enemy bossEnemy = new Enemy(EnemyType.BOSS_ENEMY);
        for (Difficulty d : Difficulty.values()) {
            GameController controller = new GameController(d, "test");
            assertTrue(bossEnemy.getAttackStrength() > controller.getHealth());
        }
    }
}