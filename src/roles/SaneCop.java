package roles;

import actions.AlignmentCop;

public class SaneCop extends Cop {

	public SaneCop(int p) {
		super(p, AlignmentCop.Sanity.SANE);
	}

	@Override
	public String getTrueName() {
		return "Sane Cop";
	}
}
