package roles;

public class Safeguard extends Role {
	
	public Safeguard(int p) {
		actions.Safeguard sg = new actions.Safeguard(p, g -> true);
		registerActionKeywords(sg);
	}

	@Override
	public String roleMessage() {
		return "You are a safeguard. Every night, you may target a user to protect them from non-killing actions.";
	}

	@Override
	public String cardFlip() {
		return "Safeguard";
	}

	@Override
	public String getTrueName() {
		return "Safeguard";
	}

}
