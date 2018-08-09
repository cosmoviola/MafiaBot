package roles;

public class Safeguard extends Role {
	
	public Safeguard(int p) {
		actions.Safeguard sg = new actions.Safeguard(p, g -> true);
		for(String key : sg.getKeywords()){
			actions.put(key, sg);
		}
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
