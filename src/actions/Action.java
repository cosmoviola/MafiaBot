package actions;

import java.util.Optional;
import java.util.Set;
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
	
	/**Returns true iff the supplied player is the actor for this action.*/
	public boolean isActor(Player p){
		return p.equals(actor);
	}
	
	/**Set the target for the given keyword.*/
	public abstract void setTarget(String key, Optional<Player> p);
	
	/**Returns true iff this keyword can target the supplied actor and target.*/
	public abstract boolean canTarget(String key, Player actor, Optional<Player> target);
	
	/**Get all valid keywords for this action.*/
	public abstract Set<String> getKeywords();
	
	/**Perform this action.*/
	public abstract void doAction(Game g);
	
	/**Returns true iff this action can be used tonight.*/
	public boolean isActive(Game g){
		return isActive.apply(g);
	}
	
	/**Returns the instructions on how to use this action on the current cycle.*/
	public abstract String actionMessageForThisNight(Game g);
	
	/**Return the priority of this action.*/
	public int getPriority(){
		return priority;
	}
	
	/**Return true if all targets are set for this action.*/
	public abstract boolean allTargetsSet();
	
	/**Reset all targets and flags for this role, preparing it for the next night.*/
	public abstract void reset();
	
}
