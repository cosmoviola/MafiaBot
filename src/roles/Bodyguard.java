package roles;

public class Bodyguard extends Role {
	
	public Bodyguard(int p){
		actions.Bodyguard bg = new actions.Bodyguard(p, g -> true);
		registerActionKeywords(bg);
	}

	@Override
	public String roleMessage() {
		return "You are a bodyguard. Every night, you may target a user to protect them from being killed. You may not target yourself.";
	}

	@Override
	public String cardFlip() {
		return "Bodyguard";
	}

	@Override
	public String getTrueName() {
		return "Bodyguard";
	}

}
