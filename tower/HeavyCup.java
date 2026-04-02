package tower;


/**
 * The cup proposal, a heavy cup that cannot be moved easily
 * 
 * @author (Murillo-Rubiano) 
 * @version (1.0)
 */
public class HeavyCup extends Cup{
    /**
     * Creates a heavy cup with the given ID.
     * @param idNumC the cup identifier
     */
   public HeavyCup(int idNumC){
       super(idNumC);
       this.color = "black";
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
       return tower.isTop(this);
   }
   
}