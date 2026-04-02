package tower;


/**
 * The normal cup used for the simulator
 * The base of the cup is 1 cm
 * 
 * @author (Murillo-Rubiano)
 * @version (1.0)
 */
public class NormalCup extends Cup{
    /**
     * Creates a normal cup with the given ID.
     * @param idNumC the cup identifier
     */
    public NormalCup(int idNumC){
        super(idNumC);
    }
    
    @Override
    public boolean canEnterTower(Tower tower){
        return true;
    }
    
    @Override
    public void onPush(Tower tower){
        tower.addCup(this);
    }
    
    @Override
    public boolean canBeRemoved(Tower tower){
        return true;
    }
}