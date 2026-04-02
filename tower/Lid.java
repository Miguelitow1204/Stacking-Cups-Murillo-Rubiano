package tower;
import shapes.Rectangle;

/**
 * Lid class, a lid can be paired with a cup.
 * all lids are 1 cm height
 * 
 * C4 - Abstract base class for all lid types 
 * 
 * @author (Murillo-Rubiano)
 * @version (3.0)
 */
public abstract class Lid {
    private int number;
    protected String color;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private Cup pairedCup;
    private Rectangle lidRectangle;

    private int currentX;
    private int currentY;

    protected static final int HEIGHT_CM = 1;
    protected static final int PIXELS_PER_CM = 10;

    public Lid(int number) {
        this(number, null);
    }

    public Lid(int number, String color) {
        this.number = number;
        this.color = (color != null) ? color : getColorForId(number);
        this.color = getColorForId(number);
        this.xPosition = 0;
        this.yPosition = 0;
        this.isVisible = false;
        this.pairedCup = null;

        lidRectangle = new Rectangle();
        lidRectangle.changeColor(this.color);

        // initial position
        currentX = 70;
        currentY = 15;
    }

    private String getColorForId(int id) {
        String[] colors = { "blue", "red", "green", "yellow", "magenta", "cyan", "orange" };
        return colors[(id - 1) % colors.length];
    }

    /**
     * Sets position when lid is alone in the stack (not on a cup)
     * 
     * @param x       center x coordinate
     * @param bottomY bottom y coordinate for the lid
     */
    public void setPositionAlone(int x, int bottomY) {
        erase();
        this.xPosition = x;
        this.yPosition = bottomY;

        int lidHeight = HEIGHT_CM * PIXELS_PER_CM;
        int lidWidth = 15 + (this.number * 3); // Same width formula as cups

        int newX = x - lidWidth / 2;
        int newY = bottomY - lidHeight;

        lidRectangle.changeSize(lidHeight, lidWidth);

        lidRectangle.moveHorizontal(newX - currentX);
        lidRectangle.moveVertical(newY - currentY);

        currentX = newX;
        currentY = newY;

        draw();
    }

    public void setPosition(int x, int yCupBottom, int cupWidth, int cupTotalHeight) {
        erase();

        this.xPosition = x;
        this.yPosition = yCupBottom;

        int lidHeight = HEIGHT_CM * PIXELS_PER_CM;

        int newX = x - cupWidth / 2;
        int newY = yCupBottom - cupTotalHeight - lidHeight;

        lidRectangle.changeSize(lidHeight, cupWidth);

        lidRectangle.moveHorizontal(newX - currentX);
        lidRectangle.moveVertical(newY - currentY);

        currentX = newX;
        currentY = newY;

        draw();
    }
    
    /**
     * Draws the lid on the canvas
     */
    private void draw() {
        if (isVisible) {
            lidRectangle.makeVisible();
        }
    }

    /**
     * Erases the lid from the canvas
     */
    private void erase() {
        if (isVisible) {
            lidRectangle.makeInvisible();
        }
    }

    /**
     * Makes the lid visible
     */
    public void makeVisible() {
        this.isVisible = true;
        lidRectangle.makeVisible();
    }

    /**
     * Makes the lid invisible
     */
    public void makeInvisible() {
        this.isVisible = false;
        lidRectangle.makeInvisible();
    }

    /**
     * Pairs this lid with a cup
     * 
     * @param cup (The cup to pair with)
     */
    public void pairWith(Cup cup) {
        if (cup == null) {
            return;
        }

        if (this.pairedCup != cup) {
            this.pairedCup = cup;
            cup.pairWith(this);
        }
    }

    /**
     * Unpairs this lid from its cup
     */
    public void unpair() {
        if (this.pairedCup != null) {
            Cup tempCup = this.pairedCup;
            this.pairedCup = null;
            tempCup.unpair();
        }
    }

    /**
     * Checks if lid is paired with a cup
     * 
     * @retrun true if is paired, false if is not
     */
    public boolean isPaired() {
        return pairedCup != null;
    }

    /**
     * Returns the paired Cup
     * 
     * @return the paired Cup, or null if not paired
     */
    public Cup getPairedCup() {
        return pairedCup;
    }

    /**
     * @return lid's number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the lid's height
     */
    public int getHeight() {
        return HEIGHT_CM;
    }
    
    /**
     * Determines whether this lid is allowed to enter the tower.
     * Each lid subtype defines its own entry condition.
     *
     * @param tower the Tower this lid is trying to enter
     * @return true if the lid can be pushed onto the tower
     */
    public abstract boolean canEnterTower(Tower tower);
    
    /**
     * Defines the insertion behavior when this lid is pushed onto the tower.
     * Each lid subtype decides how it adds itself to the stack.
     *
     * @param tower the Tower this lid is being pushed onto
     */
    public abstract void onPush(Tower tower);
    
    /**
     * Determines whether this lid is allowed to be removed from the tower.
     * Each lid subtype defines its own removal condition.
     *
     * @param tower the Tower this lid is in
     * @return true if the lid can be removed from the tower
     */
    public abstract boolean canBeRemoved(Tower tower);
    
}