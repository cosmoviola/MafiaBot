package roles;

import java.util.HashSet;

import game.Game;
import game.Player;

public abstract class Role {
	
	protected String copResult = " is a cop.";
	protected String wolfResult = " is the wolf.";
	protected Player target;
	protected Player actor;
	protected String id;
	
	public Role(String id){
		this.id = id;
	}

	public abstract void doAction(Game g);
	
	public void setActor(Player p){
		actor = p;
	}
	
	public void setTarget(Player p){
		target = p;
	}
	
	public abstract String roleMessage();
	
	public abstract String winCondition();
	
	public abstract String cardFlip();
	
	public abstract HashSet<String> getCommands();
	
}
