package roles;

import actions.AlignmentCop;

public class ParanoidCop extends Cop {

	public ParanoidCop(int p) {
		super(p, AlignmentCop.Sanity.PARANOID);
	}

	@Override
	public String getTrueName() {
		return "Paranoid Cop";
	}	
}
