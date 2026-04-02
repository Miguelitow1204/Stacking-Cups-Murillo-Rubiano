package tower;


/**
 * The normal lid used for the simulator
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class NormalLid extends Lid{
    
    /**
     * Creates a normal lid with the given ID.
     * @param number the lid identifier
     */
    public NormalLid(int number){
        super(number);
    }
    
    /**
     * Creates a normal lid with the given ID and color.
     * @param number the lid identifier
     * @param color  the color inherited from its companion cup
     */
    public NormalLid(int number, String color){
        super(number, color);
    }
    
    @Override
    public boolean canEnterTower(Tower tower){
        return true;
    }
    
    @Override
    public void onPush(Tower tower){
        tower.addLid(this);
    }
    
    @Override
    public boolean canBeRemoved(Tower tower){
        return true;
    }
}