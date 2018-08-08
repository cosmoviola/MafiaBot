package roles;

public class Vanilla extends Role {

	@Override
	public String roleMessage() {
		return "You are a vanilla. You have no night action.";
	}

	@Override
	public String cardFlip() {
		return "Vanilla";
	}

	@Override
	public String getTrueName() {
		// TODO Auto-generated method stub
		return "Vanilla";
	}

}
