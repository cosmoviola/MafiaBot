package roles;

import game.Game;

public class SaneCop extends Cop {

	public SaneCop(String id) {
		super(id);
	}
	
	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else if(target!=null){
				if(target.isCop()){
					actor.privateMessage(target.getIdentifier()+copResult);
				}else{
					actor.privateMessage(target.getIdentifier()+wolfResult);
				}
			}
		}
	}
}
