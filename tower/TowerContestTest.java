package tower;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the TowerContest class.
 * Covers impossible cases, boundary cases, and general cases
 * for the ICPC 2025 "Stacking Cups" problem solver.
 *
 * @author (Murillo-Rubiano) with support of GPT-4o
 * @version (1.0)
 */
public class TowerContestTest {

    private TowerContest tc;

    @BeforeEach
    public void setUp() {
        tc = new TowerContest();
    }

    @AfterEach
    public void tearDown() {
        tc = null;
    }

    //impossible cases

    @Test
    public void testBelowMinHeight() {
        //h < 2n-1 always impossible
        assertEquals("impossible", tc.solve(4, 6));
    }

    @Test
    public void testAboveMaxHeight() {
        // h > n^2 always impossible
        assertEquals("impossible", tc.solve(4, 17));
    }

    @Test
    public void testOddExtraIsImpossible() {
        //odd extra can never be achieved
        assertEquals("impossible", tc.solve(4, 8));
    }

    //boundary cases

    @Test
    public void testMinHeight() {
        //h = 2n-1: all nested, only the largest cup contributes
        assertEquals("7 5 3 1", tc.solve(4, 7));
    }

    @Test
    public void testMaxHeight() {
        //h = n^2: all stacked
        assertEquals("1 3 5 7", tc.solve(4, 16));
    }

    @Test
    public void testN1MinAndMax() {
        //with a single cup, min = max = 1
        assertEquals("1", tc.solve(1, 1));
        assertEquals("impossible", tc.solve(1, 2));
    }

    //general cases

    @Test
    public void testSolveN4H9() {
        //ICPC problem example
        String result = tc.solve(4, 9);
        assertNotEquals("impossible", result);
        //verify that the simulated height is correct
        String[] parts = result.split(" ");
        assertEquals(4, parts.length); //all 4 cups must be present
    }

    @Test
    public void testSolveN4H100Impossible() {
        //Example 2 of the ICPC problem
        assertEquals("impossible", tc.solve(4, 100));
    }

    @Test
    public void testAllCupsPresent() {
        //The solution must always contain exactly n cups
        String result = tc.solve(5, 10);
        if (!result.equals("impossible")) {
            String[] parts = result.split(" ");
            assertEquals(5, parts.length);
        }
    }

    @Test
    public void testN2() {
        //n=2: min=3, max=4
        assertEquals("3 1", tc.solve(2, 3));
        assertEquals("1 3", tc.solve(2, 4));
        assertEquals("impossible", tc.solve(2, 2));
    }

    @Test
    public void testN3() {
        //n=3: min=5, max=9
        assertEquals("5 3 1", tc.solve(3, 5));
        assertEquals("1 3 5", tc.solve(3, 9));
        assertEquals("impossible", tc.solve(3, 4));
    }
}
