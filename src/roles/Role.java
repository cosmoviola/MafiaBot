package roles;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import game.Game;
import game.Player;

/**A Role represents a role in a game of mafia, and contains methods and fields
 * for performing that role's actions.*/
public abstract class Role {
	
	protected String copResult = " is a cop.";
	protected String wolfResult = " is the wolf.";
	protected Optional<Player> target = Optional.empty();
	protected boolean targetSet = false;
	protected Player actor;
	
	public Role(){
		
	}

	/**Perform the action for this role.*/
	public abstract void doAction(Game g);
	
	/**Set which Player has this role.*/
	public void setActor(Player p){
		actor = p;
	}
	
	/**Set the target for this role.*/
	public void setTarget(Optional<Player> p){
		target = p;
		targetSet = true;
	}
	
	/**Returns true iff a target has been set for this night.*/
	public boolean isTargetSet(){
		return targetSet;
	}
	
	/**Prepares the role for the next cycle by resetting the target to null.*/
	public void resetTarget(){
		target = Optional.empty();
		targetSet = false;
	}
	
	/**Returns the message for describing this role to its Player.*/
	public abstract String roleMessage();
	
	/**Returns the instructions on how to use the role on the current cycle.*/
	public abstract String roleMessageForThisNight(Game g);
	
	/**Returns true iff this role can target the supplied Player.*/
	public abstract boolean canTarget(Player p);
	
	/**Returns a Collection of all the Players which are valid targets of this role.*/
	public abstract Collection<Player> getValidTargets(Game g);
	
	/**Returns the role name which displays upon the death of this role.*/
	public abstract String cardFlip();
	
	/**Returns the set of valid commands for this role.*/
	public abstract Set<String> getCommands();
	
	/**Returns the true role name of this role.
	 * This differs from cardFlip() as cardFlip may return false information.
	 */
	public abstract String getTrueName();
	
}
