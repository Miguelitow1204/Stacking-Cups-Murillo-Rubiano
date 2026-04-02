package shapes;

/**
 * Abstract class for all geometric shapes (Rectangle, Circle, Triangle)
 * 
 * @author (Murillo-Rubiano)
 * @version (1.0)
 */

public abstract class Shape{
    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;
    
    /**
     * Makes the shape visible on canvas
     */
    public abstract void makeVisible();
    
    /**
     * Makes the shape invisible
     */
    public abstract void makeInvisible();
    
    /**
     * Moves the shape horizontally by the given distance
     * @param distance the distance to move
     */
    public abstract void moveHorizontal(int distance);
    
    /**
     * Moves the shape vertically by the given distance
     * @param distance the distance to move
     */
    public abstract void moveVertical(int distance);
    
    /**
     * Changes the color of the shape
     * @param newColor
     */
    public abstract void changeColor(String newColor);
    
    /**
     * Changes the size of the shape
     * @param newHeight
     * @param newWidth
     */
    public abstract void changeSize(int newHeight, int newWidth);
    
    /**
     * Returns the current position in x
     */
    public int getXPosition(){
        return xPosition;
    }
    
    /**
     * Returns the current position in y
     */
    public int getYPosition(){
        return yPosition;
    }
    
    /**
     * Returns the current color
     */
    public String getColor(){
        return color;
    }
    
    /**
     * Returns wether the shape is visible 
     */
    public boolean isVisible(){
        return isVisible;
    }
}