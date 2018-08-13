package roles;

import actions.Redirect;

public class Redirector extends Role {
	
	public Redirector(int priority){
		Redirect red = new Redirect(priority, g -> true);
		registerActionKeywords(red);
	}

	@Override
	public String roleMessage() {
		return "You are a redirector. Every night, you may target two users to redirect one user's actions to the other user.";
	}

	@Override
	public String cardFlip() {
		return "Redirector";
	}

	@Override
	public String getTrueName() {
		return "Redirector";
	}
}
