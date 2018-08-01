package roles;

import actions.AlignmentCop;

public class InsaneCop extends Cop {

	public InsaneCop(int p) {
		super(p, AlignmentCop.Sanity.INSANE);
	}

	@Override
	public String getTrueName() {
		return "Insane Cop";
	}
}
