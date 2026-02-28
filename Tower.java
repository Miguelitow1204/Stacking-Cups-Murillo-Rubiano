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
    private static final int ORIGIN_X = 50;

    /** Y coordinate of the tower's origin point */
    private static final int ORIGIN_Y = 250;

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

    /**
     * Adds a new cup with the specified ID to the tower.
     * Creates a new Cup object and adds it to the available cups list.
     * If a cup with the same ID already exists, reports an error and sets
     * lastOperationOk to false. Makes the cup visible if the tower is visible.
     * 
     * @param i the unique identifier for the new cup
     */
    public void pushCup(int i) {
        if (cupExists(i)) {
            reportError("Cup " + i + " already exists in the tower");
            lastOperationOk = false;
            return;
        }

        Cup newCup = new Cup(i);
        stack.add(newCup);
        positionTopElement();
        if (isVisible) {
            newCup.makeVisible();
        }
        lastOperationOk = true;

    }

    private void positionTopElement() {
        if (stack.isEmpty())
            return;
        int lastIndex = stack.size() - 1;
        Object top = stack.get(lastIndex);
        int elementBottomY = ORIGIN_Y - getHeightUpTo(lastIndex) * PIXELS_PER_CM;
        if (top instanceof Cup) {
            ((Cup) top).setPosition(ORIGIN_X, elementBottomY);
        } else if (top instanceof Lid) {
            ((Lid) top).setPosition(ORIGIN_X, elementBottomY);
        }
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

    public void removeCup(int number) {
        int index = findCupIndex(number);
        if (index == -1) {
            reportError("Cup " + number + " does not exist in the tower.");
            lastOperationOk = false;
            return;
        }
        Cup cup = (Cup) stack.get(index);
        // Si la taza esta emparejada con una tapa, tambien la quito
        if (cup.isLidded()) {
            stack.remove(cup.getPairedLid());
            cup.getPairedLid().makeInvisible();
            cup.unpair();
        }
        cup.makeInvisible();
        stack.remove(index);
        repositionFrom(index);
    }

    private int findCupIndex(int number) {
        for (int k = 0; k < stack.size(); k++) {
            Object element = stack.get(k);
            if (element instanceof Cup && ((Cup) element).getId() == number) {
                return k;
            }
        }
        return -1;
    }

    private void repositionFrom(int fromIndex) {
        for (int k = fromIndex; k < stack.size(); k++) {
            Object element = stack.get(k);
            int heightBelow = getHeightUpTo(k);
            int elementBottomY = ORIGIN_Y - heightBelow * PIXELS_PER_CM;
            if (element instanceof Cup) {
                ((Cup) element).setPosition(ORIGIN_X, elementBottomY);
            } else if (element instanceof Lid) {
                ((Lid) element).setPosition(ORIGIN_X, elementBottomY);
            }
        }
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
}
