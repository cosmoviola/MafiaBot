package actions;

import java.util.Collection;
import java.util.function.Function;

import game.Game;
import game.Player;

public class Bodyguard extends SingleTargetableKeywordAction {
	
	public Bodyguard(int p, Function<Game, Boolean> f) {
		super(p, f, "bodyguard");
	}

	public Bodyguard(int p, Function<Game, Boolean> f, String s) {
		super(p, f, s);
	}

	@Override
	public boolean canTarget(Player p) {
		return p.isAlive();
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g)){
			if(actor.isHooked()){
				actor.appendResult("Your bodyguard action failed.");
			}else target.ifPresent(t-> {
				t.bodyguard();
			});
		}
	}

	@Override
	public String actionMessageForThisNight(Game g) {
		if(!isActive(g)){
			return "";
		}
		String beginning = "Message me '"+keyword+" <user>' to bodyguard target user.";
		Collection<Player> validTargets = getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}

}
