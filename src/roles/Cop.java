package roles;

import actions.AlignmentCop;

public abstract class Cop extends Role {

	public Cop(int p, AlignmentCop.Sanity san) {
		actions.AlignmentCop cop = new actions.AlignmentCop(p, g -> true, san);
		cop.addKeywordMappings(keywords);
		cop.addKeywordActiveMappings(keywordActive);
		actions.add(cop);
	}

	@Override
	public String roleMessage() {
		return "You are a cop. At night, message me check <user> to determine user's alignment. "
				+ "Be warned: you do not know your sanity."; 
	}

	@Override
	public String cardFlip() {
		return "Cop";
	}
}
