package roles;

import java.util.HashSet;

public abstract class Cop extends Role {

	public Cop(String id) {
		super(id);
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
