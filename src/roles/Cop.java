package roles;

import java.util.HashSet;

import game.Game;

public abstract class Cop extends Role {

	public Cop() {
	
	}

	@Override
	public String roleMessage() {
		return "You are a cop. At night, message me check <user> to determine user's alignment. "
				+ "Be warned: you do not know your sanity."; 
	}
	
	@Override
	public String roleMessageForThisNight(Game g){
		return "It is Night "+g.getCycle()+". Message me 'check <user>' to determine user's alignment.";
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
