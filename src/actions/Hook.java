package actions;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import game.Game;
import game.Player;

public class Hook extends SingleTargetableKeywordAction {

	public Hook(int p, Function<Game, Boolean> f) {
		this(p, f, "hook");
	}
	
	public Hook(int p, Function<Game, Boolean> f, String s){
		super(p, f, s);
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g) && target.isPresent()){
			if(actor.isHooked()){
				actor.appendResult("Your hook action failed.");
			}else{
				Player t = target.get();
				if(t.isSafeguarded()){
					actor.appendResult("You tried to use your hook action, but your target was protected.");
				}else{
					target.get().hook();
				}
			}
		}
	}

	@Override
	public String actionMessageForThisNight(Game g) {
		if(!isActive(g)){
			return "";
		}
		String beginning = "Message me '"+keyword+" <user>' to hook target user.";
		Collection<Player> validTargets = getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}
	
	/**Returns true iff this role can target the supplied Player.*/
	public boolean canTarget(String key, Player actor, Optional<Player> target){
		if(!key.equals(keyword)){
			return false;
		}
		return !target.isPresent() || target.get().isAlive();
	}
}
