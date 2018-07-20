package roles;

import game.Game;

public class ParanoidCop extends Cop {

	public ParanoidCop() {

	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else target.ifPresent(t ->{
				actor.privateMessage(t.getIdentifier()+wolfResult);
			});
		}
	}	
}
