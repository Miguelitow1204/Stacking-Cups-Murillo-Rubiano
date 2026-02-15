
/**
 * Cup - Represents a cup in the stacking tower.
 * The base of the cup is 1 cm.
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
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
    private Rectangle bodyRectangle;
    private Rectangle baseRectangle;
    
    /**
     * Constructor
     * 
     * @param idNumC
     */
    public Cup(int idNumC){
        this.idNumC = idNumC;
        this.height = calculateHeight(idNumC);
        this.color = getColorForId(idNumC);
        this.xPosition = 0;
        this.yPosition = 0;
        this.isVisible = false;
        this.pairedLid = null;
        
        //Initialize body rectangle (cup without base)
        //The body height is total height minus 1cm of the base
        this.bodyRectangle = new Rectangle();
        this.bodyRectangle.changeColor(this.color);
        this.bodyRectangle.changeSize((this.height - 1)*10, 30);
        
        //Initialize base rectangle (1 cm thick base)
        this.baseRectangle = new Rectangle();
        this.baseRectangle.changeColor(this.color);
        this.baseRectangle.changeSize(10, 30); // 1cm = 10 píxeles    
    }
    
    //Public methods
    /**
     * Returns the cup's identifier
     * @return The cup idNumC
     */
    public int getId() {
        return idNumC;
    }
    
    /**
     * Returns the cup's height
     * @return The height in cm
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Returns the cup's color
     * @return The color as a String
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Sets the position of the cup on the canvas
     * The position refers to the bottom of the cup
     * @param x The x coordinate
     * @param y The y coordinate (bottom of cup)
     */
    public void setPosition(int x, int y) {
        erase();
        
        this.xPosition = x;
        this.yPosition = y; // yPosition is the bottom of the cup
        
        //Calculate positions for both rectangles
        //Y=0 is at the top and increases going down
        //Base position: sits at yPosition
        int baseX = xPosition;
        int baseY = yPosition;
        
        //Body position: sits above the base. Y is smaller
        int bodyX = xPosition;
        int bodyY = yPosition - ((height - 1)*10); //Body starts (height-1) above base
        
        //Move rectangles to calculated positions
        //moveHorizontal and moveVertical are relative to default position (70, 15)
        baseRectangle.moveHorizontal(baseX - 70);
        baseRectangle.moveVertical(baseY - 15);
        
        bodyRectangle.moveHorizontal(bodyX - 70);
        bodyRectangle.moveVertical(bodyY - 15);
        
        draw();
    }
    
    /**
     * Draws the cup on the canvas
     */
    public void draw() {
        if (isVisible) {
            bodyRectangle.makeVisible();
            baseRectangle.makeVisible();
        }
    }
    
    /**
     * Erases the cup from the canvas
     */
    public void erase() {
        if (isVisible) {
            bodyRectangle.makeInvisible();
            baseRectangle.makeInvisible();
        }
    }
    
    /**
     * Makes the cup visible
     */
    public void makeVisible() {
        this.isVisible = true;
        bodyRectangle.makeVisible();
        baseRectangle.makeVisible();
    }
    
    /**
     * Makes the cup invisible
     */
    public void makeInvisible() {
        this.isVisible = false;
        bodyRectangle.makeInvisible();
        baseRectangle.makeInvisible();
    }
    
    /**
     * Pairs this cup with a lid
     * @param lid The lid to pair with
     */
    public void pairWith(Lid lid) {
        if (lid == null) {
            return;
        }
        //Avoid infinite recursion by checking if already paired
        if (this.pairedLid != lid) {
            this.pairedLid = lid;
            //lid.pairWith(this); //quitar el comentario en minicilco4 //Bidirectional pairing
        }
    }
    
    /**
     * Unpairs this cup from its lid
     */
    public void unpair() {
        if (this.pairedLid != null) {
            Lid tempLid = this.pairedLid;
            this.pairedLid = null;
            // tempLid.unpair(); //quitar comentario en miniciclo4 //Bidirectional unpairing
        }
    }
    
    /**
     * Checks if the cup has a lid paired
     * @return true if the cup has a lid, false otherwise
     */
    public boolean isLidded() {
        return pairedLid != null;
    }
    
    /**
     * Returns the paired lid
     * @return The paired Lid object, or null if not paired
     */
    public Lid getPairedLid() {
        return pairedLid;
    }
    
    //Private auxiliary methods
    /**
     * Calculates the height of a cup based on its id
     * Height formula: 2^(idNumC-1)
     * Examples: idNumC=1 → 2^0=1, idNumC=2 → 2^1=2, idNumC=3 → 2^2=4, idNumC=4 → 2^3=8
     * @param idNumC The cup identifier
     * @return The calculated height
     */
    private int calculateHeight(int id) {
        return (int) Math.pow(2, id - 1);
    }
    
    /**
     * Assigns a unique color based on the cup's id
     * Colors cycle through the array if there are many cups
     * @param idNumC The cup identifier
     * @return The color as a String
     */
    private String getColorForId(int idNumC) {
        String[] colors = {"blue", "red", "green", "yellow", "magenta", "cyan", "orange"};
        return colors[(idNumC - 1) % colors.length];
    }
}