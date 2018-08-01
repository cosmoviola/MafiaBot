package roles;

import actions.Hook;
import actions.Kill;

public class C5Wolf extends Role {
	
	/**Construct a c5-style Wolf with the given priorities.*/
	public C5Wolf(int hookPriority, int killPriority) {
		Hook hook = new Hook(hookPriority, g -> g.getCycle() == 0);
		Kill kill = new Kill(killPriority, g -> g.getCycle() != 0);
		hook.addKeywordMappings(keywords);
		hook.addKeywordActiveMappings(keywordActive);
		kill.addKeywordMappings(keywords);
		kill.addKeywordActiveMappings(keywordActive);
		actions.add(hook);
		actions.add(kill);
	}

	@Override
	public String roleMessage() {
		return "You are the wolf. At night on cycle 0, you may hook one target user. "
				+ "On following nights, you may kill one target user.";
	}
	
	@Override
	public String cardFlip() {
		return "Wolf";
	}

	@Override
	public String getTrueName() {
		return "Wolf";
	}
}
