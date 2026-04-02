package tower;


/**
 * cup that eliminates all the lids that prevent it from passing through
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class OpenerCup extends Cup{
    /**
     * Creates an opener cup with the given ID.
     * @param idNumC the cup identifier
     */
    public OpenerCup(int idNumC){
        super(idNumC);
        this.color = "orange";
    }
    
    @Override
    public boolean canEnterTower(Tower tower){
        return true;
    }
    
    @Override
    public void onPush(Tower tower){
        tower.removeAllLids();
        tower.addCup(this);
    }
    
    @Override
    public boolean canBeRemoved(Tower tower){
        return true;
    }
    
}