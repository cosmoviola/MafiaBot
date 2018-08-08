package roles;

public class Bodyguard extends Role {
	
	public Bodyguard(int p){
		actions.Bodyguard bg = new actions.Bodyguard(p, g -> true);
		for(String key : bg.getKeywords()){
			actions.put(key, bg);
		}
	}

	@Override
	public String roleMessage() {
		return "You are a bodyguard. Every night, you may target a user to protect them from killers. You may not target yourself.";
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
