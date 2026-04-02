package tower;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the new cup and lid types developed in cycle 4.
 * Covers one test per cup type and one test per lid type.
 *
 * @author Murillo-Rubiano with support of GPT-4o
 * @version 1.0
 */
public class TowerC4Test {

    private Tower tower;

    @BeforeEach
    public void setUp() {
        tower = new Tower(150, 30);
    }

    @AfterEach
    public void tearDown() {
        tower = null;
    }

    //Cups

    @Test
    public void testNormalCupEntersAndLeaves() {
        //NormalCup comes and goes without restrictions
        tower.pushCup(3, "normal");
        assertTrue(tower.ok());
        tower.popCup();
        assertTrue(tower.ok());
    }

    @Test
    public void testOpenerCupRemovesLids() {
        //OpenerCup removes all lids upon entry
        tower.pushCup(4, "normal");
        tower.pushLid(4);
        tower.pushLid(3);
        tower.pushCup(1, "opener");
        assertTrue(tower.ok());
        //There should be no lids on the stack
        String[][] items = tower.stackingItems();
        for (String[] item : items) {
            assertNotEquals("lid", item[0]);
        }
    }

    @Test
    public void testHierarchicalCupDisplacesSmaller() {
        //HierarchicalCup is inserted before cups with a smaller ID.
        tower.pushCup(1, "normal");
        tower.pushCup(2, "normal");
        tower.pushCup(4, "hierarchical");
        assertTrue(tower.ok());
        //cup 4 must be before cup 1 and 2 in the stack
        String[][] items = tower.stackingItems();
        int idx4 = -1, idx1 = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i][0].equals("cup") && items[i][1].equals("4")) idx4 = i;
            if (items[i][0].equals("cup") && items[i][1].equals("1")) idx1 = i;
        }
        assertTrue(idx4 < idx1);
    }

    @Test
    public void testHeavyCupCannotBeRemovedFromMiddle() {
        //HeavyCup cannot be removed unless it is on top.
        tower.pushCup(3, "heavy");
        tower.pushCup(1, "normal");
        tower.popCup(); //remove cup 1
        assertTrue(tower.ok());
        tower.popCup(); //Remove cup 3 heavy (it's now on top)
        assertTrue(tower.ok());
    }

    //Lids

    @Test
    public void testNormalLidEntersAndLeaves() {
        //NormalLid enters and exits without restrictions
        tower.pushCup(3, "normal");
        tower.pushLid(3, "normal");
        assertTrue(tower.ok());
        tower.popLid();
        assertTrue(tower.ok());
    }

    @Test
    public void testFearfulLidDoesNotEnterWithoutCup() {
        //FearfulLid does not enter if it cup is not in the stack
        tower.pushLid(3, "fearful");
        assertFalse(tower.ok());
    }

    @Test
    public void testFearfulLidDoesNotLeaveWhileCovering() {
        //FearfulLid doesn't come out if it is covering his cup
        tower.pushCup(3, "normal");
        tower.pushLid(3, "fearful");
        assertTrue(tower.ok());
        tower.popLid();
        assertFalse(tower.ok());
    }

    @Test
    public void testCrazyLidGoesBelowItsCup() {
        //CrazyLid is located under its cup
        tower.pushCup(3, "normal");
        tower.pushLid(3, "crazy");
        assertTrue(tower.ok());
        //The lid must be before the cup in the stack
        String[][] items = tower.stackingItems();
        int idxLid = -1, idxCup = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i][0].equals("lid") && items[i][1].equals("3")) idxLid = i;
            if (items[i][0].equals("cup") && items[i][1].equals("3")) idxCup = i;
        }
        assertTrue(idxLid < idxCup);
    }

    @Test
    public void testLockedLidBlocksPopCup() {
        //LockedLid locks popCup while it's on
        tower.pushCup(3, "normal");
        tower.pushLid(3, "locked");
        tower.popCup();
        assertFalse(tower.ok()); //should not be able
        tower.popLid();
        assertTrue(tower.ok()); //removes lid
        tower.popCup();
        assertTrue(tower.ok()); //should be able
    }
}