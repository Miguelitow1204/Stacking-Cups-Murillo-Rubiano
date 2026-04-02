package tower;
import shapes.Rectangle;
import java.util.ArrayList;

/**
 * Class Tower - Main controller for the stacking cup simulator.
 * Manages the tower structure, cup and lid operations and visualization.
 * This class handles the creation and manipulation of cups and lids,
 * maintains the stack of elements, and provides visual representation
 * of the tower with a frame and height tick marks.
 * 
 * @author (Murillo-Rubiano)
 * @version (1.5)
 */
public class Tower {
    /** The width of the tower in pixels */
    private int width;

    /** The maximum height of the tower in centimeters */
    private int maxHeight;

    /** Flag indicating whether the tower is currently visible */
    private boolean isVisible;

    /** Flag indicating whether the last operation was successful */
    private boolean lastOperationOk;

    /** List of cups available in the tower (not stacked yet) */
    private ArrayList<Cup> cups;

    /** List of lids available in the tower (not stacked yet) */
    private ArrayList<Lid> lids;

    /** Stack of elements from bottom to top (can contain Cup or Lid objects) */
    private ArrayList<Object> stack;

    /** Visual frame (border) of the tower */
    private Rectangle frame;

    /** Tick marks for height measurement (one Rectangle per centimeter) */
    private ArrayList<Rectangle> ticks;

    /** X coordinate of the tower's origin point */
    private static final int ORIGIN_X = 150;

    /** Y coordinate of the tower's origin point */
    private static final int ORIGIN_Y = 280;

    /** Scale factor: number of pixels per centimeter */
    private static final int PIXELS_PER_CM = 10;

    /**
     * Creates a new tower with the given width and maximum height.
     * Initializes all collections (cups, lids, stack, ticks) and builds
     * the visual representation including the frame and height tick marks.
     * 
     * @param width     the width of the tower in pixels
     * @param maxHeight the maximum height of the tower in centimeters
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.isVisible = true;
        this.lastOperationOk = true;

        this.cups = new ArrayList<>();
        this.lids = new ArrayList<>();
        this.stack = new ArrayList<>();
        this.ticks = new ArrayList<>();

        buildFrame();
        buildTicks();

    }

    /**
     * Builds the visual frame (border) of the tower.
     * Creates a white rectangle that represents the outer boundary of the tower.
     * The frame size is based on the maximum height and width specified.
     * Positions are calculated relative to the tower's origin coordinates.
     */
    private void buildFrame() {
        frame = new Rectangle();
        frame.changeSize(maxHeight * PIXELS_PER_CM, width);
        frame.changeColor("White");
        // position the frame: moveHorizontal/moveVertical are relative to default
        // (70,15)
        frame.moveHorizontal(ORIGIN_X - 70);
        frame.moveVertical((ORIGIN_Y - maxHeight * PIXELS_PER_CM) - 15);
        frame.makeVisible();
    }

    /**
     * Builds tick marks for each centimeter of height.
     * Creates a small horizontal line for each centimeter from 1 to maxHeight.
     * Each tick mark is a red rectangle (1 pixel tall, 5 pixels wide) positioned
     * at the corresponding height level on the tower frame.
     */
    private void buildTicks() {
        for (int cm = 1; cm <= maxHeight; cm++) {
            Rectangle tick = new Rectangle();
            tick.changeSize(1, 5); // 1px tall, 5px wide
            tick.changeColor("red");
            int tickX = ORIGIN_X;
            int tickY = ORIGIN_Y - cm * PIXELS_PER_CM;
            tick.moveHorizontal(tickX - 70);
            tick.moveVertical(tickY - 15);
            tick.makeVisible();
            ticks.add(tick);
        }
    }

    /**
     * Reports an error message to the user.
     * Displays a dialog box with the error message if the tower is visible.
     * 
     * @param message the error message to display
     */
    private void reportError(String message) {
        if (isVisible) {
            javax.swing.JOptionPane.showMessageDialog(null, message);
        }
    }

    /**
     * Pushes a new cup onto the stack with the given ID.
     * The cup ID must be positive and must not already exist in the tower.
     * A cup can be nested inside the topmost cup if its ID is smaller.
     * If it does not nest, the tower height must not exceed maxHeight after adding.
     * On success, the cup is added to both the cups list and the stack,
     * and all elements are repositioned visually.
     *
     * @param i the positive integer ID of the cup to push
     */
    public void pushCup(int i) {
        lastOperationOk = false;

        if (i <= 0) { 
            reportError("Cup ID must be positive."); return; 
        }
    
        if (cupExistsInTower(i)) { 
            reportError("Cup " + i + " already exists in the tower."); 
            return; 
        }

        Cup newCup = new NormalCup(i);
    
        if (!newCup.canEnterTower(this)) { 
            reportError("Cup cannot enter the tower."); 
            return; 
        }

        stack.add(newCup);
        int realNewHeight = height();
        stack.remove(stack.size() - 1);
    
        if (realNewHeight > maxHeight) { 
            reportError("Tower would exceed maximum height."); 
            return; 
        }

        newCup.onPush(this);
        repositionStack();
    
        if (isVisible) {
            newCup.makeVisible();
        }

        lastOperationOk = true;
    }
    
    /**
     * Pushes a new cup of the specified type onto the stack with the given ID.
     * Validates the ID, checks for duplicates, verifies the cup can enter,
     * checks height constraints, and delegates insertion to the cup's onPush().
     *
     * @param i    the positive integer ID of the cup to push
     * @param type the cup type: "normal", "opener", "hierarchical", or "heavy"
     */
    public void pushCup(int i, String type) {
        lastOperationOk = false;

        if (i <= 0) { 
            reportError("Cup ID must be positive."); 
            return; 
        }
        
        if (cupExistsInTower(i)) { 
            reportError("Cup " + i + " already exists."); 
            return; 
        }

        Cup newCup;
        
        switch (type.toLowerCase()) {
            case "opener":       newCup = new OpenerCup(i);       break;
            case "hierarchical": newCup = new HierarchicalCup(i); break;
            case "heavy":        newCup = new HeavyCup(i);        break;
            default:             newCup = new NormalCup(i);        break;
        }

        if (!newCup.canEnterTower(this)) { 
            reportError("Cup cannot enter the tower."); 
            return; 
        }

        if (!(newCup instanceof HierarchicalCup)) {
            stack.add(newCup);
            int realNewHeight = height();
            stack.remove(stack.size() - 1);
            if (realNewHeight > maxHeight) { 
                reportError("Tower would exceed maximum height."); 
                return; 
            }
        }

        newCup.onPush(this);
        repositionStack();
        if (isVisible) {
            newCup.makeVisible();
        }

        lastOperationOk = true;
    }

    /**
     * Searches the stack from top to bottom and returns the first Cup found.
     * Lids and other non-Cup elements are skipped during the search.
     *
     * @return the topmost Cup in the stack, or null if no cup is present
     */
    private Cup findTopmostCupInStack() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Cup) {
                return (Cup) stack.get(i);
            }
        }
        return null;
    }

    /**
     * Removes the cup with the given ID from the stack.
     * If the cup is paired with a lid, the lid is removed first and the pairing
     * is dissolved. After removal, all remaining elements are repositioned.
     * Sets {@code lastOperationOk} to true on success, false otherwise.
     *
     * @param i the ID of the cup to remove
     */
    public void removeCup(int i) {
        int index = findCupIndexInStack(i);
        if (index == -1) {
            reportError("Cup " + i + " is not in the stack.");
            lastOperationOk = false;
            return;
        }

        Cup cup = (Cup) stack.get(index);

        // If the cup is lidded, remove the lid first
        if (cup.isLidded()) {
            Lid lid = cup.getPairedLid();
            int lidIndex = stack.indexOf(lid);
            if (lidIndex != -1) {
                lid.makeInvisible();
                stack.remove(lidIndex);
                lids.remove(lid);
                // Adjust cup index if lid was below
                if (lidIndex < index) {
                    index--;
                }
            }
            cup.unpair();
        }

        cup.makeInvisible();
        stack.remove(index);
        cups.remove(cup);
        repositionFrom(index);
        lastOperationOk = true;
    }

    /**
     * Finds the index of a cup in the stack by its ID.
     * Iterates through the stack and returns the position of the first Cup
     * whose ID matches the given value.
     *
     * @param id the cup ID to search for
     * @return the zero-based index of the cup in the stack, or -1 if not found
     */
    private int findCupIndexInStack(int id) {
        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i) instanceof Cup && ((Cup) stack.get(i)).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks whether a lid with the given ID is already registered in the tower.
     * Searches the lids list (not the stack directly) for a matching ID.
     *
     * @param id the lid ID to check
     * @return true if a lid with the given ID exists in the tower, false otherwise
     */
    private boolean lidExistsInTower(int id) {
        for (Lid l : lids) {
            if (l.getNumber() == id)
                return true;
        }
        return false;
    }

    /**
     * Checks whether a cup with the given ID is already registered in the tower.
     * Searches the cups list (not the stack directly) for a matching ID.
     *
     * @param id the cup ID to check
     * @return true if a cup with the given ID exists in the tower, false otherwise
     */
    private boolean cupExistsInTower(int id) {
        for (Cup c : cups) {
            if (c.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * Searches the stack for a cup that can be paired with a lid of the given ID.
     * A cup is a valid match if its ID equals the given ID and it is not already
     * paired with another lid.
     *
     * @param id the lid ID to find a matching cup for
     * @return the matching unlidded Cup, or null if none is found
     */
    private Cup findMatchingCupForLid(int id) {
        for (Object element : stack) {
            if (element instanceof Cup) {
                Cup cup = (Cup) element;
                // Match: same ID and cup has no lid yet
                if (cup.getId() == id && !cup.isLidded()) {
                    return cup;
                }
            }
        }
        return null;
    }

    /**
     * Pushes a new lid onto the stack with the given ID.
     * The lid ID must be positive and must not already exist in the tower.
     * Adding the lid must not cause the total tower height to exceed maxHeight.
     * <p>
     * If a cup with the same ID exists in the tower, the lid inherits that cup's
     * color and is visually paired with it (placed on top of the cup). If no
     * matching cup is found, the lid is placed as a standalone element at the
     * current top of the stack.
     * </p>
     * Sets {@code lastOperationOk} to true on success, false otherwise.
     *
     * @param i the positive integer ID of the lid to push
     */
    public void pushLid(int i) {
        // 1. Validate ID
        if (i <= 0) {
            reportError("Lid ID must be positive.");
            lastOperationOk = false;
            return;
        }

        // 2. Ensure no duplicate lid
        if (lidExistsInTower(i)) {
            reportError("Lid " + i + " already exists in the tower.");
            lastOperationOk = false;
            return;
        }

        // 3. Inherit color from matching cup if one exists (same ID)
        Cup matchingCupForColor = findCupById(i);
        String lidColor = (matchingCupForColor != null) ? matchingCupForColor.getColor() : null;
        Lid newLid = new NormalLid(i, lidColor);

        // 4. Check height constraint before adding
        int newHeight = height() + newLid.getHeight();
        if (newHeight > maxHeight) {
            reportError("Lid " + i + " does not fit. Current height: " + height() +
                    ", max: " + maxHeight);
            lastOperationOk = false;
            return;
        }

        // 5. Register and push the lid onto the stack
        lids.add(newLid);
        stack.add(newLid);

        // 6. Pair with matching cup if found; otherwise position as standalone
        Cup matchingCup = findMatchingCupForLid(i);
        if (matchingCup != null) {
            // Pair: position the lid over its cup
            matchingCup.pairWith(newLid);
            int cupIndex = stack.indexOf(matchingCup);
            int cupBottomPixels = getHeightUpTo(cupIndex) * PIXELS_PER_CM;
            matchingCup.setPosition(ORIGIN_X, ORIGIN_Y - cupBottomPixels);
        } else {
            // No matching cup: place lid independently at the top of the stack
            int lidHeightPixels = getHeightUpTo(stack.size() - 1) * PIXELS_PER_CM;
            positionElement(newLid, lidHeightPixels);
        }

        // 7. Show lid only if the simulator is currently visible
        if (isVisible) {
            newLid.makeVisible();
        }
        lastOperationOk = true;

    }
    
    /**
     * Pushes a new lid of the specified type onto the stack with the given ID.
     * Validates the ID, checks for duplicates, verifies the lid can enter,
     * checks height constraints, and delegates insertion to the lid's onPush().
     *
     * @param i    the positive integer ID of the lid to push
     * @param type the lid type: "normal", "fearful", "crazy", or "locked"
     */
    public void pushLid(int i, String type) {
        if (i <= 0) { 
            reportError("Lid ID must be positive."); 
            lastOperationOk = false; 
            return; 
        }
        
        if (lidExistsInTower(i)) { 
            reportError("Lid " + i + " already exists."); 
            lastOperationOk = false; 
            return; 
        }

        Cup matchingCupForColor = findCupById(i);
        String lidColor = (matchingCupForColor != null) ? matchingCupForColor.getColor() : null;

        Lid newLid;
        switch (type.toLowerCase()) {
            case "fearful": newLid = new FearfulLid(i, lidColor); break;
            case "crazy":   newLid = new CrazyLid(i, lidColor);   break;
            case "locked":  newLid = new LockedLid(i, lidColor);  break;
            default:        newLid = new NormalLid(i, lidColor);   break;
        }

        if (!newLid.canEnterTower(this)) { 
            reportError("Lid cannot enter the tower."); 
            lastOperationOk = false; 
            return; 
        }

        int newHeight = height() + newLid.getHeight();
        if (newHeight > maxHeight) { 
            reportError("Lid does not fit."); 
            lastOperationOk = false; 
            return; 
        }

        newLid.onPush(this);

        Cup matchingCup = findMatchingCupForLid(i);
        if (matchingCup != null) {
            matchingCup.pairWith(newLid);
            int cupIndex = stack.indexOf(matchingCup);
            int cupBottomPixels = getHeightUpTo(cupIndex) * PIXELS_PER_CM;
            matchingCup.setPosition(ORIGIN_X, ORIGIN_Y - cupBottomPixels);
        } else {
            int lidHeightPixels = getHeightUpTo(stack.size() - 1) * PIXELS_PER_CM;
            positionElement(newLid, lidHeightPixels);
        }

        if (isVisible) newLid.makeVisible();
        lastOperationOk = true;
    }

    /**
     * Calculates the accumulated height of all non-nested elements in the stack
     * up to (but not including) the element at the given index.
     * Nested cups (those nested inside other cups) are skipped in the calculation.
     * Each lid contributes 1 centimeter to the height.
     *
     * @param index the zero-based position in the stack (height is calculated up to
     *              this index)
     * @return the total height in centimeters of all elements before the given
     *         index
     */
    private int getHeightUpTo(int index) {
        int total = 0;
        for (int k = 0; k < index; k++) {
            // Skip cups that are nested inside the cup below them
            if (isNestedInBelow(k))
                continue;
            Object element = stack.get(k);
            if (element instanceof Cup) {
                total += ((Cup) element).getHeight();
            } else if (element instanceof Lid) {
                total += 1;
            }
        }
        return total;
    }

    /**
     * Removes the topmost (most recently pushed) cup from the stack.
     * If the cup is paired with a lid, the lid must be directly above the cup
     * and is automatically removed along with it before unpair happens.
     * All remaining elements are repositioned after removal.
     * Sets {@code lastOperationOk} to true on success, false otherwise.
     */
    public void popCup() {
        // 1. Check if stack is not empty
        if (stack.isEmpty()) {
            reportError("Stack is empty.");
            lastOperationOk = false;
            return;
        }

        // 2. Find the topmost cup (skip any lids on top)
        int topCupIndex = -1;
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Cup) {
                topCupIndex = i;
                break;
            }
        }

        // 3. Verify a cup was found
        if (topCupIndex == -1) {
            reportError("No cups in the stack.");
            lastOperationOk = false;
            return;
        }

        Cup topCup = (Cup) stack.get(topCupIndex);
        
        if(topCup.isLidded() && topCup.getPairedLid() instanceof LockedLid){
            reportError("Cannot remove cup: it is locked by a Locked lid, Remove the lid first.");
            lastOperationOk = false;
            return;
        }
        
        if (!topCup.canBeRemoved(this)) {
            reportError("Cup cannot be removed.");
            lastOperationOk = false;
            return;
        }

        // 4. If there's a lid directly above, check if it's paired with this cup
        if (topCupIndex < stack.size() - 1) {
            Object above = stack.get(topCupIndex + 1);
            if (above instanceof Lid && topCup.isLidded() && topCup.getPairedLid() == above) {
                // Remove the paired lid first
                Lid lid = (Lid) above;
                lid.makeInvisible();
                stack.remove(topCupIndex + 1);
                lids.remove(lid);
                topCup.unpair();
            } else {
                // Cups cannot be removed if there are unpaired elements above
                reportError("Cannot pop cup - there are elements above it.");
                lastOperationOk = false;
                return;
            }
        }

        // 5. Remove the cup and reposition remaining elements
        topCup.makeInvisible();
        stack.remove(topCupIndex);
        cups.remove(topCup);
        repositionFrom(topCupIndex);
        lastOperationOk = true;
    }

    /**
     * Removes the topmost (most recently pushed) lid from the stack.
     * The lid must be at the very top of the stack with no elements above it.
     * If the lid is paired with a cup, the pairing is dissolved.
     * All remaining elements are repositioned after removal.
     * Sets {@code lastOperationOk} to true on success, false otherwise.
     */
    public void popLid() {
        // 1. Check if stack is not empty
        if (stack.isEmpty()) {
            reportError("Stack is empty.");
            lastOperationOk = false;
            return;
        }

        // 2. Find the topmost lid
        int topLidIndex = -1;
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Lid) {
                topLidIndex = i;
                break;
            }
        }

        // 3. Verify a lid was found
        if (topLidIndex == -1) {
            reportError("No lids in the stack.");
            lastOperationOk = false;
            return;
        }

        // 4. Ensure the lid is truly at the top (no elements above it)
        if (topLidIndex < stack.size() - 1) {
            reportError("Cannot pop lid - there are elements above it.");
            lastOperationOk = false;
            return;
        }

        Lid topLid = (Lid) stack.get(topLidIndex);
        
        if(!topLid.canBeRemoved(this)){
            reportError("This lid cannot be removed");
            lastOperationOk = false;
            return;
        }

        // 5. If paired with a cup, dissolve the pairing
        if (topLid.isPaired()) {
            topLid.unpair();
        }

        // 6. Remove the lid and reposition remaining elements
        topLid.makeInvisible();
        stack.remove(topLidIndex);
        lids.remove(topLid);
        repositionFrom(topLidIndex);
        lastOperationOk = true;
    }

    /**
     * Positions a single element (cup or lid) at the specified height in pixels.
     * For cups, sets position and makes visible if the simulator is visible.
     * For lids, checks if they are paired; paired lids are not positioned here
     * (their position is already handled by their paired cup). Unpaired lids
     * are positioned independently at the given height.
     *
     * @param element      the Cup or Lid object to position
     * @param heightPixels the vertical position in pixels (measured from tower
     *                     origin)
     */
    private void positionElement(Object element, int heightPixels) {

        int x = ORIGIN_X;
        int y = ORIGIN_Y - heightPixels;

        if (element instanceof Cup) {

            Cup cup = (Cup) element;
            cup.setPosition(x, y);
            if (isVisible) {
                cup.makeVisible();
            }

        } else if (element instanceof Lid) {

            Lid lid = (Lid) element;
            // If paired, the cup has already positioned it; skip here
            if (lid.isPaired()) {
                return;
            }
            // Unpaired lid: position independently
            lid.setPositionAlone(x, y);
            if (isVisible) {
                lid.makeVisible();
            }
        }
    }

    /**
     * Repositions all elements in the stack starting from the given index.
     * This method is called after removing an element to ensure all remaining
     * elements maintain correct visual positions and heights are recalculated.
     *
     * @param fromIndex the starting position for repositioning (typically the index
     *                  where an element was removed)
     */
    private void repositionFrom(int fromIndex) {
        // Reposition the entire stack to maintain consistency
        repositionStack();
    }

    /**
     * Calculates the total height of the stack.
     * 
     * This method goes through all the elements in the stack and determines
     * how much height each one contributes. Cups can either stack on top
     * of others or be placed inside another cup (nested), which changes
     * how their height is counted. Lids always add a fixed height.
     * 
     * @return the total height of the stack in centimeters
     */
    public int height() {
        if (stack.isEmpty()) return 0;

        int[] topCm = new int[stack.size()];

        for (int i = 0; i < stack.size(); i++) {
            Object element = stack.get(i);

            if (element instanceof Cup) {
                Cup cup = (Cup) element;
                // Buscar el contenedor directo: el más pequeño que pueda contenerla
                int containerIdx = findSmallestContainer(i);

                if (containerIdx == -1) {
                    //No está anidada: se apila sobre el tope anterior
                    int base = (i == 0) ? 0 : topCm[i - 1];
                    topCm[i] = base + cup.getHeight();
                } else {
                    //Está anidada: su base es 1cm (fondo del contenedor) más
                    //el tope de cualquier otra copa ya apilada dentro del mismo contenedor
                    int base = topCm[containerIdx] - ((Cup) stack.get(containerIdx)).getHeight() + 1;
                    for (int j = containerIdx + 1; j < i; j++) {
                        if (stack.get(j) instanceof Cup && findSmallestContainer(j) == containerIdx) {
                            base = Math.max(base, topCm[j]);
                        }
                    }
                    topCm[i] = base + cup.getHeight();
                }

            } else if (element instanceof Lid) {
                int prev = (i == 0) ? 0 : topCm[i - 1];
                topCm[i] = prev + 1;
            }
        }

        int max = 0;
        for (int t : topCm) max = Math.max(max, t);
        return max;
    }

    /**
     * Determines whether the element at the given index is a cup nested inside
     * any larger cup below it.
     * A cup is considered nested when it can fit inside any larger cup
     * that appears earlier in the stack.
     * 
     * @param index the index of the element to evaluate
     * @return true if the current cup is nested inside the cup below, false
     *         otherwise
     */
    private boolean isNestedInBelow(int index) {
        if (index == 0)
            return false;
        Object below = stack.get(index - 1);
        Object current = stack.get(index);
        if (below instanceof Cup && current instanceof Cup) {
            return ((Cup) current).getId() < ((Cup) below).getId();
        }
        return false;
    }

    /**
     * Recalculates and updates the visual position of every element in the stack.
     * 
     * UPDATED LOGIC for ICPC problem:
     * - A cup nests inside the SMALLEST cup below it that can contain it
     * - If multiple cups nest in the same container, they stack vertically
     * - Height is measured from the bottom to the highest point
     */
    private void repositionStack() {
        if (stack.isEmpty()) return;

        int[] bottomPx = new int[stack.size()];
        int[] topPx    = new int[stack.size()];

        for (int i = 0; i < stack.size(); i++) {
            Object element = stack.get(i);

            if (element instanceof Cup) {
                Cup cup = (Cup) element;
                int cupHeightPx  = cup.getHeight() * PIXELS_PER_CM;
                int containerIdx = findSmallestContainer(i);

                if (containerIdx == -1) {
                    // Not nested: place on top of the previous element
                    int base    = (i == 0) ? 0 : topPx[i - 1];
                    bottomPx[i] = base;
                    topPx[i]    = base + cupHeightPx;
                } else {
                    // Nested: start 1 cm (PIXELS_PER_CM) above container floor
                    int base = bottomPx[containerIdx] + PIXELS_PER_CM;

                    // Raise base if sibling cups already occupy space inside
                    // the same direct container
                    for (int j = containerIdx + 1; j < i; j++) {
                        if (stack.get(j) instanceof Cup
                                && findSmallestContainer(j) == containerIdx) {
                            base = Math.max(base, topPx[j]);
                        }
                    }
                    bottomPx[i] = base;
                    topPx[i]    = base + cupHeightPx;
                }

                positionElement(element, bottomPx[i]);

            } else if (element instanceof Lid) {
                // Lids always sit on top of the previous element
                int prev    = (i == 0) ? 0 : topPx[i - 1];
                bottomPx[i] = prev;
                topPx[i]    = prev + PIXELS_PER_CM;
                positionElement(element, bottomPx[i]);
            }
        }
    }

    /**
     * Finds the direct container for the cup at the given stack index.
     * Walks backwards and returns the first cup with a larger ID.
     * If the first cup found has a smaller or equal ID, there is no container.
     *
     * @param cupIndex index of the cup to find a container for
     * @return index of the direct container, or -1 if none
     */
    private int findSmallestContainer(int cupIndex) {
        if (!(stack.get(cupIndex) instanceof Cup)) return -1;

        Cup currentCup = (Cup) stack.get(cupIndex);

        for (int i = cupIndex - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Cup) {
                Cup candidate = (Cup) stack.get(i);
                if (candidate.getId() > currentCup.getId()) {
                    // Primera copa hacia atrás con ID mayor = contenedor directo
                    return i;
                } else {
                    // Primera copa hacia atrás con ID menor o igual = no hay contenedor
                    return -1;
                }
            }
        }
        return -1;
    }
    
    /**
     * Removes the lid with the given ID from the stack.
     * If the lid is currently paired with a cup, the pairing is dissolved first.
     * After removal, the remaining elements are repositioned.
     * Sets {@code lastOperationOk} to true on success, false otherwise.
     *
     * @param i the lid ID to remove
     */
    public void removeLid(int i) {
        // 1. Find the lid in the current stack
        int index = findLidIndexInStack(i);
        if (index == -1) {
            reportError("Lid " + i + " is not in the stack.");
            lastOperationOk = false;
            return;
        }

        Lid lid = (Lid) stack.get(index);

        // 2. Dissolve pairing if this lid is attached to a cup
        if (lid.isPaired())
            lid.unpair();

        // 3. Remove the lid and update the remaining layout
        lid.makeInvisible();
        stack.remove(index);
        lids.remove(lid);
        repositionFrom(index);
        lastOperationOk = true;
    }

    /**
     * Finds the index of a lid in the stack by its ID.
     * Iterates through the stack and returns the position of the first Lid whose
     * number matches the given value.
     *
     * @param id the lid ID to search for
     * @return the zero-based index of the lid in the stack, or -1 if not found
     */
    private int findLidIndexInStack(int id) {
        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i) instanceof Lid && ((Lid) stack.get(i)).getNumber() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Reorganizes the stack from largest to smallest (largest at bottom, smallest
     * at top).
     * Only includes elements that fit within maxHeight.
     * When a cup and its lid share the same id, the lid is placed on top of the
     * cup.
     */
    public void orderTower() {
        clearStack();
        ArrayList<Cup> sorted = new ArrayList<>(cups);
        sorted.sort((a, b) -> b.getId() - a.getId());
        int accumulated = 0;
        for (Cup cup : sorted) {
            int needed = accumulated + cup.getHeight();
            Lid matchingLid = findLidById(cup.getId());
            if (matchingLid != null)
                needed += matchingLid.getHeight();
            if (needed <= maxHeight) {
                stack.add(cup);
                accumulated += cup.getHeight();
                if (matchingLid != null) {
                    if (!cup.isLidded())
                        cup.pairWith(matchingLid);
                    stack.add(matchingLid);
                    accumulated += matchingLid.getHeight();
                }

            }
            for (Lid lid : lids) {
                if (!stack.contains(lid) && height() + lid.getHeight() <= maxHeight) {
                    stack.add(lid);
                }
            }
            repositionStack();
            lastOperationOk = true;
        }
    }

    /**
     * Reorganizes the stack in reverse order (smallest at bottom, largest at top).
     * Only includes elements that fit within maxHeight.
     */
    public void reverseTower() {
        clearStack();
        ArrayList<Cup> sorted = new ArrayList<>(cups);
        sorted.sort((a, b) -> a.getId() - b.getId());
        int accumulated = 0;
        for (Cup cup : sorted) {
            int needed = accumulated + cup.getHeight();
            Lid matchingLid = findLidById(cup.getId());
            if (matchingLid != null)
                needed += matchingLid.getHeight();
            if (needed <= maxHeight) {
                stack.add(cup);
                accumulated += cup.getHeight();
                if (matchingLid != null) {
                    if (!cup.isLidded())
                        cup.pairWith(matchingLid);
                    stack.add(matchingLid);
                    accumulated += matchingLid.getHeight();
                }
            }
        }
        for (Lid lid : lids) {
            if (!stack.contains(lid) && height() + lid.getHeight() <= maxHeight) {
                stack.add(lid);
            }
        }
        repositionStack();
        lastOperationOk = true;
    }

    /**
     * Returns the ids of all cups currently paired with their lid, sorted
     * ascending.
     * 
     * @return int array of lidded cup ids
     */
    public int[] lidedCups() {
        ArrayList<Integer> result = new ArrayList<>();
        for (Cup cup : cups) {
            if (cup.isLidded())
                result.add(cup.getId());
        }
        result.sort(Integer::compareTo);
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Returns the type and number of each element in the stack from bottom to top.
     * Example: {{"cup","4"},{"lid","4"}}
     * 
     * @return 2D String array with [type, id] for each element
     */
    public String[][] stackingItems() {
        String[][] result = new String[stack.size()][2];
        for (int i = 0; i < stack.size(); i++) {
            Object element = stack.get(i);
            if (element instanceof Cup) {
                result[i][0] = "cup";
                result[i][1] = String.valueOf(((Cup) element).getId());
            } else if (element instanceof Lid) {
                result[i][0] = "lid";
                result[i][1] = String.valueOf(((Lid) element).getNumber());
            }
        }
        return result;
    }

    /**
     * Makes the tower and all its elements visible.
     */
    public void makeVisible() {
        isVisible = true;
        frame.makeVisible();
        for (Rectangle tick : ticks)
            tick.makeVisible();
        for (Object element : stack) {
            if (element instanceof Cup)
                ((Cup) element).makeVisible();
            else if (element instanceof Lid)
                ((Lid) element).makeVisible();
        }
    }

    /**
     * Makes the tower and all its elements invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        frame.makeInvisible();
        for (Rectangle tick : ticks)
            tick.makeInvisible();
        for (Object element : stack) {
            if (element instanceof Cup)
                ((Cup) element).makeInvisible();
            else if (element instanceof Lid)
                ((Lid) element).makeInvisible();
        }
    }

    /**
     * Terminates the simulator.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Returns whether the last operation was successful.
     * 
     * @return true if the last operation completed without errors
     */
    public boolean ok() {
        return lastOperationOk;
    }

    /**
     * Clears the current visual stack and resets cup-lid relationships.
     * 
     * Every stacked element is made invisible and removed from {@code stack},
     * but the master collections {@code cups} and {@code lids} are kept so the
     * tower can be rebuilt later (for example, in ordering operations).
     * 
     */
    private void clearStack() {
        for (Object element : stack) {
            if (element instanceof Cup)
                ((Cup) element).makeInvisible();
            else if (element instanceof Lid)
                ((Lid) element).makeInvisible();
        }
        stack.clear();
        for (Cup cup : cups) {
            // Ensure no stale references to lids after clearing the visual stack.
            if (cup.isLidded())
                cup.unpair();
        }
    }

    /**
     * Finds and returns a cup by its identifier from the registered cups list.
     *
     * @param id the cup ID to search for
     * @return the matching {@code Cup}, or {@code null} if no cup has that ID
     */
    private Cup findCupById(int id) {
        for (Cup cup : cups) {
            if (cup.getId() == id)
                return cup;
        }
        return null;
    }

    /**
     * Finds a lid in the lids list by its id number.
     * 
     * @param id the lid id to search for
     * @return the Lid, or null if not found
     */
    private Lid findLidById(int id) {
        for (Lid lid : lids) {
            if (lid.getNumber() == id)
                return lid;
        }
        return null;
    }

    // Ciclo #2
    // Requisitos Funcionales
    // 10. Create Extensión
    /**
     * Creates a new tower with the specified number of cups.
     * This constructor automatically generates cups with IDs from 1 to the given
     * number.
     * The tower is created with default width and calculates the required maximum
     * height
     * to fit all cups when they are stacked in order (largest at bottom)
     * No lids are created in this constructor
     * 
     * @param cups
     */
    public Tower(int cups) {
        //When cups are nested, only the largest one determines the height
        //Largest cup has height 2*cups - 1
        int requiredHeight = 2 * cups - 1;

        // Usar ancho por defecto
        this.width = 10;
        this.maxHeight = requiredHeight;
        this.isVisible = true;
        this.lastOperationOk = true;

        this.cups = new ArrayList<>();
        this.lids = new ArrayList<>();
        this.stack = new ArrayList<>();
        this.ticks = new ArrayList<>();

        buildFrame();
        buildTicks();

        // Crear cups de 1 a cups y añadirlos a stack
        // Añadir en orden de cups a 1
        for (int i = cups; i >= 1; i--) {
            Cup newCup = new NormalCup(i);
            this.cups.add(newCup);
            this.stack.add(newCup);
        }

        // Posicionar todas las cups
        repositionStack();

        // Hacerlas visibles
        if (isVisible) {
            for (Cup cup : this.cups) {
                cup.makeVisible();
            }
        }
        lastOperationOk = true;
    }

    // Requisitos 11 y 12. Reorganize
    /**
     * Automatically pairs all cups with their matching lid in the tower.
     * For each cup in tower, if a lid with the same number exists,
     * they are paired together. The lid is visually positioned on top of its cup.
     * Only cups and lids that are not already paired will be matched
     * Sets lastOperationOk to true when complete.
     */
    public void cover() {
        // Iterar sobre todas las cups en tower
        for (Cup cup : cups) {
            // Skip si ya está lidded
            if (cup.isLidded()) {
                continue;
            }
            // Buscar una lid que coincida con el mismo numero
            Lid matchingLid = findLidById(cup.getId());
            // Si existe una lid compatible y no esta emparejada
            if (matchingLid != null && !matchingLid.isPaired()) {
                cup.pairWith(matchingLid);
            }
        }

        repositionStack();
        lastOperationOk = true;
    }

    /**
     * Swaps the positions of two objects in the stack
     * Objects are indentified by their type and number
     * Both objects must exist in the stack
     * After swap, all elements are repositioned visually
     * 
     * @param o1 Array with [type, number] of first object
     * @param o2 Array with [type, number] of second object
     */
    public void swap(String[] o1, String[] o2) {
        lastOperationOk = false;
        // Validar input
        if (o1 == null || o1.length != 2 || o2 == null || o2.length != 2) {
            reportError("Invalid object format");
            return;
        }
        // Encontrar los indices de ambos objetos
        int index1 = findObjectInStack(o1[0], o1[1]); // Este toca hacerlo private
        int index2 = findObjectInStack(o2[0], o2[1]);

        // Validar que los objetos existan
        if (index1 == -1) {
            reportError("Object" + o1[0] + "#" + o1[1] + "not found");
            return;
        }
        if (index2 == -1) {
            reportError("Object" + o2[0] + "#" + o2[1] + "not found");
            return;
        }

        // Cambiar los objetos
        Object temp = stack.get(index1);
        stack.set(index1, stack.get(index2));
        stack.set(index2, temp);

        // Reposicionar
        repositionStack();
        lastOperationOk = true;
    }

    /**
     * Finds the index of an object in the stack by its type and number.
     * 
     * @param type      "cup" or "lid"
     * @param numberStr the number as a String
     * @return the index in the stack, or -1 if not found
     */
    private int findObjectInStack(String type, String numberStr) {
        // Convert string to int manually
        int number = 0;
        for (int i = 0; i < numberStr.length(); i++) {
            char c = numberStr.charAt(i);
            if (c >= '0' && c <= '9') {
                number = number * 10 + (c - '0');
            } else {
                // Invalid character, return -1
                return -1;
            }
        }

        // Search in stack
        for (int i = 0; i < stack.size(); i++) {
            Object element = stack.get(i);

            // Check if it's a cup with matching number
            if (type.equals("cup") && element instanceof Cup) {
                Cup cup = (Cup) element;
                if (cup.getId() == number) {
                    return i;
                }
            }

            // Check if it's a lid with matching number
            if (type.equals("lid") && element instanceof Lid) {
                Lid lid = (Lid) element;
                if (lid.getNumber() == number) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Suggests a swap of two elements that would reduce the tower height.
     * Tries all possible pairs and returns the first one that decreases height.
     * If the simulator is visible, asks the user for confirmation before applying
     * the swap. If invisible, applies the swap directly without prompting.
     * Elements are identified by their type and number.
     * Example return value: {{"cup","4"},{"lid","4"}}
     *
     * @return a 2-element String[][] with the suggested swap,
     *         or null if no height-reducing swap exists
     */
    public String[][] swapToReduce() {
        int currentHeight = height();
        String[][] suggestion = null;

        // First-improvement strategy: stop at the first swap that lowers height.
        for (int i = 0; i < stack.size() - 1 && suggestion == null; i++) {
            for (int j = i + 1; j < stack.size() && suggestion == null; j++) {
                if (heightAfterSwap(i, j) < currentHeight) {
                    suggestion = new String[][] { describeElement(i), describeElement(j) };
                }
            }
        }

        if (suggestion == null) {
            return null;
        }

        if (!isVisible) {
            swap(suggestion[0], suggestion[1]);
        } else {
            String message = "Suggested swap to reduce height:\n"
                    + suggestion[0][0] + " " + suggestion[0][1]
                    + "  <->  "
                    + suggestion[1][0] + " " + suggestion[1][1]
                    + "\nDo you want to apply this swap?";
            int choice = javax.swing.JOptionPane.showConfirmDialog(
                    null, message, "Swap to Reduce", javax.swing.JOptionPane.YES_NO_OPTION);
            if (choice == javax.swing.JOptionPane.YES_OPTION) {
                swap(suggestion[0], suggestion[1]);
            }
        }

        return suggestion;
    }

    /**
     * Calculates the tower height if the elements at positions i and j were
     * swapped.
     * Performs the swap in memory, measures height, then restores the original
     * order.
     * The visual representation is not affected.
     *
     * @param i index of the first element
     * @param j index of the second element
     * @return the hypothetical height after the swap
     */
    private int heightAfterSwap(int i, int j) {
        Object temp = stack.get(i);
        stack.set(i, stack.get(j));
        stack.set(j, temp);
        int h = height();
        temp = stack.get(i);
        stack.set(i, stack.get(j));
        stack.set(j, temp);
        return h;
    }

    /**
     * Returns a String array describing the element at the given stack index.
     * Format: {"cup", "4"} or {"lid", "4"}
     *
     * @param index the position in the stack
     * @return String[] with type and number of the element
     */
    private String[] describeElement(int index) {
        Object el = stack.get(index);
        if (el instanceof Cup) {
            return new String[] { "cup", String.valueOf(((Cup) el).getId()) };
        }
        return new String[] { "lid", String.valueOf(((Lid) el).getNumber()) };
    }
    
    /**
     * Adds a cup to both the cups list and the stack.
     * Called by cup subclasses from their onPush() method.
     *
     * @param cup the Cup to add
     */
    public void addCup(Cup cup) {
        cups.add(cup);
        stack.add(cup);
    }
    
    /**
     * Checks whether the given cup is at the top of the stack.
     *
     * @param cup the Cup to check
     * @return true if the cup is the topmost element in the stack
     */
    public boolean isTop(Cup cup) {
        return stack.get(stack.size() - 1) == cup;
    }
    
    /**
     * Checks whether the given cup is at the bottom of the stack.
     *
     * @param cup the Cup to check
     * @return true if the cup is the bottommost element in the stack
     */
    public boolean isAtBottom(Cup cup) {
        return stack.get(0) == cup;
    }
    
    /**
     * Removes all lids currently in the stack, making them invisible.
     * Called by OpenerCup when it enters the tower.
     */
    public void removeAllLids() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Lid) {
                Lid lid = (Lid) stack.get(i);
                lid.makeInvisible();
                lids.remove(lid);
                stack.remove(i);
            }
        }
    }
    
    /**
     * Inserts a hierarchical cup into the stack before any cup with a smaller ID.
     * If no smaller cup is found, the cup is placed at the top.
     * Adds the cup to both the cups list and the stack.
     *
     * @param cup the HierarchicalCup to insert
     */
    public void insertHierarchicalCup(Cup cup) {
        int pos = stack.size();

        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i) instanceof Cup) {
                Cup c = (Cup) stack.get(i);
                if (c.getId() < cup.getId()) {
                    pos = i;
                    break;
                }
            }
        }

        stack.add(pos, cup);
        cups.add(cup);
    }
    
    /**
     * Adds a lid to both the lids list and the stack.
     * Called by lid subclasses from their onPush() method.
     *
     * @param lid the Lid to add
     */
    public void addLid(Lid lid){
        lids.add(lid);
        stack.add(lid);
    }
    
    /**
     * Checks whether a cup with the given ID is currently in the stack.
     * Used by FearfulLid to verify its companion cup is present.
     *
     * @param id the cup ID to search for
     * @return true if a cup with the given ID exists in the stack
     */
    public boolean cupExistsInStack(int id){
        for(Object element : stack){
            if(element instanceof Cup && ((Cup) element).getId() == id){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks whether the given lid is directly on top of its companion cup.
     * Used by FearfulLid to prevent removal while covering its cup.
     *
     * @param lid the Lid to check
     * @return true if the lid is directly above its companion cup in the stack
     */
    public boolean isLiddingItsCup(Lid lid){
        int lidIdx = stack.indexOf(lid);
        if(lidIdx <= 0){
            return false;
        }
        
        Object below = stack.get(lidIdx - 1);
        return below instanceof Cup && ((Cup) below).getId()== lid.getNumber();
    }
    
    /**
     * Inserts a lid directly below its companion cup in the stack.
     * If the companion cup is not found, the lid is placed at the top.
     * Called by CrazyLid from its onPush() method.
     *
     * @param lid the CrazyLid to insert below its companion cup
     */
    public void addLidBelowCup(Lid lid){
        lids.add(lid);
        int cupIdx = -1;
        
        for(int i = 0; i < stack.size(); i++){
            if (stack.get(i) instanceof Cup && ((Cup) stack.get(i)).getId() == lid.getNumber()){
                cupIdx = i;
                break;
            }
        }
        
        if(cupIdx != -1){
            stack.add(cupIdx, lid); //inserta justo debajo de la cup
        } else {
            stack.add(lid); //si no encuentra su cup, va arriba normal
        }
        repositionStack();
    }
    
    /**
     * Checks whether the given cup is blocked by a LockedLid directly above it.
     * Used by popCup() to prevent removal of a locked cup.
     *
     * @param cup the Cup to check
     * @return true if the cup has a LockedLid directly above it
     */
    public boolean CupIsLocked(Cup cup){
        int cupIdx = stack.indexOf(cup);
        
        if(cupIdx == -1 || cupIdx >= stack.size() - 1){
            return false;
        }
        
        Object above = stack.get(cupIdx + 1);
        return above instanceof LockedLid;
    }
}
