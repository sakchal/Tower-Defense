package com.example.towerdefense;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {
    @Test
    /*
     * (M4) Tests if each enemy has unique attributes (health & attack strength) from each other.
     */
    public void enemyAttributeVariationTest() {
        HashSet<Pair<Integer, Integer>> existingValues = new HashSet<>();
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            Pair<Integer, Integer> attributes = new Pair<>(enemy.getHealth(),
                enemy.getAttackStrength());
            assertTrue(existingValues.add(attributes));
        }
    }

    @Test
    /*
     * (M4) Tests if the images for every enemy can be found & loaded
     */
    public void enemyImageLoadTest() {
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            assertNotNull(getClass().getResourceAsStream(enemy.getImagePath()));
        }
    }

    @Test
    /*
     * (M4) Tests if takeDamage correctly updates an enemy's health
     */
    public void enemyTakeDamageTest() {
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            enemy.takeDamage(enemy.getHealth());
            assertEquals(enemy.getHealth(), 0);
        }
    }

    @Test
    /*
     * (M4) Tests if getters correctly get the enemy's center (x,y) position
     */
    public void enemyGetCenterPositionTest() {
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            assertEquals(enemy.getCenterX(), Enemy.IMAGE_SIZE / 2.0);
            assertEquals(enemy.getCenterY(), Enemy.IMAGE_SIZE / 2.0);
        }
    }

    @Test
    /*
     * (M4) Tests if translating the enemy correctly update the enemies center (x, y) position
     */
    public void enemyTranslateTest() {
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            double initialCenterX = enemy.getCenterX();
            double initialCenterY = enemy.getCenterY();
            enemy.setTranslateX(50);
            enemy.setTranslateY(50);

            assertEquals(enemy.getCenterX() - initialCenterX, 50);
            assertEquals(enemy.getCenterY() - initialCenterY, 50);
        }
    }

    @Test
    /*
     * (M4) Tests whether each enemy type has a different image path
     */
    public void enemyImagePathVariationTest() {
        HashSet<String> existingValues = new HashSet<>();
        for (EnemyType t : EnemyType.values()) {
            Enemy enemy = new Enemy(t);
            String imagePath = enemy.getImagePath();
            assertTrue(existingValues.add(imagePath));
        }
    }

    @Test
    /*
     * (M5) Tests if enemy can take damage up to 0 health and not into negative health.
     */
    public void takeDamageTest() {
        Enemy enemy = new Enemy(EnemyType.CIRCLE_ENEMY);
        enemy.takeDamage(Integer.MAX_VALUE);
        assertEquals(0, enemy.getHealth());
    }

    @Test
    /*
     * (M5) Tests if tower isInRange method correctly identifies an enemy in range.
     */
    public void towerInRangeTest() {
        Tower tower = new Tower(TowerType.SNIPER_TOWER);
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        tower.setCenterX(enemy.getCenterX());
        tower.setCenterY(enemy.getCenterY());
        assertTrue(tower.isInRange(enemy));
    }

    @Test
    /*
     * (M5) Tests if tower isInRange method correctly identifies an enemy in range.
     */
    public void towerInRangeTest2() {
        Tower tower = new Tower(TowerType.GRENADE_TOWER);
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        tower.setCenterX(enemy.getCenterX() + tower.getRange() / 2.0);
        tower.setCenterY(enemy.getCenterY() + tower.getRange() / 2.0);
        assertTrue(tower.isInRange(enemy));
    }

    @Test
    /*
     * (M5) Tests if tower isInRange method correctly identifies an enemy out of range.
     */
    public void towerNotInRangeTest() {
        Tower tower = new Tower(TowerType.SNIPER_TOWER);
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        tower.setCenterX(enemy.getCenterX());
        tower.setCenterY(enemy.getCenterY() + tower.getRange() + 1);
        assertFalse(tower.isInRange(enemy));
    }

    @Test
    /*
     * (M5) Tests if tower isInRange method correctly identifies an enemy out of range.
     */
    public void towerNotInRangeTest2() {
        Tower tower = new Tower(TowerType.SNIPER_TOWER);
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        tower.setCenterX(enemy.getCenterX() + tower.getRange() / Math.sqrt(2) + 0.1);
        tower.setCenterY(enemy.getCenterY() + tower.getRange() / Math.sqrt(2) + 0.1);
        assertFalse(tower.isInRange(enemy));
    }

    @Test
    /*
     * (M5) Tests if enemy is correctly set to underAttack when in tower range.
     */
    public void isUnderAttack() {
        Tower tower = new Tower(TowerType.SNIPER_TOWER);
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        tower.setCenterX(enemy.getCenterX() + tower.getRange() / 2.0);
        tower.setCenterY(enemy.getCenterY() + tower.getRange() / 2.0);
        enemy.setUnderAttack(tower.isInRange(enemy));
        assertTrue(enemy.isUnderAttack());
    }

    @Test
    /*
     * (M5) Tests if enemy is correctly set to not underAttack when in tower range.
     */
    public void isUnderAttack2() {
        Tower tower = new Tower(TowerType.SNIPER_TOWER);
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        tower.setCenterX(enemy.getCenterX() + tower.getRange());
        tower.setCenterY(enemy.getCenterY() + tower.getRange());
        enemy.setUnderAttack(tower.isInRange(enemy));
        assertFalse(enemy.isUnderAttack());
    }

    @Test
    /*
     * (M5) Tests if enemy setVisibility method is able to update the enemy's visibility.
     */
    public void testVisibility() {
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        enemy.setUnderAttack(false);
        enemy.setVisible(!enemy.isUnderAttack());
        assertTrue(enemy.isVisible());
    }

    @Test
    /*
     * (M5) Tests if enemy flash animation runs when underAttack.
     */
    public void testUnderAttackAnimation() {
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        enemy.setUnderAttack(true);
        boolean wasInvisible = false;

        for (int i = 0; i < 10; i++) {
            enemy.handleFlashAnimation(i);
            if (!enemy.isImageVisible()) {
                wasInvisible = true;
            }
        }
        assertTrue(wasInvisible);
    }

    @Test
    /*
     * (M5) Tests if enemy stops flashing when not under attack.
     */
    public void testUnderAttackAnimation2() {
        Enemy enemy = new Enemy(EnemyType.SQUARE_ENEMY);
        boolean wasInvisible = false;

        for (int i = 0; i < 10; i++) {
            enemy.handleFlashAnimation(i);
            if (!enemy.isImageVisible()) {
                wasInvisible = true;
            }
        }
        assertFalse(wasInvisible);
    }

    @Test
    /*
     * (M6) Tests if the boss enemy has higher health & attack attributes than all other enemies
     */
    public void bossAttributeBalanceTest() {
        Enemy boss = new Enemy(EnemyType.BOSS_ENEMY);
        for (EnemyType t : EnemyType.values()) {
            if (t != EnemyType.BOSS_ENEMY) {
                Enemy enemy = new Enemy(t);
                assertTrue(boss.getHealth() > enemy.getHealth());
                assertTrue(boss.getAttackStrength() > enemy.getAttackStrength());
            }
        }
    }

}
