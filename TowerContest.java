import java.util.ArrayList;
import java.util.List;

/**
 * TowerContest - Solver for the ICPC 2025 "Stacking Cups" problem.
 *
 * Finds an ordering of n cups (cup i has height 2i-1) such that the tower
 * reaches exactly h cm, or reports "impossible".
 *
 * Min height = 2n-1 (all nested). Max height = n² (all stacked).
 * General case: pull a subset of cups out of the main container so that
 * their combined extra contribution equals (h - minHeight).
 *
 * @author Murillo-Rubiano
 * @version 4.0
 */
public class TowerContest {

    /**
     * Solves the stacking cups problem.
     *
     * @param n number of cups
     * @param h target height in centimeters
     * @return space-separated cup heights in placement order, or "impossible"
     */
    public String solve(int n, long h) {
        long minHeight = 2L * n - 1;
        long maxHeight = (long) n * n;

        if (h < minHeight || h > maxHeight) return "impossible";
        if (h == minHeight) return allNested(n);
        if (h == maxHeight) return allStacked(n);

        // Cada contributor i aporta 2*(i-1) cm extra, por lo tanto extra debe ser par
        long extra = h - minHeight;
        if (extra % 2 != 0) return "impossible";
        long target = extra / 2;

        // Copa i tiene contribucion (i-1); available = {0, 1, ..., n-2}
        List<Integer> available = new ArrayList<>();
        for (int i = 1; i < n; i++) available.add(i - 1);

        List<Integer> subsetContribs = findSubsetSum(available, target);
        if (subsetContribs == null) return "impossible";

        // Convertir contribucion c a ID: c+1
        List<Integer> contributors = new ArrayList<>();
        for (int c : subsetContribs) contributors.add(c + 1);

        return buildOrdering(n, contributors);
    }

    /**
     * Returns the placement order when all cups are nested.
     * Cup n goes first; cups n-1 down to 1 follow inside it.
     *
     * @param n number of cups
     * @return space-separated heights in placement order
     */
    private String allNested(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n; i >= 1; i--) {
            sb.append(2 * i - 1);
            if (i > 1) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Returns the placement order when all cups are stacked.
     * Cups go smallest to largest so none nests inside the previous.
     *
     * @param n number of cups
     * @return space-separated heights in placement order
     */
    private String allStacked(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(2 * i - 1);
            if (i < n) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Finds a subset of contribution values that sums exactly to target.
     *
     * @param contribs list of available contribution values
     * @param target   required sum
     * @return list of chosen values, or null if impossible
     */
    private List<Integer> findSubsetSum(List<Integer> contribs, long target) {
        List<Integer> result = new ArrayList<>();
        if (findSubsetHelper(contribs, 0, target, result)) return result;
        return null;
    }

    /**
     * Recursive backtracking helper for subset-sum.
     * Values in contribs are used directly as contribution amounts.
     *
     * @param contribs  list of contribution values
     * @param index     current position in the list
     * @param remaining remaining sum needed
     * @param current   values chosen so far
     * @return true if a valid subset was found
     */
    private boolean findSubsetHelper(List<Integer> contribs, int index,
                                     long remaining, List<Integer> current) {
        if (remaining == 0) return true;
        if (remaining < 0 || index >= contribs.size()) return false;

        // Usar valor directamente como contribucion
        int val = contribs.get(index);
        current.add(val);
        if (findSubsetHelper(contribs, index + 1, remaining - val, current)) return true;

        current.remove(current.size() - 1);
        return findSubsetHelper(contribs, index + 1, remaining, current);
    }

    /**
     * Builds the final cup placement order given the contributor IDs.
     *
     * Order:
     *   1. Cup n first (main container).
     *   2. Non-contributors decreasing (nest inside cup n).
     *   3. Contributors decreasing (stack on top of the nested group).
     *
     * @param n            total number of cups
     * @param contributors IDs of cups selected to stack outside cup n
     * @return space-separated heights in placement order
     */
    private String buildOrdering(int n, List<Integer> contributors) {
        List<Integer> order = new ArrayList<>();
        boolean[] isContributor = new boolean[n + 1];
        for (int id : contributors) isContributor[id] = true;

        // Copa n siempre primero
        order.add(n);

        // No-contributors en orden decreciente → se anidan dentro de copa n
        for (int i = n - 1; i >= 1; i--) {
            if (!isContributor[i]) order.add(i);
        }

        // Contributors en orden decreciente → cada uno contiene al siguiente
        contributors.sort((a, b) -> b - a);
        order.addAll(contributors);

        return orderToString(order);
    }

    /**
     * Converts a list of cup IDs to a space-separated string of their heights.
     *
     * @param order list of cup IDs in placement order
     * @return space-separated heights string
     */
    private String orderToString(List<Integer> order) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < order.size(); i++) {
            sb.append(2 * order.get(i) - 1);
            if (i < order.size() - 1) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Solves and visualizes the result in the Tower simulator.
     *
     * @param n number of cups
     * @param h target height in centimeters
     */
    public void simulate(int n, int h) {
        String solution = solve(n, h);

        if (solution.equals("impossible")) {
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "No solution exists for n=" + n + ", h=" + h,
                "Impossible",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String[] heightsStr = solution.split(" ");
        List<Integer> cupIds = new ArrayList<>();
        for (String heightStr : heightsStr) {
            int height = Integer.parseInt(heightStr);
            cupIds.add((height + 1) / 2);
        }

        Tower tower = new Tower(150, h + 2);
        for (int cupId : cupIds) tower.pushCup(cupId);

        System.out.println("Solution: " + solution);
    }
}