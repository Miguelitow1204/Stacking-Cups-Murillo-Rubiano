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
     * Creates a black rectangle that represents the outer boundary of the tower.
     * The frame size is based on the maximum height and width specified.
     * Positions are calculated relative to the tower's origin coordinates.
     */
    private void buildFrame() {
        frame = new Rectangle();
        frame.changeSize(maxHeight * PIXELS_PER_CM, width);
        frame.changeColor("black");
        // position the frame: moveHorizontal/moveVertical are relative to default
        // (70,15)
        frame.moveHorizontal(ORIGIN_X - 70);
        frame.moveVertical((ORIGIN_Y - maxHeight * PIXELS_PER_CM) - 15);
        frame.makeVisible();
    }

    /**
     * Builds tick marks for each centimeter of height.
     * Creates a small horizontal line for each centimeter from 1 to maxHeight.
     * Each tick mark is a black rectangle (1 pixel tall, 5 pixels wide) positioned
     * at the corresponding height level on the tower frame.
     */
    private void buildTicks() {
        for (int cm = 1; cm <= maxHeight; cm++) {
            Rectangle tick = new Rectangle();
            tick.changeSize(1, 5); // 1px tall, 5px wide
            tick.changeColor("black");
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
     * Checks if a cup with the given ID already exists in the tower.
     * Searches through the list of available cups to find a matching ID.
     * 
     * @param i the cup ID to search for
     * @return true if a cup with the given ID exists, false otherwise
     */
    private boolean cupExists(int i) {
        for (Cup c : cups) {
            if (c.getId() == i)
                return true;
        }
        return false;
    }

    public void pushCup(int i) {
        if (i <= 0) {
            reportError("Cup ID must be positive.");
            lastOperationOk = false;
            return;
        }

        if (cupExistsInTower(i)) {
            reportError("Cup " + i + " already exists in the tower.");
            lastOperationOk = false;
            return;
        }

        Cup newCup = new Cup(i);
        int newHeight = height() + newCup.getHeight();

        if (newHeight > maxHeight) {
            reportError("Cup " + i + " does not fit. Current height: " + height() +
                    ", cup height: " + newCup.getHeight() + ", max: " + maxHeight);
            lastOperationOk = false;
            return;
        }

        cups.add(newCup);
        stack.add(newCup);
        positionElement(stack.size() - 1);

        if (isVisible) {
            newCup.makeVisible();
        }
        lastOperationOk = true;
    }

    private int getStackHeightWithoutTop() {
        int total = 0;
        for (int k = 0; k < stack.size() - 1; k++) {
            Object element = stack.get(k);
            if (element instanceof Cup) {
                total += ((Cup) element).getHeight() + 1; // height + lid if lidded?
            } else if (element instanceof Lid) {
                total += 1;
            }
        }
        return total;
    }

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

    private int findCupIndexInStack(int id) {
        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i) instanceof Cup && ((Cup) stack.get(i)).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private boolean lidExistsInTower(int id) {
        for (Lid l : lids) {
            if (l.getNumber() == id)
                return true;
        }
        return false;
    }

    private boolean cupExistsInTower(int id) {
        for (Cup c : cups) {
            if (c.getId() == id)
                return true;
        }
        return false;
    }

    public void pushLid(int i) {
        if (i <= 0) {
            reportError("Lid ID must be positive.");
            lastOperationOk = false;
            return;
        }

        if (lidExistsInTower(i)) {
            reportError("Lid " + i + " already exists in the tower.");
            lastOperationOk = false;
            return;
        }

        Lid newLid = new Lid(i);
        int newHeight = height() + newLid.getHeight();

        if (newHeight > maxHeight) {
            reportError("Lid " + i + " does not fit. Current height: " + height() +
                    ", max: " + maxHeight);
            lastOperationOk = false;
            return;
        }

        lids.add(newLid);
        stack.add(newLid);

        // Check if there's a matching cup directly below (same ID, not already lidded)
        boolean paired = false;
        if (stack.size() >= 2) {
            Object below = stack.get(stack.size() - 2);
            if (below instanceof Cup) {
                Cup cupBelow = (Cup) below;
                if (cupBelow.getId() == i && !cupBelow.isLidded()) {
                    cupBelow.pairWith(newLid);
                    paired = true;
                }
            }
        }

        // Position the lid
        if (paired) {
            // The cup will handle positioning through pairWith
            Cup pairedCup = newLid.getPairedCup();
            int cupIndex = stack.indexOf(pairedCup);
            int cupBottomY = ORIGIN_Y - getHeightUpTo(cupIndex) * PIXELS_PER_CM;
            pairedCup.setPosition(ORIGIN_X, cupBottomY); // This updates lid position too
        } else {
            positionElement(stack.size() - 1);
        }

        if (isVisible) {
            newLid.makeVisible();
        }
        lastOperationOk = true;
    }

    private int getHeightUpTo(int index) {
        int total = 0;
        for (int k = 0; k < index; k++) {
            Object element = stack.get(k);
            if (element instanceof Cup) {
                total += ((Cup) element).getHeight();
            } else if (element instanceof Lid) {
                total += 1;
            }
        }
        return total;
    }

    public void popCup() {
        if (stack.isEmpty()) {
            reportError("Stack is empty.");
            lastOperationOk = false;
            return;
        }

        // Find the topmost cup
        int topCupIndex = -1;
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Cup) {
                topCupIndex = i;
                break;
            }
        }

        if (topCupIndex == -1) {
            reportError("No cups in the stack.");
            lastOperationOk = false;
            return;
        }

        Cup topCup = (Cup) stack.get(topCupIndex);

        // Check if there's a lid directly above
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
                reportError("Cannot pop cup - there are elements above it.");
                lastOperationOk = false;
                return;
            }
        }

        topCup.makeInvisible();
        stack.remove(topCupIndex);
        cups.remove(topCup);
        repositionFrom(topCupIndex);
        lastOperationOk = true;
    }

    public void popLid() {
        if (stack.isEmpty()) {
            reportError("Stack is empty.");
            lastOperationOk = false;
            return;
        }

        // Find the topmost lid
        int topLidIndex = -1;
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Lid) {
                topLidIndex = i;
                break;
            }
        }

        if (topLidIndex == -1) {
            reportError("No lids in the stack.");
            lastOperationOk = false;
            return;
        }

        // Check if there are elements above
        if (topLidIndex < stack.size() - 1) {
            reportError("Cannot pop lid - there are elements above it.");
            lastOperationOk = false;
            return;
        }

        Lid topLid = (Lid) stack.get(topLidIndex);

        // If paired, unpair
        if (topLid.isPaired()) {
            topLid.unpair();
        }

        topLid.makeInvisible();
        stack.remove(topLidIndex);
        lids.remove(topLid);
        repositionFrom(topLidIndex);
        lastOperationOk = true;
    }

    private void positionElement(int index) {
        if (index < 0 || index >= stack.size())
            return;

        Object element = stack.get(index);
        int heightBelow = getHeightUpTo(index);
        int elementBottomY = ORIGIN_Y - heightBelow * PIXELS_PER_CM;

        if (element instanceof Cup) {
            Cup cup = (Cup) element;
            cup.setPosition(ORIGIN_X, elementBottomY);
            // Note: if cup is paired with lid, Cup.setPosition will update lid position
        } else if (element instanceof Lid) {
            Lid lid = (Lid) element;
            if (lid.isPaired()) {
                // Lid is paired - its position is managed by the cup
                // But we need to ensure it's positioned correctly
                Cup pairedCup = lid.getPairedCup();
                int cupWidth = pairedCup.getCupWidth();
                int cupTotalHeightPx = pairedCup.getTotalHeightPixels();
                // Find where the cup's bottom is
                int cupIndex = stack.indexOf(pairedCup);
                if (cupIndex != -1) {
                    int cupBottomY = ORIGIN_Y - getHeightUpTo(cupIndex) * PIXELS_PER_CM;
                    lid.setPosition(ORIGIN_X, cupBottomY, cupWidth, cupTotalHeightPx);
                }
            } else {
                // Lid is alone - position it independently
                lid.setPositionAlone(ORIGIN_X, elementBottomY);
            }
        }
    }

    /**
     * Repositions all elements starting from a given index.
     */
    private void repositionFrom(int fromIndex) {
        for (int i = fromIndex; i < stack.size(); i++) {
            positionElement(i);
        }
    }

    public int height() {
        int total = 0;
        for (Object element : stack) {
            if (element instanceof Cup) {
                total += ((Cup) element).getHeight();
            } else if (element instanceof Lid) {
                total += ((Lid) element).getHeight();
            }
        }
        return total;
    }
}
