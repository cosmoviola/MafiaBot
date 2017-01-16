package roles;

import game.Game;

public class Wolf extends Role {

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else if(target!=null){
				if(g.getCycle()==0){
					target.hook();
				}else{
					target.kill();
					g.postMessage(target.getDiscriminator()+" has been killed. "
							+ "They were a "+target.getRole().cardFlip()+".");
				}
			}
		}
	}

	@Override
	public String roleMessage() {
		return "You are the wolf. At night on cycle 0, message me !hook <user> to hook user. "
				+ "On following nights, message me !kill <user> to kill user.";
	}

	@Override
	public String winCondition() {
		return "You win when you are the last player standing.";
	}
	
	@Override
	public String cardFlip() {
		return "Wolf";
	}
}
