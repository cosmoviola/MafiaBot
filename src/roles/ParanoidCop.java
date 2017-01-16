package roles;

import game.Game;

public class ParanoidCop extends Role {

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else if(target!=null){
				actor.privateMessage(target.getDiscriminator()+wolfResult);
			}
		}
	}

	@Override
	public String roleMessage() {
		return "You are a cop. At night, message me !check <user> to determine user's alignment. "
				+ "Be warned: you do not know your sanity."; 
	}
	
	@Override
	public String winCondition() {
		return "You win when the wolf is dead.";
	}

	@Override
	public String cardFlip() {
		return "Cop";
	}		
}
