package actions;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import game.Game;
import game.Player;

public class Safeguard extends SingleTargetableKeywordAction {
	
	public Safeguard(int p, Function<Game, Boolean> f) {
		super(p, f, "safeguard");
	}


	public Safeguard(int p, Function<Game, Boolean> f, String s) {
		super(p, f, s);
	}

	@Override
	public boolean canTarget(String key, Player actor, Optional<Player> target) {
		return !target.isPresent() || target.get().isAlive();
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g) && target.isPresent()){
			if(actor.isHooked()){
				actor.appendResult("Your safeguard action failed.");
			}else{
				target.get().safeguard();
			}
		}
	}

	@Override
	public String actionMessageForThisNight(Game g) {
		if(!isActive(g)){
			return "";
		}
		String beginning = "Message me '"+keyword+" <user>' to safeguard target user.";
		Collection<Player> validTargets = getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}

}
