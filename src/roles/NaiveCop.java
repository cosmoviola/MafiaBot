package roles;

import actions.AlignmentCop;

public class NaiveCop extends Cop {

	public NaiveCop(int p) {
		super(p, AlignmentCop.Sanity.NAIVE);
	}

	@Override
	public String getTrueName() {
		return "Naive Cop";
	}
}
