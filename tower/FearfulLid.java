package tower;


/**
 * A fearful lid that only enters if its cup is in the tower.
 * and does not leave if it is currently lidding its cup 
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class FearfulLid extends Lid{
    
    /**
     * Creates a fearful lid with the given ID.
     * @param number the lid identifier
     */
    public FearfulLid(int number){
        super(number);
    }
    
    /**
     * Creates a fearful lid with the given ID and color.
     * @param number the lid identifier
     * @param color  the color inherited from its companion cup
     */
    public FearfulLid(int number, String color){
        super(number, color);
    }
    
    @Override
    public boolean canEnterTower(Tower tower){
        return tower.cupExistsInStack(getNumber());
    }
    
    @Override 
    public void onPush(Tower tower){
        tower.addLid(this);
    }
    
    @Override
    public boolean canBeRemoved(Tower tower){
        return !tower.isLiddingItsCup(this);
    }
    
}