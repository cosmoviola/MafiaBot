package roles;

import game.Game;

public class InsaneCop extends Cop {

	public InsaneCop() {
	
	}
	
	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else target.ifPresent(t -> {
				if(t.isCop()){
					actor.privateMessage(t.getIdentifier()+wolfResult);
				}else{
					actor.privateMessage(t.getIdentifier()+copResult);
				}
			});
		}
	}
}
