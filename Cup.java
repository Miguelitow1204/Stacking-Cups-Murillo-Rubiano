
/**
 * Cup - Represents a cup in the stacking tower.
 * The base of the cup is 1 cm.
 * 
 * @author (Murillo-Rubiano)
 * @version (1.5)
 */
public class Cup {
    // instance variables - replace the example below with your own
    private int idNumC;
    private int height;
    private String color;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private Lid pairedLid;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private Rectangle base;
    private int leftWallX, leftWallY; // Posición actual de la pared izquierda (top-left)
    private int rightWallX, rightWallY; // Posición actual de la pared derecha
    private int baseX, baseY; // Posición actual de la base

    private static final int PIXELS_PER_CM = 10;
    private static final int WALL_THICKNESS = 3;
    private static final int BASE_HEIGHT_CM = 1;

    /**
     * Constructor
     * 
     * @param idNumC
     */
    public Cup(int idNumC) {
        this.idNumC = idNumC;
        this.height = calculateHeight(idNumC);
        this.color = getColorForId(idNumC);
        this.xPosition = 0;
        this.yPosition = 0;
        this.isVisible = false;
        this.pairedLid = null;

        // Cup dimensions in pixels
        int cupWidth = getCupWidth();
        int wallHeight = getWallHeightPixels();
        int baseHeightPx = BASE_HEIGHT_CM * PIXELS_PER_CM;

        // Create rectangles
        this.leftWall = new Rectangle();
        this.leftWall.changeColor(this.color);
        this.leftWall.changeSize(wallHeight, WALL_THICKNESS);

        this.rightWall = new Rectangle();
        this.rightWall.changeColor(this.color);
        this.rightWall.changeSize(wallHeight, WALL_THICKNESS);

        this.base = new Rectangle();
        this.base.changeColor(this.color);
        this.base.changeSize(baseHeightPx, cupWidth);

        // Save initial position (default BlueJ shapes: 70, 15)
        this.leftWallX = 70;
        this.leftWallY = 15;
        this.rightWallX = 70;
        this.rightWallY = 15;
        this.baseX = 70;
        this.baseY = 15;

        setPosition(100, 200);
    }

    // Public methods
    /**
     * Returns the cup's identifier
     * 
     * @return The cup idNumC
     */
    public int getId() {
        return idNumC;
    }

    public int getCupWidth() {
        return 15 + (this.idNumC * 3);
    }

    private int getWallHeightPixels() {
        int wallHeightCm = this.height - BASE_HEIGHT_CM;
        return Math.max(0, wallHeightCm * PIXELS_PER_CM);
    }

    public int getTotalHeightPixels() {
        return this.height * PIXELS_PER_CM;
    }

    public String getColor() {
        return color;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Sets the position of the cup on the canvas
     * The position refers to the bottom of the cup
     * 
     * @param x The x coordinate
     * @param y The y coordinate (bottom of cup)
     */
    public void setPosition(int x, int y) {
        erase();

        this.xPosition = x;
        this.yPosition = y;

        // Calcular dimensiones
        int cupWidth = getCupWidth();
        int wallHeight = getWallHeightPixels();
        int baseHeightPx = BASE_HEIGHT_CM * PIXELS_PER_CM;

        // Posición de la base: su borde inferior debe estar en y
        int newBaseX = x - (cupWidth / 2);
        int newBaseY = y - baseHeightPx; // Esquina superior izquierda de la base

        // Pared izquierda: sobre la base, misma x, su borde inferior coincide con el
        // borde superior de la base
        int newLeftWallX = newBaseX;
        int newLeftWallY = y - wallHeight - baseHeightPx; // Esquina superior izquierda de la pared

        // Pared derecha
        int newRightWallX = newBaseX + cupWidth - WALL_THICKNESS;
        int newRightWallY = y - wallHeight - baseHeightPx; // Esquina superior derecha de la pared

        // Mover cada rectángulo de su posición actual a la nueva (movimiento relativo)
        base.moveHorizontal(newBaseX - baseX);
        base.moveVertical(newBaseY - baseY);
        leftWall.moveHorizontal(newLeftWallX - leftWallX);
        leftWall.moveVertical(newLeftWallY - leftWallY);
        rightWall.moveHorizontal(newRightWallX - rightWallX);
        rightWall.moveVertical(newRightWallY - rightWallY);

        // Actualizar las posiciones guardadas
        baseX = newBaseX;
        baseY = newBaseY;
        leftWallX = newLeftWallX;
        leftWallY = newLeftWallY;
        rightWallX = newRightWallX;
        rightWallY = newRightWallY;

        if (pairedLid != null) {
            int totalHeight = getTotalHeightPixels();
            pairedLid.setPosition(xPosition, yPosition, cupWidth, totalHeight);
        }

        draw();
    }

    /**
     * Draws the cup on the canvas
     */
    public void draw() {
        if (isVisible) {
            leftWall.makeVisible();
            rightWall.makeVisible();
            base.makeVisible();
        }
    }

    /**
     * Erases the cup from the canvas
     */
    public void erase() {
        if (isVisible) {
            leftWall.makeInvisible();
            rightWall.makeInvisible();
            base.makeInvisible();
        }
    }

    /**
     * Makes the cup visible
     */
    public void makeVisible() {
        this.isVisible = true;
        leftWall.makeVisible();
        rightWall.makeVisible();
        base.makeVisible();
    }

    /**
     * Makes the cup invisible
     */
    public void makeInvisible() {
        this.isVisible = false;
        leftWall.makeInvisible();
        rightWall.makeInvisible();
        base.makeInvisible();
    }

    /**
     * Pairs this cup with a lid
     * 
     * @param lid The lid to pair with
     */
    public void pairWith(Lid lid) {
        if (lid == null) {
            return;
        }
        // Avoid infinite recursion by checking if already paired
        if (this.pairedLid != lid) {
            this.pairedLid = lid;
            lid.pairWith(this); // Bidirectional
            // pairing
        }
    }

    /**
     * Unpairs this cup from its lid
     */
    public void unpair() {
        if (this.pairedLid != null) {
            Lid tempLid = this.pairedLid;
            this.pairedLid = null;
            tempLid.unpair(); // Bidirectional unpairing
        }
    }

    /**
     * Checks if the cup has a lid paired
     * 
     * @return true if the cup has a lid, false otherwise
     */
    public boolean isLidded() {
        return pairedLid != null;
    }

    /**
     * Returns the paired lid
     * 
     * @return The paired Lid object, or null if not paired
     */
    public Lid getPairedLid() {
        return pairedLid;
    }

    // Private auxiliary methods
    /**
     * Calculates the height of a cup based on its id
     * Height formula: 2^(idNumC-1)
     * Examples: idNumC=1 → 2^0=1, idNumC=2 → 2^1=2, idNumC=3 → 2^2=4, idNumC=4 →
     * 2^3=8
     * 
     * @param idNumC The cup identifier
     * @return The calculated height
     */
    private int calculateHeight(int id) {
        return (int) Math.pow(2, id - 1);
    }

    /**
     * Assigns a unique color based on the cup's id
     * Colors cycle through the array if there are many cups
     * 
     * @param idNumC The cup identifier
     * @return The color as a String
     */
    private String getColorForId(int idNumC) {
        String[] colors = { "blue", "red", "green", "yellow", "magenta", "cyan", "orange" };
        return colors[(idNumC - 1) % colors.length];
    }
}