package roles;

import actions.Hook;

public class Hooker extends Role {
	
	public Hooker(int p) {
		Hook hook = new Hook(p, g -> true);
		for(String key : hook.getKeywords()){
			actions.put(key, hook);
		}
	}

	@Override
	public String roleMessage() {
		return "You are a hooker. Every night, you may target a user to cause all of their abilities to fail.";
	}

	@Override
	public String cardFlip() {
		return "Hooker";
	}

	@Override
	public String getTrueName() {
		return "Hooker";
	}

}
