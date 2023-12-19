package com.example.towerdefense;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class TowerTest {
    @Test
    /*
      (M3) Tests if every tower has a different cost for each difficulty level.
     */
    public void towerCostVariationTest() {
        for (TowerType t : TowerType.values()) {
            HashSet<Integer> existingValues = new HashSet<>();
            for (Difficulty d : Difficulty.values()) {
                assertTrue(existingValues.add(Tower.getCost(t, d)));
            }
        }
    }

    @Test
    /*
     * (M3) Tests if each tower has unique attributes (health & attack strength) from each other.
     */
    public void towerAttributeVariationTest() {
        HashSet<Integer> existingValues = new HashSet<>();
        for (TowerType t : TowerType.values()) {
            Tower tower = new Tower(t);
            assertTrue(existingValues.add(tower.getAttackStrength()));
        }
    }

    @Test
    /*
     * (M3) Tests if the images for every tower can be found & loaded
     */
    public void towerImageLoadTest() {
        for (TowerType t : TowerType.values()) {
            Tower tower = new Tower(t);
            assertNotNull(getClass().getResourceAsStream(tower.getImagePath()));
        }
    }

    @Test
    /*
     * (M6) Tests if all level upgrades increase either the range or attack attribute for each tower
     */
    public void towerUpgradeAttributesTest() {
        for (TowerType t : TowerType.values()) {
            Tower tower = new Tower(t);
            for (int i = 1; i < Tower.MAX_LEVEL; i++) {
                int initialRange = tower.getRange();
                int initialAttack = tower.getAttackStrength();
                tower.upgrade();
                assertTrue(initialRange < tower.getRange()
                    || initialAttack < tower.getAttackStrength());
            }
        }
    }

    @Test
    /*
     * (M6) Tests if towers can no longer upgrade once they reach max level
     */
    public void towerUpgradeMaxLevelTest() {
        for (TowerType t : TowerType.values()) {
            Tower tower = new Tower(t);
            for (int i = 1; i < Tower.MAX_LEVEL; i++) {
                tower.upgrade();
            }
            int initialRange = tower.getRange();
            int initialAttack = tower.getAttackStrength();
            tower.upgrade();
            assertEquals(initialRange, tower.getRange());
            assertEquals(initialAttack, tower.getAttackStrength());
        }
    }

}
