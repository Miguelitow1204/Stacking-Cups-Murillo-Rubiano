package tower;


/**
 * A crazy lid that places itself below its cup as a base.
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class CrazyLid extends Lid{
    /**
     * Creates a crazy lid with the given ID.
     * @param number the lid identifier
     */
    public CrazyLid(int number){
        super(number);
    }
    
    /**
     * Creates a crazy lid with the given ID and color.
     * @param number the lid identifier
     * @param color  the color inherited from its companion cup
     */
    public CrazyLid(int number, String color){
        super(number, color);
    }
    
    @Override 
    public boolean canEnterTower(Tower tower){
        return true;
    }
    
    @Override
    public void onPush(Tower tower){
        tower.addLidBelowCup(this);
    }
    
    @Override
    public boolean canBeRemoved(Tower tower){
        return true;
    }
}