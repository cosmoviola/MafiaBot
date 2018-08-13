package roles;

public class Martyr extends Role {

	public Martyr(int p) {
		actions.Martyr martyr = new actions.Martyr(p, g -> true);
		registerActionKeywords(martyr);
	}
	
	@Override
	public String roleMessage() {
		return "You are a martyr. Every night, you may target a user to redirect their actions to yourself. You may not target yourself.";
	}

	@Override
	public String cardFlip() {
		return "Martyr";
	}

	@Override
	public String getTrueName() {
		return "Martyr";
	}
}
