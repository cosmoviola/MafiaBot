package roles;

import game.Game;

public class SaneCop extends Role {

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()&&!actor.isHooked()){
			if(target.isCop()){
				actor.privateMessage(target.getName()+copResult);
			}else{
				actor.privateMessage(target.getName()+wolfResult);
			}
		}
	}
	
}
