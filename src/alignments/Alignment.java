package alignments;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import actions.Action;
import game.Game;
import game.Player;

public abstract class Alignment {
	
	protected String name;
	protected static Map<String, Alignment> alignments = new HashMap<String, Alignment>();
	protected Set<Player> members = new HashSet<Player>();
	protected Map<String, Action> actions = new HashMap<>();
	
	/**Construct an Alignment with name n. Names should be unique.*/
	public Alignment(String n){
		name = n;
		alignments.put(name, this);
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Alignment){
			return ((Alignment) obj).name.equals(this.name);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
	
	/**Returns the name of this alignment.*/
	public String getName(){
		return name;
	}
	
	/**Returns the Set of Player objects in this alignment.*/
	public Set<Player> getMembers(){
		return members;
	}
	
	/**Adds Player p to this Alignment.*/
	public void addPlayer(Player p){
		members.add(p);
	}
	
	/**Gets the alignment with name n if it exists.*/
	public static Alignment getAlignment(String n){
		if(alignments.containsKey(n)){
			return alignments.get(n);
		}
		return null;
	}
	
	/**Returns a Collection containing all alignments in the game.*/
	public static Collection<Alignment> getAllAlignments(){
		return alignments.values();
	}
	
	/**Returns if this faction wins this game.*/
	public abstract boolean checkVictory(Game g);
	
	/**Returns a string saying what the player's win condition is.*/
	public abstract String winCondition();
	
	/**Returns the set of actions performable by this alignment.*/
	public Collection<Action> getActions(){
		return actions.values();
	}
	
	/**Returns the set of keywords for the actions performable by this alignment.*/
	public Set<String> getKeywords(){
		return actions.keySet();
	}
	
	/**Set the actor and target for the action with the given keyword.*/
	public void setTarget(String key, Player actor, Optional<Player> target){
		Action a = actions.get(key);
		a.setTarget(key, target);
		a.setActor(actor);
	}
}
