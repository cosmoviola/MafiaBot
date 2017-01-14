package roles;

import game.Game;

public class ParanoidCop extends Role {

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			g.privateMessage(actor, target.getName()+wolfResult);
		}
	}

}
