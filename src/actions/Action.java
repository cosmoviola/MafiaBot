package actions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import game.Game;
import game.Player;

public abstract class Action {

	protected int priority; //dictates when this action is performed relative to other actions
	protected Function<Game, Boolean> isActive; //true if this action can be used tonight
	protected Player actor; //the Player performing this action
	
	public Action(int p, Function<Game, Boolean> f){
		priority = p;
		isActive = f;
	}
	
	/**Set the actor for this action.*/
	public void setActor(Player p){
		actor = p;
	}
	
	/**Perform this action.*/
	public abstract void doAction(Game g);
	
	/**Returns true iff this action can be used tonight.*/
	public boolean isActive(Game g){
		return isActive.apply(g);
	}
	
	/**Returns the instructions on how to use this action on the current cycle.*/
	public abstract String actionMessageForThisNight(Game g);
	
	/**Add all keyword mappings for this action to the given map.
	 * 
	 * @throws IllegalArgumentException if the map already has a conflicting keyword.
	 */
	public abstract void addKeywordMappings(Map<String, Consumer<Optional<Player>>> map);
	
	/**Add all keyword active mappings for this action to the given map.
	 * 
	 * @throws IllegalArgumentException if the map already has a conflicting keyword.
	 */
	public abstract void addKeywordActiveMappings(Map<String, Function<Game, Boolean>> map);
	
	/**Return the priority of this action.*/
	public int getPriority(){
		return priority;
	}
	
	/**Return true if all targets are set for this action.*/
	public abstract boolean allTargetsSet();
	
	/**Reset all targets and flags for this role, preparing it for the next night.*/
	public abstract void reset();
	
}
