package tower;


/**
 * Upon entering the tower, it displaces all the smaller objects, 
 * and if it manages to reach the bottom of the tower, it cannot be removed
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class HierarchicalCup extends Cup{
    /**
     * Creates a hierarchical cup with the given ID.
     * @param idNumC the cup identifier
     */
    public HierarchicalCup(int idNumC){
        super(idNumC);
        this.color = "purple";
    }
    
    @Override
    public boolean canEnterTower(Tower tower){
        return true;
    }
    
    @Override
    public void onPush(Tower tower){
        tower.insertHierarchicalCup(this);
    }
    
    @Override
    public boolean canBeRemoved(Tower tower){
        return !tower.isAtBottom(this);
    }
    
}