package actions;

import java.util.Collection;
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
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g)){
			if(actor.isHooked()){
				actor.appendResult("Your action failed as you were hooked.");
			}else target.ifPresent(t-> {
				t.hook();
			});
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
	public boolean canTarget(Player p){
		return p.isAlive();
	}
}
