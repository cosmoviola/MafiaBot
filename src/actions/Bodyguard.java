package actions;

import java.util.Collection;
import java.util.Optional;
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
				actor.appendResult("Your bodyguard action failed.");
			}else{
				Player t = target.get();
				if(t.isSafeguarded()){
					actor.appendResult("You tried to use your bodyguard action, but your target was protected.");
				}else{
					target.get().bodyguard();
				}
			}
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
