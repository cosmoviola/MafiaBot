package actions;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import game.Game;
import game.Player;

public class Martyr extends SingleTargetableKeywordAction {
	
	public Martyr(int p, Function<Game, Boolean> f){
		super(p, f, "martyr");
	}

	public Martyr(int p, Function<Game, Boolean> f, String s) {
		super(p, f, s);
	}

	/**Returns true iff this role can target the supplied Player.*/
	public boolean canTarget(String key, Player actor, Optional<Player> target){
		if(!key.equals(keyword)){
			return false;
		}
		return !target.isPresent() || (target.get().isAlive() && !target.get().equals(actor));
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g) && target.isPresent()){
			if(actor.isHooked()){
				actor.appendResult("Your martyr action failed.");
			}else{
				target.get().redirectTo(actor);
			}
		}
	}

	@Override
	public String actionMessageForThisNight(Game g) {
		if(!isActive(g)){
			return "";
		}
		String beginning = "Message me '"+keyword+" <user>' to redirect target user to you.";
		Collection<Player> validTargets = getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}
}
