package tower;


/**
 * A locked lid that prevents its cup from being removed
 * while it is in place. The lid must be removed first
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class LockedLid extends Lid{
    /**
     * Creates a locked lid with the given ID.
     * @param number the lid identifier
     */
    public LockedLid(int number){
        super(number);
    }
    
    /**
     * Creates a locked lid with the given ID and color.
     * @param number the lid identifier
     * @param color  the color inherited from its companion cup
     */
    public LockedLid(int number, String color){
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