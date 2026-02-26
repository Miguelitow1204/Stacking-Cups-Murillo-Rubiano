import java.util.ArrayList;

/**
 * class Tower, Main controller for the stacking cup simulator.
 * manages the tower structure, cup and lid operations and visualization.
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class Tower{
    private int width;
    private int maxHeight;
    private boolean isVisible;
    private boolean lastOperationOk;
    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;
    private ArrayList<Rectangle> heightMarkers;
    private ArrayList<String> stackOrder;
    private Rectangle visualFrame;
}