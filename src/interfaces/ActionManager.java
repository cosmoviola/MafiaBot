package interfaces;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import actions.Action;
import game.Game;
import game.Player;

public interface ActionManager {
	
	/**Returns the set of keywords for the actions performable by this ActionManager.*/
	public Set<String> getCommands();

	/**Returns whether the supplied keyword is currently active.*/
	public boolean isActive(String keyword, Game game);

	/**Set the actor and target for the action with the given keyword.*/
	public void setTarget(String keyword, Player actor, Optional<Player> target);
	
	/**Returns true iff all targets have been set for this night.*/
	public boolean allTargetsSet(Game g);
	
	/**Prepares this ActionManager for the next night.*/
	public void reset();
	
	/**Returns the set of actions performable by this ActionManager.*/
	public Collection<Action> getActions();
	
	/**Get the collection of players who should receive results.*/
	public Set<Player> getResultRecipients();
}
