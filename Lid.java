
/**
 * Lid class, a lid can be paired with a cup.
 * all lids are 1 cm height
 * 
 * @author (Murillo-Rubiano)
 * @version (1.5)
 */
public class Lid {
    private int number;
    private String color;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private Cup pairedCup;
    private Rectangle lidRectangle;
    
    private int currentX;
    private int currentY;

    private static final int HEIGHT_CM = 1;
    private static final int PIXELS_PER_CM = 10;

    public Lid(int number) {
        this.number = number;
        this.color = getColorForId(number);
        this.xPosition = 0;
        this.yPosition = 0;
        this.isVisible = false;
        this.pairedCup = null;

        lidRectangle = new Rectangle();
        lidRectangle.changeColor(this.color);
        
        //initial position
        currentX = 70;
        currentY = 15;
    }

    private String getColorForId(int id) {
        String[] colors = { "blue", "red", "green", "yellow", "magenta", "cyan", "orange" };
        return colors[(id - 1) % colors.length];
    }

    public void setPosition(int x,int yCupBottom,int cupWidth,int cupTotalHeight){
        erase();

        this.xPosition = x;
        this.yPosition = yCupBottom;

        int lidHeight = HEIGHT_CM * PIXELS_PER_CM;

        int newX = x - cupWidth/2;
        int newY = yCupBottom - cupTotalHeight - lidHeight;

        lidRectangle.changeSize(lidHeight,cupWidth);

        lidRectangle.moveHorizontal(newX - currentX);
        lidRectangle.moveVertical(newY - currentY);

        currentX = newX;
        currentY = newY;

        draw();
    }

    private void draw() {
        if (isVisible) {
            lidRectangle.makeVisible();
        }
    }

    private void erase() {
        if (isVisible) {
            lidRectangle.makeInvisible();
        }
    }

    public void makeVisible() {
        this.isVisible = true;
        lidRectangle.makeVisible();
    }

    public void makeInvisible() {
        this.isVisible = false;
        lidRectangle.makeInvisible();
    }
    
    /**
     * Pairs this lid with a cup
     * @param cup (The cup to pair with)
     */
    public void pairWith(Cup cup){
        if(cup == null){
            return;
        }
        
        if(this.pairedCup != cup){
            this.pairedCup = cup;
            cup.pairWith(this);
        }
    }
    
    /**
     * Unpairs this lid from its cup
     */
    public void unpair(){
        if(this.pairedCup != null){
            Cup tempCup = this.pairedCup;
            this.pairedCup = null;
            tempCup.unpair();
        }
    }
    
    /**
     * Checks if lid is paired with a cup
     * @retrun true if is paired, false if is not
     */
    public boolean isPaired(){
        return pairedCup != null;
    }
    
    /**
     * Returns the paired Cup
     * @return the paired Cup, or null if not paired
     */
    public Cup getPairedCup(){
        return pairedCup;
    }
    
    /**
     * @return lid's number
     */
    public int getNumber(){
        return number;
    }
    
    /**
     * @return the lid's height 
     */
    public int getHeight(){
        return HEIGHT_CM;
    }
}