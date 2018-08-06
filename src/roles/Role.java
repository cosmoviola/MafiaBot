package roles;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import actions.Action;
import game.Game;
import game.Player;

/**A Role represents a role in a game of mafia, and contains methods and fields
 * for performing that role's actions.*/
public abstract class Role {
	
	protected Optional<Player> target = Optional.empty();
	protected Player actor;
	Map<String, Action> actions = new HashMap<>();
	
	/**Set which Player has this role.*/
	public void setActor(Player p){
		actor = p;
		for(Action a : actions.values()){
			a.setActor(p);
		}
	}
	
	/**Set the target for the action specified by the given keyword.*/
	public void setTarget(String key, Optional<Player> p){
		actions.get(key).setTarget(key, p);
	}
	
	/**Returns true iff all targets have been set for this night.*/
	public boolean allTargetsSet(Game g){
		return actions.values().stream().allMatch(a -> a.allTargetsSet() || (!a.isActive(g)));
	}
	
	/**Prepares the role for the next cycle by resetting the target to null.*/
	public void reset(){
		for(Action a: actions.values()){
			a.reset();
		}
	}
	
	/**Returns the message for describing this role to its Player.*/
	public abstract String roleMessage();
	
	/**Returns the instructions on how to use the role on the current cycle.*/
	public String roleMessageForThisNight(Game g){
		String initial = "It is Night "+g.getCycle()+".\n";
		if(actions.values().stream().allMatch(a -> !a.isActive(g))){
			return initial + "You have no actions to perform tonight.";
		}
		StringBuilder message = new StringBuilder(initial);
		for(Action a : actions.values()){
			String s = a.actionMessageForThisNight(g);
			if(!s.equals("")){
				message.append(s).append("\n");
			}
		}
		String result = message.toString();
		if(result.equals(initial)){
			return result + "You have no valid targets for your actions.";
		}
		return result.substring(0, result.length()-1);
	}
	
	/**Returns the role name which displays upon the death of this role.*/
	public abstract String cardFlip();
	
	/**Returns the set of valid commands for this role.*/
	public Set<String> getCommands(){
		return actions.keySet();
	}
	
	/**Returns the true role name of this role.
	 * This differs from cardFlip() as cardFlip() may return false information.
	 */
	public abstract String getTrueName();
	
	/**Return the set of actions for this role.*/
	public Collection<Action> getActions(){
		return actions.values();
	}
	
	/**Returns whether the supplied keyword is currently active.*/
	public boolean isActive(String keyword, Game g){
		return actions.get(keyword).isActive(g);
	}
	
}
