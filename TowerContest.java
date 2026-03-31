import java.util.ArrayList;
import java.util.List;

/**
 * TowerContest - Solver for the ICPC Stacking Cups problem.
 * 
 * Uses a greedy approach with subset sum to find exact height.
 * 
 * @author (Murillo-Rubiano)
 * @version (3.1)
 */
public class TowerContest{
    
    /**
     * Solves the ICPC Stacking Cups problem.
     * 
     * @param n number of cups (1 to n)
     * @param h target height in centimeters
     * @return String with cup heights in order, or "impossible"
     */
    public String solve(int n, long h){
        //Minimum: all nested (only largest cup)
        long minHeight = 2L * n - 1;
        
        //Maximum: all stacked (sum 1+3+5+...+(2n-1) = n²)
        long maxHeight = (long) n * n;
        
        //Quick validation
        if(h < minHeight || h > maxHeight){
            return "impossible";
        }
        
        //Special case: h = minHeight (all nested)
        if(h == minHeight){
            return allNested(n);
        }
        
        //Special case: h = maxHeight (all stacked)
        if(h == maxHeight){
            return allStacked(n);
        }
        
        //General case: need to find subset that sums to (h - minHeight)
        long extra = h - minHeight;
        
        //Available cups for contribution (all except largest)
        List<Integer> available = new ArrayList<>();
        for(int i = 1; i < n; i++){
            available.add(i);
        }
        
        //Find subset using dynamic programming
        List<Integer> subset = findSubsetSum(available, extra);
        
        if(subset == null){
            return "impossible";
        }
        
        //Build final ordering
        return buildOrdering(n, subset);
    }
    
    /**
     * Returns ordering for all cups nested (height = largest cup only).
     */
    private String allNested(int n){
        StringBuilder sb = new StringBuilder();
        for(int i = n; i >= 1; i--){
            sb.append(2 * i - 1);
            if(i > 1) sb.append(" ");
        }
        return sb.toString();
    }
    
    /**
     * Returns ordering for all cups stacked (height = sum of all).
     */
    private String allStacked(int n){
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= n; i++){
            sb.append(2 * i - 1);
            if(i < n) sb.append(" ");
        }
        return sb.toString();
    }
    
    /**
     * Finds a subset of cups whose heights sum to exactly target.
     * Uses simple backtracking (efficient for small n).
     * 
     * @param cups list of cup IDs available
     * @param target target sum
     * @return list of cup IDs that sum to target, or null if impossible
     */
    private List<Integer> findSubsetSum(List<Integer> cups, long target){
        List<Integer> result = new ArrayList<>();
        if(findSubsetHelper(cups, 0, target, result)){
            return result;
        }
        return null;
    }
    
    private boolean findSubsetHelper(List<Integer> cups, int index, long remaining, List<Integer> current){
        //Found exact sum
        if(remaining == 0){
            return true;
        }
        
        //Exceeded or ran out of cups
        if(remaining < 0 || index >= cups.size()){
            return false;
        }
        
        //Try including current cup
        int cupId = cups.get(index);
        int cupHeight = 2 * cupId - 1;
        current.add(cupId);
        
        if(findSubsetHelper(cups, index + 1, remaining - cupHeight, current)){
            return true;
        }
        
        //Backtrack: try NOT including current cup
        current.remove(current.size() - 1);
        
        if(findSubsetHelper(cups, index + 1, remaining, current)){
            return true;
        }
        
        return false;
    }
    
    /**
     * Builds the final ordering given which cups contribute to height.
     * 
     * Strategy:
     * 1. Place largest cup first (Cup n)
     * 2. Nest all non-contributing cups inside it (decreasing order)
     * 3. Stack contributing cups on top (increasing order for max nesting)
     * 
     * @param n total cups
     * @param contributors cups that add to height
     * @return ordering string
     */
    private String buildOrdering(int n, List<Integer> contributors){
        List<Integer> order = new ArrayList<>();
        boolean[] isContributor = new boolean[n + 1];
        
        for(int id : contributors){
            isContributor[id] = true;
        }
        
        //Step 1: Add largest cup (always first)
        order.add(n);
        
        //Step 2: Add non-contributors in decreasing order (they nest)
        for(int i = n - 1; i >= 1; i--){
            if(!isContributor[i]) {
                order.add(i);
            }
        }
        
        //Step 3: Add contributors in increasing order (they stack)
        contributors.sort(Integer::compareTo);
        order.addAll(contributors);
        
        return orderToString(order);
    }
    
    /**
     * Converts ordering to output format.
     */
    private String orderToString(List<Integer> order){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < order.size(); i++){
            int height = 2 * order.get(i) - 1;
            sb.append(height);
            if(i < order.size() - 1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    
    /**
     * Simulates the solution visually.
     */
    public void simulate(int n, int h){
        String solution = solve(n, h);
        
        if(solution.equals("impossible")){
            javax.swing.JOptionPane.showMessageDialog(
                null, 
                "No solution exists for n=" + n + ", h=" + h,
                "Impossible",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        //Parse and visualize
        String[] heightsStr = solution.split(" ");
        List<Integer> cupIds = new ArrayList<>();
        
        for(String heightStr : heightsStr){
            int height = Integer.parseInt(heightStr);
            int cupId = (height + 1) / 2;
            cupIds.add(cupId);
        }
        
        Tower tower = new Tower(150, h + 10);
        
        for(int cupId : cupIds){
            tower.pushCup(cupId);
        }
        
        System.out.println("Solution: " + solution);
    }
}