package tower;


import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for Tower - Cycle 2.
 * All tests run in invisible mode.
 *
 * @author Murillo-Rubiano (with support from Claude Sonnet 4.6)
 * @version (1)
 */
public class TowerC2Test {

    // pruebas para el constructor 2
    /**
     * Should create exactly n cups with IDs from 1 to n.
     */

    @Test
    public void accordingMRShouldCreateExactlyNCups() {
        Tower tower = new Tower(4);
        String[][] items = tower.stackingItems();
        assertEquals(4, items.length);
    }

    /**
     * Should calculate maximum height as 2^n - 1.
     */
    @Test
    public void accordingMRShouldCalculateCorrectMaxHeight() {
        Tower tower = new Tower(4);
        // Real stacked height: only cup 4 counts = 2^(4-1) = 8
        int realHeight = tower.height();
        assertEquals(8, realHeight);
        // Must stay within maxHeight = 2^4 - 1 = 15
        assertTrue(realHeight <= 15);
    }

    /**
     * Should stack cups from largest at bottom to smallest at top.
     */
    @Test
    public void accordingMRShouldStackCupsLargestAtBottom() {
        Tower tower = new Tower(3);
        String[][] items = tower.stackingItems();
        assertEquals("3", items[0][1]);
        assertEquals("1", items[items.length - 1][1]);
    }

    /**
     * Should return ok() true after construction.
     */
    @Test
    public void accordingMRShouldReturnOkAfterConstruction() {
        Tower tower = new Tower(4);
        assertTrue(tower.ok());
    }

    /**
     * Should not create any lids.
     */
    @Test
    public void accordingMRShouldNotCreateAnyLids() {
        Tower tower = new Tower(4);
        String[][] items = tower.stackingItems();
        for (String[] item : items) {
            assertNotEquals("lid", item[0]);
        }
    }

    /**
     * Should not leave the stack empty.
     */
    @Test
    public void accordingMRShouldNotLeaveStackEmpty() {
        Tower tower = new Tower(4);
        String[][] items = tower.stackingItems();
        assertTrue(items.length > 0);
    }

    /**
     * Should not stack cups in reverse order (smallest at bottom).
     */
    @Test
    public void accordingMRShouldNotStackSmallestAtBottom() {
        Tower tower = new Tower(3);
        String[][] items = tower.stackingItems();
        assertNotEquals("1", items[0][1]);
    }

    /**
     * Should not create cups with IDs outside the range 1 to n.
     */
    @Test
    public void accordingMRShouldNotCreateCupsOutsideRange() {
        Tower tower = new Tower(3);
        String[][] items = tower.stackingItems();
        for (String[] item : items) {
            int id = Integer.parseInt(item[1]);
            assertTrue(id >= 1 && id <= 3);
        }
    }

    // Pruebas metodo swap(String[] o1, String[] o2)

    /**
     * Should correctly swap two cups in the stack.
     */
    @Test
    public void accordingMRShouldSwapTwoCups() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.pushCup(3);
        String[] o1 = { "cup", "1" };
        String[] o2 = { "cup", "3" };
        tower.swap(o1, o2);
        String[][] items = tower.stackingItems();
        assertEquals("3", items[0][1]);
        assertEquals("1", items[1][1]);
        assertTrue(tower.ok());
    }

    /**
     * Should correctly swap two lids in the stack.
     */
    @Test
    public void accordingMRShouldSwapTwoLids() {
        Tower tower = new Tower(10, 20);
        tower.pushLid(1);
        tower.pushLid(2);
        String[] o1 = { "lid", "1" };
        String[] o2 = { "lid", "2" };
        tower.swap(o1, o2);
        String[][] items = tower.stackingItems();
        assertEquals("2", items[0][1]);
        assertEquals("1", items[1][1]);
        assertTrue(tower.ok());
    }

    /**
     * Should correctly swap a cup with a lid.
     */
    @Test
    public void accordingMRShouldSwapCupWithLid() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.pushLid(2);
        String[] o1 = { "cup", "1" };
        String[] o2 = { "lid", "2" };
        tower.swap(o1, o2);
        String[][] items = tower.stackingItems();
        assertEquals("lid", items[0][0]);
        assertEquals("cup", items[1][0]);
        assertTrue(tower.ok());
    }

    /**
     * Should not swap if first object does not exist in stack.
     */
    @Test
    public void accordingMRShouldNotSwapIfFirstObjectNotFound() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.pushCup(2);
        String[] o1 = { "cup", "99" };
        String[] o2 = { "cup", "2" };
        tower.swap(o1, o2);
        assertFalse(tower.ok());
    }

    /**
     * Should not swap if second object does not exist in stack.
     */
    @Test
    public void accordingMRShouldNotSwapIfSecondObjectNotFound() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.pushCup(2);
        String[] o1 = { "cup", "1" };
        String[] o2 = { "cup", "99" };
        tower.swap(o1, o2);
        assertFalse(tower.ok());
    }

    /**
     * Should not swap if input is null.
     */
    @Test
    public void accordingMRShouldNotSwapIfInputIsNull() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.swap(null, null);
        assertFalse(tower.ok());
    }

    /**
     * Should not change the number of elements in the stack after swap.
     */
    @Test
    public void accordingMRShouldNotChangeStackSizeAfterSwap() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.pushCup(2);
        int sizeBefore = tower.stackingItems().length;
        String[] o1 = { "cup", "1" };
        String[] o2 = { "cup", "2" };
        tower.swap(o1, o2);
        int sizeAfter = tower.stackingItems().length;
        assertEquals(sizeBefore, sizeAfter);
    }

    /**
     * Should not modify IDs or types of elements after swap.
     */
    @Test
    public void accordingMRShouldNotModifyElementsAfterSwap() {
        Tower tower = new Tower(10, 20);
        tower.pushCup(1);
        tower.pushCup(3);
        String[] o1 = { "cup", "1" };
        String[] o2 = { "cup", "3" };
        tower.swap(o1, o2);
        String[][] items = tower.stackingItems();
        boolean foundCup1 = false;
        boolean foundCup3 = false;
        for (String[] item : items) {
            if (item[0].equals("cup") && item[1].equals("1"))
                foundCup1 = true;
            if (item[0].equals("cup") && item[1].equals("3"))
                foundCup3 = true;
        }
        assertTrue(foundCup1 && foundCup3);
    }

}
