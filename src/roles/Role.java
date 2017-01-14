package roles;

import game.Game;

public abstract class Role {
	
	protected String copResult = " is a cop.";
	protected String wolfResult = " is the wolf.";

	public abstract void doAction(Game g);
	
}
