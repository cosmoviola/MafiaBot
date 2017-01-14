package roles;

import game.Game;
import game.Player;

public abstract class Role {
	
	protected String copResult = " is a cop.";
	protected String wolfResult = " is the wolf.";
	protected Player target;
	protected Player actor;

	public abstract void doAction(Game g);
	
}
