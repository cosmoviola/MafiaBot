package roles;

public class Mayor extends Role {
	
	@Override
	public int voteStrength(){
		return 2;
	}

	@Override
	public String roleMessage() {
		return "You are the mayor. You have no night action, but your vote in the lynch counts double.";
	}

	@Override
	public String cardFlip() {
		return "Mayor";
	}

	@Override
	public String getTrueName() {
		return "Mayor";
	}

}
