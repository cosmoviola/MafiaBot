package alignments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Player;

public abstract class Alignment {
	
	protected String name;
	protected static Map<String, Alignment> alignments = new HashMap<String, Alignment>();
	protected Set<Player> members = new HashSet<Player>();
	
	/**Construct an Alignment with name n. Names should be unique and lower case.*/
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
}
