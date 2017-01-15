package roles;

import game.Game;

public class ParanoidCop extends Role {

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else{
				actor.privateMessage(target.getName()+wolfResult);
			}
		}
	}

}
