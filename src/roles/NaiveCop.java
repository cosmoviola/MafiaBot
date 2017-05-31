package roles;

import game.Game;

public class NaiveCop extends Cop {

	public NaiveCop() {

	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else if(target!=null){
				actor.privateMessage(target.getIdentifier()+copResult);
			}
		}
	}
}
