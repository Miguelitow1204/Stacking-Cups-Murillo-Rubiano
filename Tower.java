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

        // 1. Validar ID
        if (i <= 0) {
            reportError("Cup ID must be positive.");
            return;
        }

        // 2. Verificar que no exista
        if (cupExistsInTower(i)) {
            reportError("Cup " + i + " already exists in the tower.");
            return;
        }

        // 3. Crear la copa
        Cup newCup = new Cup(i);

        // 4. Buscar la copa en el tope real (ignorando lids sin pareja que estén
        // arriba)
        Cup topCup = findTopmostCupInStack();
        boolean fitsInsideTop = (topCup != null) && (newCup.getId() < topCup.getId());

        if (!fitsInsideTop && height() + newCup.getHeight() > maxHeight) {
            reportError("Tower would exceed maximum height.");
            return;
        }

        // 5. Agregar a la lista de copas y al stack
        cups.add(newCup);
        stack.add(newCup);

        // 6. Reposicionar todos los elementos
        repositionStack();

        // 7. Hacer visible solo si el simulador está visible
        if (isVisible) {
            newCup.makeVisible();
        }

        // 8. Operación exitosa
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
        Lid newLid = new Lid(i, lidColor);

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
     * Calculates the total height of all non-nested elements currently in the
     * stack.
     * Nested cups (those that fit inside a larger cup below them) are excluded
     * from the height calculation. Lids contribute 1 centimeter each to the total.
     *
     * @return the total height in centimeters of all displayed elements
     */
    public int height() {
        int total = 0;
        for (int i = 0; i < stack.size(); i++) {
            Object element = stack.get(i);
            if (element instanceof Cup) {
                Cup cup = (Cup) element;
                // Skip cups that are nested inside the cup below them
                if (isNestedInBelow(i))
                    continue;
                total += cup.getHeight();
            } else if (element instanceof Lid) {
                // Lids always contribute 1cm to the height
                total += ((Lid) element).getHeight();
            }
        }
        return total;
    }

    /**
     * Determines whether the element at the given index is a cup nested inside
     * the cup immediately below it.
     * A cup is considered nested when both the current element and the element
     * below are cups, and the current cup has a smaller ID than the one below.
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
     * Nested cups are placed on the inner base of the cup below and do not add
     * to the external tower height. Non-nested elements increase the accumulated
     * height used to position subsequent elements.
     */
    private void repositionStack() {
        int heightPixels = 0;
        int[] bottomPixels = new int[stack.size()];

        for (int i = 0; i < stack.size(); i++) {
            Object element = stack.get(i);

            if (isNestedInBelow(i)) {
                // Place nested element on the internal base of the cup below (1cm above)
                int innerBottom = bottomPixels[i - 1] + PIXELS_PER_CM;
                bottomPixels[i] = innerBottom;
                positionElement(element, innerBottom);
                // Nested elements do not increase the external tower height
            } else {
                bottomPixels[i] = heightPixels;
                positionElement(element, heightPixels);
                if (element instanceof Cup) {
                    heightPixels += ((Cup) element).getHeight() * PIXELS_PER_CM;
                } else if (element instanceof Lid) {
                    heightPixels += ((Lid) element).getHeight() * PIXELS_PER_CM;
                }
            }
        }
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
     * <p>
     * Every stacked element is made invisible and removed from {@code stack},
     * but the master collections {@code cups} and {@code lids} are kept so the
     * tower can be rebuilt later (for example, in ordering operations).
     * </p>
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
    
    //Segundo Ciclo
    //Requisitos Funcionales
    //10. Create Extensión
    /**
     * Creates a new tower with the specified number of cups.
     * This constructor automatically generates cups with IDs from 1 to the given number.
     * The tower is created with default width and calculates the required maximum height
     * to fit all cups when they are stacked in order (largest at bottom)
     * No lids are created in this constructor
     * @param cups
     */
    public Tower(int cups){
        //Calcular la altura que se necesita: sumar 2^0 + 2^1 + ... + 2^(cups-1)
        int requiredHeight = (int) Math.pow(2, cups) - 1;
        
        //Usar ancho por defecto
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
        
        //Crear cups de 1 a cups y añadirlos a stack
        //Añadir en orden de cups a 1
        for(int i = cups; i >= 1; i--){
            Cup newCup = new Cup(i);
            this.cups.add(newCup);
            this.stack.add(newCup);
        }
        
        //Posicionar todas las cups
        repositionStack();
        
        //Hacerlas visibles
        if(isVisible){
            for(Cup cup: this.cups){
                cup.makeVisible();
            }
        }
        lastOperationOk = true;
    }
    
    //Requisitos 11 y 12. Reorganize
    /**
     * Automatically pairs all cups with their matching lid in the tower.
     * For each cup in tower, if a lid with the same number exists,
     * they are paired together. The lid is visually positioned on top of its cup.
     * Only cups and lids that are not already paired will be matched
     * Sets lastOperationOk to true when complete.
     */
    public void cover(){
        //Iterar sobre todas las cups en tower
        for(Cup cup: cups){
            //Skip si ya está lidded
            if(cup.isLidded()){
                continue;
            }
            //Buscar una lid que coincida con el mismo numero
            Lid matchingLid = findLidById(cup.getId());
            // Si existe una lid compatible y no esta emparejada
            if(matchingLid != null && !matchingLid.isPaired()){
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
    public void swap(String[] o1, String[] o2){
        lastOperationOk = false;
        //Validar input
        if(o1 == null || o1.length != 2 || o2 == null || o2.length != 2){
            reportError("Invalid object format");
            return;
        }
        //Encontrar los indices de ambos objetos
        int index1 = findObjectInStack(o1[0], o1[1]); //Este toca hacerlo private
        int index2 = findObjectInStack(o2[0], o2[1]);
        
        //Validar que los objetos existan
        if(index1 == -1){
            reportError("Object" + o1[0] + "#" + o1[1] + "not found");
            return;
        }
        if(index2 == -1){
            reportError("Object" + o2[0] + "#" + o2[1] + "not found");
            return;
        }
        
        //Cambiar los objetos
        Object temp = stack.get(index1);
        stack.set(index1, stack.get(index2));
        stack.set(index2, temp);
        
        //Reposicionar
        repositionStack();
        lastOperationOk = true;
    }
    /**
     * Finds the index of an object in the stack by its type and number.
     * 
     * @param type "cup" or "lid"
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
}
