package roles;

import game.Game;
import game.Player;

public abstract class Role {
	
	protected String copResult = " is a cop.";
	protected String wolfResult = " is the wolf.";
	protected Player target;
	protected Player actor;

	public abstract void doAction(Game g);
	
	public void setActor(Player p){
		actor = p;
	}
	
	public abstract String roleMessage();
	
	public abstract String winCondition();
	
	public abstract String cardFlip();
	
	public abstract String[] getCommands();
	
}
