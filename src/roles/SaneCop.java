package roles;

import java.util.HashSet;

import game.Game;

public class SaneCop extends Role {

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
	
	@Override
	public String roleMessage() {
		return "You are a cop. At night, message me "+id+" check <user> to determine user's alignment. "
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

	@Override
	public HashSet<String> getCommands() {
		HashSet<String> s = new HashSet<String>();
		s.add("check");
		return s;
	}
}
